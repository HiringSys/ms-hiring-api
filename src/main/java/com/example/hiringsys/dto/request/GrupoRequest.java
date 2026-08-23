package com.example.hiringsys.dto.request;

import com.example.hiringsys.enums.EstadoGrupo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record GrupoRequest(
        @NotBlank(message = "O nome do grupo é obrigatório")
        @Size(max = 100, message = "O nome do grupo deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "A área do grupo é obrigatória")
        @Size(max = 100, message = "A área deve ter no máximo 100 caracteres")
        String area,

        @NotNull(message = "O estado do grupo é obrigatório")
        EstadoGrupo estado,

        @NotNull(message = "A quantidade de vagas disponíveis é obrigatória")
        @PositiveOrZero(message = "A quantidade de vagas não pode ser negativa")
        Integer disponiveis,

        @Size(max = 100, message = "O cargo deve ter no máximo 100 caracteres")
        String cargo
) {
}
