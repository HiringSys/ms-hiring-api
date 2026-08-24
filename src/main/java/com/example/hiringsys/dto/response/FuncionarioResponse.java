package com.example.hiringsys.dto.response;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FuncionarioResponse(
        Long id, String nome, String email, String telefone, BigDecimal salario, String cidade,
        String departamento,
        StatusFuncionario status, ExperienciaFuncionario experiencia, Integer anosExperiencia,
        List<CargoResponse> cargos, List<RedeResponse> redes,
        List<GrupoFuncionarioResponse> grupos, List<ArquivoFuncionarioResponse> arquivos,
        LocalDateTime criadoEm, LocalDateTime atualizadoEm
) {}
