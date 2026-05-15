package com.example.server.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
): OncePerRequestFilter() {
    // ทำงานกับทุก request ก่อนถึง controller
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // ดึง header Authorization
        val authHeader = request.getHeader("Authorization")
        // ตรวจสอบว่ามี Bearer token หรือไม่
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // ถ้า token ถูกต้อง
            if (jwtService.validateAccessToken(authHeader)) {
                // ดึง userId จาก token
                val userId = jwtService.getUserIdFromToken(authHeader)
                // สร้าง Authentication object แล้วเก็บใน SecurityContext
                val auth = UsernamePasswordAuthenticationToken(userId, null)
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        // ส่ง request ต่อไปยัง filter ตัวถัดไป / controller
        filterChain.doFilter(request, response)
    }

}