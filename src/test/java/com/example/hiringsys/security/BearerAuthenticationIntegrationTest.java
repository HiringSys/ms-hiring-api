package com.example.hiringsys.security;

import com.example.hiringsys.config.SecurityConfig;
import com.example.hiringsys.controller.AuthController;
import com.example.hiringsys.service.JwtRevocationService;
import com.example.hiringsys.service.JwtService;
import com.example.hiringsys.service.RecuperacaoSenhaService;
import com.example.hiringsys.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class,
        JwtRevocationValidator.class
})
@TestPropertySource(properties = {
        "app.security.jwt.secret=01234567890123456789012345678901",
        "app.frontend.origin=http://localhost:5173"
})
class BearerAuthenticationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtEncoder jwtEncoder;

    @MockitoBean private UsuarioService usuarioService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private RecuperacaoSenhaService recuperacaoSenhaService;
    @MockitoBean private JwtRevocationService jwtRevocationService;

    @Test
    void autenticaTokenBearerAntesDeExecutarLogout() throws Exception {
        when(jwtRevocationService.estaRevogado("session-id")).thenReturn(false);

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ms-hiring-api")
                .subject("admin@hiringsys.local")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .id("session-id")
                .claim("scope", "ADMIN")
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
        )).getTokenValue();

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        verify(jwtRevocationService).revogar(any());
    }
}
