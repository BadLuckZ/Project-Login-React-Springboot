package com.example.server.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date

enum class TOKEN {
    ACCESS,
    REFRESH
}

@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidMs = 15L * 60 * 1000
    val refreshTokenValidMs = 30L * 24 * 60 * 60 * 1000

    private fun generateToken(userId: String, type: TOKEN, expiry: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String): String {
        return generateToken(userId, TOKEN.ACCESS, accessTokenValidMs)
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, TOKEN.REFRESH, refreshTokenValidMs)
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if(token.startsWith("Bearer ")) {
            token.removePrefix("Bearer ")
        } else token
        return try {
            Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            null
        }
    }

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? TOKEN ?: return false
        return tokenType == TOKEN.ACCESS
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? TOKEN ?: return false
        return tokenType == TOKEN.REFRESH
    }

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token) ?: throw IllegalArgumentException("Invalid token")
        return claims.subject
    }
}