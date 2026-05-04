package com.betha.common.security

import org.mindrot.jbcrypt.BCrypt

/**
 * Service interface for password hashing and verification
 */
interface PasswordService {
    /**
     * Hash a password using BCrypt
     * @param password Plain text password
     * @return BCrypt hashed password
     */
    fun hash(password: String): String

    /**
     * Verify a password against a hash
     * @param password Plain text password
     * @param hash BCrypt hash to verify against
     * @return true if password matches, false otherwise
     */
    fun verify(password: String, hash: String): Boolean
}

/**
 * BCrypt implementation of PasswordService
 */
class BcryptPasswordService : PasswordService {

    override fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    override fun verify(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            false
        }
    }
}