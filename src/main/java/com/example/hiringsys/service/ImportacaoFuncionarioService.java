package com.example.hiringsys.service;

import com.example.hiringsys.dto.request.ImportacaoFuncionariosRequest;
import com.example.hiringsys.dto.response.ImportacaoFuncionariosResponse;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.GrupoRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class ImportacaoFuncionarioService {

    private static final String CALL_IMPORTACAO =
            "CALL importar_funcionarios_em_lote(?, CAST(? AS jsonb))";

    private final GrupoRepository grupos;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ImportacaoFuncionarioService(
            GrupoRepository grupos,
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper
    ) {
        this.grupos = grupos;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ImportacaoFuncionariosResponse importar(
            Long grupoId,
            ImportacaoFuncionariosRequest request
    ) {
        if (!grupos.existsById(grupoId)) {
            throw new ResourceNotFoundException("Grupo nao encontrado: " + grupoId);
        }

        String funcionariosJson;
        try {
            funcionariosJson = objectMapper.writeValueAsString(request.funcionarios());
        } catch (JacksonException exception) {
            throw new BusinessRuleException("Nao foi possivel preparar os dados da importacao");
        }

        try {
            jdbcTemplate.update(CALL_IMPORTACAO, grupoId, funcionariosJson);
        } catch (DataAccessException exception) {
            Throwable cause = exception.getMostSpecificCause();
            String message = cause == null ? exception.getMessage() : cause.getMessage();
            throw new BusinessRuleException(limparMensagemBanco(message));
        }

        return new ImportacaoFuncionariosResponse(
                grupoId,
                request.funcionarios().size(),
                "Funcionarios importados e vinculados ao grupo"
        );
    }

    private String limparMensagemBanco(String message) {
        if (message == null || message.isBlank()) {
            return "A importacao em lote nao pode ser concluida";
        }
        int detailIndex = message.indexOf("\n");
        String firstLine = detailIndex >= 0 ? message.substring(0, detailIndex) : message;
        return firstLine.replaceFirst("^ERROR:\\s*", "").trim();
    }
}
