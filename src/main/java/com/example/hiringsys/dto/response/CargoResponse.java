package com.example.hiringsys.dto.response;

public record CargoResponse(
        Long id,
        String nome,
        Long departamentoId,
        String departamentoNome
) {
}
