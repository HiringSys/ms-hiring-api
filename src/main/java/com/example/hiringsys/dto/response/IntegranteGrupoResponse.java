package com.example.hiringsys.dto.response;
import java.math.BigDecimal;
public record IntegranteGrupoResponse(Long funcionarioId, String nome, String email, BigDecimal scoreProximidade) {}
