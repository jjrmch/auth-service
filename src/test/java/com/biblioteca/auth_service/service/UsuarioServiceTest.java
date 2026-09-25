package com.biblioteca.auth_service.service;

import com.biblioteca.auth_service.dto.LoginRequest;
import com.biblioteca.auth_service.dto.LoginResponse;
import com.biblioteca.auth_service.dto.UsuarioRequest;
import com.biblioteca.auth_service.dto.UsuarioResponse;
import com.biblioteca.auth_service.exception.CredencialesInvalidasException;
import com.biblioteca.auth_service.exception.EmailYaRegistradoException;
import com.biblioteca.auth_service.model.Rol;
import com.biblioteca.auth_service.model.Usuario;
import com.biblioteca.auth_service.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequest requestRegistro() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("ana@test.com");
        request.setPassword("secreto123");
        request.setNombre("Ana");
        return request;
    }

    private LoginRequest requestLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ana@test.com");
        request.setPassword("secreto123");
        return request;
    }

    @Test
    void registrarCreaUnClienteConLaPasswordHasheada() {
        when(usuarioRepository.existsByEmail("ana@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secreto123")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse response = usuarioService.registrar(requestRegistro());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();
        assertEquals("ana@test.com", guardado.getEmail());
        assertEquals("hash-bcrypt", guardado.getPassword());
        assertEquals(Rol.CLIENTE, guardado.getRol());
        assertTrue(guardado.getActivo());
        assertEquals(Rol.CLIENTE, response.getRol());
        assertEquals("ana@test.com", response.getEmail());
    }

    @Test
    void registrarConEmailDuplicadoLanzaConflicto() {
        when(usuarioRepository.existsByEmail("ana@test.com")).thenReturn(true);

        assertThrows(EmailYaRegistradoException.class, () -> usuarioService.registrar(requestRegistro()));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void loginValidoDevuelveTokenYRoles() {
        Usuario usuario = usuarioActivo(Rol.BIBLIOTECARIO);
        when(usuarioRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secreto123", "hash-bcrypt")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("token.jwt");
        when(jwtService.getExpiracionSegundos()).thenReturn(3600L);

        LoginResponse response = usuarioService.login(requestLogin());

        assertEquals("token.jwt", response.getToken());
        assertEquals(Rol.BIBLIOTECARIO, response.getRol());
        assertEquals("Ana", response.getNombre());
        assertEquals(3600L, response.getExpiraEnSegundos());
    }

    @Test
    void loginConEmailInexistenteLanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("ana@test.com")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.login(requestLogin()));
        verify(jwtService, never()).generarToken(any(Usuario.class));
    }

    @Test
    void loginConPasswordIncorrectaLanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(usuarioActivo(Rol.CLIENTE)));
        when(passwordEncoder.matches("secreto123", "hash-bcrypt")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.login(requestLogin()));
        verify(jwtService, never()).generarToken(any(Usuario.class));
    }

    @Test
    void loginDeUsuarioDesactivadoLanzaCredencialesInvalidas() {
        Usuario inactivo = usuarioActivo(Rol.CLIENTE);
        inactivo.setActivo(false);
        when(usuarioRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(inactivo));
        when(passwordEncoder.matches("secreto123", "hash-bcrypt")).thenReturn(true);

        assertThrows(CredencialesInvalidasException.class, () -> usuarioService.login(requestLogin()));
    }

    @Test
    void crearAdminSiNoExisteLoCreaConRolAdmin() {
        when(usuarioRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(passwordEncoder.encode("admin12345")).thenReturn("hash-admin");

        usuarioService.crearAdminSiNoExiste("admin@test.com", "admin12345");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(Rol.ADMIN, captor.getValue().getRol());
        assertEquals("hash-admin", captor.getValue().getPassword());
        assertTrue(captor.getValue().getActivo());
    }

    @Test
    void crearAdminSiNoExisteNoDuplicaSiYaEsta() {
        when(usuarioRepository.existsByEmail("admin@test.com")).thenReturn(true);

        usuarioService.crearAdminSiNoExiste("admin@test.com", "admin12345");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    private Usuario usuarioActivo(Rol rol) {
        return new Usuario(1L, "ana@test.com", "hash-bcrypt", "Ana", rol, true);
    }
}
