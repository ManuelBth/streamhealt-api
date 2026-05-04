package com.betha.doctor.document

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document

/**
 * Doctor document for MongoDB persistence
 * Represents professional doctor information in the StreamHealth telemedicine platform
 */
@Serializable
data class DoctorDocument(
    val id: String? = null,           // MongoDB ObjectId
    val userId: String,                // Reference to User.idNumber (Cédula)
    val titulo: String? = null,        // Título profesional (Dr., Mg., PhD, etc.)
    val universidad: String? = null,   // Universidad otorgante
    val especialidades: List<String> = emptyList(),
    val doctorados: List<String> = emptyList(),
    val licencia: String? = null,       // Número de licencia médica
    val telefono: String? = null,
    val direccion: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        fun fromDocument(document: Document): DoctorDocument = json.decodeFromString(document.toJson())
    }
}