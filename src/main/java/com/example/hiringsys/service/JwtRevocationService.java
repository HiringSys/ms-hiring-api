package com.example.hiringsys.service;

import com.example.hiringsys.entity.TokenRevogado;
import com.example.hiringsys.repository.TokenRevogadoRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class JwtRevocationService {

    private final TokenRevogadoRepository tokensRevogados;

    public JwtRevocationService(TokenRevogadoRepository tokensRevogados) {
        this.tokensRevogados = tokensRevogados;
    }

    @Transactional
    public void revogar(Jwt jwt) {
        tokensRevogados.deleteByExpiraEmBefore(Instant.now());
        tokensRevogados.save(new TokenRevogado(jwt.getId(), jwt.getExpiresAt()));
    }

    @Transactional(readOnly = true)
    public boolean estaRevogado(String jti) {
        return tokensRevogados.existsById(jti);
    }
}
