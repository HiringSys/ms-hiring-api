package com.example.hiringsys.dto.request;
import com.example.hiringsys.enums.EstadoGrupo;
import jakarta.validation.constraints.*;
public record GrupoRequest(@NotBlank @Size(max=100) String nome, @NotBlank @Size(max=100) String area, @NotNull EstadoGrupo estado, @NotNull @Min(0) Integer disponiveis, @Size(max=100) String cargo) {}
