package com.example.hiringsys.dto.request;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record FuncionarioCreateRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String telefone,
        @PositiveOrZero BigDecimal salario,
        @Size(max = 100) String cidade,
        @Size(max = 100) String departamento,
        ExperienciaFuncionario experiencia,
        @PositiveOrZero Integer anosExperiencia,
        @NotEmpty Set<@Positive Long> cargoIds,
        List<@Valid RedeRequest> redes
) {}
