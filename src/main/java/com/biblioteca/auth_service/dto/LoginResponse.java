package com.biblioteca.auth_service.dto;

import com.biblioteca.auth_service.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String nombre;
    private Rol rol;
    private long expiraEnSegundos;
}
