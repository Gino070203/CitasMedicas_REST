# API REST — Centro Médico (Avance_03_Rest)

Documentación de referencia para el equipo de frontend (Angular).

- **Base URL:** `http://localhost:8080`
- **Formato:** JSON (`application/json`)
- **Autenticación:** JWT enviado en el header `Authorization: Bearer <token>`

---

## 1. Autenticación

### 1.1 Login con contraseña

```
POST /api/auth/login
```

Body:
```json
{ "correo": "ana@gmail.com", "password": "clave1234" }
```

Respuesta `200`:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "correo": "ana@gmail.com",
  "rol": "PACIENTE",
  "nombre": "Ana",
  "apellido": "Perez",
  "idUsuario": 1,
  "expiraEnSegundos": 7200
}
```

Errores: `401` si las credenciales son incorrectas o la cuenta está inactiva (no verificada/desactivada).

### 1.2 Registro (envía código de verificación)

```
POST /api/auth/registro
```

Body:
```json
{
  "nombre": "Ana",
  "apellido": "Perez",
  "dni": "12345678",
  "correo": "ana@gmail.com",
  "password": "clave1234",
  "telefono": "999888777",
  "direccion": "Av. Siempre Viva 123"
}
```

Respuesta `201`:
```json
{ "mensaje": "Registro exitoso. Revisa tu correo para verificar tu cuenta.", "correo": "ana@gmail.com" }
```

- El usuario queda con `estado=false` hasta verificar.
- El código de 6 dígitos llega al correo.

### 1.3 Verificar cuenta

```
POST /api/auth/verificar
```

Body:
```json
{ "correo": "ana@gmail.com", "codigo": "123456" }
```

Respuesta `200`:
```json
{ "mensaje": "Cuenta verificada correctamente." }
```

Error: `400` si el código es incorrecto.

### 1.4 Login con Google (OAuth2)

El frontend redirige el navegador a:

```
GET /oauth2/authorization/google
```

Tras autenticar en Google, el backend redirige a Angular con el token:

```
http://localhost:4200/auth/oauth2-redirect?token=<JWT>&correo=...&rol=...&nombre=...&apellido=...
```

La ruta Angular `/auth/oauth2-redirect` debe leer esos query params y guardar el `token`.

> **Requisito:** registrar `http://localhost:8080/login/oauth2/code/google` como URI de redirección en Google Cloud Console.

### 1.5 Usuario actual

```
GET /api/usuario/me
Authorization: Bearer <token>
```

Respuesta `200`:
```json
{ "idUsuario": 1, "correo": "ana@gmail.com", "nombre": "Ana", "apellido": "Perez", "rol": "PACIENTE" }
```

---

## 2. Roles y permisos

| Rol | Prefijo |
|-----|---------|
| ADMIN | `/api/admin/**`, `/api/usuario/**` |
| MEDICO | `/api/medico/**`, `/api/usuario/**` |
| PACIENTE | `/api/paciente/**`, `/api/usuario/**` |

Cualquier petición sin token válido devuelve `401`; con token válido pero rol insuficiente devuelve `403`.

---

## 3. Administrador — Usuarios

Base: `/api/admin/usuarios`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/admin/usuarios?q=&rol=&estado=` | Listar/filtrar |
| GET | `/api/admin/usuarios/{id}` | Obtener uno |
| POST | `/api/admin/usuarios` | Crear |
| PUT | `/api/admin/usuarios/{id}` | Actualizar |
| PATCH | `/api/admin/usuarios/{id}/estado` | Activar/desactivar |

Body para POST/PUT (`UsuarioRequest`):
```json
{
  "nombre": "Ana",
  "apellido": "Perez",
  "dni": "12345678",
  "correo": "ana@gmail.com",
  "telefono": "999888777",
  "direccion": "Av. Siempre Viva 123",
  "password": "clave1234",
  "rol": "PACIENTE"
}
```

`password` es obligatorio en creación; en actualización opcional (solo si se cambia). `rol` ∈ `ADMIN`, `MEDICO`, `PACIENTE`.

Respuesta (`UsuarioResponse`):
```json
{
  "idUsuario": 1, "nombre": "Ana", "apellido": "Perez", "dni": "12345678",
  "correo": "ana@gmail.com", "telefono": "999888777", "direccion": "...",
  "estado": true, "rolNombre": "PACIENTE"
}
```

---

## 4. Administrador — Pacientes

Base: `/api/admin/pacientes`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/admin/pacientes?q=&estado=&conSis=` | Listar/filtrar |
| GET | `/api/admin/pacientes/{id}` | Obtener uno |
| POST | `/api/admin/pacientes` | Registrar perfil |
| PUT | `/api/admin/pacientes/{id}` | Actualizar |
| PATCH | `/api/admin/pacientes/{id}/estado` | Activar/desactivar |

