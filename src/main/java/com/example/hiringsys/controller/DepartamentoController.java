package com.example.hiringsys.controller;

import com.example.hiringsys.dto.request.DepartamentoRequest;
import com.example.hiringsys.dto.response.DepartamentoResponse;
import com.example.hiringsys.entity.Departamento;
import com.example.hiringsys.mapper.DepartamentoMapper;
import com.example.hiringsys.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService service;
    private final DepartamentoMapper mapper;

    public DepartamentoController(DepartamentoService service, DepartamentoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> listarTodos() {
        List<DepartamentoResponse> departamentos = service.listarTodos()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(departamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> buscarPorId(@PathVariable Long id) {
        Departamento departamento = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(departamento));
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> cadastrar(
            @Valid @RequestBody DepartamentoRequest request
    ) {
        Departamento departamento = mapper.toEntity(request);
        Departamento salvo = service.salvar(departamento);

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
    public ResponseEntity<DepartamentoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DepartamentoRequest request
    ) {
        Departamento dados = mapper.toEntity(request);
        Departamento atualizado = service.atualizar(id, dados);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
