package com.example.hiringsys.controller;

import com.example.hiringsys.dto.request.*;
import com.example.hiringsys.dto.response.*;
import com.example.hiringsys.entity.*;
import com.example.hiringsys.enums.EstadoGrupo;
import com.example.hiringsys.service.GrupoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/grupos") @Tag(name="Grupos")
public class GrupoController {
    private final GrupoService service;
    public GrupoController(GrupoService service){this.service=service;}
    @GetMapping public List<GrupoResponse> listar(@RequestParam(required=false) String nome,@RequestParam(required=false) EstadoGrupo estado){List<Grupo> grupos=nome!=null?service.buscarPorNome(nome):estado!=null?service.buscarPorEstado(estado):service.listarTodos();return grupos.stream().map(this::grupoResponse).toList();}
    @GetMapping("/{id}") public GrupoResponse buscar(@PathVariable Long id){return grupoResponse(service.buscarPorId(id));}
    @PostMapping public ResponseEntity<GrupoResponse> criar(@Valid @RequestBody GrupoRequest request){Grupo salvo=service.salvar(toEntity(request));return ResponseEntity.created(URI.create("/grupos/"+salvo.getId())).body(grupoResponse(salvo));}
    @PutMapping("/{id}") public GrupoResponse atualizar(@PathVariable Long id,@Valid @RequestBody GrupoRequest request){return grupoResponse(service.atualizar(id,toEntity(request)));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id){service.excluir(id);return ResponseEntity.noContent().build();}
    @GetMapping("/{id}/funcionarios") @Operation(summary="Lista funcionários e seus scores no grupo") public List<IntegranteGrupoResponse> listarFuncionarios(@PathVariable Long id){return service.listarFuncionarios(id).stream().map(this::vinculoResponse).toList();}
    @PostMapping("/{id}/funcionarios") @Operation(summary="Vincula um funcionário ao grupo; o score pode ser nulo") public ResponseEntity<IntegranteGrupoResponse> vincular(@PathVariable Long id,@Valid @RequestBody VinculoGrupoFuncionarioRequest request){GrupoFuncionario salvo=service.vincular(id,request.funcionarioId(),request.scoreProximidade());return ResponseEntity.status(201).body(vinculoResponse(salvo));}
    @PatchMapping("/{id}/funcionarios/{funcionarioId}/score") @Operation(summary="Atualiza ou limpa o score de proximidade") public IntegranteGrupoResponse atualizarScore(@PathVariable Long id,@PathVariable Long funcionarioId,@Valid @RequestBody ScoreProximidadeRequest request){return vinculoResponse(service.atualizarScore(id,funcionarioId,request.scoreProximidade()));}
    @DeleteMapping("/{id}/funcionarios/{funcionarioId}") public ResponseEntity<Void> desvincular(@PathVariable Long id,@PathVariable Long funcionarioId){service.desvincular(id,funcionarioId);return ResponseEntity.noContent().build();}
    private Grupo toEntity(GrupoRequest r){Grupo g=new Grupo();g.setNome(r.nome());g.setArea(r.area());g.setEstado(r.estado());g.setDisponiveis(r.disponiveis());g.setCargo(r.cargo());return g;}
    private GrupoResponse grupoResponse(Grupo g){return new GrupoResponse(g.getId(),g.getNome(),g.getArea(),g.getEstado(),g.getDisponiveis(),g.getCargo(),g.getCriadoEm());}
    private IntegranteGrupoResponse vinculoResponse(GrupoFuncionario v){return new IntegranteGrupoResponse(v.getFuncionario().getId(),v.getFuncionario().getNome(),v.getFuncionario().getEmail(),v.getScoreProximidade());}
}
