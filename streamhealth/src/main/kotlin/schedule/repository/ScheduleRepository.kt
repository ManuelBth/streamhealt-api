package com.betha.schedule.repository

import com.betha.schedule.document.AppointmentDocument

/**
 * Repository interface for appointment (schedule) operations
 */
interface ScheduleRepository {
    /**
     * Find appointment by MongoDB ID
     * @param id MongoDB ObjectId as String
     * @return AppointmentDocument if found, null otherwise
     */
    suspend fun findById(id: String): AppointmentDocument?

    /**
     * Find appointment by compound appointment ID
     * @param appointmentId Compound appointment ID (e.g., "20241215-ABC123")
     * @return AppointmentDocument if found, null otherwise
     */
    suspend fun findByAppointmentId(appointmentId: String): AppointmentDocument?

    /**
     * Find all appointments for a patient
     * @param patientId Patient's identification number (cédula)
     * @return List of AppointmentDocuments
     */
    suspend fun findByPatientId(patientId: String): List<AppointmentDocument>

    /**
     * Find all appointments for a doctor
     * @param doctorId Doctor's identification number (cédula)
     * @return List of AppointmentDocuments
     */
    suspend fun findByDoctorId(doctorId: String): List<AppointmentDocument>

    /**
     * Save a new appointment
     * @param appointment AppointmentDocument to save (with appointmentId already generated)
     * @return Saved AppointmentDocument
     */
    suspend fun save(appointment: AppointmentDocument): AppointmentDocument

    /**
     * Update an existing appointment
     * @param appointmentId Compound appointment ID
     * @param appointment Updated AppointmentDocument
     * @return Updated AppointmentDocument if found, null otherwise
     */
    suspend fun update(appointmentId: String, appointment: AppointmentDocument): AppointmentDocument?

    /**
     * Delete an appointment
     * @param appointmentId Compound appointment ID
     * @return true if deleted, false otherwise
     */
    suspend fun delete(appointmentId: String): Boolean
}