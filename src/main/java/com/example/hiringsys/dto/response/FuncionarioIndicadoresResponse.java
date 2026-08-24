package com.example.hiringsys.dto.response;

public record FuncionarioIndicadoresResponse(
        long total,
        long emAnalise,
        long aprovados,
        long reprovados,
        long contratados
) {}
