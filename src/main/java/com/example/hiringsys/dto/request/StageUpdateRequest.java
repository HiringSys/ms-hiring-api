package com.example.hiringsys.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StageUpdateRequest(
        @NotBlank @Size(max = 100) String jobTitle,
        @NotBlank @Size(max = 100) String department,
        @NotNull @Min(0) Integer availableSlots,
        @NotNull @Min(0) Integer approvalLimit,
        @NotBlank @Email @Size(max = 150) String teamEmail
) {}
