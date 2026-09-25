package com.biblioteca.auth_service.service;

import com.biblioteca.auth_service.config.JwtConfig;
import com.biblioteca.auth_service.model.Rol;
import com.biblioteca.auth_service.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "secreto-de-pruebas-suficientemente-largo-123456";

    private final JwtConfig jwtConfig = new JwtConfig();
    private final SecretKey key = jwtConfig.jwtSecretKey(SECRET);
    private final JwtEncoder encoder = jwtConfig.jwtEncoder(key);
    private final JwtDecoder decoder = jwtConfig.jwtDecoder(key);
    private final JwtService jwtService = new JwtService(encoder, 60);

    private Usuario usuario() {
        return new Usuario(1L, "ana@test.com", "hash", "Ana", Rol.BIBLIOTECARIO, true);
    }

    @Test
    void generaUnTokenConLosClaimsDelUsuario() {
        Jwt jwt = decoder.decode(jwtService.generarToken(usuario()));

        assertEquals("ana@test.com", jwt.getSubject());
        assertEquals("BIBLIOTECARIO", jwt.getClaimAsString("rol"));
        assertEquals("Ana", jwt.getClaimAsString("nombre"));
        assertEquals("auth-service", jwt.getClaimAsString("iss"));
        assertTrue(jwt.getExpiresAt().isAfter(jwt.getIssuedAt()));
    }

    @Test
    void exponeLaExpiracionEnSegundos() {
        assertEquals(3600, jwtService.getExpiracionSegundos());
    }

    @Test
    void unTokenFirmadoConOtroSecretoNoValida() {
        String token = jwtService.generarToken(usuario());
        SecretKey otraClave = jwtConfig.jwtSecretKey("otro-secreto-distinto-pero-igual-de-largo-1234");
        JwtDecoder otroDecoder = jwtConfig.jwtDecoder(otraClave);

        assertThrows(JwtException.class, () -> otroDecoder.decode(token));
    }

    @Test
    void unSecretoDemasiadoCortoNoEsValido() {
        assertThrows(IllegalStateException.class, () -> jwtConfig.jwtSecretKey("corto"));
    }
}
