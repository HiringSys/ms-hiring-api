package com.example.hiringsys.dto.request;

import com.example.hiringsys.enums.TipoRede;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RedeRequest(
        @NotNull(message = "O tipo da rede é obrigatório") TipoRede tipo,
        @NotBlank(message = "A URL da rede é obrigatória")
        @Size(max = 255, message = "A URL deve ter no máximo 255 caracteres") String url
) {}
