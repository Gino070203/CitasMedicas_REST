package com.citas.medicas.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.citas.medicas.paciente.DniPacienteDuplicadoException;
import com.citas.medicas.paciente.IdentificadorPacienteDuplicadoException;
import com.citas.medicas.paciente.PerfilPacienteIncompletoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IdentificadorPacienteDuplicadoException.class)
	public ResponseEntity<ErrorResponse> handleIdentificadorDuplicado(IdentificadorPacienteDuplicadoException ex) {
		Map<String, String> fields = new HashMap<>();
		fields.put(ex.getCampo(), ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Identificador duplicado", ex.getMessage(), fields);
	}

	@ExceptionHandler(DniPacienteDuplicadoException.class)
	public ResponseEntity<ErrorResponse> handleDniDuplicado(DniPacienteDuplicadoException ex) {
		return build(HttpStatus.BAD_REQUEST, "DNI duplicado", ex.getMessage(), Map.of("dni", ex.getMessage()));
	}

	@ExceptionHandler(PerfilPacienteIncompletoException.class)
	public ResponseEntity<ErrorResponse> handlePerfilIncompleto(PerfilPacienteIncompletoException ex) {
		return build(HttpStatus.BAD_REQUEST, "Perfil incompleto", ex.getMessage(), null);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		return build(HttpStatus.BAD_REQUEST, "Solicitud inválida", ex.getMessage(), null);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
		return build(HttpStatus.CONFLICT, "Conflicto", ex.getMessage(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
		Map<String, String> fields = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
				fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
		return build(HttpStatus.BAD_REQUEST, "Datos inválidos", "Revisa los campos enviados.", fields);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenerico(Exception ex) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex.getMessage(), null);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String error,
												String message, Map<String, String> fields) {
		ErrorResponse body = new ErrorResponse(
				java.time.Instant.now(), status.value(), error, message, fields);
		return ResponseEntity.status(status).body(body);
	}
}
