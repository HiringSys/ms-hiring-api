package com.example.hiringsys.service;

import com.example.hiringsys.entity.EmailEnvioLog;
import com.example.hiringsys.entity.GrupoFuncionario;
import com.example.hiringsys.enums.StatusEnvioEmail;
import com.example.hiringsys.enums.StatusSelecao;
import com.example.hiringsys.enums.TipoEmail;
import com.example.hiringsys.exception.EmailDeliveryException;
import com.example.hiringsys.repository.EmailEnvioLogRepository;
import com.example.hiringsys.repository.GrupoFuncionarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoAprovacaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoAprovacaoService.class);
    private static final TipoEmail TIPO_EMAIL = TipoEmail.APROVACAO_CANDIDATO;

    private final GrupoFuncionarioRepository vinculos;
    private final EmailEnvioLogRepository logs;
    private final EmailService emailService;
    private final int maxTentativas;

    public NotificacaoAprovacaoService(
            GrupoFuncionarioRepository vinculos,
            EmailEnvioLogRepository logs,
            EmailService emailService,
            @Value("${app.notifications.approval.max-attempts:3}") int maxTentativas
    ) {
        this.vinculos = vinculos;
        this.logs = logs;
        this.emailService = emailService;
        this.maxTentativas = maxTentativas;
    }

    @Transactional(readOnly = true)
    public List<Long> buscarPendentes(int tamanhoLote) {
        return vinculos.findIdsPendentesDeNotificacao(
                StatusSelecao.APROVADO,
                StatusEnvioEmail.ENVIADO,
                StatusEnvioEmail.PROCESSANDO,
                TIPO_EMAIL,
                maxTentativas,
                PageRequest.of(0, tamanhoLote)
        );
    }

    @Transactional
    public void processar(Long vinculoId) {
        GrupoFuncionario vinculo = vinculos.findByIdForUpdate(vinculoId).orElse(null);
        if (vinculo == null || vinculo.getStatusSelecao() != StatusSelecao.APROVADO) return;

        EmailEnvioLog log = logs.findByGrupoFuncionarioIdAndTipo(vinculoId, TIPO_EMAIL)
                .orElseGet(() -> novoLog(vinculo));
        if (log.getStatus() == StatusEnvioEmail.ENVIADO
                || log.getStatus() == StatusEnvioEmail.PROCESSANDO
                || log.getTentativas() >= maxTentativas) {
            return;
        }

        String destinatario = vinculo.getFuncionario().getEmail();
        log.iniciarTentativa(destinatario);
        logs.saveAndFlush(log);

        try {
            emailService.enviarAprovacao(
                    destinatario,
                    vinculo.getFuncionario().getNome(),
                    vinculo.getGrupo().getNome()
            );
            log.marcarEnviado();
            LOGGER.info("E-mail de aprovacao aceito pelo SendGrid para o vinculo {}", vinculoId);
        } catch (EmailDeliveryException exception) {
            log.marcarFalha(exception.getMessage());
            LOGGER.warn(
                    "Falha na tentativa {} de enviar aprovacao para o vinculo {}: {}",
                    log.getTentativas(),
                    vinculoId,
                    exception.getMessage()
            );
        }

        logs.save(log);
    }

    private EmailEnvioLog novoLog(GrupoFuncionario vinculo) {
        EmailEnvioLog log = new EmailEnvioLog();
        log.setGrupoFuncionarioId(vinculo.getId());
        log.setTipo(TIPO_EMAIL);
        log.setStatus(StatusEnvioEmail.FALHA);
        log.setDestinatario(vinculo.getFuncionario().getEmail());
        log.setTentativas(0);
        return log;
    }
}
