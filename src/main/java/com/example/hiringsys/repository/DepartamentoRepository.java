package com.example.hiringsys.repository;

import com.example.hiringsys.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
