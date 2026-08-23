package com.example.hiringsys.dto.request;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record FuncionarioImportacaoRequest(
        @NotBlank @Size(max = 150)
        @Schema(example = "Ana Beatriz Costa")
        String nome,

        @NotBlank @Email @Size(max = 150)
        @Schema(example = "ana.costa@hiringsys.local")
        String email,

        @Size(max = 20)
        @Schema(example = "11981110001")
        String telefone,

        @NotNull @PositiveOrZero
        @Schema(example = "5200.00")
        BigDecimal salario,

        @Size(max = 100)
        @Schema(example = "Sao Paulo")
        String cidade,

        @NotNull
        StatusFuncionario status,

        @NotNull
        ExperienciaFuncionario experiencia,

        @PositiveOrZero
        @Schema(description = "Opcional. Quando ausente, e inferido a partir da experiencia.")
        Integer anosExperiencia,

        @NotEmpty
        List<@NotBlank @Size(max = 100) String> cargos
) {}
