package com.example.hiringsys.mapper;

import com.example.hiringsys.dto.patch.FuncionarioPatchRequest;
import com.example.hiringsys.dto.request.FuncionarioCreateRequest;
import com.example.hiringsys.dto.request.FuncionarioUpdateRequest;
import com.example.hiringsys.dto.response.FuncionarioResponse;
import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FuncionarioMapper {

    private final CargoMapper cargoMapper;

    public FuncionarioMapper(CargoMapper cargoMapper) {
        this.cargoMapper = cargoMapper;
    }

    public Funcionario toEntity(FuncionarioCreateRequest request) {
        Funcionario funcionario = criarComDadosComuns(
                request.nome(),
                request.email(),
                request.telefone(),
                request.salario(),
                request.cidade(),
                request.cargoId()
        );

        return funcionario;
    }

    public Funcionario toEntity(FuncionarioUpdateRequest request) {
        Funcionario funcionario = criarComDadosComuns(
                request.nome(),
                request.email(),
                request.telefone(),
                request.salario(),
                request.cidade(),
                request.cargoId()
        );
        funcionario.setStatus(request.status());
        return funcionario;
    }

    public Map<String, Object> toPatchMap(FuncionarioPatchRequest request) {
        Map<String, Object> campos = new LinkedHashMap<>();

        if (request.nome() != null) campos.put("nome", request.nome());
        if (request.email() != null) campos.put("email", request.email());
        if (request.telefone() != null) campos.put("telefone", request.telefone());
        if (request.salario() != null) campos.put("salario", request.salario());
        if (request.cidade() != null) campos.put("cidade", request.cidade());
        if (request.status() != null) campos.put("status", request.status().name());
        if (request.cargoId() != null) campos.put("cargo", request.cargoId());

        return campos;
    }

    public FuncionarioResponse toResponse(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getEmail(),
                funcionario.getTelefone(),
                funcionario.getSalario(),
                funcionario.getCidade(),
                funcionario.getStatus(),
                cargoMapper.toResponse(funcionario.getCargo()),
                funcionario.getCriadoEm(),
                funcionario.getAtualizadoEm()
        );
    }

    private Funcionario criarComDadosComuns(
            String nome,
            String email,
            String telefone,
            java.math.BigDecimal salario,
            String cidade,
            Long cargoId
    ) {
        Cargo cargo = new Cargo();
        cargo.setId(cargoId);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setTelefone(telefone);
        funcionario.setSalario(salario);
        funcionario.setCidade(cidade);
        funcionario.setCargo(cargo);
        return funcionario;
    }
}
