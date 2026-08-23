package com.example.hiringsys.controller;

import com.example.hiringsys.dto.patch.AtualizarStatusRequest;
import com.example.hiringsys.dto.patch.FuncionarioPatchRequest;
import com.example.hiringsys.dto.request.FuncionarioCreateRequest;
import com.example.hiringsys.dto.request.FuncionarioUpdateRequest;
import com.example.hiringsys.dto.response.FuncionarioResponse;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.mapper.FuncionarioMapper;
import com.example.hiringsys.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários", description = "Cadastro e processo seletivo de funcionários")
public class FuncionarioController {

    private final FuncionarioService service;
    private final FuncionarioMapper mapper;

    public FuncionarioController(FuncionarioService service, FuncionarioMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponse>> listarTodos() {
        List<FuncionarioResponse> funcionarios = service.listarTodos()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable Long id) {
        Funcionario funcionario = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(funcionario));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<FuncionarioResponse>> buscarPorNome(@RequestParam String nome) {
        List<FuncionarioResponse> funcionarios = service.buscarPorNome(nome)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FuncionarioResponse>> buscarPorStatus(
            @PathVariable StatusFuncionario status
    ) {
        List<FuncionarioResponse> funcionarios = service.buscarPorStatus(status)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/cargo/{cargoId}")
    public ResponseEntity<List<FuncionarioResponse>> buscarPorCargo(@PathVariable Long cargoId) {
        List<FuncionarioResponse> funcionarios = service.buscarPorCargo(cargoId)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<FuncionarioResponse>> buscarPorGrupo(@PathVariable Long grupoId) {
        List<FuncionarioResponse> funcionarios = service.buscarPorGrupo(grupoId)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/experiencia/{experiencia}")
    public ResponseEntity<List<FuncionarioResponse>> buscarPorExperiencia(
            @PathVariable ExperienciaFuncionario experiencia
    ) {
        List<FuncionarioResponse> funcionarios = service.buscarPorExperiencia(experiencia)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(funcionarios);
    }

    @PostMapping
    @Operation(summary = "Cadastra um funcionário com cargos e redes")
    public ResponseEntity<FuncionarioResponse> cadastrar(
            @Valid @RequestBody FuncionarioCreateRequest request
    ) {
        Funcionario funcionario = mapper.toEntity(request);
        Funcionario salvo = service.salvar(funcionario);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(mapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioUpdateRequest request
    ) {
        Funcionario dados = mapper.toEntity(request);
        Funcionario atualizado = service.atualizar(id, dados);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> atualizarParcialmente(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioPatchRequest request
    ) {
        Map<String, Object> campos = mapper.toPatchMap(request);
        Funcionario atualizado = service.atualizarParcial(id, campos);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Avança o status respeitando as transições permitidas")
    public ResponseEntity<FuncionarioResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request
    ) {
        Map<String, Object> campoStatus = Map.of("status", request.status());
        Funcionario atualizado = service.atualizarParcial(id, campoStatus);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
