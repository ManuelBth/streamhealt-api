# PRD - Plataforma de Telemedicina Betha

## 1. Descripción del Producto

**Nombre del Proyecto:** Betha - Plataforma de Citas de Telemedicina

**Descripción:** Aplicación móvil y web para agendar citas de videollamada entre pacientes y doctores. Los pacientes pueden agendar citas con doctores específicos, enviar síntomas/quejas previas, y recibir recetas. Los doctores pueden gestionar sus citas, crear historiales clínicos y recetar medicamentos.

**Stack Tecnológico:**
- Backend: Ktor (Framework Kotlin)
- Base de Datos: MongoDB
- Autenticación: JWT (JSON Web Tokens)

---

## 2. Objetivos del Producto

1. Permitir a pacientes agendar citas con doctores específicos
2. Facilitar la comunicación de síntomas/quejas previo a la cita
3. Permitir a doctores gestionar y actualizar estado de citas
4. Crear historial clínico por cada cita atendida
5. Gestionar recetas médicas vinculadas al historial clínico
6. Autenticación segura con JWT

---

## 3. Roles de Usuario

| Rol | Descripción | Permisos |
|-----|-----------|---------|
| **patient** | Paciente que agenda citas | Crear/cancelar citas, ver historial clínico, ver recetas, ver doctores |
| **doctor** | Doctor que atiende citas | Ver/actualizar citas, crear historial clínico, crear recetas |

---

## 4. Casos de Uso (User Stories)

### 4.1 Autenticación

| ID | Actor | Acción | Descripción |
|----|-----|--------|---------|
| US-01 | Usuario | Iniciar sesión | Como paciente/doctor, quiero iniciar sesión con mi ID y contraseña para acceder a la plataforma |
| US-02 | Usuario | Registrarse | Como nuevo usuario, quiero registrarme proporcionando mis datos personales para crear una cuenta |

### 4.2 Citas

| ID | Actor | Acción | Descripción |
|----|-----|--------|---------|
| US-03 | Patient | Agendar cita | Como paciente, quiero agendar una cita con un doctor específico en una fecha/hora para recibir atención médica |
| US-04 | Patient | Ver mis citas | Como paciente, quiero ver todas mis citas agendadas para conocer mi agenda |
| US-05 | Patient | Cancelar cita | Como paciente, quiero cancelar una cita antes de que ocurra |
| US-06 | Doctor | Ver mis citas | Como doctor, quiero ver todas las citas agendadas conmigo |
| US-07 | Doctor | Actualizar cita | Como doctor, quiero actualizar el estado de una cita (confirmar/cancelar/completar) |

### 4.3 Doctores

| ID | Actor | Acción | Descripción |
|----|-----|--------|---------|
| US-08 | Patient | Buscar doctores | Como paciente, quiero buscar doctores por nombre para seleccionar uno |
| US-09 | Patient | Ver perfil doctor | Como paciente, quiero ver el perfil profesional de un doctor antes de agendar |

### 4.4 Historial Clínico

| ID | Actor | Acción | Descripción |
|----|-----|--------|---------|
| US-10 | Doctor | Crear historial | Como doctor, quiero crear un historial clínico después de una cita |
| US-11 | Doctor | Actualizar historial | Como doctor, quiero actualizar un historial clínico existente |
| US-12 | Patient | Ver historiales | Como paciente, quiero ver todos mis historiales clínicos |
| US-13 | Patient | Ver detalle historial | Como paciente, quiero ver el detalle de un historial específico |

### 4.5 Recetas

| ID | Actor | Acción | Descripción |
|----|-----|--------|---------|
| US-14 | Doctor | Crear receta | Como doctor, quiero crear una receta médica vinculada a un historial |
| US-15 | Patient | Ver recetas | Como paciente, quiero ver todas las recetas que me han formulado |
| US-16 | Patient | Ver detalle receta | Como paciente, quiero ver el detalle de una receta específica |

### 4.6 Perfil de Usuario

| ID | Actor | Acción | Descripción |
|----|-----|--------|---------|
| US-17 | Usuario | Ver mi perfil | Como usuario, quiero ver mi información de perfil |
| US-18 | Usuario | Actualizar perfil | Como usuario, quiero actualizar mi información de perfil |

---

## 5. Endpoints de API

### 5.1 Autenticación

| Método | Endpoint | Descripción | Requiere Auth |
|--------|---------|-----------|--------------|
| POST | /api/v1/auth/login | Iniciar sesión | No |
| POST | /api/v1/auth/register | Registrarse | No |
| POST | /api/v1/auth/logout | Cerrar sesión | Sí |

**Payloads:**

