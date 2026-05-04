package com.betha.schedule.repository

import com.betha.common.config.ConfigLoader
import com.betha.schedule.document.AppointmentDocument
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document

/**
 * Schedule repository implementation using KMongo
 */
class ScheduleRepositoryImpl : ScheduleRepository {

    private val database = MongoClients
        .create(ConfigLoader.get().mongodb.uri)
        .getDatabase("streamhealth")

    private val collection: MongoCollection<Document>
        get() = database.getCollection("citas", Document::class.java)

    override suspend fun findById(id: String): AppointmentDocument? = withContext(Dispatchers.IO) {
        val document = collection.find(Filters.eq("_id", id)).first()
        document?.let { AppointmentDocument.fromDocument(it) }
    }

    override suspend fun findByPatientId(patientId: String): List<AppointmentDocument> = withContext(Dispatchers.IO) {
        val documents = collection.find(Filters.eq("patientId", patientId)).into(mutableListOf<Document>())
        documents.map { AppointmentDocument.fromDocument(it) }
    }

    override suspend fun findByDoctorId(doctorId: String): List<AppointmentDocument> = withContext(Dispatchers.IO) {
        val documents = collection.find(Filters.eq("doctorId", doctorId)).into(mutableListOf<Document>())
        documents.map { AppointmentDocument.fromDocument(it) }
    }

    override suspend fun save(appointment: AppointmentDocument): AppointmentDocument = withContext(Dispatchers.IO) {
        val document = appointment.toDocument()
        collection.insertOne(document)

        // Get the inserted ID
        val insertedId = document.getObjectId("_id").toString()
        appointment.copy(id = insertedId)
    }

    override suspend fun update(id: String, appointment: AppointmentDocument): AppointmentDocument? = withContext(Dispatchers.IO) {
        val result = collection.updateOne(
            Filters.eq("_id", id),
            Updates.combine(
                Updates.set("patientId", appointment.patientId),
                Updates.set("doctorId", appointment.doctorId),
                Updates.set("fecha", appointment.fecha),
                Updates.set("motivo", appointment.motivo),
                Updates.set("estado", appointment.estado),
                Updates.set("updatedAt", System.currentTimeMillis())
            ),
            UpdateOptions().upsert(false)
        )

        if (result.modifiedCount > 0) {
            findById(id)
        } else {
            null
        }
    }

    override suspend fun delete(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = collection.deleteOne(Filters.eq("_id", id))
        result.deletedCount > 0
    }
}