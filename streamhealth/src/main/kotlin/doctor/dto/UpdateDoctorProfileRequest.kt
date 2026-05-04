package com.betha.doctor.dto

import kotlinx.serialization.Serializable

/**
 * Update doctor profile request DTO - for partial updates
 */
@Serializable
data class UpdateDoctorProfileRequest(
    val titulo: String? = null,
    val universidad: String? = null,
    val especialidades: List<String>? = null,
    val doctorados: List<String>? = null,
    val licencia: String? = null,
    val telefono: String? = null,
    val direccion: String? = null
)