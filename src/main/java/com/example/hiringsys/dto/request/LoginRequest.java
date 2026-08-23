package com.example.hiringsys.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O usuário é obrigatório")
        @JsonAlias("email") String username,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
