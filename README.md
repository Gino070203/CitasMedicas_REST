# Avance_03_Rest — API REST del Centro Médico

Backend en **Spring Boot** (Java 21) con **JWT**, que expone una API REST consumida por el frontend en **Angular**.

## Requisitos

- **Java 21**
- **MySQL 8** (o compatible)
- Opcional: Maven (se incluye el wrapper `mvnw`)

## Configuración inicial

1. Crear la base de datos en MySQL:

   ```sql
   CREATE DATABASE citasMedicas;
   ```

2. Configurar credenciales:

   - Copia `src/main/resources/application.properties.example` a `application.properties` (en la misma carpeta).
   - Completa las credenciales de base de datos, correo (Gmail) y OAuth de Google.
   - **Importante:** `application.properties` **NO** se sube a GitHub (está en `.gitignore`).

3. Ejecutar la aplicación:

   ```bash
   ./mvnw.cmd spring-boot:run      # Windows
   ./mvnw spring-boot:run          # Linux / macOS
   ```

   El servidor queda en `http://localhost:8080`.

## Documentación de la API

Consulta **[ENDPOINTS.md](ENDPOINTS.md)** para el detalle de todos los endpoints (autenticación, administrador, paciente y médico), formatos de request/response y roles.

## Estructura

```
src/main/java/com/citas/medicas/
├── config/         # Seguridad (JWT, CORS, OAuth2)
├── security/       # JWT + usuario autenticado
├── auth/           # Login, registro y verificación
├── usuario/        # Usuarios (admin)
├── rol/
├── paciente/
├── medico/
├── especialidad/
├── horarios/
├── cita/
├── pago/
├── dashboard/
└── common/         # Manejo global de errores
```

## Nota sobre credenciales

Las credenciales están **hardcodeadas** en el `application.properties` local de cada desarrollador (no versionado). Para externalizarlas a variables de entorno, basta con reemplazar los valores por `${NOMBRE_VARIABLE}` (varias propiedades ya usan ese patrón).
