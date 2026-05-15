package com.example.server.controller

import com.example.server.dto.AuthResponse
import com.example.server.service.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    data class RegisterRequest(
        @field:Pattern(
            regexp = "^[a-zA-Z0-9_]{3,20}$",
            message = "Username must be 3-20 characters and contain only letters, numbers, and underscores"
        )
        val username: String,
        @field:Email(message = "Invalid email format")
        val email: String,
        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must contain at least 8 characters, 1 uppercase, 1 lowercase and 1 number"
        )
        val password: String
    )

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.register(request.email, request.password, request.username)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(message = e.message ?: "Registration failed"))
        }
    }
}