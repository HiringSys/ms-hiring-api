package com.example.hiringsys.dto.response;

public record StageResponse(
        Long id,
        String jobTitle,
        String department,
        String status,
        Integer availableSlots,
        long participants,
        String role,
        Integer approvalLimit,
        String teamEmail
) {}
