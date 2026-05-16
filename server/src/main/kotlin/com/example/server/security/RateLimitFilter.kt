package com.example.server.security

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitFilter() : OncePerRequestFilter() {
    private val buckets = ConcurrentHashMap<String, Bucket>()
    // 20 requests per minute
    private fun newBucket(): Bucket = Bucket.builder()
        .addLimit(Bandwidth.builder()
            .capacity(20)
            .refillGreedy(20, Duration.ofMinutes(1))
            .build())
        .build()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Get client IP and retrieve (or create) their rate limit bucket
        val ip = request.remoteAddr
        val bucket = buckets.computeIfAbsent(ip) { newBucket() }

        // Allow request if tokens remaining, otherwise reject with 429
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response)
        } else {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write("Too many requests")
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.requestURI.startsWith("/auth/")
    }
}