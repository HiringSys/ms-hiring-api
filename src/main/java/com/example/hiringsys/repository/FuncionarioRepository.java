package com.example.hiringsys.repository;

import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.enums.StatusFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    Optional<Funcionario> findByEmailIgnoreCase(String email);
    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
    List<Funcionario> findByStatus(StatusFuncionario status);
    @Query("select distinct f from Funcionario f join f.cargos c where c = :cargo")
    List<Funcionario> findByCargo(@Param("cargo") Cargo cargo);
}
