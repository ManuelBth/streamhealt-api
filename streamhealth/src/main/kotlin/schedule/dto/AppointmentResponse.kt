package com.betha.schedule.dto

import com.betha.schedule.document.AppointmentDocument
import kotlinx.serialization.Serializable

/**
 * Response DTO for appointment data
 */
@Serializable
data class AppointmentResponse(
    val id: String,
    val patientId: String,
    val doctorId: String,
    val fecha: String,
    val motivo: String,
    val estado: String,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Extension function to convert AppointmentDocument to AppointmentResponse
 */
fun AppointmentDocument.toAppointmentResponse(): AppointmentResponse {
    return AppointmentResponse(
        id = this.id ?: "",
        patientId = this.patientId,
        doctorId = this.doctorId,
        fecha = this.fecha,
        motivo = this.motivo,
        estado = this.estado,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}