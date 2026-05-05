package com.betha.medicalHistory.dto

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a new medical history
 */
@Serializable
data class CreateMedicalHistoryRequest(
    val patientId: String,
    val doctorId: String,
    val appointmentId: String,
    val diagnostico: String,
    val observaciones: String
)