Body POST (`PacienteRegistroRequest`):
```json
{ "idUsuario": 1, "numeroHistoria": "H001", "sis": "SIS123", "fechaNacimiento": "2000-05-10", "genero": "FEMENINO", "direccion": "..." }
```

Body PUT (`PacienteActualizacionRequest`):
```json
{ "numeroHistoria": "H001", "sis": "SIS123", "fechaNacimiento": "2000-05-10", "genero": "FEMENINO", "direccion": "...", "numeroHistoriaVerificado": true, "sisVerificado": true }
```

`genero` ∈ `MASCULINO`, `FEMENINO`, `OTRO`.

---

## 5. Administrador — Médicos

Base: `/api/admin/medicos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/admin/medicos?q=&idEspecialidad=&estado=` | Listar/filtrar |
| GET | `/api/admin/medicos/{id}` | Obtener uno |
| POST | `/api/admin/medicos` | Registrar |
| PUT | `/api/admin/medicos/{id}` | Actualizar |
| PATCH | `/api/admin/medicos/{id}/estado` | Activar/desactivar |

Body POST (`MedicoRequest`):
```json
{ "idUsuario": 5, "idEspecialidad": 2, "cmp": "12345" }
```

Body PUT (`MedicoUpdateRequest`):
```json
{ "idEspecialidad": 3, "cmp": "67890" }
```

Respuesta (`MedicoResponse`):
```json
{ "idMedico": 1, "idUsuario": 5, "nombre": "Juan", "apellido": "Rojas", "cmp": "12345", "idEspecialidad": 2, "especialidadNombre": "Cardiología", "estado": true }
```

---

## 6. Administrador — Especialidades

Base: `/api/admin/especialidades`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/admin/especialidades?q=&estado=` | Listar/filtrar |
| GET | `/api/admin/especialidades/{id}` | Obtener una |
| POST | `/api/admin/especialidades` | Crear |
| PUT | `/api/admin/especialidades/{id}` | Actualizar |
| PATCH | `/api/admin/especialidades/{id}/estado` | Activar/desactivar |

Body (`EspecialidadRequest`):
```json
{ "nombre": "Cardiología", "descripcion": "Atención cardiovascular" }
```

---

## 7. Administrador — Horarios

Base: `/api/admin/horarios`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/admin/horarios?q=&diaSemana=&estado=` | Listar/filtrar |
| GET | `/api/admin/horarios/{id}` | Obtener uno |
| POST | `/api/admin/horarios` | Crear |
| PUT | `/api/admin/horarios/{id}` | Actualizar |
| PATCH | `/api/admin/horarios/{id}/estado` | Activar/desactivar |

Body (`HorarioRequest`):
```json
{ "idMedico": 1, "diaSemana": "MONDAY", "horaInicio": "08:00", "horaFin": "12:00" }
```

`diaSemana` ∈ `MONDAY`…`SUNDAY`. `horaInicio`/`horaFin` formato `HH:mm` (o `HH:mm:ss`).

Respuesta (`HorarioResponse`):
```json
{ "idHorario": 1, "idMedico": 1, "nombreMedico": "Juan Rojas", "especialidadNombre": "Pediatría", "diaSemana": "MONDAY", "horaInicio": "08:00", "horaFin": "12:00", "estado": true, "turnosPosibles": 8, "fechasDisponibles": "2026-11-05,2026-11-12" }
```

---

## 8. Administrador — Citas

