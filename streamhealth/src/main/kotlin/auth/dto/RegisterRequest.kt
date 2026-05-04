package com.betha.auth.dto

import com.betha.common.document.Rol
import com.betha.common.document.Sexo
import kotlinx.serialization.Serializable

/**
 * Register request DTO (PRD: section 5.1)
 */
@Serializable
data class RegisterRequest(
    val idNumber: String,
    val nombres: String,
    val apellidos: String,
    val edad: Int,
    val sexo: Sexo,
    val residencia: String,
    val password: String,
    val rol: Rol = Rol.PACIENTE
)