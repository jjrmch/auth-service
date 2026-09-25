package com.biblioteca.auth_service.support;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class TestJwtFactory {

    public static final String SECRET = "secreto-de-pruebas-suficientemente-largo-123456";

    private TestJwtFactory() {
    }

    public static String token(String email, String rol) {
        Instant ahora = Instant.now();
        return token(email, rol, ahora, ahora.plus(1, ChronoUnit.HOURS));
    }

    public static String expirado(String email, String rol) {
        Instant expira = Instant.now().minus(1, ChronoUnit.HOURS);
        return token(email, rol, expira.minus(1, ChronoUnit.HOURS), expira);
    }

    private static String token(String email, String rol, Instant emitido, Instant expira) {
        SecretKey key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(emitido)
                .expiresAt(expira)
                .subject(email)
                .claim("rol", rol)
                .claim("nombre", "Usuario de pruebas")
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
