package com.example.server.dto

data class AuthResponse(
    val message: String,
    val user: UserDto? = null
)

data class UserDto(
    val email: String,
    val username: String
)