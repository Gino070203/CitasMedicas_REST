package com.citas.medicas.auth.dto;

public record AuthResponse(
		String token,
		String correo,
		String rol,
		String nombre,
		String apellido,
		Long idUsuario,
		long expiraEnSegundos) {
}
