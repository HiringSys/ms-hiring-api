package com.example.hiringsys.controller;

import com.example.hiringsys.dto.request.GrupoRequest;
import com.example.hiringsys.dto.response.GrupoResponse;
import com.example.hiringsys.entity.Grupo;
import com.example.hiringsys.enums.EstadoGrupo;
import com.example.hiringsys.mapper.GrupoMapper;
import com.example.hiringsys.service.GrupoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private final GrupoService service;
    private final GrupoMapper mapper;

    public GrupoController(GrupoService service, GrupoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<GrupoResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(service.buscarPorId(id)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<GrupoResponse>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<GrupoResponse>> buscarPorEstado(@PathVariable EstadoGrupo estado) {
        return ResponseEntity.ok(service.buscarPorEstado(estado).stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<GrupoResponse> cadastrar(@Valid @RequestBody GrupoRequest request) {
        Grupo salvo = service.salvar(mapper.toEntity(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrupoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GrupoRequest request
    ) {
        return ResponseEntity.ok(mapper.toResponse(service.atualizar(id, mapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
