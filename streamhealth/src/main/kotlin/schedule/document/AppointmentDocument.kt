package com.betha.schedule.document

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document

/**
 * Enum representing appointment status in the system
 */
enum class AppointmentStatus {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    COMPLETADA
}

/**
 * Appointment document for MongoDB persistence
 * Represents an appointment in the StreamHealth telemedicine platform
 */
@Serializable
data class AppointmentDocument(
    val id: String? = null,           // MongoDB ObjectId
    val appointmentId: String,         // Compound ID: 20241215-ABC123
    val patientId: String,            // idNumber (cédula del paciente)
    val doctorId: String,            // idNumber (cédula del doctor)
    val fecha: String,               // ISO8601 datetime
    val motivo: String,             // Reason/symptoms
    val estado: String = "pendiente", // pendiente | confirmada | cancelada | completada
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        fun fromDocument(document: Document): AppointmentDocument = json.decodeFromString(document.toJson())
    }
}