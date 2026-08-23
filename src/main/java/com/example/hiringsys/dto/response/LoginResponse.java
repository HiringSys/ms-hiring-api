package com.example.hiringsys.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String perfil
) {
}
