package com.citas.medicas.medico.dto;

public record MedicoResponse(
		Long idMedico,
		Long idUsuario,
		String nombre,
		String apellido,
		String cmp,
		Long idEspecialidad,
		String especialidadNombre,
		Boolean estado) {
}
