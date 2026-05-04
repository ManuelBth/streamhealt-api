package com.betha.user.repository

import com.betha.common.document.UserDocument

/**
 * Repository interface for user operations
 */
interface UserRepository {
    /**
     * Find user by ID
     * @param id User ID (MongoDB ObjectId as String)
     * @return UserDocument if found, null otherwise
     */
    suspend fun findById(id: String): UserDocument?

    /**
     * Find user by ID Number (cedula)
     * @param idNumber User's identification number
     * @return UserDocument if found, null otherwise
     */
    suspend fun findByIdNumber(idNumber: String): UserDocument?

    /**
     * Update user document by ID Number
     * @param idNumber User's identification number
     * @param user Updated user document
     * @return Updated UserDocument if found, null otherwise
     */
    suspend fun updateByIdNumber(idNumber: String, user: UserDocument): UserDocument?

    /**
     * Update user document
     * @param id User ID
     * @param user Updated user document
     * @return Updated UserDocument if found, null otherwise
     */
    suspend fun update(id: String, user: UserDocument): UserDocument?
}