package org.keddad.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import org.keddad.routes.configureOrders

fun Application.configureRouting() {
    var isDebug = environment.config.property("application.debug").getString() != ""
    var dbConnection = connectToPostgres(isDebug)

    configureOrders(dbConnection)
}
