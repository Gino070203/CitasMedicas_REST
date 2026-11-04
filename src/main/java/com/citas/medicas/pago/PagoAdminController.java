package com.citas.medicas.pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.cita.CitaMapper;
import com.citas.medicas.cita.Citas;
import com.citas.medicas.cita.CitasRepository;
import com.citas.medicas.cita.dto.CitaResponse;
import com.citas.medicas.pago.dto.PagoRequest;
import com.citas.medicas.pago.dto.PagoResponse;

@RestController
@RequestMapping("/api/admin/pagos")
public class PagoAdminController {

	private final PagoService pagoService;
	private final CitasRepository citasRepository;

	public PagoAdminController(PagoService pagoService,
							   CitasRepository citasRepository) {
		this.pagoService = pagoService;
		this.citasRepository = citasRepository;
	}

	@GetMapping
	public List<PagoResponse> listar(@RequestParam(required = false) String q,
									 @RequestParam(required = false) String estado,
									 @RequestParam(required = false) String metodo,
									 @RequestParam(required = false) LocalDate desde,
									 @RequestParam(required = false) LocalDate hasta) {
		return pagoService.filtrar(q, estado, metodo, desde, hasta).stream()
				.map(PagoAdminController::toResponse)
				.toList();
	}

	@GetMapping("/total")
	public BigDecimal totalRecaudado() {
		return pagoService.totalRecaudado();
	}

	@GetMapping("/citas-pendientes")
	public List<CitaResponse> citasPendientes() {
		return citasRepository.listarPendientesPago().stream()
				.filter(c -> c.getPaciente() != null)
				.filter(c -> {
					String sis = c.getPaciente().getSis();
					boolean sinSis = sis == null || sis.trim().isEmpty();
					boolean sisPendiente = Boolean.FALSE.equals(c.getPaciente().getSisVerificado());
					return sinSis || sisPendiente;
				})
				.map(CitaMapper::toResponse)
				.toList();
	}

	@PostMapping
	public ResponseEntity<PagoResponse> guardar(@RequestBody PagoRequest request) {
		Pago pago = pagoService.registrar(request.idCita(), request.monto(), request.metodoPago());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(toResponse(pagoService.obtener(pago.getIdPago())));
	}

	private static PagoResponse toResponse(Pago p) {
		String nombrePaciente = null;
		String nombreMedico = null;
		Citas cita = p.getCita();
		if (cita != null) {
			if (cita.getPaciente() != null && cita.getPaciente().getUsuario() != null) {
				nombrePaciente = cita.getPaciente().getUsuario().getNombre() + " "
						+ cita.getPaciente().getUsuario().getApellido();
			}
			if (cita.getMedico() != null && cita.getMedico().getUsuario() != null) {
				nombreMedico = cita.getMedico().getUsuario().getNombre() + " "
						+ cita.getMedico().getUsuario().getApellido();
			}
		}
		return new PagoResponse(
				p.getIdPago(),
				cita != null ? cita.getIdCita() : null,
				nombrePaciente,
				nombreMedico,
				p.getMonto(),
				p.getFechaPago(),
				p.getMetodoPago(),
				p.getEstado());
	}
}
