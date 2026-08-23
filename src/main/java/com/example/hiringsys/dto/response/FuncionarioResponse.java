package com.example.hiringsys.dto.response;

import com.example.hiringsys.enums.StatusFuncionario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FuncionarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        BigDecimal salario,
        String cidade,
        StatusFuncionario status,
        CargoResponse cargo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
