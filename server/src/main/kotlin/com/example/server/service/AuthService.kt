package com.example.server.service

import com.example.server.dto.AuthResponse
import com.example.server.dto.UserDto
import com.example.server.model.User
import com.example.server.repository.UserRepository
import com.example.server.security.HashEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder
) {
    fun register(email: String, password: String, username: String): AuthResponse {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists")
        }

        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }

        val hashedPassword = hashEncoder.encode(password)
        val savedUser = userRepository.save(
            User(email = email, hashedPassword = hashedPassword, username = username)
        )

        return AuthResponse(
            message = "Registration successful",
            user = UserDto(email = savedUser.email, username = savedUser.username)
        )
    }
}