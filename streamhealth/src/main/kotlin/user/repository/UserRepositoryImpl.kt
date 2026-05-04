package com.betha.user.repository

import com.betha.common.config.ConfigLoader
import com.betha.common.document.UserDocument
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document

class UserRepositoryImpl : UserRepository {

    private val database = MongoClients
        .create(ConfigLoader.get().mongodb.uri)
        .getDatabase("streamhealth")

    private val collection: MongoCollection<Document>
        get() = database.getCollection("usuarios", Document::class.java)

    override suspend fun findById(id: String): UserDocument? = withContext(Dispatchers.IO) {
        val document = collection.find(Filters.eq("_id", id)).first()
        document?.let { UserDocument.fromDocument(it) }
    }

    override suspend fun findByIdNumber(idNumber: String): UserDocument? = withContext(Dispatchers.IO) {
        val document = collection.find(Filters.eq("idNumber", idNumber)).first()
        document?.let { UserDocument.fromDocument(it) }
    }

    override suspend fun updateByIdNumber(idNumber: String, user: UserDocument): UserDocument? = withContext(Dispatchers.IO) {
        val result = collection.updateOne(
            Filters.eq("idNumber", idNumber),
            Updates.combine(
                Updates.set("nombres", user.nombres),
                Updates.set("apellidos", user.apellidos),
                Updates.set("edad", user.edad),
                Updates.set("sexo", user.sexo.name),
                Updates.set("residencia", user.residencia),
                Updates.set("login", user.login),
                Updates.set("updatedAt", System.currentTimeMillis())
            ),
            UpdateOptions().upsert(false)
        )

        if (result.modifiedCount > 0) {
            findByIdNumber(idNumber)
        } else {
            null
        }
    }

    override suspend fun update(id: String, user: UserDocument): UserDocument? = withContext(Dispatchers.IO) {
        val result = collection.updateOne(
            Filters.eq("_id", id),
            Updates.combine(
                Updates.set("nombres", user.nombres),
                Updates.set("apellidos", user.apellidos),
                Updates.set("edad", user.edad),
                Updates.set("sexo", user.sexo.name),
                Updates.set("residencia", user.residencia),
                Updates.set("login", user.login),
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
}