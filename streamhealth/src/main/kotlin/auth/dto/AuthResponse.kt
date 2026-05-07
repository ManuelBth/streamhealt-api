package com.betha.auth.dto

import com.betha.common.document.Rol
import kotlinx.serialization.Serializable


@Serializable
data class UserInfo(
    val id: String,
    val nombres: String,
    val apellidos: String,
    val rol: Rol
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserInfo
)