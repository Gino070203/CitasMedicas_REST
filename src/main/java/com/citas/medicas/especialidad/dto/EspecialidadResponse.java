package com.citas.medicas.especialidad.dto;

public record EspecialidadResponse(
		Long idEspecialidad,
		String nombre,
		String descripcion,
		Boolean estado) {
}
