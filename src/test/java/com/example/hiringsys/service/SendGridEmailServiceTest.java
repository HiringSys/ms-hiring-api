package com.example.hiringsys.service;

import com.example.hiringsys.exception.EmailDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

    @Mock private HttpClient httpClient;
    @Mock private HttpResponse<String> httpResponse;

    private SendGridEmailService service;

    @BeforeEach
    void setUp() {
        service = new SendGridEmailService(
                httpClient,
                new ObjectMapper(),
                "SG.chave-de-teste",
                "contato@hiringsys.local",
                "HiringSys",
                new ByteArrayResource(
                        "<html><strong>{{NOVA_SENHA}}</strong></html>"
                                .getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    @Test
    void enviaEmailPelaApiDoSendGrid() throws Exception {
        when(httpResponse.statusCode()).thenReturn(202);
        when(httpClient.send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(httpResponse);

        service.enviarNovaSenha("rh@hiringsys.local", "NovaSenha#2026");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(
                requestCaptor.capture(),
                org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        );
        HttpRequest request = requestCaptor.getValue();
        assertThat(request.uri().toString()).isEqualTo("https://api.sendgrid.com/v3/mail/send");
        assertThat(request.method()).isEqualTo("POST");
        assertThat(request.headers().firstValue("Authorization"))
                .contains("Bearer SG.chave-de-teste");

        String payload = service.montarPayload("rh@hiringsys.local", "NovaSenha#2026");
        assertThat(payload)
                .contains("rh@hiringsys.local")
                .contains("NovaSenha#2026")
                .contains("text/plain")
                .contains("text/html")
                .doesNotContain("{{NOVA_SENHA}}");
    }

    @Test
    void rejeitaRespostaDeErroDoSendGrid() throws Exception {
        when(httpResponse.statusCode()).thenReturn(401);
        when(httpClient.send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(httpResponse);

        assertThatThrownBy(() -> service.enviarNovaSenha(
                "rh@hiringsys.local",
                "NovaSenha#2026"
        )).isInstanceOf(EmailDeliveryException.class)
                .hasMessageContaining("401");
    }

    @Test
    void rejeitaConfiguracaoSemApiKey() throws Exception {
        SendGridEmailService semApiKey = new SendGridEmailService(
                httpClient,
                new ObjectMapper(),
                "",
                "contato@hiringsys.local",
                "HiringSys",
                new ByteArrayResource("template".getBytes(StandardCharsets.UTF_8))
        );

        assertThatThrownBy(() -> semApiKey.enviarNovaSenha(
                "rh@hiringsys.local",
                "NovaSenha#2026"
        )).isInstanceOf(EmailDeliveryException.class)
                .hasMessageContaining("configuração");
        verify(httpClient, never()).send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        );
    }
}