```json
// POST /api/v1/auth/login
{
  "id": "string",
  "password": "string"
}

// POST /api/v1/auth/register
{
  "id": "string",
  "nombres": "string",
  "apellidos": "string",
  "edad": number,
  "sexo": "Masculino" | "Femenino",
  "residencia": "string",
  "password": "string"
}

// Response (login/register)
{
  "token": "string",
  "user": {
    "id": "string",
    "nombres": "string",
    "apellidos": "string",
    "rol": "patient" | "doctor"
  }
}
```

---

### 5.2 Citas

| Método | Endpoint | Descripción | Requiere Auth |
|--------|---------|-----------|--------------|
| GET | /api/v1/schedule/patient/{patientId} | Ver citas del paciente | Sí |
| POST | /api/v1/schedule/patient | Crear cita | Sí |
| PUT | /api/v1/schedule/patient/{appointmentId} | Actualizar/cancelar cita | Sí |
| DELETE | /api/v1/schedule/patient/{appointmentId} | Eliminar cita | Sí |
| GET | /api/v1/schedule/doctor/{doctorId} | Ver citas del doctor | Sí |
| PUT | /api/v1/schedule/doctor/{appointmentId} | Actualizar estado cita | Sí |

**Payloads:**

```json
// POST /api/v1/schedule/patient
{
  "fecha": "ISO8601 datetime",
  "doctorId": "string",
  "patientId": "string",
  "motivo": "string"
}

// PUT /api/v1/schedule/patient o /doctor
{
  "estado": "pendiente" | "confirmada" | "cancelada" | "completada"
}

// Response: Listado de citas
[
  {
    "id": "string",
    "fecha": "ISO8601 datetime",
    "doctorId": "string",
    "patientId": "string",
    "motivo": "string",
    "estado": "pendiente" | "confirmada" | "cancelada" | "completada"
  }
]
```

---

### 5.3 Doctores

| Método | Endpoint | Descripción | Requiere Auth |
|--------|---------|-----------|--------------|
| GET | /api/v1/doctors | Listar todos los doctores | Sí |
| GET | /api/v1/doctors/search?q={name} | Buscar doctores por nombre | Sí |
| GET | /api/v1/doctors/{doctorId} | Ver perfil del doctor | Sí |

**Response:**

```json
// GET /api/v1/doctors
[
  {
    "id": "string",
    "nombres": "string",
    "apellidos": "string",
    "titulo": "string",
    "universidad": "string",
    "especialidades": ["string"],
    "doctorados": ["string"]
  }
]
```

---

### 5.4 Historial Clínico

| Método | Endpoint | Descripción | Requiere Auth |
|--------|---------|-----------|--------------|
| GET | /api/v1/medicalHistory/patient/{patientId} | Ver todos los historiales del paciente | Sí |
| GET | /api/v1/medicalHistory/{historyId} | Ver detalle historial | Sí |
| POST | /api/v1/medicalHistory | Crear historial clínico | Sí |
| PUT | /api/v1/medicalHistory/{historyId} | Actualizar historial | Sí |
| DELETE | /api/v1/medicalHistory/{historyId} | Eliminar historial | Sí |

**Payloads:**

```json
// POST /api/v1/medicalHistory
{
  "patientId": "string",
  "doctorId": "string",
  "appointmentId": "string",
  "diagnostico": "string",
  "observaciones": "string",
  "recetas": ["string"]  // IDs de recetas
}

// GET /api/v1/medicalHistory/patient/{patientId}
[
  {
    "id": "string",
    "patientId": "string",
    "doctorId": "string",
    "fecha": "ISO8601 datetime",
    "diagnostico": "string",
    "observaciones": "string"
  }
]
```

---

### 5.5 Recetas

| Método | Endpoint | Descripción | Requiere Auth |
|--------|---------|-----------|--------------|
| GET | /api/v1/prescriptions/patient/{patientId} | Ver recetas del paciente | Sí |
| GET | /api/v1/prescriptions/{prescriptionId} | Ver detalle receta | Sí |
| POST | /api/v1/prescriptions | Crear receta | Sí |

**Payloads:**

```json
// POST /api/v1/prescriptions
{
  "patientId": "string",
  "doctorId": "string",
  "medicalHistoryId": "string",
  "medicamento": "string",
  "cantidad": "string",
  "frecuencia": "string",
  "recomendaciones": "string",
  "fechaValidacion": "ISO8601 datetime"
}

// GET /api/v1/prescriptions/patient/{patientId}
[
  {
    "id": "string",
    "medicalHistoryId": "string",
    "doctorId": "string",
    "medicamento": "string",
    "cantidad": "string",
    "frecuencia": "string",
    "recomendaciones": "string",
    "fechaValidacion": "ISO8601 datetime",
    "doctor": {
      "nombres": "string",
      "apellidos": "string"
    }
  }
]
```

---

### 5.6 Usuarios

