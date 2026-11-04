package com.citas.medicas.horarios.dto;

public record HorarioResponse(
		Long idHorario,
		Long idMedico,
		String nombreMedico,
		String especialidadNombre,
		String diaSemana,
		String horaInicio,
		String horaFin,
		Boolean estado,
		int turnosPosibles,
		String fechasDisponibles) {
}
