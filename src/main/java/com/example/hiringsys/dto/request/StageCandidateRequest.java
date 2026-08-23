package com.example.hiringsys.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record StageCandidateRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String phone,
        @NotBlank @Size(max = 100) String role,
        @NotBlank @Pattern(regexp = "junior|pleno|senior") String seniority,
        @NotNull @PositiveOrZero Integer experienceYears,
        @NotNull @PositiveOrZero BigDecimal salaryExpectation
) {}
