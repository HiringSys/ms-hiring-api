package com.example.hiringsys.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartamentoRequest(
        @NotBlank(message = "O nome do departamento é obrigatório")
        @Size(max = 100, message = "O nome do departamento deve ter no máximo 100 caracteres")
        String nome
) {
}
