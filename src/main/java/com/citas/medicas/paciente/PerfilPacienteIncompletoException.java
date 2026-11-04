package com.citas.medicas.paciente;

public class PerfilPacienteIncompletoException extends IllegalArgumentException {

	public PerfilPacienteIncompletoException() {
		super("Completa tu nombre, DNI, fecha de nacimiento y género antes de reservar.");
	}
}
