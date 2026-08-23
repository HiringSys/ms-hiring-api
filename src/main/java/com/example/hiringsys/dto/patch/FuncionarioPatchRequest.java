package com.example.hiringsys.dto.patch;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record FuncionarioPatchRequest(
        @Size(min = 1, max = 150, message = "O nome deve ter entre 1 e 150 caracteres")
        String nome,

        @Email(message = "O e-mail deve possuir um formato válido")
        @Size(min = 1, max = 150, message = "O e-mail deve ter entre 1 e 150 caracteres")
        String email,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone,

        @PositiveOrZero(message = "O salário não pode ser negativo")
        BigDecimal salario,

        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,

        StatusFuncionario status,

        ExperienciaFuncionario experiencia,

        Set<@Positive(message = "O ID do cargo deve ser positivo") Long> cargoIds,

        Set<@Positive(message = "O ID do grupo deve ser positivo") Long> grupoIds,

        Set<@Positive(message = "O ID da rede deve ser positivo") Long> redeIds
) {
}
