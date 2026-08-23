package com.example.hiringsys.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "app.notifications.approval.enabled",
        havingValue = "true"
)
public class NotificacaoAprovacaoScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoAprovacaoScheduler.class);

    private final NotificacaoAprovacaoService service;
    private final int tamanhoLote;

    public NotificacaoAprovacaoScheduler(
            NotificacaoAprovacaoService service,
            @Value("${app.notifications.approval.batch-size:50}") int tamanhoLote
    ) {
        this.service = service;
        this.tamanhoLote = tamanhoLote;
    }

    @Scheduled(cron = "${app.notifications.approval.cron:0 * * * * *}")
    public void enviarAprovacoesPendentes() {
        for (Long vinculoId : service.buscarPendentes(tamanhoLote)) {
            try {
                service.processar(vinculoId);
            } catch (RuntimeException exception) {
                LOGGER.error("Erro inesperado ao processar a notificacao do vinculo {}", vinculoId, exception);
            }
        }
    }
}
