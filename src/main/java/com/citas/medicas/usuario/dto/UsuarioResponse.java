package com.citas.medicas.usuario.dto;

public record UsuarioResponse(
		Long idUsuario,
		String nombre,
		String apellido,
		String dni,
		String correo,
		String telefono,
		String direccion,
		Boolean estado,
		String rolNombre) {
}
