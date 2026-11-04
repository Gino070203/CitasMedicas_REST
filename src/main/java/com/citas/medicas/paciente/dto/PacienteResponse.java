package com.citas.medicas.paciente.dto;

import java.time.LocalDate;

public record PacienteResponse(
		Long idPaciente,
		Long idUsuario,
		String nombre,
		String apellido,
		String dni,
		String correo,
		String numeroHistoria,
		String sis,
		Boolean numeroHistoriaVerificado,
		Boolean sisVerificado,
		LocalDate fechaNacimiento,
		String genero,
		String direccion,
		Boolean estado) {
}
