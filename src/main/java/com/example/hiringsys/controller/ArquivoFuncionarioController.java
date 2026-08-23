package com.example.hiringsys.controller;

import com.example.hiringsys.dto.response.ArquivoFuncionarioResponse;
import com.example.hiringsys.entity.ArquivoFuncionario;
import com.example.hiringsys.enums.CategoriaArquivo;
import com.example.hiringsys.service.ArquivoFuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/funcionarios/{funcionarioId}/arquivos")
@Tag(name = "Arquivos de funcionários")
public class ArquivoFuncionarioController {

    private final ArquivoFuncionarioService service;

    public ArquivoFuncionarioController(ArquivoFuncionarioService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Envia um arquivo ao bucket privado e registra seus metadados")
    public ResponseEntity<ArquivoFuncionarioResponse> upload(
            @PathVariable Long funcionarioId,
            @RequestParam CategoriaArquivo categoria,
            @RequestPart("file") MultipartFile file
    ) {
        ArquivoFuncionario salvo = service.upload(funcionarioId, categoria, file);
        return ResponseEntity.status(201).body(toResponse(salvo));
    }

    @GetMapping
    @Operation(summary = "Lista os metadados dos arquivos do funcionário")
    public List<ArquivoFuncionarioResponse> listar(
            @PathVariable Long funcionarioId,
            @RequestParam(required = false) CategoriaArquivo categoria
    ) {
        return service.listar(funcionarioId, categoria).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{arquivoId}/download")
    @Operation(summary = "Baixa um arquivo do bucket privado")
    public ResponseEntity<byte[]> download(@PathVariable Long funcionarioId, @PathVariable Long arquivoId) {
        ArquivoFuncionarioService.DownloadArquivo download = service.download(funcionarioId, arquivoId);
        ArquivoFuncionario arquivo = download.arquivo();
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(arquivo.getMimeType());
        } catch (Exception ignored) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(download.conteudo().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(arquivo.getNomeArquivo(), StandardCharsets.UTF_8).build().toString())
                .body(download.conteudo());
    }

    @DeleteMapping("/{arquivoId}")
    @Operation(summary = "Exclui o objeto do bucket e seus metadados")
    public ResponseEntity<Void> excluir(@PathVariable Long funcionarioId, @PathVariable Long arquivoId) {
        service.excluir(funcionarioId, arquivoId);
        return ResponseEntity.noContent().build();
    }

    private ArquivoFuncionarioResponse toResponse(ArquivoFuncionario arquivo) {
        return new ArquivoFuncionarioResponse(
                arquivo.getId(), arquivo.getFuncionario().getId(), arquivo.getNomeArquivo(),
                arquivo.getCategoria(), arquivo.getMimeType(), arquivo.getExtensao(),
                arquivo.getTamanhoBytes(), arquivo.getCriadoEm()
        );
    }
}
