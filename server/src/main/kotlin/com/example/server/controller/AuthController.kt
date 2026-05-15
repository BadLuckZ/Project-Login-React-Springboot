package com.example.server.controller

import com.example.server.dto.AuthLoginRequest
import com.example.server.dto.AuthLoginResponse
import com.example.server.dto.AuthRegisterRequest
import com.example.server.dto.AuthRegisterResponse
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
import org.springframework.security.authentication.BadCredentialsException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: AuthRegisterRequest): ResponseEntity<AuthRegisterResponse> {
        return try {
            val response = authService.register(request.email, request.password, request.username)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthRegisterResponse(message = e.message ?: "Registration failed"))
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: AuthLoginRequest): ResponseEntity<AuthLoginResponse> {
        return try {
            val response = authService.login(request.email, request.password)
            ResponseEntity.ok(response)
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthLoginResponse(message = e.message ?: "Invalid credentials", null))
        }
    }


}