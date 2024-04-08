package org.traum.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.traum.auth.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
3
    configureDI()
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureAdministration()
    configureRouting()
}