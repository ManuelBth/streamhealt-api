package com.betha.auth.dto

import kotlinx.serialization.Serializable

/**
 * Login request DTO (PRD: uses "id" field)
 */
@Serializable
data class LoginRequest(
    val id: String,
    val password: String
)