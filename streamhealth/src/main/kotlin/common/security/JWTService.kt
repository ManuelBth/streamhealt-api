package com.betha.common.security

import com.betha.common.document.Rol
import com.betha.common.config.ConfigLoader
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT Token payload
 */
data class DecodedJWT(
    val userId: String,
    val rol: Rol,
    val expiration: Long
)

/**
 * Service interface for JWT token generation and verification
 */
interface JWTService {
    /**
     * Generate a JWT token for a user
     * @param userId The user's unique identifier
     * @param rol The user's role
     * @return JWT token string
     */
    fun generate(userId: String, rol: Rol): String

    /**
     * Verify and decode a JWT token
     * @param token JWT token string
     * @return DecodedJWT if valid, null if invalid
     */
    fun verify(token: String): DecodedJWT?
}

/**
 * JWT implementation using JJWT library
 * Uses HS256 algorithm
 */
class JwtService : JWTService {

    private val secretKey: SecretKey
    private val expiration: Long

    init {
        val config = ConfigLoader.get()
        this.secretKey = Keys.hmacShaKeyFor(config.jwt.secret.toByteArray())
        this.expiration = config.jwt.expiration
    }

    override fun generate(userId: String, rol: Rol): String {
        val now = System.currentTimeMillis()
        val expirationTime = now + expiration

        return Jwts.builder()
            .subject(userId)
            .claim("rol", rol.name)
            .issuedAt(Date(now))
            .expiration(Date(expirationTime))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    override fun verify(token: String): DecodedJWT? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            val userId = claims.subject
            val rolName = claims.get("rol", String::class.java)
            val rol = try {
                Rol.valueOf(rolName)
            } catch (e: IllegalArgumentException) {
                null
            }
            val expiration = claims.expiration.time

            if (userId == null || rol == null) {
                return null
            }

            DecodedJWT(userId, rol, expiration)
        } catch (e: ExpiredJwtException) {
            null
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}