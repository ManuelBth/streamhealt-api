package com.betha.user.dto

import com.betha.common.document.Rol
import com.betha.common.document.Sexo
import kotlinx.serialization.Serializable

/**
 * User response DTO - returned by GET endpoints
 * Based on PRD profile payload
 */
@Serializable
data class UserResponse(
    val id: String,
    val idNumber: String,
    val nombres: String,
    val apellidos: String,
    val edad: Int,
    val sexo: Sexo,
    val residencia: String? = null,
    val rol: Rol,
    val login: Boolean = false
)

/**
 * Extension function to convert UserDocument to UserResponse
 */
fun com.betha.common.document.UserDocument.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id ?: "",
        idNumber = this.idNumber,
        nombres = this.nombres,
        apellidos = this.apellidos,
        edad = this.edad,
        sexo = this.sexo,
        residencia = this.residencia,
        rol = this.rol,
        login = this.login
    )
}