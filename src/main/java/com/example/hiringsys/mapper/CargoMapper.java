package com.example.hiringsys.mapper;

import org.springframework.stereotype.Component;

import com.example.hiringsys.dto.request.CargoRequest;
import com.example.hiringsys.dto.response.CargoResponse;
import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Departamento;

// O mapper centraliza as conversões entre os DTOs da API e as entidades da aplicação.
@Component
public class CargoMapper {

    // Converte os dados recebidos no request em uma entidade Cargo.
    public Cargo toEntity(CargoRequest request) {
        Departamento departamento = new Departamento();
        departamento.setId(request.departamentoId());

        Cargo cargo = new Cargo();
        cargo.setNome(request.nome());
        cargo.setDepartamento(departamento);
        return cargo;
    }

    // Converte a entidade em um DTO de resposta para o frontend.
    public CargoResponse toResponse(Cargo cargo) {
        Departamento departamento = cargo.getDepartamento();

        return new CargoResponse(
                cargo.getId(),
                cargo.getNome(),
                departamento.getId(),
                departamento.getNome()
        );
    }
}
