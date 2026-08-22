package com.example.hiringsys.dto.error;

public record FieldValidationError(
        String field,
        String message
) {
}
