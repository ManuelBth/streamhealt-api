package com.betha.doctor.repository

import com.betha.common.config.ConfigLoader
import com.betha.doctor.document.DoctorDocument
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document

/**
 * Doctor repository implementation using KMongo
 */
class DoctorRepositoryImpl : DoctorRepository {

    private val database = MongoClients
        .create(ConfigLoader.get().mongodb.uri)
        .getDatabase("streamhealth")

    private val collection: MongoCollection<Document>
        get() = database.getCollection("doctores", Document::class.java)

    override suspend fun findByUserId(userId: String): DoctorDocument? = withContext(Dispatchers.IO) {
        val document = collection.find(Filters.eq("userId", userId)).first()
        document?.let { DoctorDocument.fromDocument(it) }
    }

    override suspend fun findById(id: String): DoctorDocument? = withContext(Dispatchers.IO) {
        val document = collection.find(Filters.eq("_id", id)).first()
        document?.let { DoctorDocument.fromDocument(it) }
    }

    override suspend fun findAll(): List<DoctorDocument> = withContext(Dispatchers.IO) {
        val documents = collection.find().into(mutableListOf<Document>())
        documents.map { DoctorDocument.fromDocument(it) }
    }

    override suspend fun save(doctor: DoctorDocument): DoctorDocument = withContext(Dispatchers.IO) {
        val document = doctor.toDocument()
        collection.insertOne(document)

        // Get the inserted ID
        val insertedId = document.getObjectId("_id").toString()
        doctor.copy(id = insertedId)
    }

    override suspend fun updateByUserId(userId: String, doctor: DoctorDocument): DoctorDocument? = withContext(Dispatchers.IO) {
        val result = collection.updateOne(
            Filters.eq("userId", userId),
            Updates.combine(
                Updates.set("userId", doctor.userId),
                Updates.set("titulo", doctor.titulo),
                Updates.set("universidad", doctor.universidad),
                Updates.set("especialidades", doctor.especialidades),
                Updates.set("doctorados", doctor.doctorados),
                Updates.set("licencia", doctor.licencia),
                Updates.set("telefono", doctor.telefono),
                Updates.set("direccion", doctor.direccion),
                Updates.set("updatedAt", System.currentTimeMillis())
            ),
            UpdateOptions().upsert(false)
        )

        if (result.modifiedCount > 0) {
            findByUserId(userId)
        } else {
            null
        }
    }

    override suspend fun update(id: String, doctor: DoctorDocument): DoctorDocument? = withContext(Dispatchers.IO) {
        val result = collection.updateOne(
            Filters.eq("_id", id),
            Updates.combine(
                Updates.set("userId", doctor.userId),
                Updates.set("titulo", doctor.titulo),
                Updates.set("universidad", doctor.universidad),
                Updates.set("especialidades", doctor.especialidades),
                Updates.set("doctorados", doctor.doctorados),
                Updates.set("licencia", doctor.licencia),
                Updates.set("telefono", doctor.telefono),
                Updates.set("direccion", doctor.direccion),
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