Base: `/api/admin/citas`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/admin/citas?q=&estado=&fecha=&idMedico=` | Listar/filtrar |
| GET | `/api/admin/citas/{id}` | Obtener una |
| GET | `/api/admin/citas/turnos?idHorario=&fecha=` | Turnos disponibles |
| POST | `/api/admin/citas` | Reservar |
| PATCH | `/api/admin/citas/{id}/cancelar` | Cancelar |

Body POST (`CitaRequest`):
```json
{ "idPaciente": 1, "idMedico": 1, "idHorario": 1, "fecha": "2026-11-20", "horaInicio": "08:00" }
```

`GET /turnos` responde:
```json
[ { "horaInicio": "08:00", "horaFin": "08:30" }, { "horaInicio": "08:30", "horaFin": "09:00" } ]
```

Respuesta de cita (`CitaResponse`):
```json
{
  "idCita": 1, "idPaciente": 1, "nombrePaciente": "Ana Perez", "idMedico": 1,
  "nombreMedico": "Juan Rojas", "especialidadNombre": "Pediatría", "idHorario": 1, "fecha": "2026-11-20",
  "diaSemana": "FRIDAY", "horaInicio": "08:00", "horaFin": "08:30", "estado": "PENDIENTE_PAGO", "diagnostico": null
}
```

Estados de cita: `CONFIRMADA`, `PENDIENTE_PAGO`, `CANCELADA`, `ATENDIDA`, `NO_ASISTIO`.

---

## 9. Administrador — Pagos

Base: `/api/admin/pagos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/admin/pagos?q=&estado=&metodo=&desde=&hasta=` | Listar/filtrar |
| GET | `/api/admin/pagos/total` | Total recaudado (número) |
| GET | `/api/admin/pagos/citas-pendientes` | Citas pendientes de pago |
| POST | `/api/admin/pagos` | Registrar pago |

Body POST (`PagoRequest`):
```json
{ "idCita": 1, "monto": 50.00, "metodoPago": "EFECTIVO" }
```

`metodoPago` ∈ `EFECTIVO`, `TARJETA`, `TRANSFERENCIA`.

Respuesta (`PagoResponse`):
```json
{ "idPago": 1, "idCita": 1, "nombrePaciente": "Ana Perez", "nombreMedico": "Juan Rojas", "monto": 50.00, "fechaPago": "2026-11-02", "metodoPago": "EFECTIVO", "estado": "PAGADO" }
```

---

## 10. Administrador — Dashboard

```
GET /api/admin/dashboard
```

Respuesta `200`:
```json
{ "totalUsuarios": 10, "totalMedicos": 3, "totalPacientes": 5, "totalEspecialidades": 6, "totalCitas": 20, "citasHoy": 2, "totalPagos": 12 }
```

---

## 11. Paciente (autoservicio)

Base: `/api/paciente` (requiere rol `PACIENTE`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/paciente/perfil` | Obtener perfil |
| PUT | `/api/paciente/perfil` | Actualizar perfil |
| GET | `/api/paciente/especialidades` | Especialidades activas |
| GET | `/api/paciente/horarios?idEspecialidad=` | Horarios disponibles |
| GET | `/api/paciente/turnos?idHorario=&fecha=` | Turnos disponibles |
| POST | `/api/paciente/reservar` | Reservar cita |
| GET | `/api/paciente/citas` | Historial de citas |

PUT `/api/paciente/perfil` (`PerfilPacienteForm`):
```json
{
  "nombre": "Ana", "apellido": "Perez", "dni": "12345678",
  "numeroHistoria": "H001", "sis": "SIS123", "telefono": "999888777",
  "direccion": "...", "fechaNacimiento": "2000-05-10", "genero": "FEMENINO"
}
```

POST `/api/paciente/reservar` (`ReservaRequest`):
```json
{ "idHorario": 1, "fecha": "2026-11-20", "horaInicio": "08:00" }
```

Respuesta: `CitaResponse` (ver sección 8). Si el paciente no completó su perfil, devuelve `400` con `"Perfil incompleto"`.

---

## 12. Médico (panel)

Base: `/api/medico` (requiere rol `MEDICO`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/medico/dashboard` | Resumen + agenda |
| GET | `/api/medico/citas/{id}` | Detalle de cita |
| PUT | `/api/medico/citas/{id}/diagnostico` | Registrar diagnóstico |

PUT `/api/medico/citas/{id}/diagnostico` (`DiagnosticoRequest`):
```json
{ "diagnostico": "Paciente estable, continuar tratamiento." }
```

Respuesta de `GET /api/medico/dashboard`:
```json
{
  "idMedico": 1, "nombre": "Juan", "apellido": "Rojas", "especialidad": "Cardiología",
  "agendaDeHoy": [ /* CitaResponse */ ],
  "porAtender": [ /* CitaResponse */ ],
  "historial": [ /* CitaResponse */ ]
}
```

---

## 13. Rol

```
POST /api/rol?nombreRol=ADMIN
```

---

## 14. Formato de error

Todas las respuestas de error usan el mismo formato (`ErrorResponse`):

```json
{
  "timestamp": "2026-11-02T00:00:00Z",
  "status": 400,
  "error": "Solicitud inválida",
  "message": "El DNI debe tener 8 dígitos",
  "fieldErrors": { "dni": "El DNI debe contener exactamente 8 dígitos." }
}
```

Códigos comunes:
- `400` — datos inválidos o regla de negocio incumplida.
- `401` — sin token o token inválido/expirado.
- `403` — rol insuficiente.
- `409` — conflicto de estado (p. ej. cuenta sin perfil).
- `500` — error interno.

---

## Notas para el frontend

- Enviar siempre `Authorization: Bearer <token>` en los endpoints protegidos.
- Los roles se comparan como `ROLE_ADMIN`, `ROLE_MEDICO`, `ROLE_PACIENTE`, pero el token y las respuestas exponen el nombre sin prefijo (`ADMIN`, `MEDICO`, `PACIENTE`).
- Las fechas van en formato ISO `yyyy-MM-dd` y las horas en `HH:mm`.
- El token expira según `app.jwt.expiration-ms` (por defecto 2 horas); ante un `401` el frontend debe redirigir al login.
