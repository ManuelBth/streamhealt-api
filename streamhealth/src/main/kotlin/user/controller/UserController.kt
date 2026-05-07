package com.betha.user.controller

import com.betha.auth.service.AuthService
import com.betha.user.dto.UpdateUserRequest
import com.betha.user.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.openapi.describe

/**
 * User controller with profile routes
 *
 * Endpoints:
 * - GET /api/v1/user/me - Get current user's profile (requires JWT auth)
 * - GET /api/v1/user/{userId} - Get specific user profile (requires JWT auth)
 * - PUT /api/v1/user/me - Update own profile (only own profile)
 * - PUT /api/v1/user/{userId} - Update profile by ID
 *
 * @see UserResponse for response structure
 * @see UpdateUserRequest for update fields
 */
fun Routing.userController(
    userService: UserService,
    authService: AuthService
) {
    route("/api/v1/user") {
        /**
         * Get current user profile
         *
         * Retrieves the profile of the authenticated user based on JWT token
         *
         * @header Authorization Bearer JWT token
         * @return UserResponse with user profile data
         */
        get("/me") {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                // Use idNumber from token to get profile
                val user = userService.getUserByIdNumber(userInfo.id)
                    ?: throw IllegalArgumentException("Usuario no encontrado")

                call.respond(user)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = mapOf("error" to (e.message ?: "Error de autenticación"))
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (e.message ?: "Error interno"))
                )
            }
        }.describe {
            summary = "Get current user profile"
            description = "Retrieves the profile of the authenticated user based on JWT token"
            responses {
                HttpStatusCode.OK {
                    description = "User profile retrieved successfully"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
            }
        }

        /**
         * Get user profile by ID
         *
         * Retrieves a specific user's profile by their ID
         *
         * @param userId Path parameter - The unique identifier of the user (cedula)
         * @header Authorization Bearer JWT token
         * @return UserResponse with user profile data
         */
        get("/{userId}") {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                val userId = call.parameters["userId"]
                    ?: throw IllegalArgumentException("ID de usuario requerido")

                val user = userService.getUserByIdNumber(userId)
                    ?: throw IllegalArgumentException("Usuario no encontrado")

                call.respond(user)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = mapOf("error" to (e.message ?: "Error de autenticación"))
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (e.message ?: "Error interno"))
                )
            }
        }.describe {
            summary = "Get user by ID Number"
            description = "Retrieves a specific user's profile by their identification number (cedula)"
            responses {
                HttpStatusCode.OK {
                    description = "User profile found"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
                HttpStatusCode.NotFound {
                    description = "User not found"
                }
            }
        }

        /**
         * Update user profile
         *
         * Updates the authenticated user's own profile
         * Users can only update their own profile
         *
         * @param UpdateUserRequest Optional fields: nombres, apellidos, sexo, residencia
         * @header Authorization Bearer JWT token
         * @return UserResponse with updated profile data
         */
        put("/me") {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                val request = call.receive<UpdateUserRequest>()
                val user = userService.updateUserByIdNumber(userInfo.id, request)
                    ?: throw IllegalArgumentException("Usuario no encontrado")

                call.respond(user)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to (e.message ?: "Error de validación"))
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (e.message ?: "Error interno"))
                )
            }
        }.describe {
            summary = "Update current user profile"
            description = "Updates the authenticated user's own profile"
            responses {
                HttpStatusCode.OK {
                    description = "Profile updated successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
            }
        }

        /**
         * Update user profile by ID (for admins or specific lookups)
         */
        put("/{userId}") {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                val targetUserId = call.parameters["userId"]
                    ?: throw IllegalArgumentException("ID de usuario requerido")

                if (userInfo.id != targetUserId) {
                    throw IllegalArgumentException("No puedes actualizar el perfil de otro usuario")
                }

                val request = call.receive<UpdateUserRequest>()
                val user = userService.updateUser(targetUserId, request)
                    ?: throw IllegalArgumentException("Usuario no encontrado")

                call.respond(user)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to (e.message ?: "Error de validación"))
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (e.message ?: "Error interno"))
                )
            }
        }.describe {
            summary = "Update user profile by ID"
            description = "Updates a specific user's profile by their identification number"
            responses {
                HttpStatusCode.OK {
                    description = "Profile updated successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input or unauthorized"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
                HttpStatusCode.NotFound {
                    description = "User not found"
                }
            }
        }
    }
}