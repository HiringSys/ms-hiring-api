package com.example.hiringsys.service;

import com.example.hiringsys.exception.EmailDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    public SendGridEmailService(
            HttpClient httpClient,
            ObjectMapper objectMapper,
            @Value("${app.sendgrid.api-key:}") String apiKey,
            @Value("${app.sendgrid.from-email:}") String fromEmail,
            @Value("${app.sendgrid.from-name:HiringSys}") String fromName,
            @Value("classpath:templates/email/password-recovery.html") Resource passwordRecoveryTemplate
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.passwordRecoveryTemplate = passwordRecoveryTemplate;
    }

    @Override
    public void enviarNovaSenha(String destinatario, String novaSenha) {
        validarConfiguracao();

        try {
            String payload = montarPayload(destinatario, novaSenha);

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
        } catch (JacksonException exception) {
            throw new EmailDeliveryException("Não foi possível preparar o e-mail", exception);
        } catch (IOException exception) {
            throw new EmailDeliveryException("Não foi possível comunicar com o SendGrid", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new EmailDeliveryException("O envio do e-mail foi interrompido", exception);
        }
    }

    String montarPayload(String destinatario, String novaSenha) throws IOException {
        String html = carregarTemplate().replace("{{NOVA_SENHA}}", novaSenha);
        return objectMapper.writeValueAsString(Map.of(
                "personalizations", List.of(Map.of(
                        "to", List.of(Map.of("email", destinatario))
                )),
                "from", Map.of("email", fromEmail, "name", fromName),
                "subject", "Sua nova senha de acesso ao HiringSys",
                "content", List.of(
                        Map.of(
                                "type", "text/plain",
                                "value", "Sua nova senha de acesso ao HiringSys é: " + novaSenha
                        ),
                        Map.of("type", "text/html", "value", html)
                )
        ));
    }

    private String carregarTemplate() throws IOException {
        try (var inputStream = passwordRecoveryTemplate.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void validarConfiguracao() {
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(fromEmail)) {
            throw new EmailDeliveryException("A configuração do SendGrid está incompleta");
        }
    }
}
