package com.example.hiringsys.service;

import com.example.hiringsys.exception.EmailDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class SendGridEmailService implements EmailService {

    private static final URI SENDGRID_MAIL_SEND_URI =
            URI.create("https://api.sendgrid.com/v3/mail/send");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String fromEmail;
    private final String fromName;
    private final Resource passwordRecoveryTemplate;
    private final Resource candidateApprovedTemplate;

    public SendGridEmailService(
            HttpClient httpClient,
            ObjectMapper objectMapper,
            @Value("${app.sendgrid.api-key:}") String apiKey,
            @Value("${app.sendgrid.from-email:}") String fromEmail,
            @Value("${app.sendgrid.from-name:HiringSys}") String fromName,
            @Value("classpath:templates/email/password-recovery.html") Resource passwordRecoveryTemplate,
            @Value("classpath:templates/email/candidate-approved.html") Resource candidateApprovedTemplate
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.passwordRecoveryTemplate = passwordRecoveryTemplate;
        this.candidateApprovedTemplate = candidateApprovedTemplate;
    }

    @Override
    public void enviarNovaSenha(String destinatario, String novaSenha) {
        validarConfiguracao();

        try {
            String html = carregarTemplate(passwordRecoveryTemplate)
                    .replace("{{NOVA_SENHA}}", HtmlUtils.htmlEscape(novaSenha));
            enviar(
                    destinatario,
                    "Sua nova senha de acesso ao HiringSys",
                    "Sua nova senha de acesso ao HiringSys é: " + novaSenha,
                    html
            );
        } catch (IOException exception) {
            throw new EmailDeliveryException("Não foi possível carregar o modelo do e-mail", exception);
        }
    }

    @Override
    public void enviarAprovacao(String destinatario, String nomeCandidato, String nomeProcesso) {
        validarConfiguracao();

        try {
            enviarPayload(montarPayloadAprovacao(destinatario, nomeCandidato, nomeProcesso));
        } catch (IOException exception) {
            throw new EmailDeliveryException("Não foi possível carregar o modelo do e-mail", exception);
        }
    }

    String montarPayload(String destinatario, String novaSenha) throws IOException {
        String html = carregarTemplate(passwordRecoveryTemplate)
                .replace("{{NOVA_SENHA}}", HtmlUtils.htmlEscape(novaSenha));
        return montarPayload(
                destinatario,
                "Sua nova senha de acesso ao HiringSys",
                "Sua nova senha de acesso ao HiringSys é: " + novaSenha,
                html
        );
    }

    String montarPayloadAprovacao(
            String destinatario,
            String nomeCandidato,
            String nomeProcesso
    ) throws IOException {
        String html = carregarTemplate(candidateApprovedTemplate)
                .replace("{{NOME_CANDIDATO}}", HtmlUtils.htmlEscape(nomeCandidato))
                .replace("{{NOME_PROCESSO}}", HtmlUtils.htmlEscape(nomeProcesso));
        return montarPayload(
                destinatario,
                "Aprovação no processo seletivo " + nomeProcesso,
                "Olá, " + nomeCandidato + "! Você foi aprovado(a) no processo seletivo "
                        + nomeProcesso + ". A equipe de RH entrará em contato com os próximos passos.",
                html
        );
    }

    private void enviar(String destinatario, String assunto, String texto, String html) {
        try {
            enviarPayload(montarPayload(destinatario, assunto, texto, html));
        } catch (JacksonException exception) {
            throw new EmailDeliveryException("Não foi possível preparar o e-mail", exception);
        }
    }

    private void enviarPayload(String payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder(SENDGRID_MAIL_SEND_URI)
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new EmailDeliveryException(
                        "O SendGrid recusou o envio do e-mail (status " + response.statusCode() + ")"
                );
            }
        } catch (IOException exception) {
            throw new EmailDeliveryException("Não foi possível comunicar com o SendGrid", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new EmailDeliveryException("O envio do e-mail foi interrompido", exception);
        }
    }

    private String montarPayload(
            String destinatario,
            String assunto,
            String texto,
            String html
    ) throws JacksonException {
        return objectMapper.writeValueAsString(Map.of(
                "personalizations", List.of(Map.of(
                        "to", List.of(Map.of("email", destinatario))
                )),
                "from", Map.of("email", fromEmail, "name", fromName),
                "subject", assunto,
                "content", List.of(
                        Map.of("type", "text/plain", "value", texto),
                        Map.of("type", "text/html", "value", html)
                )
        ));
    }

    private String carregarTemplate(Resource template) throws IOException {
        try (var inputStream = template.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void validarConfiguracao() {
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(fromEmail)) {
            throw new EmailDeliveryException("A configuração do SendGrid está incompleta");
        }
    }
}
