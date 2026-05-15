package com.example.server.controller

import com.example.server.dto.AuthResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<AuthResponse> {
        val errorMessage = ex.bindingResult.fieldErrors
            .joinToString(", ") { it.defaultMessage ?: "Invalid value" }

        return ResponseEntity.badRequest().body(
            AuthResponse(message = errorMessage)
        )
    }
}