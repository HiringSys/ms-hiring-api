package com.example.hiringsys.repository;

import com.example.hiringsys.entity.TokenRevogado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface TokenRevogadoRepository extends JpaRepository<TokenRevogado, String> {

    long deleteByExpiraEmBefore(Instant instante);
}
