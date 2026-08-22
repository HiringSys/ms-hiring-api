package com.example.hiringsys.repository;

import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.enums.StatusFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    Optional<Funcionario> findByEmailIgnoreCase(String email);
    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
    List<Funcionario> findByStatus(StatusFuncionario status);
    List<Funcionario> findByCargo(Cargo cargo);
}
