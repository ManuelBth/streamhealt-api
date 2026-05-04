package com.betha.user.service

import com.betha.common.document.UserDocument
import com.betha.user.dto.UpdateUserRequest
import com.betha.user.dto.UserResponse
import com.betha.user.dto.toUserResponse
import com.betha.user.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

/**
 * User service implementation
 */
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    companion object {
        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

    override suspend fun getUserById(userId: String): UserResponse? = withContext(Dispatchers.IO) {
        log.info("Getting profile for userId: {}", userId)
        
        val user = userRepository.findById(userId)
        user?.toUserResponse()
    }

    override suspend fun getUserByIdNumber(idNumber: String): UserResponse? = withContext(Dispatchers.IO) {
        log.info("Getting profile for idNumber: {}", idNumber)
        
        val user = userRepository.findByIdNumber(idNumber)
        user?.toUserResponse()
    }

    override suspend fun updateUserByIdNumber(idNumber: String, request: UpdateUserRequest): UserResponse? = withContext(Dispatchers.IO) {
        log.info("Updating profile for idNumber: {}", idNumber)
        
        // Find existing user by idNumber
        val existingUser = userRepository.findByIdNumber(idNumber)
            ?: throw IllegalArgumentException("Usuario no encontrado")

        // Build updated user document (partial update)
        val updatedUser = existingUser.copy(
            nombres = request.nombres ?: existingUser.nombres,
            apellidos = request.apellidos ?: existingUser.apellidos,
            edad = request.edad ?: existingUser.edad,
            sexo = request.sexo ?: existingUser.sexo,
            residencia = request.residencia ?: existingUser.residencia,
            updatedAt = System.currentTimeMillis()
        )

        // Update in repository using idNumber directly
        val result = userRepository.updateByIdNumber(idNumber, updatedUser)
        result?.toUserResponse()
    }

    override suspend fun updateUser(userId: String, request: UpdateUserRequest): UserResponse? = withContext(Dispatchers.IO) {
        log.info("Updating profile for userId: {}", userId)
        
        // Find existing user
        val existingUser = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")

        // Build updated user document (partial update)
        val updatedUser = existingUser.copy(
            nombres = request.nombres ?: existingUser.nombres,
            apellidos = request.apellidos ?: existingUser.apellidos,
            edad = request.edad ?: existingUser.edad,
            sexo = request.sexo ?: existingUser.sexo,
            residencia = request.residencia ?: existingUser.residencia,
            updatedAt = System.currentTimeMillis()
        )

        // Update in repository
        val result = userRepository.update(userId, updatedUser)
        result?.toUserResponse()
    }
}