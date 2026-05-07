@file:Suppress("DEPRECATION")

package com.betha.auth.controller

import com.betha.auth.dto.AuthResponse
import com.betha.auth.dto.LoginRequest
import com.betha.auth.dto.RegisterRequest
import com.betha.auth.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.openapi.describe

/**
 * Auth controller with login and register routes
 *
 * Endpoints:
 * - POST /api/v1/auth/login - User login with credentials
 * - POST /api/v1/auth/register - User registration
 *
 * @see LoginRequest for login credentials structure
 * @see RegisterRequest for registration fields
 * @see AuthResponse for response structure
 */
fun Routing.authController(authService: AuthService) {
    route("/api/v1/auth") {
        /**
         * User login endpoint
         *
         * Authenticate user and return JWT token
         *
         * @param LoginRequest id - User identifier/username
         * @param LoginRequest password - User password
         * @return AuthResponse with JWT token and user info
         */
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val response = authService.login(request)
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = mapOf("error" to (e.message ?: "Credenciales inválidas"))
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (e.message ?: "Error interno"))
                )
            }
        }.describe {
            summary = "User Login"
            description = "Authenticate user with cedula and password"
            responses {
                HttpStatusCode.OK {
                    description = "Login successful"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid credentials"
                }
            }
        }

        /**
         * User registration endpoint
         *
         * Register a new user in the system
         *
         * @param RegisterRequest User registration data (idNumber, nombres, apellidos, edad, sexo, residencia, password, rol)
         * @return AuthResponse with JWT token and user info
         */
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val response = authService.register(request)
                call.respond(response)
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
            summary = "User Registration"
            description = "Register a new user in the system"
            responses {
                HttpStatusCode.Created {
                    description = "User registered successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input"
                }
            }
        }
    }
}