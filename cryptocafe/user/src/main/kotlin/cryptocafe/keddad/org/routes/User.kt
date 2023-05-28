package cryptocafe.keddad.org.routes

import cryptocafe.keddad.org.models.GenericApiResponse
import cryptocafe.keddad.org.models.LoginRequest
import cryptocafe.keddad.org.models.User
import cryptocafe.keddad.org.models.UserService
import cryptocafe.keddad.org.plugins.createUserJwt
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import java.lang.Exception
import java.security.MessageDigest
import java.sql.Connection
import java.util.*
import kotlin.text.Charsets.UTF_8

// Salt? sorry, not hungry
fun hashPassword(password: String): String =
    Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(password.toByteArray(UTF_8)))


fun Application.configureUserRoutes(dbConnection: Connection) {
    val userService = UserService(dbConnection)

    routing {
        post("/user") {
            val user = call.receive<User>()

            if (!user.email.contains("@")) {
                // no way i'm using a regexp
                call.respond(HttpStatusCode.BadRequest, GenericApiResponse("Malformed email"))
            }

            if (user.password == "" || user.username == "") {
                call.respond(HttpStatusCode.BadRequest, GenericApiResponse("Request can't contain empty fields"))
            }

            user.password = hashPassword(user.password)

            try {
                val id = userService.create(user)
                call.respond(HttpStatusCode.OK, GenericApiResponse("Created user with id $id"))
            } catch (e: org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException) {
                call.respond(HttpStatusCode.BadRequest, GenericApiResponse("Non-unique username!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, GenericApiResponse(e.toString()))
            }
        }

        post("/user/login") {
            val loginRequest = call.receive<LoginRequest>()

            val user = userService.read(loginRequest.username)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, GenericApiResponse("Non such username!"))
            }

            if (user!!.password != hashPassword(loginRequest.password)) {
                call.respond(HttpStatusCode.Unauthorized, GenericApiResponse("Wrong password!"))
            }

            val token = this.application.createUserJwt(user)
            call.response.header(HttpHeaders.Authorization, "Bearer $token")
            call.respond(HttpStatusCode.OK, GenericApiResponse("OK"))
        }

        authenticate {
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()

                val user = userService.read(username)
                call.respond(HttpStatusCode.OK, user!!)
            }
        }
    }
}