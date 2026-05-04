package com.betha.auth.repository

import com.betha.common.config.ConfigLoader
import com.betha.common.document.UserDocument
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document

class AuthRepositoryImpl : AuthRepository {

    private val database = MongoClients
        .create(ConfigLoader.get().mongodb.uri)
        .getDatabase("streamhealth")

    private val collection: MongoCollection<Document>
        get() = database.getCollection("usuarios", Document::class.java)

    override suspend fun findByIdNumber(idNumber: String): UserDocument? = withContext(Dispatchers.IO) {
        val document = collection.find(Filters.eq("idNumber", idNumber)).first()
        document?.let { UserDocument.fromDocument(it) }
    }

    override suspend fun save(user: UserDocument): UserDocument = withContext(Dispatchers.IO) {
        val doc = user.toDocument()
        collection.insertOne(doc)
        
        // Get the generated ID from MongoDB (it's in the document after insert)
        val generatedId = doc.getObjectId("_id").toString()
        
        // Return user with ID assigned
        user.copy(id = generatedId)
    }

    override suspend fun existsByIdNumber(idNumber: String): Boolean = withContext(Dispatchers.IO) {
        collection.countDocuments(Filters.eq("idNumber", idNumber)) > 0
    }
}