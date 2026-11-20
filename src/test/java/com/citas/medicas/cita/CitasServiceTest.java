package com.citas.medicas.cita;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.citas.medicas.horarios.Horarios;
import com.citas.medicas.horarios.HorariosRepository;
import com.citas.medicas.medico.Medico;
import com.citas.medicas.medico.MedicoRepository;
import com.citas.medicas.paciente.Paciente;
import com.citas.medicas.paciente.PacienteRepository;
import com.citas.medicas.paciente.PerfilPacienteIncompletoException;
import com.citas.medicas.pago.PagoRepository;
import com.citas.medicas.usuario.Usuario;

class CitasServiceTest {

    private static final long ID_PACIENTE = 1L;
    private static final long ID_MEDICO = 2L;
    private static final long ID_HORARIO = 3L;
    private static final LocalTime HORA_INICIO = LocalTime.of(9, 0);

    private final CitasRepository citasRepository = mock(CitasRepository.class);
    private final PacienteRepository pacienteRepository = mock(PacienteRepository.class);
    private final MedicoRepository medicoRepository = mock(MedicoRepository.class);
    private final HorariosRepository horariosRepository = mock(HorariosRepository.class);
    private final PagoRepository pagoRepository = mock(PagoRepository.class);
    private final CitasService service = new CitasService(
            citasRepository, pacienteRepository, medicoRepository,
            horariosRepository, pagoRepository, 30);

    private final Paciente paciente = new Paciente();
    private final Horarios horario = new Horarios();
    private final LocalDate fechaCita = LocalDate.now().plusDays(1);

    @BeforeEach
    void prepararDatosValidos() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellido("Prueba");
        usuario.setDni("12345678");
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(LocalDate.of(1995, 1, 10));
        paciente.setGenero("FEMENINO");
        paciente.setEstado(true);

        Medico medicoDelHorario = new Medico();
        medicoDelHorario.setIdMedico(ID_MEDICO);
        medicoDelHorario.setEstado(true);
        horario.setMedico(medicoDelHorario);
        horario.setEstado(true);
        horario.setDiaSemana(fechaCita.getDayOfWeek().name());
        horario.setHoraInicio(HORA_INICIO);
        horario.setHoraFin(LocalTime.of(10, 0));
    }

    @Test
    void generaDosTurnosDeTreintaMinutosDentroDelBloque() {
        List<TurnoCita> turnos = service.generarTurnosDentroDelBloque(horario);

        assertEquals(List.of(
                new TurnoCita(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                new TurnoCita(LocalTime.of(9, 30), LocalTime.of(10, 0))), turnos);
    }

    @Test
    void rechazaElRegistroSiElBloqueEstaInactivo() {
        horario.setEstado(false);
        simularPacienteYHorario();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, this::registrarCita);

        assertEquals("El bloque de atención no está activo.", error.getMessage());
        verificarQueNoSeGuardoLaCita();
    }

    @Test
    void rechazaElRegistroSiElPacienteEstaInactivo() {
        paciente.setEstado(false);
        simularPacienteYHorario();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, this::registrarCita);

        assertEquals("El paciente está inactivo.", error.getMessage());
        verificarQueNoSeGuardoLaCita();
    }

    @Test
    void rechazaElRegistroSiElPerfilDelPacienteEstaIncompleto() {
        paciente.setFechaNacimiento(null);
        simularPacienteYHorario();

        assertThrows(PerfilPacienteIncompletoException.class, this::registrarCita);

        verificarQueNoSeGuardoLaCita();
    }

    @Test
    void rechazaElRegistroSiElMedicoEstaInactivoAlConfirmarLaCita() {
        simularPacienteYHorario();
        // El bloque se leyó con el médico activo, antes de la consulta con bloqueo.
        Medico medicoInactivo = new Medico();
        medicoInactivo.setIdMedico(ID_MEDICO);
        medicoInactivo.setEstado(false);
        when(medicoRepository.bloquearMedico(ID_MEDICO)).thenReturn(Optional.of(medicoInactivo));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, this::registrarCita);

        assertEquals("El médico está inactivo.", error.getMessage());
        verificarQueNoSeGuardoLaCita();
    }

    private void simularPacienteYHorario() {
        when(pacienteRepository.buscarPorIdConUsuario(ID_PACIENTE)).thenReturn(Optional.of(paciente));
        when(horariosRepository.findById(ID_HORARIO)).thenReturn(Optional.of(horario));
    }

    private void registrarCita() {
        service.registrar(ID_PACIENTE, ID_MEDICO, ID_HORARIO, fechaCita, HORA_INICIO);
    }

    private void verificarQueNoSeGuardoLaCita() {
        verify(citasRepository, never()).save(any(Citas.class));
    }
}
