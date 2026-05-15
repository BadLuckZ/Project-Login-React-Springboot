package com.example.server.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import java.util.Base64
import java.util.Date

@Service
class JwtService(
    // อ่าน JWT secret จาก application.properties หรือ env
    @Value("\${JWT_SECRET_BASE64}")
    private val jwtSecret: String
) {
    // แปลง secret จาก Base64 -> SecretKey สำหรับ sign JWT
    private val secretKey =
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    // ประเภท token
    private val ACCESS = "access"
    private val REFRESH = "refresh"
    // access token 15 นาที
    private val accessTokenValidMs = 15L * 60 * 1000
    // refresh token 30 วัน
    private val refreshValidMs = 30L * 24 * 60 * 60 * 1000

    // สร้าง JWT token
    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long
    ): String? {
        // เวลาปัจจุบัน
        val now = Date()
        // เวลาหมดอายุ token
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            // เก็บ userId ใน subject
            .subject(userId)
            // เก็บประเภท token
            .claim("type", type)
            // เวลาออก token
            .issuedAt(now)
            // เวลาหมดอายุ token
            .expiration(expiryDate)
            // sign token ด้วย secret key
            .signWith(secretKey, Jwts.SIG.HS256)
            // สร้าง token string
            .compact()
    }

    // สร้าง access token
    fun generateAccessToken(userId: String): String? {
        return generateToken(userId, ACCESS, accessTokenValidMs)
    }

    // สร้าง refresh token
    fun generateRefreshToken(userId: String): String? {
        return generateToken(userId, REFRESH, refreshValidMs)
    }

    // อ่าน claims ทั้งหมดจาก token
    private fun parseAllClaims(token: String): Claims? {
        // ตัด "Bearer " ออก ถ้ามี
        val rawToken = if (token.startsWith("Bearer ")) {
                token.removePrefix("Bearer ")
            } else token
        return try {
            // parse และ verify JWT
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            // token ไม่ถูกต้อง
            null
        }
    }

    // ตรวจสอบว่าเป็น access token หรือไม่
    fun validateAccessToken(accessToken: String): Boolean {
        // อ่าน claims
        val claims = parseAllClaims(accessToken) ?: return false
        // อ่าน type
        val tokenType = claims["type"] as? String ?: return false
        // เช็คว่า type = access
        return tokenType == ACCESS
    }

    // ตรวจสอบว่าเป็น refresh token หรือไม่
    fun validateRefreshToken(accessToken: String): Boolean {
        // อ่าน claims
        val claims = parseAllClaims(accessToken) ?: return false
        // อ่าน type
        val tokenType = claims["type"] as? String ?: return false
        // เช็คว่า type = refresh
        return tokenType == REFRESH
    }

    // Authorization: Bearer <token>
    // ดึง userId จาก token
    fun getUserIdFromToken(token: String): String? {
        // อ่าน claims
        val claims = parseAllClaims(token) ?: throw IllegalArgumentException("Invalid token")
        // คืนค่า subject (userId)
        return claims.subject
    }
}