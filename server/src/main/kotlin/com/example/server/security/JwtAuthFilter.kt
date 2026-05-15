package com.example.server.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter (
    private val jwtService: JwtService
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // ดึง Authorization header
        val authHeader = request.getHeader("Authorization")

        // เช็คว่าเป็น Bearer Token หรือไม่
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // ตรวจสอบว่า JWT ใช้งานได้
            if (jwtService.validateAccessToken(authHeader)) {
                // ดึง userId จาก token
                val userId = jwtService.getUserIdFromToken(authHeader)
                // สร้าง Authentication object
                val auth = UsernamePasswordAuthenticationToken(userId, null)
                // เก็บข้อมูล login ไว้ใน SecurityContext
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        // ส่ง request ต่อไป
        filterChain.doFilter(request, response)
    }

}