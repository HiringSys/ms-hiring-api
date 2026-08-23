package com.example.hiringsys.dto.request; 
import jakarta.validation.constraints.*;

public record CargoRequest(
    @NotBlank(message="O nome do cargo é obrigatório") 
    @Size(max=100,message="O nome do cargo deve ter no máximo 100 caracteres") 
    String nome
) { 
}
