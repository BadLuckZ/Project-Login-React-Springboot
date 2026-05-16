package com.example.server.service

import com.example.server.dto.AuthLoginResponse
import com.example.server.dto.AuthMeResponse
import com.example.server.dto.AuthMeUser
import com.example.server.dto.AuthRegisterResponse
import com.example.server.model.RefreshToken
import com.example.server.model.User
import com.example.server.repository.RefreshTokenRepository
import com.example.server.repository.UserRepository
import com.example.server.security.HashEncoder
import com.example.server.security.JwtService
import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun register(email: String, password: String, username: String): AuthRegisterResponse {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists")
        }

        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }

        val hashedPassword = hashEncoder.encode(password)
        userRepository.save(User(email = email, hashedPassword = hashedPassword, username = username))
        return AuthRegisterResponse(
            message = "Registration successful",
        )
    }

    fun login(email: String, password: String): Pair<AuthLoginResponse, String> {
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid credentials")
        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return Pair(
            AuthLoginResponse(message = "Login successful", accessToken = newAccessToken),
            newRefreshToken)
    }

    fun me(refreshToken: String): Pair<AuthMeResponse, String> {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            IllegalArgumentException("User not found")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw BadCredentialsException("Refresh token not recognized")
        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())
        storeRefreshToken(user.id, newRefreshToken)

        return Pair(
            AuthMeResponse(message = "User found",
                accessToken = newAccessToken,
                user= AuthMeUser(
                    email = user.email,
                    username = user.username
                )),
            newRefreshToken
        )
    }

    private fun storeRefreshToken(userId: ObjectId, refreshToken: String) {
        val hashedRefreshToken = hashToken(refreshToken)
        val expiryMs = jwtService.refreshTokenValidMs
        val expiresAt = Instant.now().plusMillis(expiryMs)
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashedRefreshToken
            )
        )

    }

    private fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    fun refresh(refreshToken: String): Pair<String, String> {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            IllegalArgumentException("Invalid refresh token")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed) ?: throw BadCredentialsException("Refresh token not recognized")
        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)
        return Pair(newAccessToken, newRefreshToken)
    }
}