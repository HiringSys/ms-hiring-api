package com.example.hiringsys.service;

import com.example.hiringsys.config.SecurityConfig;
import com.example.hiringsys.security.JwtRevocationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock private JwtEncoder jwtEncoder;

    @Test
    void incluiIdentificadorUnicoDeSessaoNoToken() {
        Jwt encodedJwt = Jwt.withTokenValue("token-assinado")
                .header("alg", "HS256")
                .subject("rh@hiringsys.local")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(encodedJwt);
        JwtService service = new JwtService(jwtEncoder, 3600);
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "rh@hiringsys.local",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_RH"))
        );

        String token = service.gerarToken(authentication);

        assertThat(token).isEqualTo("token-assinado");
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parametersCaptor.capture());
        assertThat(parametersCaptor.getValue().getClaims().getClaims().get("jti"))
                .isInstanceOf(String.class)
                .asString()
                .hasSize(36);
    }

    @Test
    void tokenGeradoEhAceitoPeloDecoderDaApi() {
        SecurityConfig securityConfig = new SecurityConfig();
        SecretKey secretKey = securityConfig.jwtSecretKey(
                "segredo-de-teste-com-pelo-menos-32-caracteres"
        );
        JwtRevocationService revocationService = mock(JwtRevocationService.class);
        JwtEncoder encoder = securityConfig.jwtEncoder(secretKey);
        JwtDecoder decoder = securityConfig.jwtDecoder(
                secretKey,
                new JwtRevocationValidator(revocationService)
        );
        JwtService service = new JwtService(encoder, 3600);
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "rh@hiringsys.local",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_RH"))
        );

        Jwt decoded = decoder.decode(service.gerarToken(authentication));

        assertThat(decoded.getSubject()).isEqualTo("rh@hiringsys.local");
        assertThat(decoded.getId()).isNotBlank();
        assertThat(decoded.getClaimAsString("scope")).isEqualTo("RH");
        verify(revocationService).estaRevogado(decoded.getId());
    }
}
