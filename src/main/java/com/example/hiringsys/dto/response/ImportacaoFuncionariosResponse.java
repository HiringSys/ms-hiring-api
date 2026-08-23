package com.example.hiringsys.dto.response;

public record ImportacaoFuncionariosResponse(
        Long grupoId,
        int totalRecebidos,
        String mensagem
) {}
