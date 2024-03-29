package cryptocafe.keddad.org

// package name is reversed because ktor generator is weird and i'm not fixing that

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import cryptocafe.keddad.org.plugins.*
import io.ktor.http.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    // I hate CORS with burning passion but swagger api is horrifying JS contraption that doesn't work without it

    install(CORS) {
        anyHost()
        allowNonSimpleContentTypes = true
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        exposeHeader(HttpHeaders.Authorization)
    }

    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
