package com.citas.medicas.medico.dto;

import java.util.List;

import com.citas.medicas.cita.dto.CitaResponse;

public record MedicoDashboardResponse(
		Long idMedico,
		String nombre,
		String apellido,
		String especialidad,
		List<CitaResponse> agendaDeHoy,
		List<CitaResponse> porAtender,
		List<CitaResponse> historial) {
}
