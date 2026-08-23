package com.example.hiringsys.dto.response;
import java.math.BigDecimal;
public record GrupoFuncionarioResponse(Long id, String nome, String area, BigDecimal scoreProximidade) {}
