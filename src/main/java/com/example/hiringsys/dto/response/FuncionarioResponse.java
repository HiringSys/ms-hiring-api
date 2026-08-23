package com.example.hiringsys.dto.response;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record FuncionarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        BigDecimal salario,
        String cidade,
        StatusFuncionario status,
        ExperienciaFuncionario experiencia,
        Set<CargoResponse> cargos,
        Set<GrupoResponse> grupos,
        Set<RedeResponse> redes,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
