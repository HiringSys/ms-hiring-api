package com.example.hiringsys.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record StageCandidateResponse(
        Long id,
        String name,
        String email,
        String avatarUrl,
        String status,
        String phone,
        List<StageSocialLinkResponse> networks,
        String seniority,
        Integer experienceYears,
        String role,
        BigDecimal salaryExpectation,
        String curriculumUrl,
        @Schema(
                description = "Afinidade com a vaga. NULL significa que ainda nao foi calculada; zero e um resultado calculado valido.",
                nullable = true,
                minimum = "0",
                maximum = "100"
        )
        BigDecimal jobAffinity
) {}
