package com.example.server.repository

import com.example.server.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, ObjectId> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}