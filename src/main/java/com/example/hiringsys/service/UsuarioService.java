package com.example.hiringsys.service;

import com.example.hiringsys.entity.Usuario;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario ou senha invalidos"));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getTipo().name())
                .build();
    }
}
