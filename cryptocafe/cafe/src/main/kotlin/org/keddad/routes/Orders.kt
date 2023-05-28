package org.keddad.routes

import cryptocafe.keddad.org.models.GenericApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.keddad.models.OrderService
import java.sql.Connection
import io.ktor.server.routing.*
import org.keddad.models.Order
import org.keddad.models.OrderCreateRequest

fun Application.configureOrders(dbConnection: Connection) {
    val orderService = OrderService(dbConnection)

    routing {
        authenticate {
            post("/order/create") {
                val principal = call.principal<JWTPrincipal>()
                val orderRequest = call.receive<OrderCreateRequest>()

                if (orderRequest.dishes.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, GenericApiResponse("No dishes specified"))
                    return@post
                }

                val id = principal!!.payload.getClaim("id").asInt()

                val order =
                    Order(id, "queued", orderRequest.comment, orderRequest.dishes.map { it.id to it.amount }.toMap())

                try {
                    val orderId = orderService.create(order)
                    call.respond(HttpStatusCode.OK, GenericApiResponse("Created order with ID $orderId"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, GenericApiResponse(e.toString()))
                }
            }
        }

        get("/order/{orderId}") {
            val id = call.parameters["orderId"]!!.toInt()
            val order = orderService.read(id)

            if (order == null) {
                call.respond(HttpStatusCode.NotFound, GenericApiResponse("No such order"))
                return@get
            }

            call.respond(HttpStatusCode.OK, order)
        }
    }
}