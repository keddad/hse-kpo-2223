package cryptocafe.keddad.org.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cryptocafe.keddad.org.models.User
import io.ktor.server.application.*
import java.util.*

fun Application.createUserJwt(user: User): String {
    val token = JWT.create().withIssuer(this.environment.config.property("jwt.domain").getString())
        .withClaim("username", user.username).withClaim("isManager", user.isManager)
        .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
        .sign(Algorithm.HMAC256(this.environment.config.property("jwt.secret").getString()))

    return token
}

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
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
