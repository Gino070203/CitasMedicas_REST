package com.citas.medicas.cita;

import java.time.LocalTime;

/** Intervalo reservable calculado dentro de un bloque semanal. */
public record TurnoCita(LocalTime horaInicio, LocalTime horaFin) {
}
