package com.biblioteca.auth_service.controller;

import com.biblioteca.auth_service.dto.LoginRequest;
import com.biblioteca.auth_service.dto.LoginResponse;
import com.biblioteca.auth_service.dto.UsuarioRequest;
import com.biblioteca.auth_service.dto.UsuarioResponse;
import com.biblioteca.auth_service.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse creado = usuarioService.registrar(request);
        return ResponseEntity.status(201).body(creado);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return usuarioService.login(request);
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        return usuarioService.buscarPorEmail(authentication.getName());
    }
}
