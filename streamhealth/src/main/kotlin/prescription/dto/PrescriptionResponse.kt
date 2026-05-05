package com.betha.prescription.dto

import com.betha.prescription.document.PrescriptionDocument
import kotlinx.serialization.Serializable

@Serializable
data class PrescriptionResponse(
    val id: String,
    val prescriptionId: String,
    val patientId: String,
    val doctorId: String,
    val medicalHistoryId: String,
    val medicamento: String,
    val cantidad: String,
    val frecuencia: String,
    val recomendaciones: String?,
    val fechaValidacion: String,
    val createdAt: String
)

fun PrescriptionDocument.toPrescriptionResponse(): PrescriptionResponse {
    return PrescriptionResponse(
        id = this._id ?: "",
        prescriptionId = this.prescriptionId,
        patientId = this.patientId,
        doctorId = this.doctorId,
        medicalHistoryId = this.medicalHistoryId,
        medicamento = this.medicamento,
        cantidad = this.cantidad,
        frecuencia = this.frecuencia,
        recomendaciones = this.recomendaciones,
        fechaValidacion = this.fechaValidacion,
        createdAt = this.createdAt
    )
}