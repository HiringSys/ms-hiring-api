package com.example.hiringsys.security;

import com.example.hiringsys.service.JwtRevocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtRevocationValidatorTest {

    @Mock private JwtRevocationService revocationService;

    private JwtRevocationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new JwtRevocationValidator(revocationService);
    }

    @Test
    void aceitaTokenAtivo() {
        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);
        when(jwt.getId()).thenReturn("sessao-ativa");
        when(revocationService.estaRevogado("sessao-ativa")).thenReturn(false);

        OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void rejeitaTokenRevogado() {
        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);
        when(jwt.getId()).thenReturn("sessao-encerrada");
        when(revocationService.estaRevogado("sessao-encerrada")).thenReturn(true);

        OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void rejeitaTokenAntigoSemJti() {
        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);

        OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
        verify(revocationService, never()).estaRevogado(
                org.mockito.ArgumentMatchers.anyString()
        );
    }
}
