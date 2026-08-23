package com.example.hiringsys.dto.response;
import com.example.hiringsys.enums.CategoriaArquivo;
import java.time.LocalDateTime;
public record ArquivoFuncionarioResponse(Long id, Long funcionarioId, String nomeArquivo, CategoriaArquivo categoria, String mimeType, String extensao, Long tamanhoBytes, LocalDateTime criadoEm) {}
