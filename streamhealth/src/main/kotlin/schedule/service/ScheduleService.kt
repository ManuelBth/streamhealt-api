package com.betha.schedule.service

import com.betha.schedule.dto.AppointmentResponse
import com.betha.schedule.dto.CreateAppointmentRequest
import com.betha.schedule.dto.UpdateAppointmentRequest

/**
 * Service interface for schedule/appointment operations
 */
interface ScheduleService {
    /**
     * Create a new appointment
     * @param request Appointment creation data
     * @return AppointmentResponse if valid, null otherwise
     */
    suspend fun createAppointment(request: CreateAppointmentRequest): AppointmentResponse?

    /**
     * Get all appointments for a patient
     * @param patientId Patient's identification number (cédula)
     * @return List of AppointmentResponses
     */
    suspend fun getPatientAppointments(patientId: String): List<AppointmentResponse>

    /**
     * Get all appointments for a doctor
     * @param doctorId Doctor's identification number (cédula)
     * @return List of AppointmentResponses
     */
    suspend fun getDoctorAppointments(doctorId: String): List<AppointmentResponse>

    /**
     * Update an appointment by patient (can only cancel)
     * @param appointmentId Appointment ID
     * @param request Update request with status
     * @return Updated AppointmentResponse if valid, null otherwise
     */
    suspend fun updatePatientAppointment(appointmentId: String, request: UpdateAppointmentRequest): AppointmentResponse?

    /**
     * Update an appointment by doctor (can confirm, cancel, complete)
     * @param appointmentId Appointment ID
     * @param request Update request with status
     * @return Updated AppointmentResponse if valid, null otherwise
     */
    suspend fun updateDoctorAppointment(appointmentId: String, request: UpdateAppointmentRequest): AppointmentResponse?

    /**
     * Delete/cancel an appointment
     * @param appointmentId Appointment ID
     * @return true if deleted, false otherwise
     */
    suspend fun deleteAppointment(appointmentId: String): Boolean
}