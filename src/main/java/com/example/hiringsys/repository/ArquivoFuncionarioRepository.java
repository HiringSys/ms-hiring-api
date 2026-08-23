package com.example.hiringsys.repository;

import com.example.hiringsys.entity.ArquivoFuncionario;
import com.example.hiringsys.enums.CategoriaArquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArquivoFuncionarioRepository extends JpaRepository<ArquivoFuncionario, Long> {
    Optional<ArquivoFuncionario> findByIdAndFuncionarioId(Long id, Long funcionarioId);
    List<ArquivoFuncionario> findByFuncionarioIdOrderByCriadoEmDesc(Long funcionarioId);
    List<ArquivoFuncionario> findByFuncionarioIdAndCategoriaOrderByCriadoEmDesc(
            Long funcionarioId,
            CategoriaArquivo categoria
    );
}
