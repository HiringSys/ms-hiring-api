package com.example.hiringsys.mapper;

import com.example.hiringsys.dto.patch.FuncionarioPatchRequest;
import com.example.hiringsys.dto.request.FuncionarioCreateRequest;
import com.example.hiringsys.dto.request.FuncionarioUpdateRequest;
import com.example.hiringsys.dto.request.RedeRequest;
import com.example.hiringsys.dto.response.ArquivoFuncionarioResponse;
import com.example.hiringsys.dto.response.CargoResponse;
import com.example.hiringsys.dto.response.FuncionarioResponse;
import com.example.hiringsys.dto.response.GrupoFuncionarioResponse;
import com.example.hiringsys.dto.response.RedeResponse;
import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.entity.Rede;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FuncionarioMapper {

    public Funcionario toEntity(FuncionarioCreateRequest request) {
        Funcionario funcionario = dadosComuns(request.nome(), request.email(), request.telefone(),
                request.salario(), request.cidade(), request.departamento(), request.cargoIds(), request.redes());
        funcionario.setExperiencia(request.experiencia());
        funcionario.setAnosExperiencia(request.anosExperiencia());
        return funcionario;
    }

    public Funcionario toEntity(FuncionarioUpdateRequest request) {
        Funcionario funcionario = dadosComuns(request.nome(), request.email(), request.telefone(),
                request.salario(), request.cidade(), request.departamento(), request.cargoIds(), request.redes());
        funcionario.setStatus(request.status());
        funcionario.setExperiencia(request.experiencia());
        funcionario.setAnosExperiencia(request.anosExperiencia());
        return funcionario;
    }

    public Map<String, Object> toPatchMap(FuncionarioPatchRequest request) {
        Map<String, Object> campos = new LinkedHashMap<>();
        if (request.nome() != null) campos.put("nome", request.nome());
        if (request.email() != null) campos.put("email", request.email());
        if (request.telefone() != null) campos.put("telefone", request.telefone());
        if (request.salario() != null) campos.put("salario", request.salario());
        if (request.cidade() != null) campos.put("cidade", request.cidade());
        if (request.departamento() != null) campos.put("departamento", request.departamento());
        if (request.status() != null) campos.put("status", request.status());
        if (request.experiencia() != null) campos.put("experiencia", request.experiencia());
        if (request.anosExperiencia() != null) campos.put("anosExperiencia", request.anosExperiencia());
        if (request.cargoIds() != null) campos.put("cargoIds", request.cargoIds());
        if (request.redes() != null) campos.put("redes", request.redes());
        return campos;
    }

    public FuncionarioResponse toResponse(Funcionario funcionario) {
        List<CargoResponse> cargos = funcionario.getCargos().stream()
                .map(cargo -> new CargoResponse(cargo.getId(), cargo.getNome())).toList();
        List<RedeResponse> redes = funcionario.getRedes().stream()
                .map(rede -> new RedeResponse(rede.getId(), rede.getTipo(), rede.getUrl())).toList();
        List<GrupoFuncionarioResponse> grupos = funcionario.getGrupos().stream()
                .map(vinculo -> new GrupoFuncionarioResponse(vinculo.getGrupo().getId(),
                        vinculo.getGrupo().getNome(), vinculo.getGrupo().getArea(), vinculo.getScoreProximidade()))
                .toList();
        List<ArquivoFuncionarioResponse> arquivos = funcionario.getArquivos().stream()
                .map(arquivo -> new ArquivoFuncionarioResponse(arquivo.getId(), funcionario.getId(),
                        arquivo.getNomeArquivo(), arquivo.getCategoria(), arquivo.getMimeType(),
                        arquivo.getExtensao(), arquivo.getTamanhoBytes(), arquivo.getCriadoEm()))
                .toList();
        return new FuncionarioResponse(funcionario.getId(), funcionario.getNome(), funcionario.getEmail(),
                funcionario.getTelefone(), funcionario.getSalario(), funcionario.getCidade(),
                funcionario.getDepartamento(),
                funcionario.getStatus(), funcionario.getExperiencia(), funcionario.getAnosExperiencia(),
                cargos, redes, grupos, arquivos,
                funcionario.getCriadoEm(), funcionario.getAtualizadoEm());
    }

    private Funcionario dadosComuns(String nome, String email, String telefone,
                                    java.math.BigDecimal salario, String cidade, String departamento,
                                    Set<Long> cargoIds, List<RedeRequest> redes) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setTelefone(telefone);
        funcionario.setSalario(salario);
        funcionario.setCidade(cidade);
        funcionario.setDepartamento(departamento);
        Set<Cargo> cargos = new LinkedHashSet<>();
        if (cargoIds != null) cargoIds.forEach(id -> { Cargo cargo = new Cargo(); cargo.setId(id); cargos.add(cargo); });
        funcionario.setCargos(cargos);
        Set<Rede> entidadesRede = new LinkedHashSet<>();
        if (redes != null) redes.forEach(item -> { Rede rede = new Rede(); rede.setTipo(item.tipo()); rede.setUrl(item.url()); entidadesRede.add(rede); });
        funcionario.setRedes(entidadesRede);
        return funcionario;
    }

}
