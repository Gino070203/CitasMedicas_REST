package com.citas.medicas.auth.dto;

public record UsuarioMeResponse(
		Long idUsuario,
		String correo,
		String nombre,
		String apellido,
		String rol) {
}
