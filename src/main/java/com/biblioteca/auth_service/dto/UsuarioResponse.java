package com.biblioteca.auth_service.dto;

import com.biblioteca.auth_service.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String email;
    private String nombre;
    private Rol rol;
}
