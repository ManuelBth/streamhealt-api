package com.betha.prescription.controller

import com.betha.auth.service.AuthService
import com.betha.prescription.dto.CreatePrescriptionRequest
import com.betha.prescription.dto.UpdatePrescriptionRequest
import com.betha.prescription.service.PrescriptionService
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
 * Prescription controller for managing medical prescriptions
 *
 * Endpoints:
 * - POST /api/v1/prescriptions - Create prescription
 * - GET /api/v1/prescriptions/patient/{patientId} - Get patient prescriptions
 * - GET /api/v1/prescriptions/{prescriptionId} - Get prescription by ID
 * - PUT /api/v1/prescriptions/{prescriptionId} - Update prescription
 * - DELETE /api/v1/prescriptions/{prescriptionId} - Delete prescription
 */
fun Routing.prescriptionController(
    prescriptionService: PrescriptionService,
    authService: AuthService
) {
    route("/api/v1/prescriptions") {
        /**
         * Create prescription (POST /api/v1/prescriptions)
         */
        post {
            try {
                val request = call.receive<CreatePrescriptionRequest>()
                val response = prescriptionService.createPrescription(request)

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
            summary = "Create prescription"
            description = "Create a new medical prescription"
            responses {
                HttpStatusCode.Created {
                    description = "Prescription created successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input or validation error"
                }
            }
        }

        /**
         * Get patient prescriptions (GET /api/v1/prescriptions/patient/{patientId})
         */
        get("/patient/{patientId}") {
            try {
                val patientId = call.parameters["patientId"]
                    ?: throw IllegalArgumentException("patientId requerido")

                val response = prescriptionService.getByPatientId(patientId)
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
            summary = "Get patient prescriptions"
            description = "Retrieve all prescriptions for a specific patient"
            responses {
                HttpStatusCode.OK {
                    description = "List of prescriptions"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid request parameters"
                }
            }
        }

        /**
         * Get prescription by ID (GET /api/v1/prescriptions/{prescriptionId})
         */
        get("/{prescriptionId}") {
            try {
                val prescriptionId = call.parameters["prescriptionId"]
                    ?: throw IllegalArgumentException("prescriptionId requerido")

                val response = prescriptionService.getByPrescriptionId(prescriptionId)
                if (response != null) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = mapOf("error" to "Receta no encontrada")
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
            summary = "Get prescription by ID"
            description = "Retrieve a specific prescription by its unique ID"
            responses {
                HttpStatusCode.OK {
                    description = "Prescription found"
                }
                HttpStatusCode.NotFound {
                    description = "Prescription not found"
                }
            }
        }

        /**
         * Update prescription (PUT /api/v1/prescriptions/{prescriptionId})
         */
        put("/{prescriptionId}") {
            try {
                val prescriptionId = call.parameters["prescriptionId"]
                    ?: throw IllegalArgumentException("prescriptionId requerido")
                val request = call.receive<UpdatePrescriptionRequest>()

                val response = prescriptionService.updatePrescription(prescriptionId, request)
                if (response != null) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = mapOf("error" to "Receta no encontrada")
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
            summary = "Update prescription"
            description = "Update an existing prescription"
            responses {
                HttpStatusCode.OK {
                    description = "Prescription updated successfully"
                }
                HttpStatusCode.BadRequest {
                    description = "Invalid input"
                }
                HttpStatusCode.NotFound {
                    description = "Prescription not found"
                }
            }
        }

        /**
         * Delete prescription (DELETE /api/v1/prescriptions/{prescriptionId})
         */
        delete("/{prescriptionId}") {
            try {
                val prescriptionId = call.parameters["prescriptionId"]
                    ?: throw IllegalArgumentException("prescriptionId requerido")

                val deleted = prescriptionService.deletePrescription(prescriptionId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = mapOf("error" to "Receta no encontrada")
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
            summary = "Delete prescription"
            description = "Delete a prescription record"
            responses {
                HttpStatusCode.NoContent {
                    description = "Prescription deleted successfully"
                }
                HttpStatusCode.NotFound {
                    description = "Prescription not found"
                }
            }
        }
    }
}