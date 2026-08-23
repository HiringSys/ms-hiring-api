package com.example.hiringsys.service;

import com.example.hiringsys.entity.ArquivoFuncionario;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.enums.CategoriaArquivo;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.ArquivoFuncionarioRepository;
import com.example.hiringsys.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ArquivoFuncionarioService {

    private static final Set<String> MIME_TYPES_PERMITIDOS = Set.of(
            "application/pdf", "image/png", "image/jpeg",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final ArquivoFuncionarioRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final StorageService storage;
    private final String bucket;
    private final long tamanhoMaximo;

    public ArquivoFuncionarioService(
            ArquivoFuncionarioRepository repository,
            FuncionarioRepository funcionarioRepository,
            StorageService storage,
            @Value("${app.supabase.bucket}") String bucket,
            @Value("${app.files.max-size-bytes:10485760}") long tamanhoMaximo
    ) {
        this.repository = repository;
        this.funcionarioRepository = funcionarioRepository;
        this.storage = storage;
        this.bucket = bucket;
        this.tamanhoMaximo = tamanhoMaximo;
    }

    @Transactional
    public ArquivoFuncionario upload(Long funcionarioId, CategoriaArquivo categoria, MultipartFile file) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado: " + funcionarioId));
        validar(file);

        String nome = limparNome(file.getOriginalFilename());
        String extensao = extrairExtensao(nome);
        String path = "funcionarios/%d/%s/%s-%s".formatted(
                funcionarioId,
                categoria.name().toLowerCase(Locale.ROOT),
                UUID.randomUUID(),
                nome
        );

        byte[] conteudo;
        try {
            conteudo = file.getBytes();
        } catch (IOException exception) {
            throw new BusinessRuleException("Não foi possível ler o arquivo enviado");
        }

        storage.upload(path, conteudo, file.getContentType());
        try {
            ArquivoFuncionario arquivo = new ArquivoFuncionario();
            arquivo.setFuncionario(funcionario);
            arquivo.setNomeArquivo(nome);
            arquivo.setCategoria(categoria);
            arquivo.setMimeType(file.getContentType());
            arquivo.setExtensao(extensao);
            arquivo.setTamanhoBytes(file.getSize());
            arquivo.setBucket(bucket);
            arquivo.setStoragePath(path);
            return repository.save(arquivo);
        } catch (RuntimeException exception) {
            try {
                storage.delete(path);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ArquivoFuncionario> listar(Long funcionarioId, CategoriaArquivo categoria) {
        if (!funcionarioRepository.existsById(funcionarioId)) {
            throw new ResourceNotFoundException("Funcionário não encontrado: " + funcionarioId);
        }
        return categoria == null
                ? repository.findByFuncionarioIdOrderByCriadoEmDesc(funcionarioId)
                : repository.findByFuncionarioIdAndCategoriaOrderByCriadoEmDesc(funcionarioId, categoria);
    }

    @Transactional(readOnly = true)
    public DownloadArquivo download(Long funcionarioId, Long arquivoId) {
        ArquivoFuncionario arquivo = buscarDoFuncionario(funcionarioId, arquivoId);
        return new DownloadArquivo(arquivo, storage.download(arquivo.getStoragePath()));
    }

    @Transactional
    public void excluir(Long funcionarioId, Long arquivoId) {
        ArquivoFuncionario arquivo = buscarDoFuncionario(funcionarioId, arquivoId);
        storage.delete(arquivo.getStoragePath());
        repository.delete(arquivo);
    }

    @Transactional
    public void excluirTodosDoFuncionario(Long funcionarioId) {
        for (ArquivoFuncionario arquivo : repository.findByFuncionarioIdOrderByCriadoEmDesc(funcionarioId)) {
            storage.delete(arquivo.getStoragePath());
            repository.delete(arquivo);
        }
    }

    private ArquivoFuncionario buscarDoFuncionario(Long funcionarioId, Long arquivoId) {
        return repository.findByIdAndFuncionarioId(arquivoId, funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado para este funcionário"));
    }

    private void validar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("O arquivo é obrigatório");
        }
        if (file.getSize() > tamanhoMaximo) {
            throw new BusinessRuleException("O arquivo excede o limite de " + tamanhoMaximo + " bytes");
        }
        if (file.getContentType() == null || !MIME_TYPES_PERMITIDOS.contains(file.getContentType())) {
            throw new BusinessRuleException("Tipo de arquivo não permitido");
        }
    }

    private String limparNome(String original) {
        String nome = original == null ? "arquivo" : original.replace('\\', '/');
        nome = nome.substring(nome.lastIndexOf('/') + 1).replaceAll("[^a-zA-Z0-9._-]", "_");
        return nome.isBlank() ? "arquivo" : nome;
    }

    private String extrairExtensao(String nome) {
        int ponto = nome.lastIndexOf('.');
        return ponto < 0 ? null : nome.substring(ponto + 1).toLowerCase(Locale.ROOT);
    }

    public record DownloadArquivo(ArquivoFuncionario arquivo, byte[] conteudo) {}
}
