package org.keddad

// package name here is ALSO weird but in a different way
// concept of "you can't realisitically write code without a generator" is stupid

import io.ktor.server.application.*
import org.keddad.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
