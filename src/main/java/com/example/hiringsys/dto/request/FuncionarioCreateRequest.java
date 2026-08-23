package com.example.hiringsys.dto.request;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record FuncionarioCreateRequest(
        @NotBlank(message = "O nome do funcionário é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve possuir um formato válido")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
        String email,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone,

        @PositiveOrZero(message = "O salário não pode ser negativo")
        BigDecimal salario,

        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,

        @NotNull(message = "A experiência é obrigatória")
        ExperienciaFuncionario experiencia,

        @NotEmpty(message = "Ao menos um cargo é obrigatório")
        Set<@Positive(message = "O ID do cargo deve ser positivo") Long> cargoIds,

        Set<@Positive(message = "O ID do grupo deve ser positivo") Long> grupoIds,

        Set<@Positive(message = "O ID da rede deve ser positivo") Long> redeIds
) {
}
