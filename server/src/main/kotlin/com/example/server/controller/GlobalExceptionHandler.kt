package com.example.server.controller

import com.example.server.dto.AuthLoginResponse
import com.example.server.dto.AuthRegisterResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        val errorMessage = ex.bindingResult.fieldErrors
            .joinToString(", ") { it.defaultMessage ?: "Invalid value" }

        // ตรวจสอบประเภทของ controller request
        val targetClass = ex.bindingResult.target?.javaClass?.simpleName
        return when (targetClass) {
            "AuthRegisterRequest" -> ResponseEntity.badRequest().body(
                AuthRegisterResponse(message = errorMessage)
            )
            "AuthLoginRequest" -> ResponseEntity.badRequest().body(
                AuthLoginResponse(message = errorMessage, accessToken = null)
            )
            else -> ResponseEntity.badRequest().body(mapOf("message" to errorMessage))
        }
    }
}