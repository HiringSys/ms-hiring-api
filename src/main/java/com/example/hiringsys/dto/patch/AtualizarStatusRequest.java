package com.example.hiringsys.dto.patch;

import com.example.hiringsys.enums.StatusFuncionario;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(
        @NotNull(message = "O status é obrigatório")
        StatusFuncionario status
) {
}
