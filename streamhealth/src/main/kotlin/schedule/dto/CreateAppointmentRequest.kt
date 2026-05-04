package com.betha.schedule.dto

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a new appointment
 */
@Serializable
data class CreateAppointmentRequest(
    val patientId: String,    // idNumber (cédula del paciente)
    val doctorId: String,    // idNumber (cédula del doctor)
    val fecha: String,       // ISO8601 datetime
    val motivo: String       // Reason/symptoms
)