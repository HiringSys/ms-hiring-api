package com.example.hiringsys.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record StageSelectionRequest(
        @NotNull List<@Positive Long> approvedCandidateIds
) {}
