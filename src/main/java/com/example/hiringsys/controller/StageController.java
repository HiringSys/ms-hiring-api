package com.example.hiringsys.controller;

import com.example.hiringsys.dto.request.StageCandidateRequest;
import com.example.hiringsys.dto.request.StageSelectionRequest;
import com.example.hiringsys.dto.request.StageUpdateRequest;
import com.example.hiringsys.dto.response.StageCandidateResponse;
import com.example.hiringsys.dto.response.StageResponse;
import com.example.hiringsys.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stages")
@Tag(name = "Peneiras", description = "Contrato consumido pelas telas Vue de processos seletivos")
public class StageController {

    private final StageService service;

    public StageController(StageService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista as peneiras", description = "Inclui a quantidade calculada de participantes de cada peneira.")
    public List<StageResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{stageId}")
    @Operation(summary = "Busca os dados de uma peneira")
    @ApiResponse(responseCode = "404", description = "Peneira nao encontrada")
    public StageResponse buscar(@Parameter(description = "ID da peneira") @PathVariable Long stageId) {
        return service.buscar(stageId);
    }

    @PutMapping("/{stageId}")
    @Operation(summary = "Edita os campos do formulario de peneira")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Peneira atualizada"),
            @ApiResponse(responseCode = "400", description = "Campos invalidos"),
            @ApiResponse(responseCode = "404", description = "Peneira nao encontrada"),
            @ApiResponse(responseCode = "409", description = "Nome duplicado ou limite abaixo dos aprovados")
    })
    public StageResponse atualizar(
            @PathVariable Long stageId,
            @Valid @RequestBody StageUpdateRequest request
    ) {
        return service.atualizar(stageId, request);
    }

    @DeleteMapping("/{stageId}")
    @Operation(summary = "Exclui uma peneira sem excluir os cadastros globais dos candidatos")
    public ResponseEntity<Void> excluir(@PathVariable Long stageId) {
        service.excluir(stageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{stageId}/candidates")
    @Operation(
            summary = "Lista os candidatos da peneira",
            description = "A ordem dos aprovados e preservada. jobAffinity nulo significa score ainda nao calculado."
    )
    public List<StageCandidateResponse> listarCandidatos(@PathVariable Long stageId) {
        return service.listarCandidatos(stageId);
    }

    @PostMapping("/{stageId}/candidates")
    @Operation(summary = "Cadastra e adiciona um candidato a peneira")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Candidato adicionado"),
            @ApiResponse(responseCode = "400", description = "Campos invalidos"),
            @ApiResponse(responseCode = "409", description = "E-mail ja cadastrado")
    })
    public ResponseEntity<StageCandidateResponse> adicionarCandidato(
            @PathVariable Long stageId,
            @Valid @RequestBody StageCandidateRequest request
    ) {
        StageCandidateResponse response = service.adicionarCandidato(stageId, request);
        return ResponseEntity.created(URI.create(
                "/stages/" + stageId + "/candidates/" + response.id())).body(response);
    }

    @PutMapping("/{stageId}/candidates/{candidateId}")
    @Operation(summary = "Edita os campos do formulario de candidato")
    public StageCandidateResponse atualizarCandidato(
            @PathVariable Long stageId,
            @PathVariable Long candidateId,
            @Valid @RequestBody StageCandidateRequest request
    ) {
        return service.atualizarCandidato(stageId, candidateId, request);
    }

    @DeleteMapping("/{stageId}/candidates/{candidateId}")
    @Operation(summary = "Remove o candidato da peneira sem excluir seu cadastro global")
    public ResponseEntity<Void> removerCandidato(
            @PathVariable Long stageId,
            @PathVariable Long candidateId
    ) {
        service.removerCandidato(stageId, candidateId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{stageId}/candidates/selection")
    @Operation(
            summary = "Salva aprovacao, reprovacao e ordem dos aprovados",
            description = "Os IDs aparecem na ordem exibida. Candidatos omitidos sao marcados como reprovados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Selecao persistida"),
            @ApiResponse(responseCode = "409", description = "Limite excedido, ID duplicado ou candidato de outra peneira")
    })
    public List<StageCandidateResponse> atualizarSelecao(
            @PathVariable Long stageId,
            @Valid @RequestBody StageSelectionRequest request
    ) {
        return service.atualizarSelecao(stageId, request.approvedCandidateIds());
    }
}
