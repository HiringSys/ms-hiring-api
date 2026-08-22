package com.example.hiringsys.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// O record representa um objeto imutável e cria automaticamente construtor, toString e getters.
// É adequado para representar os dados recebidos pela API.
public record CargoRequest(
        @NotBlank(message = "O nome do cargo é obrigatório")
        @Size(max = 100, message = "O nome do cargo deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "O departamento é obrigatório")
        @Positive(message = "O ID do departamento deve ser positivo")
        Long departamentoId
) {
}
