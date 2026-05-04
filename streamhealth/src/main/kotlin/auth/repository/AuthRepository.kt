package com.betha.auth.repository

import com.betha.common.document.UserDocument

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Find user by idNumber (used as login identifier per PRD)
     * @param idNumber The user's idNumber/login
     * @return UserDocument if found, null otherwise
     */
    suspend fun findByIdNumber(idNumber: String): UserDocument?

    /**
     * Save a user document
     * @param user User document to save
     * @return Saved user document with generated ID
     */
    suspend fun save(user: UserDocument): UserDocument

    /**
     * Check if a user with the given idNumber exists
     * @param idNumber The idNumber to check
     * @return true if exists, false otherwise
     */
    suspend fun existsByIdNumber(idNumber: String): Boolean
}