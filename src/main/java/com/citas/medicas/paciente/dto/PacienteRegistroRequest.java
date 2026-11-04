package com.citas.medicas.paciente.dto;

import java.time.LocalDate;

public record PacienteRegistroRequest(
		Long idUsuario,
		String numeroHistoria,
		String sis,
		LocalDate fechaNacimiento,
		String genero,
		String direccion) {
}
