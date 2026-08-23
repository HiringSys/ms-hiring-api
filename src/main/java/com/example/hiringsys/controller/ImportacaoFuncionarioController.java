package com.example.hiringsys.controller;

import com.example.hiringsys.dto.request.ImportacaoFuncionariosRequest;
import com.example.hiringsys.dto.response.ImportacaoFuncionariosResponse;
import com.example.hiringsys.service.ImportacaoFuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grupos/{grupoId}/funcionarios/importacao")
@Tag(name = "Importacao de funcionarios")
public class ImportacaoFuncionarioController {

    private final ImportacaoFuncionarioService service;

    public ImportacaoFuncionarioController(ImportacaoFuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Importa funcionarios extraidos do Excel",
            description = "Cria os funcionarios, seus cargos e os vincula ao grupo em uma unica transacao."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lote importado"),
            @ApiResponse(responseCode = "400", description = "JSON ou campos invalidos"),
            @ApiResponse(responseCode = "404", description = "Grupo nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Regra do lote ou limite de aprovados violado")
    })
    public ResponseEntity<ImportacaoFuncionariosResponse> importar(
            @Parameter(description = "ID do grupo que recebera os funcionarios")
            @PathVariable Long grupoId,
            @Valid @RequestBody ImportacaoFuncionariosRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.importar(grupoId, request));
    }
}
