package com.betha.user.dto

import kotlinx.serialization.Serializable

/**
 * Update user request DTO - used in PUT /{userId}
 * All fields are optional to allow partial updates
 */
@Serializable
data class UpdateUserRequest(
    val nombres: String? = null,
    val apellidos: String? = null,
    val edad: Int? = null,
    val sexo: com.betha.common.document.Sexo? = null,
    val residencia: String? = null
)