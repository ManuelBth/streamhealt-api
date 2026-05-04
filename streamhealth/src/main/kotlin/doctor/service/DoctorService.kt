package com.betha.doctor.service

import com.betha.doctor.dto.CreateDoctorProfileRequest
import com.betha.doctor.dto.DoctorResponse
import com.betha.doctor.dto.UpdateDoctorProfileRequest

/**
 * Service interface for doctor operations
 */
interface DoctorService {
    /**
     * Get all doctors
     * @return List of DoctorResponse with enriched user data
     */
    suspend fun getAllDoctors(): List<DoctorResponse>

    /**
     * Get doctor by user ID (idNumber/Cédula)
     * @param idNumber User's identification number (Cédula)
     * @return DoctorResponse with enriched user data if found, null otherwise
     */
    suspend fun getDoctorByUserId(idNumber: String): DoctorResponse?

    /**
     * Create doctor profile for a user
     * @param idNumber User's identification number (Cédula)
     * @param request Doctor profile data
     * @return DoctorResponse if user exists and is DOCTOR role, null otherwise
     */
    suspend fun createDoctorProfile(idNumber: String, request: CreateDoctorProfileRequest): DoctorResponse?

    /**
     * Update doctor profile
     * @param idNumber User's identification number (Cédula)
     * @param request Doctor profile update data
     * @return Updated DoctorResponse if found, null otherwise
     */
    suspend fun updateDoctorProfile(idNumber: String, request: UpdateDoctorProfileRequest): DoctorResponse?
}