| Método | Endpoint | Descripción | Requiere Auth |
|--------|---------|-----------|--------------|
| GET | /api/v1/user/{userId} | Ver perfil de usuario | Sí |
| PUT | /api/v1/user/{userId} | Actualizar perfil | Sí |
| DELETE | /api/v1/user/{userId} | Eliminar usuario | Sí |

**Payloads:**

```json
// PUT /api/v1/user/{userId}
{
  "nombres": "string",
  "apellidos": "string",
  "edad": number,
  "sexo": "Masculino" | "Femenino",
  "residencia": "string"
}

// Response
{
  "id": "string",
  "nombres": "string",
  "apellidos": "string",
  "edad": number,
  "sexo": "Masculino" | "Femenino",
  "residencia": "string",
  "rol": "patient" | "doctor"
}
```

---

## 6. Modelos de Datos (MongoDB)

### 6.1 User (Usuario)

```json
{
  "_id": "ObjectId",
  "id": "string (unique)",
  "password": "string (hashed)",
  "nombres": "string",
  "apellidos": "string",
  "edad": "number",
  "sexo": "string",
  "residencia": "string",
  "rol": "patient" | "doctor",
  "createdAt": "datetime",
  "updatedAt": "datetime",

  // Solo aplica para doctores
  "perfil": {
    "titulo": "string",
    "universidad": "string",
    "especialidades": ["string"],
    "doctorados": ["string"]
  }
}
```

### 6.2 Appointment (Cita)

```json
{
  "_id": "ObjectId",
  "patientId": "ObjectId (ref: User)",
  "doctorId": "ObjectId (ref: User)",
  "fecha": "datetime",
  "motivo": "string",
  "estado": "pendiente" | "confirmada" | "cancelada" | "completada",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### 6.3 MedicalHistory (Historial Clínico)

```json
{
  "_id": "ObjectId",
  "patientId": "ObjectId (ref: User)",
  "doctorId": "ObjectId (ref: User)",
  "appointmentId": "ObjectId (ref: Appointment)",
  "diagnostico": "string",
  "observaciones": "string",
  "recetas": ["ObjectId (ref: Prescription)"],
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### 6.4 Prescription (Receta)

```json
{
  "_id": "ObjectId",
  "patientId": "ObjectId (ref: User)",
  "doctorId": "ObjectId (ref: User)",
  "medicalHistoryId": "ObjectId (ref: MedicalHistory)",
  "medicamento": "string",
  "cantidad": "string",
  "frecuencia": "string",
  "recomendaciones": "string",
  "fechaValidacion": "datetime",
  "createdAt": "datetime"
}
```

---

## 7. Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado exitosamente |
| 204 | No Content - Solicitud exitosa sin contenido |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - No autorizado |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Conflicto de datos |
| 500 | Internal Server Error - Error del servidor |

---

## 8. Autenticación JWT

- **Algoritmo:** HS256
- **Payload:**
  ```json
  {
    "sub": "userId",
    "rol": "patient" | "doctor",
    "exp": "timestamp"
  }
  ```
- **Header:** `Authorization: Bearer <token>`
- **Expiración:** 24 horas (configurable)

---

## 9. Reglas de Negocio

1. **Citas:**
   - No hay tiempo mínimo de anticipación
   - No hay horario definido
   - Duración sugerida: 30 minutos
   - El paciente puede cancelar sus propias citas
   - El doctor puede actualizar el estado

2. **Doctores:**
   - Perfil profesional público
   - Búsqueda por nombre
   - Especialidades y doctorados son opcionales

3. **Historial Clínico:**
   - Múltiples historiales por paciente
   - Solo doctores pueden crear/actualizar
   - Pacientes solo pueden leer

4. **Recetas:**
   - Vinculadas al historial clínico
   - El paciente puede ver todas sus recetas

5. **Perfil:**
   - El paciente puede ver perfil del doctor
   - Solo 2 valores para sexo: Masculino, Femenino
   - Edad escrita literalmente (número)

---

## 10. Funcionalidades Futuras (No en MVP)

- Videollamada integrada
- Notificaciones (email/push)
- Calificaciones/reseñas de doctores
- Historial de cambios (audit logs)
- Pago en línea

---

## 11. Validaciones

| Campo | Validación |
|-------|-----------|
| id (login) | Requerido, string no vacía |
| password | Requerida, mínimo 6 caracteres |
| id (register) | Requerido, único en la base de datos |
| nombres | Requerido, string no vacía |
| apellidos | Requerido, string no vacía |
| edad | Requerida, número positivo |
| sexo | "Masculino" o "Femenino" |
| fecha cita | Datetime válido |
| doctorId | Debe existir en User con rol "doctor" |
| patientId | Debe existir en User con rol "patient" |

---

**Documento creado para el Laboratorio 5 - Aplicaciones Móviles**
**Ingeniería Electrónica - Universidad del Quindío**
**Semestre 10**