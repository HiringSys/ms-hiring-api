package com.example.hiringsys.controller;

import com.example.hiringsys.dto.request.RedeRequest;
import com.example.hiringsys.dto.response.RedeResponse;
import com.example.hiringsys.entity.Rede;
import com.example.hiringsys.enums.TipoRede;
import com.example.hiringsys.mapper.RedeMapper;
import com.example.hiringsys.service.RedeService;
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
@RequestMapping("/redes")
public class RedeController {

    private final RedeService service;
    private final RedeMapper mapper;

    public RedeController(RedeService service, RedeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<RedeResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RedeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(service.buscarPorId(id)));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<RedeResponse>> buscarPorTipo(@PathVariable TipoRede tipo) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo).stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<RedeResponse> cadastrar(@Valid @RequestBody RedeRequest request) {
        Rede salvo = service.salvar(mapper.toEntity(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RedeResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RedeRequest request
    ) {
        return ResponseEntity.ok(mapper.toResponse(service.atualizar(id, mapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
