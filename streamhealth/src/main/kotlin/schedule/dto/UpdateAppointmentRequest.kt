package com.betha.schedule.dto

import kotlinx.serialization.Serializable

/**
 * Request DTO for updating an appointment status
 */
@Serializable
data class UpdateAppointmentRequest(
    val estado: String? = null  // Estado opcional: confirmada | cancelada | completada
)