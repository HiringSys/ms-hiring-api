package com.example.hiringsys.controller;

import com.example.hiringsys.service.JwtService;
import com.example.hiringsys.service.JwtRevocationService;
import com.example.hiringsys.service.RecuperacaoSenhaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private DaoAuthenticationProvider databaseAuthenticationProvider;
    @Mock private JwtService jwtService;
    @Mock private RecuperacaoSenhaService recuperacaoSenhaService;
    @Mock private JwtRevocationService jwtRevocationService;

    private MockMvc mockMvc;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(
                databaseAuthenticationProvider,
                jwtService,
                recuperacaoSenhaService,
                jwtRevocationService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void solicitaRecuperacaoDeSenha() throws Exception {
        mockMvc.perform(post("/auth/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"rh@hiringsys.local\"}"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensagem").value(
                        "Se o e-mail estiver cadastrado, a nova senha será enviada."
                ));

        verify(recuperacaoSenhaService).recuperar("rh@hiringsys.local");
    }

    @Test
    void rejeitaEmailInvalido() throws Exception {
        mockMvc.perform(post("/auth/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email-invalido\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void encerraSessaoAtual() {
        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);

        ResponseEntity<Void> response = controller.logout(jwt);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(jwtRevocationService).revogar(jwt);
    }
}
