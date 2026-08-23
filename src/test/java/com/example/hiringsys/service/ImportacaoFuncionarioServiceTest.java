package com.example.hiringsys.service;

import com.example.hiringsys.dto.request.FuncionarioImportacaoRequest;
import com.example.hiringsys.dto.request.ImportacaoFuncionariosRequest;
import com.example.hiringsys.dto.response.ImportacaoFuncionariosResponse;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.GrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportacaoFuncionarioServiceTest {

    private static final String CALL_IMPORTACAO =
            "CALL importar_funcionarios_em_lote(?, CAST(? AS jsonb))";

    @Mock private GrupoRepository grupos;
    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private ObjectMapper objectMapper;

    private ImportacaoFuncionarioService service;

    @BeforeEach
    void setUp() {
        service = new ImportacaoFuncionarioService(grupos, jdbcTemplate, objectMapper);
    }

    @Test
    void importaLotePorProcedure() throws Exception {
        ImportacaoFuncionariosRequest request = request();
        String json = "[{\"nome\":\"Ana Beatriz Costa\"}]";
        when(grupos.existsById(1L)).thenReturn(true);
        when(objectMapper.writeValueAsString(request.funcionarios())).thenReturn(json);

        ImportacaoFuncionariosResponse response = service.importar(1L, request);

        assertThat(response.grupoId()).isEqualTo(1L);
        assertThat(response.totalRecebidos()).isEqualTo(1);
        verify(jdbcTemplate).update(CALL_IMPORTACAO, 1L, json);
    }

    @Test
    void rejeitaImportacaoQuandoGrupoNaoExiste() {
        ImportacaoFuncionariosRequest request = request();
        when(grupos.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.importar(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(jdbcTemplate, never()).update(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void converteErroDaProcedureEmRegraDeNegocio() throws Exception {
        ImportacaoFuncionariosRequest request = request();
        String json = "[{\"nome\":\"Ana Beatriz Costa\"}]";
        when(grupos.existsById(1L)).thenReturn(true);
        when(objectMapper.writeValueAsString(request.funcionarios())).thenReturn(json);
        when(jdbcTemplate.update(CALL_IMPORTACAO, 1L, json)).thenThrow(
                new DataIntegrityViolationException(
                        "erro",
                        new IllegalStateException("ERROR: O JSON possui e-mails duplicados\nDETAIL: lote invalido")
                )
        );

        assertThatThrownBy(() -> service.importar(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("O JSON possui e-mails duplicados");
    }

    private ImportacaoFuncionariosRequest request() {
        FuncionarioImportacaoRequest funcionario = new FuncionarioImportacaoRequest(
                "Ana Beatriz Costa",
                "ana.costa@hiringsys.local",
                "11981110001",
                new BigDecimal("5200.00"),
                "Sao Paulo",
                StatusFuncionario.EM_ANALISE,
                ExperienciaFuncionario.JUNIOR,
                1,
                List.of("Desenvolvedor Backend")
        );
        return new ImportacaoFuncionariosRequest(List.of(funcionario));
    }
}
