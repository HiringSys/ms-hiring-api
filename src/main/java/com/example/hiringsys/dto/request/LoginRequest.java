package com.example.hiringsys.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @JsonAlias("username") String email,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
