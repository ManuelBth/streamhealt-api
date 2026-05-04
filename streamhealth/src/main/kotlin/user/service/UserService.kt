package com.betha.user.service

import com.betha.user.dto.UpdateUserRequest
import com.betha.user.dto.UserResponse

/**
 * Service interface for user operations
 */
interface UserService {
    /**
     * Get user by ID (MongoDB _id)
     * @param userId MongoDB ObjectId
     * @return UserResponse if found, null otherwise
     */
    suspend fun getUserById(userId: String): UserResponse?

    /**
     * Get user by ID Number (cedula)
     * @param idNumber User's identification number
     * @return UserResponse if found, null otherwise
     */
    suspend fun getUserByIdNumber(idNumber: String): UserResponse?

    /**
     * Update user profile by ID Number
     * @param idNumber User's identification number
     * @param request Update request with fields to update
     * @return Updated UserResponse if found, null otherwise
     */
    suspend fun updateUserByIdNumber(idNumber: String, request: UpdateUserRequest): UserResponse?

    /**
     * Update user profile
     * @param userId User ID
     * @param request Update request with fields to update
     * @return Updated UserResponse if found, null otherwise
     */
    suspend fun updateUser(userId: String, request: UpdateUserRequest): UserResponse?
}