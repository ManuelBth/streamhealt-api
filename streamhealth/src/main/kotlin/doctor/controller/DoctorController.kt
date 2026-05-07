package com.betha.doctor.controller

import com.betha.auth.service.AuthService
import com.betha.common.document.Rol
import com.betha.doctor.dto.CreateDoctorProfileRequest
import com.betha.doctor.dto.UpdateDoctorProfileRequest
import com.betha.doctor.service.DoctorService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.openapi.describe

/**
 * Doctor controller with professional profile routes
 *
 * Endpoints:
 * - GET /api/v1/doctors - List all doctors (requires JWT auth)
 * - GET /api/v1/doctors/{idNumber} - Get doctor by ID Number/Cédula (requires JWT auth)
 * - POST /api/v1/doctors - Create doctor profile (DOCTOR role only)
 * - PUT /api/v1/doctors - Update doctor profile (DOCTOR role only)
 *
 * @see DoctorResponse for response structure
 * @see CreateDoctorProfileRequest for creation fields
 * @see UpdateDoctorProfileRequest for update fields
 */
fun Routing.doctorController(
    doctorService: DoctorService,
    authService: AuthService
) {
    route("/api/v1/doctors") {
        /**
         * List all doctors
         *
         * Retrieves all doctors with their professional profiles
         *
         * @header Authorization Bearer JWT token
         * @return List of DoctorResponse
         */
        get {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                val doctors = doctorService.getAllDoctors()
                call.respond(doctors)
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
            summary = "List all doctors"
            description = "Retrieves all doctors with their professional profiles"
            responses {
                HttpStatusCode.OK {
                    description = "List of doctors retrieved successfully"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
            }
        }

        /**
         * Get doctor by ID Number (Cédula)
         *
         * Retrieves a specific doctor's profile by their user ID (cedula)
         *
         * @param idNumber Path parameter - The user's identification number (Cédula)
         * @header Authorization Bearer JWT token
         * @return DoctorResponse with doctor profile data
         */
        get("/{idNumber}") {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                val idNumber = call.parameters["idNumber"]
                    ?: throw IllegalArgumentException("ID de doctor requerido")

                val doctor = doctorService.getDoctorByUserId(idNumber)
                    ?: throw IllegalArgumentException("Doctor no encontrado")

                call.respond(doctor)
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
            summary = "Get doctor by ID Number"
            description = "Retrieves a specific doctor's profile by their identification number (cedula)"
            responses {
                HttpStatusCode.OK {
                    description = "Doctor profile found"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
                HttpStatusCode.NotFound {
                    description = "Doctor not found"
                }
            }
        }

        /**
         * Create doctor profile
         *
         * Creates a professional doctor profile for the authenticated user
         * Only available for users with DOCTOR role
         *
         * @param CreateDoctorProfileRequest Optional: titulo, universidad, especialidades, doctorados, licencia, telefono, direccion
         * @header Authorization Bearer JWT token (must be DOCTOR role)
         * @return DoctorResponse with doctor profile data
         */
        post {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                if (userInfo.rol != Rol.DOCTOR) {
                    throw IllegalArgumentException("Solo los doctores pueden crear un perfil profesional")
                }

                val request = call.receive<CreateDoctorProfileRequest>()
                val doctor = doctorService.createDoctorProfile(userInfo.id, request)
                    ?: throw IllegalArgumentException("Error al crear el perfil profesional")

                call.respond(HttpStatusCode.Created, doctor)
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
            summary = "Create doctor profile"
            description = "Creates a professional doctor profile (requires DOCTOR role)"
            responses {
                HttpStatusCode.Created {
                    description = "Doctor profile created successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
                HttpStatusCode.Forbidden {
                    description = "Requires DOCTOR role"
                }
            }
        }

        /**
         * Update doctor profile
         *
         * Updates the professional doctor profile of the authenticated user
         * Only available for users with DOCTOR role
         *
         * @param UpdateDoctorProfileRequest Optional fields: titulo, universidad, especialidades, doctorados, licencia, telefono, direccion
         * @header Authorization Bearer JWT token (must be DOCTOR role)
         * @return DoctorResponse with updated doctor profile data
         */
        put {
            try {
                val authHeader = call.request.headers["Authorization"]
                    ?: throw IllegalArgumentException("Token requerido")

                val token = authHeader.removePrefix("Bearer ")
                val userInfo = authService.validateToken(token)
                    ?: throw IllegalArgumentException("Token inválido")

                if (userInfo.rol != Rol.DOCTOR) {
                    throw IllegalArgumentException("Solo los doctores pueden actualizar un perfil profesional")
                }

                val request = call.receive<UpdateDoctorProfileRequest>()
                val doctor = doctorService.updateDoctorProfile(userInfo.id, request)
                    ?: throw IllegalArgumentException("Error al actualizar el perfil profesional")

                call.respond(doctor)
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
            summary = "Update doctor profile"
            description = "Updates the professional doctor profile (requires DOCTOR role)"
            responses {
                HttpStatusCode.OK {
                    description = "Doctor profile updated successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input"
                }
                HttpStatusCode.Unauthorized {
                    description = "Invalid or missing token"
                }
                HttpStatusCode.Forbidden {
                    description = "Requires DOCTOR role"
                }
                HttpStatusCode.NotFound {
                    description = "Doctor profile not found"
                }
            }
        }
    }
}