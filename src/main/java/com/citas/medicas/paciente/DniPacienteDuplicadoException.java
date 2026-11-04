package com.citas.medicas.paciente;

public class DniPacienteDuplicadoException extends RuntimeException {

	public DniPacienteDuplicadoException() {
		super("Ese DNI ya está asociado a otra cuenta.");
	}
}
