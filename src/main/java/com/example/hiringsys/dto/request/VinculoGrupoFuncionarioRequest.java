package com.example.hiringsys.dto.request;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record VinculoGrupoFuncionarioRequest(@NotNull Long funcionarioId, @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal scoreProximidade) {}
