package com.citas.medicas.medico.dto;

public record MedicoRequest(
		Long idUsuario,
		Long idEspecialidad,
		String cmp) {
}
