package com.example.hiringsys.security;

import com.example.hiringsys.config.SecurityConfig;
import com.example.hiringsys.service.JwtRevocationService;
import com.example.hiringsys.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JwtRoundTripTest {

    @Test
    void tokenGeradoPelaApiTambemEhAceitoPeloResourceServer() {
        SecurityConfig config = new SecurityConfig();
        SecretKey secretKey = config.jwtSecretKey("0123456789abcdef0123456789abcdef");
        JwtRevocationService revocationService = mock(JwtRevocationService.class);
        JwtService jwtService = new JwtService(config.jwtEncoder(secretKey), 3600);
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "rh@hiringsys.local",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_RH"))
        );

        String token = jwtService.gerarToken(authentication);
        var jwt = config.jwtDecoder(
                secretKey,
                new JwtRevocationValidator(revocationService)
        ).decode(token);

        assertThat(jwt.getSubject()).isEqualTo("rh@hiringsys.local");
        assertThat(jwt.getClaimAsString("scope")).isEqualTo("RH");
        assertThat(jwt.getId()).isNotBlank();
    }
}
