package com.example.hiringsys.service;

import com.example.hiringsys.entity.EmailEnvioLog;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.entity.Grupo;
import com.example.hiringsys.entity.GrupoFuncionario;
import com.example.hiringsys.enums.StatusEnvioEmail;
import com.example.hiringsys.enums.StatusSelecao;
import com.example.hiringsys.enums.TipoEmail;
import com.example.hiringsys.exception.EmailDeliveryException;
import com.example.hiringsys.repository.EmailEnvioLogRepository;
import com.example.hiringsys.repository.GrupoFuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoAprovacaoServiceTest {

    @Mock private GrupoFuncionarioRepository vinculos;
    @Mock private EmailEnvioLogRepository logs;
    @Mock private EmailService emailService;

    private NotificacaoAprovacaoService service;
    private GrupoFuncionario vinculo;

    @BeforeEach
    void setUp() {
        service = new NotificacaoAprovacaoService(vinculos, logs, emailService, 3);

        Funcionario funcionario = new Funcionario();
        funcionario.setId(10L);
        funcionario.setNome("Maria da Silva");
        funcionario.setEmail("maria@email.com");

        Grupo grupo = new Grupo();
        grupo.setId(20L);
        grupo.setNome("Backend Java");

        vinculo = new GrupoFuncionario();
        vinculo.setId(30L);
        vinculo.setFuncionario(funcionario);
        vinculo.setGrupo(grupo);
        vinculo.setStatusSelecao(StatusSelecao.APROVADO);
    }

    @Test
    void enviaAprovacaoERegistraSucesso() {
        when(vinculos.findByIdForUpdate(30L)).thenReturn(Optional.of(vinculo));
        when(logs.findByGrupoFuncionarioIdAndTipo(30L, TipoEmail.APROVACAO_CANDIDATO))
                .thenReturn(Optional.empty());

        service.processar(30L);

        verify(emailService).enviarAprovacao(
                "maria@email.com",
                "Maria da Silva",
                "Backend Java"
        );
        ArgumentCaptor<EmailEnvioLog> logCaptor = ArgumentCaptor.forClass(EmailEnvioLog.class);
        verify(logs).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(StatusEnvioEmail.ENVIADO);
        assertThat(logCaptor.getValue().getTentativas()).isEqualTo(1);
    }

    @Test
    void naoReenviaQuandoJaFoiEnviado() {
        EmailEnvioLog log = logExistente(StatusEnvioEmail.ENVIADO, 1);
        when(vinculos.findByIdForUpdate(30L)).thenReturn(Optional.of(vinculo));
        when(logs.findByGrupoFuncionarioIdAndTipo(30L, TipoEmail.APROVACAO_CANDIDATO))
                .thenReturn(Optional.of(log));

        service.processar(30L);

        verify(emailService, never()).enviarAprovacao(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    @Test
    void registraFalhaParaPermitirNovaTentativa() {
        when(vinculos.findByIdForUpdate(30L)).thenReturn(Optional.of(vinculo));
        when(logs.findByGrupoFuncionarioIdAndTipo(30L, TipoEmail.APROVACAO_CANDIDATO))
                .thenReturn(Optional.empty());
        doThrow(new EmailDeliveryException("SendGrid indisponivel"))
                .when(emailService)
                .enviarAprovacao("maria@email.com", "Maria da Silva", "Backend Java");

        service.processar(30L);

        ArgumentCaptor<EmailEnvioLog> logCaptor = ArgumentCaptor.forClass(EmailEnvioLog.class);
        verify(logs).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(StatusEnvioEmail.FALHA);
        assertThat(logCaptor.getValue().getTentativas()).isEqualTo(1);
        assertThat(logCaptor.getValue().getUltimoErro()).contains("SendGrid indisponivel");
    }

    private EmailEnvioLog logExistente(StatusEnvioEmail status, int tentativas) {
        EmailEnvioLog log = new EmailEnvioLog();
        log.setGrupoFuncionarioId(30L);
        log.setTipo(TipoEmail.APROVACAO_CANDIDATO);
        log.setStatus(status);
        log.setDestinatario("maria@email.com");
        log.setTentativas(tentativas);
        return log;
    }
}
