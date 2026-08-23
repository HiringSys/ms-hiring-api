package com.example.hiringsys.controller;

import com.example.hiringsys.dto.request.LoginRequest;
import com.example.hiringsys.dto.request.RecuperacaoSenhaRequest;
import com.example.hiringsys.dto.response.LoginResponse;
import com.example.hiringsys.dto.response.RecuperacaoSenhaResponse;
import com.example.hiringsys.service.JwtService;
import com.example.hiringsys.service.RecuperacaoSenhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RecuperacaoSenhaService recuperacaoSenhaService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String perfil = authentication.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_"))
                .findFirst()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse("");

        LoginResponse response = new LoginResponse(
                jwtService.gerarToken(authentication),
                "Bearer",
                jwtService.getExpirationSeconds(),
                perfil
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-recovery")
    @SecurityRequirements
    @Operation(
            summary = "Recupera a senha do usuário",
            description = "Gera uma nova senha e a envia ao e-mail cadastrado, quando a conta existir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Solicitação processada"),
            @ApiResponse(responseCode = "400", description = "E-mail inválido"),
            @ApiResponse(responseCode = "502", description = "Falha no serviço de e-mail")
    })
    public ResponseEntity<RecuperacaoSenhaResponse> recuperarSenha(
            @Valid @RequestBody RecuperacaoSenhaRequest request
    ) {
        recuperacaoSenhaService.recuperar(request.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                new RecuperacaoSenhaResponse(
                        "Se o e-mail estiver cadastrado, a nova senha será enviada."
                )
        );
    }
}
