package org.keddad.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*

fun Application.configureSecurity() {

    authentication {
        jwt {
            val secret = this@configureSecurity.environment.config.property("jwt.secret").getString()
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()

            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString()).build()
            )

            validate { credential ->
                if (credential.payload.getClaim("id").asString() != "" && credential.payload.getClaim("isManager").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
