package com.example.server.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {
    private val bcrypt = BCryptPasswordEncoder()

    fun encode(raw: String): String {
        return bcrypt.encode(raw) ?: ""
    }

}