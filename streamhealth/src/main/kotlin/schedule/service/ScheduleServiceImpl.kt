package com.betha.schedule.service

import com.betha.common.document.Rol
import com.betha.common.document.UserDocument
import com.betha.schedule.document.AppointmentDocument
import com.betha.schedule.dto.AppointmentResponse
import com.betha.schedule.dto.CreateAppointmentRequest
import com.betha.schedule.dto.UpdateAppointmentRequest
import com.betha.schedule.dto.toAppointmentResponse
import com.betha.schedule.repository.ScheduleRepository
import com.betha.user.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Schedule service implementation
 */
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository,
    private val userRepository: UserRepository
) : ScheduleService {

    companion object {
        private val log = LoggerFactory.getLogger(ScheduleServiceImpl::class.java)

        // Estados válidos por rol
        val PACIENTE_ESTADOS_VALIDOS = setOf("cancelada")
        val DOCTOR_ESTADOS_VALIDOS = setOf("confirmada", "cancelada", "completada")
    }

    override suspend fun createAppointment(request: CreateAppointmentRequest): AppointmentResponse? = withContext(Dispatchers.IO) {
        log.info("Creating appointment for patient: {} with doctor: {}", request.patientId, request.doctorId)

        // Validar doctor existe y tiene rol DOCTOR
        val doctor = userRepository.findByIdNumber(request.doctorId)
            ?: throw IllegalArgumentException("Doctor no encontrado")
        if (doctor.rol != Rol.DOCTOR) {
            throw IllegalArgumentException("El usuario no es un doctor válido")
        }

        // Validar paciente existe y tiene rol PACIENTE
        val patient = userRepository.findByIdNumber(request.patientId)
            ?: throw IllegalArgumentException("Paciente no encontrado")
        if (patient.rol != Rol.PACIENTE) {
            throw IllegalArgumentException("El usuario no es un paciente válido")
        }

        // Validar fecha es en el futuro
        validateFutureDate(request.fecha)

        // Crear cita
        val appointment = AppointmentDocument(
            patientId = request.patientId,
            doctorId = request.doctorId,
            fecha = request.fecha,
            motivo = request.motivo,
            estado = "pendiente"
        )

        val saved = scheduleRepository.save(appointment)
        saved.toAppointmentResponse()
    }

    override suspend fun getPatientAppointments(patientId: String): List<AppointmentResponse> = withContext(Dispatchers.IO) {
        log.info("Getting appointments for patient: {}", patientId)

        // Validar paciente existe
        val patient = userRepository.findByIdNumber(patientId)
            ?: throw IllegalArgumentException("Paciente no encontrado")

        val appointments = scheduleRepository.findByPatientId(patientId)
        appointments.map { it.toAppointmentResponse() }
    }

    override suspend fun getDoctorAppointments(doctorId: String): List<AppointmentResponse> = withContext(Dispatchers.IO) {
        log.info("Getting appointments for doctor: {}", doctorId)

        // Validar doctor existe
        val doctor = userRepository.findByIdNumber(doctorId)
            ?: throw IllegalArgumentException("Doctor no encontrado")
        if (doctor.rol != Rol.DOCTOR) {
            throw IllegalArgumentException("El usuario no es un doctor válido")
        }

        val appointments = scheduleRepository.findByDoctorId(doctorId)
        appointments.map { it.toAppointmentResponse() }
    }

    override suspend fun updatePatientAppointment(appointmentId: String, request: UpdateAppointmentRequest): AppointmentResponse? = withContext(Dispatchers.IO) {
        log.info("Patient updating appointment: {}", appointmentId)

        // Buscar cita existente
        val existing = scheduleRepository.findById(appointmentId)
            ?: throw IllegalArgumentException("Cita no encontrada")

        // Paciente solo puede cancelar
        val nuevoEstado = request.estado ?: existing.estado
        if (nuevoEstado != existing.estado) {
            if (nuevoEstado !in PACIENTE_ESTADOS_VALIDOS) {
                throw IllegalArgumentException("El paciente solo puede cancelar la cita. Estados permitidos: $PACIENTE_ESTADOS_VALIDOS")
            }
        }

        val updated = existing.copy(
            estado = nuevoEstado,
            updatedAt = System.currentTimeMillis()
        )

        val result = scheduleRepository.update(appointmentId, updated)
            ?: throw IllegalArgumentException("Error al actualizar la cita")

        result.toAppointmentResponse()
    }

    override suspend fun updateDoctorAppointment(appointmentId: String, request: UpdateAppointmentRequest): AppointmentResponse? = withContext(Dispatchers.IO) {
        log.info("Doctor updating appointment: {}", appointmentId)

        // Buscar cita existente
        val existing = scheduleRepository.findById(appointmentId)
            ?: throw IllegalArgumentException("Cita no encontrada")

        // Doctor puede confirmar, cancelar o completar
        val nuevoEstado = request.estado ?: existing.estado
        if (nuevoEstado != existing.estado) {
            if (nuevoEstado !in DOCTOR_ESTADOS_VALIDOS) {
                throw IllegalArgumentException("El doctor solo puede confirmar, cancelar o completar la cita. Estados permitidos: $DOCTOR_ESTADOS_VALIDOS")
            }
        }

        val updated = existing.copy(
            estado = nuevoEstado,
            updatedAt = System.currentTimeMillis()
        )

        val result = scheduleRepository.update(appointmentId, updated)
            ?: throw IllegalArgumentException("Error al actualizar la cita")

        result.toAppointmentResponse()
    }

    override suspend fun deleteAppointment(appointmentId: String): Boolean = withContext(Dispatchers.IO) {
        log.info("Deleting appointment: {}", appointmentId)

        // Validar que la cita existe
        val existing = scheduleRepository.findById(appointmentId)
            ?: throw IllegalArgumentException("Cita no encontrada")

        scheduleRepository.delete(appointmentId)
    }

    /**
     * Validar que la fecha es en el futuro
     * Acepta formatos ISO8601: "2024-12-15T10:00:00Z", "2024-12-15T10:00:00-03:00", "2024-12-15T10:00:00"
     */
    private fun validateFutureDate(fecha: String) {
        try {
            // Intentar parser flexible para ISO8601
            val dateTime = parseIso8601DateTime(fecha)
            if (dateTime.isBefore(ZonedDateTime.now())) {
                throw IllegalArgumentException("La fecha debe ser en el futuro")
            }
        } catch (e: IllegalArgumentException) {
            throw e  // Re-lanzar mensajes de error ya formateados
        } catch (e: Exception) {
            throw IllegalArgumentException("Formato de fecha inválido. Use ISO8601 (ej: 2024-12-31T10:00:00Z o 2024-12-31T10:00:00-03:00)")
        }
    }

    /**
     * Parse flexible para fechas ISO8601
     * Acepta: Z, +00:00, -03:00, o sin timezone
     */
    private fun parseIso8601DateTime(fecha: String): ZonedDateTime {
        return when {
            // Termina en Z (UTC)
            fecha.endsWith("Z") -> {
                ZonedDateTime.parse(fecha.replace("Z", "+00:00"))
            }
            // Tiene offset como +00:00 o -03:00
            fecha.contains(Regex("([+-]\\d{2}:?\\d{2})$")) -> {
                ZonedDateTime.parse(fecha)
            }
            // Sin timezone - asumir hora local
            else -> {
                ZonedDateTime.of(
                    java.time.LocalDateTime.parse(fecha),
                    java.time.ZoneId.systemDefault()
                )
            }
        }
    }
}