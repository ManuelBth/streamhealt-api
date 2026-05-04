package com.betha.doctor.repository

import com.betha.doctor.document.DoctorDocument

/**
 * Repository interface for doctor document operations
 */
interface DoctorRepository {
    /**
     * Find doctor document by user ID (idNumber/Cédula)
     * @param userId User's identification number (Cédula)
     * @return DoctorDocument if found, null otherwise
     */
    suspend fun findByUserId(userId: String): DoctorDocument?

    /**
     * Find doctor document by ID
     * @param id Doctor document ID (MongoDB ObjectId as String)
     * @return DoctorDocument if found, null otherwise
     */
    suspend fun findById(id: String): DoctorDocument?

    /**
     * Find all doctor documents
     * @return List of all DoctorDocuments
     */
    suspend fun findAll(): List<DoctorDocument>

    /**
     * Save a new doctor document
     * @param doctor DoctorDocument to save
     * @return Saved DoctorDocument with generated ID
     */
    suspend fun save(doctor: DoctorDocument): DoctorDocument

    /**
     * Update an existing doctor document
     * @param id Doctor document ID
     * @param doctor Updated DoctorDocument
     * @return Updated DoctorDocument if found, null otherwise
     */
    suspend fun update(id: String, doctor: DoctorDocument): DoctorDocument?

    /**
     * Delete a doctor document
     * @param id Doctor document ID
     * @return true if deleted, false otherwise
     */
    suspend fun delete(id: String): Boolean
}