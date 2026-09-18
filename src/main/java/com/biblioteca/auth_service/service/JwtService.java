package com.biblioteca.auth_service.service;

import com.biblioteca.auth_service.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {

    private static final String ISSUER = "auth-service";

    private final JwtEncoder jwtEncoder;
    private final Duration expiracion;

    public JwtService(JwtEncoder jwtEncoder, @Value("${jwt.expiracion-minutos}") long expiracionMinutos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracion = Duration.ofMinutes(expiracionMinutos);
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expiracion))
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .claim("nombre", usuario.getNombre())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpiracionSegundos() {
        return expiracion.toSeconds();
    }
}
