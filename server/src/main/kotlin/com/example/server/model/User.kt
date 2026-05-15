package com.example.server.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

// "Users" Collection
@Document("users")
data class User(
    // Unique Email
    @Indexed(unique = true) val email: String,

    // Unique Username
    @Indexed(unique = true) val username: String,
    val hashedPassword: String,
    @Id val id: ObjectId = ObjectId.get()
)