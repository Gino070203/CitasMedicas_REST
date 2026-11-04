package com.citas.medicas.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final SecretKey clave;
	private final long expiracionMs;

	public JwtTokenProvider(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms:7200000}") long expiracionMs) {
		this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expiracionMs = expiracionMs;
	}

	public String generarToken(String correo, Long userId, String rol) {
		Date ahora = new Date();
		return Jwts.builder()
				.subject(correo)
				.claim("userId", userId)
				.claim("role", rol)
				.issuedAt(ahora)
				.expiration(new Date(ahora.getTime() + expiracionMs))
				.signWith(clave)
				.compact();
	}

	public Claims validarToken(String token) {
		return Jwts.parser()
				.verifyWith(clave)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public long expiracionSegundos() {
		return expiracionMs / 1000;
	}
}
