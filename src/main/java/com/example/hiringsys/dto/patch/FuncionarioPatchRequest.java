package com.example.hiringsys.dto.patch;

import com.example.hiringsys.dto.request.RedeRequest;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record FuncionarioPatchRequest(
        @Size(min = 1, max = 150) String nome,
        @Email @Size(min = 1, max = 150) String email,
        @Size(max = 20) String telefone,
        @PositiveOrZero BigDecimal salario,
        @Size(max = 100) String cidade,
        @Size(max = 100) String departamento,
        StatusFuncionario status,
        ExperienciaFuncionario experiencia,
        @PositiveOrZero Integer anosExperiencia,
        Set<@Positive Long> cargoIds,
        List<@Valid RedeRequest> redes
) {}
