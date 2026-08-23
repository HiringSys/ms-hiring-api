package com.example.hiringsys.repository;

import com.example.hiringsys.entity.GrupoFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoFuncionarioRepository extends JpaRepository<GrupoFuncionario, Long> {
    List<GrupoFuncionario> findByFuncionarioId(Long funcionarioId);
    List<GrupoFuncionario> findByGrupoId(Long grupoId);
}
