package com.example.hiringsys.dto.response;
import com.example.hiringsys.enums.EstadoGrupo;
import java.time.LocalDateTime;
public record GrupoResponse(
        Long id, String nome, String area, EstadoGrupo estado, Integer disponiveis, String cargo,
        Integer limiteAprovados, String emailEquipe, LocalDateTime criadoEm
) {}
