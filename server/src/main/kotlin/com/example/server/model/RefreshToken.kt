package com.example.server.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@Document("refreshTokens")
data class RefreshToken(
    val userId: ObjectId,

    // MongoDB จะลบ document อัตโนมัติเมื่อถึงเวลา expiresAt
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant,

    val hashedToken: String,
    val createdAt: Instant = Instant.now()
)