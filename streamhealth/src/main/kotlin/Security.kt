package com.betha

import com.betha.common.config.ConfigLoader
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val config = ConfigLoader.get()
    val jwtSecret = config.jwt.secret
    val jwtRealm = "StreamHealth API"
    
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.subject
                val rolClaim = credential.payload.getClaim("rol")?.asString()
                
                if (userId != null && rolClaim != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}