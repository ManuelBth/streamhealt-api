package com.betha.doctor.dto

import kotlinx.serialization.Serializable

/**
 * Doctor response DTO - returned by GET endpoints
 * Includes enriched data from user profile
 */
@Serializable
data class DoctorResponse(
    val id: String,
    val userId: String,
    val idNumber: String,          // From User - Cédula
    val nombres: String,           // From User
    val apellidos: String,         // From User
    val edad: Int,                 // From User
    val sexo: String,              // From User
    val residencia: String?,        // From User
    val titulo: String? = null,
    val universidad: String? = null,
    val especialidades: List<String> = emptyList(),
    val doctorados: List<String> = emptyList(),
    val licencia: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Extension function to convert DoctorDocument to DoctorResponse with user data
 */
fun com.betha.doctor.document.DoctorDocument.toDoctorResponse(
    idNumber: String,
    nombres: String,
    apellidos: String,
    edad: Int,
    sexo: String,
    residencia: String?
): DoctorResponse {
    return DoctorResponse(
        id = this.id ?: "",
        userId = this.userId,
        idNumber = idNumber,
        nombres = nombres,
        apellidos = apellidos,
        edad = edad,
        sexo = sexo,
        residencia = residencia,
        titulo = this.titulo,
        universidad = this.universidad,
        especialidades = this.especialidades,
        doctorados = this.doctorados,
        licencia = this.licencia,
        telefono = this.telefono,
        direccion = this.direccion,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}