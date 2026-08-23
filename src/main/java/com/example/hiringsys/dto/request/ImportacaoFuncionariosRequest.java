package com.example.hiringsys.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ImportacaoFuncionariosRequest(
        @NotEmpty
        @Size(max = 1000)
        List<@Valid FuncionarioImportacaoRequest> funcionarios
) {}
