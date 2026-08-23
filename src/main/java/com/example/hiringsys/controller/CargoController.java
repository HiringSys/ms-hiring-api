package com.example.hiringsys.controller;

import java.net.URI;
import java.util.List;

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

import com.example.hiringsys.dto.request.CargoRequest;
import com.example.hiringsys.dto.response.CargoResponse;
import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.mapper.CargoMapper;
import com.example.hiringsys.service.CargoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cargos")
public class CargoController {

    private final CargoService service;
    private final CargoMapper mapper;

    public CargoController(CargoService service, CargoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<CargoResponse>> listarTodos() {
        List<CargoResponse> cargos = service.listarTodos()
                .stream()
                .map(mapper::toResponse) // O controller converte as entidades e retorna uma lista JSON.
                .toList();

        return ResponseEntity.ok(cargos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponse> buscarPorId(@PathVariable Long id) {
        Cargo cargo = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(cargo));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CargoResponse>> buscarPorNome(@RequestParam String nome) {
        List<CargoResponse> cargos = service.buscarPorNome(nome)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(cargos);
    }

    @PostMapping
    public ResponseEntity<CargoResponse> cadastrar(@Valid @RequestBody CargoRequest request) {
        Cargo cargo = mapper.toEntity(request);
        Cargo salvo = service.salvar(cargo);

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
    public ResponseEntity<CargoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CargoRequest request
    ) {
        Cargo dados = mapper.toEntity(request);
        Cargo atualizado = service.atualizar(id, dados);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
