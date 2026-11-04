package com.citas.medicas.paciente.dto;

import java.time.LocalDate;

public record PacienteActualizacionRequest(
		String numeroHistoria,
		String sis,
		LocalDate fechaNacimiento,
		String genero,
		String direccion,
		boolean numeroHistoriaVerificado,
		boolean sisVerificado) {
}
