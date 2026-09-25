package com.biblioteca.auth_service.service;

import com.biblioteca.auth_service.dto.LoginRequest;
import com.biblioteca.auth_service.dto.LoginResponse;
import com.biblioteca.auth_service.dto.UsuarioRequest;
import com.biblioteca.auth_service.dto.UsuarioResponse;
import com.biblioteca.auth_service.exception.CredencialesInvalidasException;
import com.biblioteca.auth_service.exception.EmailYaRegistradoException;
import com.biblioteca.auth_service.exception.RecursoNoEncontradoException;
import com.biblioteca.auth_service.model.Rol;
import com.biblioteca.auth_service.model.Usuario;
import com.biblioteca.auth_service.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse registrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailYaRegistradoException("Ya existe un usuario con el email: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);

        return aResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new CredencialesInvalidasException("El usuario está desactivado");
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, usuario.getNombre(), usuario.getRol(), jwtService.getExpiracionSegundos());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
        return aResponse(usuario);
    }

    @Transactional
    public void crearAdminSiNoExiste(String email, String password) {
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setNombre("Administrador");
        admin.setRol(Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);
    }

    private UsuarioResponse aResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getEmail(),
                usuario.getNombre(), usuario.getRol());
    }
}
