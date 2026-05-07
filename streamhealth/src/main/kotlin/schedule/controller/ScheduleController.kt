package com.betha.schedule.controller

import com.betha.auth.service.AuthService
import com.betha.schedule.dto.CreateAppointmentRequest
import com.betha.schedule.dto.UpdateAppointmentRequest
import com.betha.schedule.service.ScheduleService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.openapi.describe

/**
 * Schedule controller for patient and doctor appointment management
 *
 * Patient endpoints:
 * - POST /api/v1/schedule/patient - Create appointment
 * - GET /api/v1/schedule/patient/{idNumber} - Get patient appointments
 * - PUT /api/v1/schedule/patient/{appointmentId} - Update (cancel) appointment
 * - DELETE /api/v1/schedule/patient/{appointmentId} - Cancel appointment
 *
 * Doctor endpoints:
 * - GET /api/v1/schedule/doctor/{idNumber} - Get doctor appointments
 * - PUT /api/v1/schedule/doctor/{appointmentId} - Update (confirm/complete) appointment
 */
fun Routing.scheduleController(
    scheduleService: ScheduleService,
    authService: AuthService
) {
    // =====================
    // ENDPOINTS DE PACIENTE
    // =====================

    route("/api/v1/schedule/patient") {
        /**
         * Create appointment (POST /api/v1/schedule/patient)
         */
        post {
            try {
                val request = call.receive<CreateAppointmentRequest>()
                val response = scheduleService.createAppointment(request)
                    ?: throw IllegalArgumentException("Error al crear la cita")

                call.respond(HttpStatusCode.Created, response)
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
            summary = "Create appointment"
            description = "Patient books an appointment with a doctor"
            responses {
                HttpStatusCode.Created {
                    description = "Appointment created successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input or validation error"
                }
            }
        }

        /**
         * Get patient appointments (GET /api/v1/schedule/patient/{idNumber})
         */
        get("/{idNumber}") {
            try {
                val idNumber = call.parameters["idNumber"]
                    ?: throw IllegalArgumentException("idNumber requerido")

                val response = scheduleService.getPatientAppointments(idNumber)
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
            summary = "Get patient appointments"
            description = "Retrieve all appointments for a specific patient"
            responses {
                HttpStatusCode.OK {
                    description = "List of patient appointments"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid request parameters"
                }
            }
        }

        /**
         * Update (cancel) appointment (PUT /api/v1/schedule/patient/{appointmentId})
         */
        put("/{appointmentId}") {
            try {
                val appointmentId = call.parameters["appointmentId"]
                    ?: throw IllegalArgumentException("appointmentId requerido")
                val request = call.receive<UpdateAppointmentRequest>()

                val response = scheduleService.updatePatientAppointment(appointmentId, request)
                if (response != null) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = mapOf("error" to "Cita no encontrada")
                    )
                }
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
            summary = "Update patient appointment"
            description = "Patient cancels their own appointment"
            responses {
                HttpStatusCode.OK {
                    description = "Appointment updated successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid request"
                }
                HttpStatusCode.NotFound {
                    description = "Appointment not found"
                }
            }
        }

        /**
         * Cancel appointment (DELETE /api/v1/schedule/patient/{appointmentId})
         */
        delete("/{appointmentId}") {
            try {
                val appointmentId = call.parameters["appointmentId"]
                    ?: throw IllegalArgumentException("appointmentId requerido")

                val deleted = scheduleService.deleteAppointment(appointmentId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = mapOf("error" to "Cita no encontrada")
                    )
                }
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
            summary = "Delete appointment"
            description = "Patient deletes their own appointment"
            responses {
                HttpStatusCode.NoContent {
                    description = "Appointment deleted successfully"
                }
                HttpStatusCode.NotFound {
                    description = "Appointment not found"
                }
            }
        }
    }

    // =====================
    // ENDPOINTS DE DOCTOR
    // =====================

    route("/api/v1/schedule/doctor") {
        /**
         * Get doctor appointments (GET /api/v1/schedule/doctor/{idNumber})
         */
        get("/{idNumber}") {
            try {
                val idNumber = call.parameters["idNumber"]
                    ?: throw IllegalArgumentException("idNumber requerido")

                val response = scheduleService.getDoctorAppointments(idNumber)
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
            summary = "Get doctor appointments"
            description = "Retrieve all appointments for a specific doctor"
            responses {
                HttpStatusCode.OK {
                    description = "List of doctor appointments"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid request parameters"
                }
            }
        }

        /**
         * Update (confirm/complete/cancel) appointment (PUT /api/v1/schedule/doctor/{appointmentId})
         */
        put("/{appointmentId}") {
            try {
                val appointmentId = call.parameters["appointmentId"]
                    ?: throw IllegalArgumentException("appointmentId requerido")
                val request = call.receive<UpdateAppointmentRequest>()

                val response = scheduleService.updateDoctorAppointment(appointmentId, request)
                if (response != null) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = mapOf("error" to "Cita no encontrada")
                    )
                }
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
            summary = "Update doctor appointment"
            description = "Doctor updates appointment status (confirm, complete, cancel)"
            responses {
                HttpStatusCode.OK {
                    description = "Appointment updated successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid request"
                }
                HttpStatusCode.NotFound {
                    description = "Appointment not found"
                }
            }
        }
    }
}