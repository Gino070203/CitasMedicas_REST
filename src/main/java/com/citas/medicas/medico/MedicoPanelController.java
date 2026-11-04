package com.citas.medicas.medico;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.cita.CitaMapper;
import com.citas.medicas.cita.dto.CitaResponse;
import com.citas.medicas.medico.dto.DiagnosticoRequest;
import com.citas.medicas.medico.dto.MedicoDashboardResponse;

@RestController
@RequestMapping("/api/medico")
public class MedicoPanelController {

	private final MedicoPanelService service;

	public MedicoPanelController(MedicoPanelService service) {
		this.service = service;
	}

	@GetMapping("/dashboard")
	public MedicoDashboardResponse dashboard(Authentication auth) {
		Medico medico = service.medicoActual(auth.getName());
		String nombre = null;
		String apellido = null;
		String especialidad = null;
		if (medico.getUsuario() != null) {
			nombre = medico.getUsuario().getNombre();
			apellido = medico.getUsuario().getApellido();
		}
		if (medico.getEspecialidad() != null) {
			especialidad = medico.getEspecialidad().getNombre();
		}

		return new MedicoDashboardResponse(
				medico.getIdMedico(),
				nombre,
				apellido,
				especialidad,
				service.agendaDeHoy(auth.getName()).stream().map(CitaMapper::toResponse).toList(),
				service.porAtender(auth.getName()).stream().map(CitaMapper::toResponse).toList(),
				service.historial(auth.getName()).stream().map(CitaMapper::toResponse).toList());
	}

	@GetMapping("/citas/{id}")
	public CitaResponse obtenerCita(@PathVariable Long id, Authentication auth) {
		return CitaMapper.toResponse(service.obtenerCita(auth.getName(), id));
	}

	@PutMapping("/citas/{id}/diagnostico")
	public CitaResponse guardarDiagnostico(@PathVariable Long id,
										   @RequestBody DiagnosticoRequest request,
										   Authentication auth) {
		service.registrarDiagnostico(auth.getName(), id, request.diagnostico());
		return CitaMapper.toResponse(service.obtenerCita(auth.getName(), id));
	}
}
