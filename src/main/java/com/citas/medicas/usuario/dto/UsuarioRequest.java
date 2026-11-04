package com.citas.medicas.usuario.dto;

public record UsuarioRequest(
		String nombre,
		String apellido,
		String dni,
		String correo,
		String telefono,
		String direccion,
		String password,
		String rol) {
}
