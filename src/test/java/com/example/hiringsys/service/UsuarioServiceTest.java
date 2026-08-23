package com.example.hiringsys.service;

import com.example.hiringsys.entity.Usuario;
import com.example.hiringsys.enums.TipoUsuario;
import com.example.hiringsys.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    private UsuarioService service;
    private BCryptPasswordEncoder passwordEncoder;
    private DaoAuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(repository);
        passwordEncoder = new BCryptPasswordEncoder(4);
        authenticationProvider = new DaoAuthenticationProvider(service);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void autenticaAdminComEmailSenhaEPerfilDoBanco() {
        Usuario admin = usuario("admin@hiringsys.local", "senha-admin", TipoUsuario.ADMIN);
        when(repository.findByEmailIgnoreCase("admin@hiringsys.local")).thenReturn(Optional.of(admin));

        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken("admin@hiringsys.local", "senha-admin")
        );

        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getName()).isEqualTo("admin@hiringsys.local");
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }

    @Test
    void carregaPerfilRhDoBanco() {
        Usuario rh = usuario("rh@hiringsys.local", "senha-rh", TipoUsuario.RH);
        when(repository.findByEmailIgnoreCase("rh@hiringsys.local")).thenReturn(Optional.of(rh));

        assertThat(service.loadUserByUsername("rh@hiringsys.local").getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_RH");
    }

    @Test
    void rejeitaSenhaIncorreta() {
        Usuario rh = usuario("rh@hiringsys.local", "senha-correta", TipoUsuario.RH);
        when(repository.findByEmailIgnoreCase("rh@hiringsys.local")).thenReturn(Optional.of(rh));

        assertThatThrownBy(() -> authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken("rh@hiringsys.local", "senha-incorreta")
        )).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void rejeitaEmailNaoCadastrado() {
        when(repository.findByEmailIgnoreCase("inexistente@hiringsys.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("inexistente@hiringsys.local"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    private Usuario usuario(String email, String senha, TipoUsuario tipo) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setTipo(tipo);
        return usuario;
    }
}
