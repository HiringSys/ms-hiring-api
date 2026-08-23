package com.example.hiringsys.service;

import com.example.hiringsys.entity.TokenRevogado;
import com.example.hiringsys.repository.TokenRevogadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtRevocationServiceTest {

    @Mock private TokenRevogadoRepository tokensRevogados;

    private JwtRevocationService service;

    @BeforeEach
    void setUp() {
        service = new JwtRevocationService(tokensRevogados);
    }

    @Test
    void persisteTokenRevogadoEAproveitaParaLimparExpirados() {
        Instant expiracao = Instant.now().plusSeconds(3600);
        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);
        when(jwt.getId()).thenReturn("sessao-123");
        when(jwt.getExpiresAt()).thenReturn(expiracao);

        service.revogar(jwt);

        ArgumentCaptor<TokenRevogado> tokenCaptor = ArgumentCaptor.forClass(TokenRevogado.class);
        InOrder ordem = inOrder(tokensRevogados);
        ordem.verify(tokensRevogados).deleteByExpiraEmBefore(
                org.mockito.ArgumentMatchers.any(Instant.class)
        );
        ordem.verify(tokensRevogados).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getJti()).isEqualTo("sessao-123");
        assertThat(tokenCaptor.getValue().getExpiraEm()).isEqualTo(expiracao);
    }

    @Test
    void consultaRevogacaoPeloJti() {
        when(tokensRevogados.existsById("sessao-123")).thenReturn(true);

        assertThat(service.estaRevogado("sessao-123")).isTrue();

        verify(tokensRevogados).existsById("sessao-123");
    }
}
