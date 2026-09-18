package com.biblioteca.auth_service.config;

import com.biblioteca.auth_service.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioService usuarioService;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(UsuarioService usuarioService,
                            @Value("${admin.email}") String adminEmail,
                            @Value("${admin.password}") String adminPassword) {
        this.usuarioService = usuarioService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        usuarioService.crearAdminSiNoExiste(adminEmail, adminPassword);
    }
}
