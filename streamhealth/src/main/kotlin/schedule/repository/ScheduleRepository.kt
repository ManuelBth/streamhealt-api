package com.betha.schedule.repository

import com.betha.schedule.document.AppointmentDocument

/**
 * Repository interface for appointment (schedule) operations
 */
interface ScheduleRepository {
    /**
     * Find appointment by ID
     * @param id Appointment ID (MongoDB ObjectId as String)
     * @return AppointmentDocument if found, null otherwise
     */
    suspend fun findById(id: String): AppointmentDocument?

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
     * @param appointment AppointmentDocument to save
     * @return Saved AppointmentDocument with generated ID
     */
    suspend fun save(appointment: AppointmentDocument): AppointmentDocument

    /**
     * Update an existing appointment
     * @param id Appointment ID
     * @param appointment Updated AppointmentDocument
     * @return Updated AppointmentDocument if found, null otherwise
     */
    suspend fun update(id: String, appointment: AppointmentDocument): AppointmentDocument?

    /**
     * Delete an appointment
     * @param id Appointment ID
     * @return true if deleted, false otherwise
     */
    suspend fun delete(id: String): Boolean
}