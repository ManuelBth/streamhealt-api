package com.betha.doctor.dto

import kotlinx.serialization.Serializable

/**
 * Create doctor profile request DTO
 * Used when a doctor creates their professional profile
 */
@Serializable
data class CreateDoctorProfileRequest(
    val titulo: String? = null,           // Título profesional (Dr., Mg., PhD, etc.)
    val universidad: String? = null,     // Universidad otorgante
    val especialidades: List<String> = emptyList(),
    val doctorados: List<String> = emptyList(),
    val licencia: String? = null,         // Número de licencia médica
    val telefono: String? = null,
    val direccion: String? = null
)