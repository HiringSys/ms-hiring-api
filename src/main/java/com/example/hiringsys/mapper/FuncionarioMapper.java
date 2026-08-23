package com.example.hiringsys.mapper;

import com.example.hiringsys.dto.patch.FuncionarioPatchRequest;
import com.example.hiringsys.dto.request.FuncionarioCreateRequest;
import com.example.hiringsys.dto.request.FuncionarioUpdateRequest;
import com.example.hiringsys.dto.response.FuncionarioResponse;
import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.entity.Grupo;
import com.example.hiringsys.entity.Rede;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FuncionarioMapper {

    private final CargoMapper cargoMapper;
    private final GrupoMapper grupoMapper;
    private final RedeMapper redeMapper;

    public FuncionarioMapper(CargoMapper cargoMapper, GrupoMapper grupoMapper, RedeMapper redeMapper) {
        this.cargoMapper = cargoMapper;
        this.grupoMapper = grupoMapper;
        this.redeMapper = redeMapper;
    }

    public Funcionario toEntity(FuncionarioCreateRequest request) {
        Funcionario funcionario = criarComDadosComuns(
                request.nome(),
                request.email(),
                request.telefone(),
                request.salario(),
                request.cidade(),
                request.experiencia(),
                request.cargoIds(),
                request.grupoIds(),
                request.redeIds()
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
                request.experiencia(),
                request.cargoIds(),
                request.grupoIds(),
                request.redeIds()
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
        if (request.experiencia() != null) campos.put("experiencia", request.experiencia().name());
        if (request.cargoIds() != null) campos.put("cargoIds", request.cargoIds());
        if (request.grupoIds() != null) campos.put("grupoIds", request.grupoIds());
        if (request.redeIds() != null) campos.put("redeIds", request.redeIds());

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
                funcionario.getExperiencia(),
                funcionario.getCargos().stream()
                        .map(cargoMapper::toResponse)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                funcionario.getGrupos().stream()
                        .map(grupoMapper::toResponse)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                funcionario.getRedes().stream()
                        .map(redeMapper::toResponse)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
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
            ExperienciaFuncionario experiencia,
            Set<Long> cargoIds,
            Set<Long> grupoIds,
            Set<Long> redeIds
    ) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setTelefone(telefone);
        funcionario.setSalario(salario);
        funcionario.setCidade(cidade);
        funcionario.setExperiencia(experiencia);
        funcionario.setCargos(criarCargos(cargoIds));
        funcionario.setGrupos(criarGrupos(grupoIds));
        funcionario.setRedes(criarRedes(redeIds));
        return funcionario;
    }

    private Set<Cargo> criarCargos(Set<Long> ids) {
        if (ids == null) return new LinkedHashSet<>();
        return ids.stream().map(id -> {
            Cargo cargo = new Cargo();
            cargo.setId(id);
            return cargo;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Grupo> criarGrupos(Set<Long> ids) {
        if (ids == null) return new LinkedHashSet<>();
        return ids.stream().map(id -> {
            Grupo grupo = new Grupo();
            grupo.setId(id);
            return grupo;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Rede> criarRedes(Set<Long> ids) {
        if (ids == null) return new LinkedHashSet<>();
        return ids.stream().map(id -> {
            Rede rede = new Rede();
            rede.setId(id);
            return rede;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
