package com.example.hiringsys.mapper;

import com.example.hiringsys.dto.request.DepartamentoRequest;
import com.example.hiringsys.dto.response.DepartamentoResponse;
import com.example.hiringsys.entity.Departamento;
import org.springframework.stereotype.Component;

@Component
public class DepartamentoMapper {

    public Departamento toEntity(DepartamentoRequest request) {
        Departamento departamento = new Departamento();
        departamento.setNome(request.nome());
        return departamento;
    }

    public DepartamentoResponse toResponse(Departamento departamento) {
        return new DepartamentoResponse(
                departamento.getId(),
                departamento.getNome()
        );
    }
}
