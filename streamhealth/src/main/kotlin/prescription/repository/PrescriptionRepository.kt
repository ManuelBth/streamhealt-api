package com.betha.prescription.repository

import com.betha.prescription.document.PrescriptionDocument

interface PrescriptionRepository {
    suspend fun create(document: PrescriptionDocument): PrescriptionDocument

    suspend fun findById(id: String): PrescriptionDocument?

    suspend fun findByPrescriptionId(prescriptionId: String): PrescriptionDocument?

    suspend fun findByPatientId(patientId: String): List<PrescriptionDocument>

    suspend fun findByDoctorId(doctorId: String): List<PrescriptionDocument>

    suspend fun findByMedicalHistoryId(medicalHistoryId: String): List<PrescriptionDocument>

    suspend fun update(prescriptionId: String, updates: Map<String, Any>): PrescriptionDocument?

    suspend fun delete(prescriptionId: String): Boolean
}