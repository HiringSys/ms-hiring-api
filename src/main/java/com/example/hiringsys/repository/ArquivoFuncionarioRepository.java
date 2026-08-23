package com.example.hiringsys.repository;

import com.example.hiringsys.entity.ArquivoFuncionario;
import com.example.hiringsys.enums.CategoriaArquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArquivoFuncionarioRepository extends JpaRepository<ArquivoFuncionario, Long> {
    List<ArquivoFuncionario> findByFuncionarioIdOrderByCriadoEmDesc(Long funcionarioId);
    List<ArquivoFuncionario> findByFuncionarioIdAndCategoriaOrderByCriadoEmDesc(
            Long funcionarioId,
            CategoriaArquivo categoria
    );
}
