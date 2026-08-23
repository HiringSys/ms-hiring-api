package com.example.hiringsys.service;

import com.example.hiringsys.entity.Usuario;
import com.example.hiringsys.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceTest {

    @Mock private UsuarioRepository usuarios;
    @Mock private TemporaryPasswordGenerator passwordGenerator;
    @Mock private EmailService emailService;

    private RecuperacaoSenhaService service;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(4);
        service = new RecuperacaoSenhaService(
                usuarios,
                passwordEncoder,
                passwordGenerator,
                emailService
        );
    }

    @Test
    void trocaSenhaCriptografadaEEnviaSenhaNova() {
        Usuario usuario = new Usuario();
        usuario.setEmail("rh@hiringsys.local");
        usuario.setSenha("hash-anterior");
        when(usuarios.findByEmailIgnoreCase("rh@hiringsys.local")).thenReturn(Optional.of(usuario));
        when(passwordGenerator.gerar()).thenReturn("NovaSenha#2026");

        service.recuperar(" rh@hiringsys.local ");

        assertThat(usuario.getSenha()).isNotEqualTo("NovaSenha#2026");
        assertThat(passwordEncoder.matches("NovaSenha#2026", usuario.getSenha())).isTrue();
        InOrder ordem = inOrder(usuarios, emailService);
        ordem.verify(usuarios).saveAndFlush(usuario);
        ordem.verify(emailService).enviarNovaSenha("rh@hiringsys.local", "NovaSenha#2026");
    }

    @Test
    void naoRevelaNemEnviaQuandoEmailNaoExiste() {
        when(usuarios.findByEmailIgnoreCase("inexistente@hiringsys.local"))
                .thenReturn(Optional.empty());

        service.recuperar("inexistente@hiringsys.local");

        verify(passwordGenerator, never()).gerar();
        verify(usuarios, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
        verify(emailService, never()).enviarNovaSenha(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }
}
