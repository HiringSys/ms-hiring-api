package com.example.hiringsys.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
