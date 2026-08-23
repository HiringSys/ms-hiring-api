package com.example.hiringsys.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record FuncionarioUpdateRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String telefone,
        @PositiveOrZero BigDecimal salario,
        @Size(max = 100) String cidade,
        @NotNull StatusFuncionario status,
        @NotNull ExperienciaFuncionario experiencia,
        @NotNull @PositiveOrZero Integer anosExperiencia,
        @NotNull Set<@Positive Long> cargoIds,
        @NotNull List<@Valid RedeRequest> redes
) {}
