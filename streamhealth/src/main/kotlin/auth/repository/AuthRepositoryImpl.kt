package com.betha.auth.repository

import com.betha.common.config.ConfigLoader
import com.betha.common.document.Rol
import com.betha.common.document.UserDocument
import com.betha.doctor.document.DoctorDocument
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId

class AuthRepositoryImpl : AuthRepository {

    private val database = MongoClients
        .create(ConfigLoader.get().mongodb.uri)
        .getDatabase("streamhealth")

    private val collection: MongoCollection<Document>
        get() = database.getCollection("usuarios", Document::class.java)

    private val doctoresCollection: MongoCollection<Document>
        get() = database.getCollection("doctores", Document::class.java)

    override suspend fun findByIdNumber(idNumber: String): UserDocument? = withContext(Dispatchers.IO) {
        val document = collection.find(Filters.eq("idNumber", idNumber)).first()
        document?.let { UserDocument.fromDocument(it) }
    }

    override suspend fun save(user: UserDocument): UserDocument = withContext(Dispatchers.IO) {
        val doc = user.toDocument()
        collection.insertOne(doc)
        
        // Get the generated ID from MongoDB (it's in the document after insert)
        val generatedId = doc.getObjectId("_id").toString()
        
        // Create doctor profile if user is a DOCTOR
        if (user.rol == Rol.DOCTOR) {
            val doctorDoc = DoctorDocument(
                userId = user.idNumber,
                titulo = null,
                universidad = null,
                especialidades = emptyList(),
                doctorados = emptyList()
            )
            doctoresCollection.insertOne(doctorDoc.toDocument())
        }
        
        // Return user with ID assigned
        user.copy(id = generatedId)
    }

    override suspend fun existsByIdNumber(idNumber: String): Boolean = withContext(Dispatchers.IO) {
        collection.countDocuments(Filters.eq("idNumber", idNumber)) > 0
    }
}