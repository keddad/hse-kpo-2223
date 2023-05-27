package cryptocafe.keddad.org.plugins

import cryptocafe.keddad.org.routes.configureUserRoutes
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    var isDebug = environment.config.property("application.debug").getString() != ""
    var dbConnection = connectToPostgres(isDebug)

    configureUserRoutes(dbConnection)

    routing {
        get("/") {
            call.respondText("I'm alive")
        }
    }
}
