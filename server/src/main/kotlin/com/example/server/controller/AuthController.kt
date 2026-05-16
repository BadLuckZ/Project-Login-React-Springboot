package com.example.server.controller

import com.example.server.dto.AuthLoginRequest
import com.example.server.dto.AuthLoginResponse
import com.example.server.dto.AuthRegisterRequest
import com.example.server.dto.AuthRegisterResponse
import com.example.server.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import java.time.Duration

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

    // Access Token ใน React State
    // Refresh Token ใน HttpOnly Cookie
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: AuthLoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthLoginResponse> {
        return try {
            val (result, refreshToken) = authService.login(request.email, request.password)
            val isProd = System.getenv("SPRING_PROFILES_ACTIVE") == "prod"
            val cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isProd)
                .sameSite(if (isProd) "Strict" else "Lax")
                .path("/auth/me")
                .maxAge(Duration.ofDays(30))
                .build()
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
            ResponseEntity.ok(result)
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthLoginResponse(message = e.message ?: "Invalid credentials", accessToken = null))
        }
    }

    @GetMapping("/me")
    fun me(
        @CookieValue("refreshToken") refreshToken: String?,
        response: HttpServletResponse
    ): ResponseEntity<AuthLoginResponse> {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthLoginResponse(message = "No refresh token", accessToken = null))
        }
        return try {
            val (result, newRefreshToken) = authService.me(refreshToken)
            val isProd = System.getenv("SPRING_PROFILES_ACTIVE") == "prod"
            val cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(isProd)
                .sameSite(if (isProd) "Strict" else "Lax")
                .path("/auth/me")
                .maxAge(Duration.ofDays(30))
                .build()
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthLoginResponse(message = "Unauthorized", accessToken = null))
        }
    }
}