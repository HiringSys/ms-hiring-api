package com.example.hiringsys.service;

import com.example.hiringsys.entity.Usuario;
import com.example.hiringsys.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RecuperacaoSenhaService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator passwordGenerator;
    private final EmailService emailService;

    public RecuperacaoSenhaService(
            UsuarioRepository usuarios,
            PasswordEncoder passwordEncoder,
            TemporaryPasswordGenerator passwordGenerator,
            EmailService emailService
    ) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
        this.emailService = emailService;
    }

    @Transactional
    public void recuperar(String email) {
        Optional<Usuario> usuarioEncontrado = usuarios.findByEmailIgnoreCase(email.trim());
        if (usuarioEncontrado.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioEncontrado.get();
        String novaSenha = passwordGenerator.gerar();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarios.saveAndFlush(usuario);
        emailService.enviarNovaSenha(usuario.getEmail(), novaSenha);
    }
}
