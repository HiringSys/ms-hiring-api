package com.example.hiringsys.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "token_revogado")
public class TokenRevogado {

    @Id
    @Column(length = 36)
    private String jti;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    protected TokenRevogado() {}

    public TokenRevogado(String jti, Instant expiraEm) {
        this.jti = jti;
        this.expiraEm = expiraEm;
    }

    public String getJti() {
        return jti;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }
}
