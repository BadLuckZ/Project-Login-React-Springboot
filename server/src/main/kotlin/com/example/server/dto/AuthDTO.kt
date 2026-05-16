package com.example.server.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AuthRegisterRequest(
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

data class AuthRegisterResponse(
    val message: String,
)

data class AuthLoginRequest(
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)

data class AuthLoginResponse(
    val message: String,
    val accessToken: String?
)

data class AuthMeUser(
    val email: String,
    val username: String,
)

data class AuthMeResponse(
    val message: String,
    val accessToken: String?,
    val user: AuthMeUser?
)

data class AuthLogoutResponse(
    val message: String
)