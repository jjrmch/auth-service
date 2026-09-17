package com.biblioteca.auth_service.service;

import com.biblioteca.auth_service.dto.UsuarioRequest;
import com.biblioteca.auth_service.dto.UsuarioResponse;
import com.biblioteca.auth_service.exception.EmailYaRegistradoException;
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

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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

    private UsuarioResponse aResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getEmail(),
                usuario.getNombre(), usuario.getRol());
    }
}
