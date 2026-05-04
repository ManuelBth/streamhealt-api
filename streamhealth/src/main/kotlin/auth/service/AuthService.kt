package com.betha.auth.service

import com.betha.auth.dto.AuthResponse
import com.betha.auth.dto.LoginRequest
import com.betha.auth.dto.RegisterRequest
import com.betha.auth.dto.UserInfo

/**
 * Service interface for authentication operations
 */
interface AuthService {
    /**
     * Authenticate a user with email and password
     * @param request Login credentials
     * @return AuthResponse with token and user info
     */
    suspend fun login(request: LoginRequest): AuthResponse

    /**
     * Register a new user
     * @param request Registration data
     * @return AuthResponse with token and user info
     */
    suspend fun register(request: RegisterRequest): AuthResponse

    /**
     * Validate a JWT token and return user info
     * @param token JWT token to validate
     * @return UserInfo if valid, null otherwise
     */
    suspend fun validateToken(token: String): UserInfo?
}