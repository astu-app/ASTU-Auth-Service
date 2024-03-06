package org.traum.auth.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.traum.auth.useCases.JWTPayloadInteractor

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        unhandled { call ->
            call.respondText("\uD83D\uDC7A")

        }
    }
    routing {
        get("/") {
            call.respondText("msg")
        }

        authenticate("auth-jwt") {
            get("/secured") {
                call.respondText("you have access to this resource")
            }
        }

        route("token/{token}") {
            get("userId") {
                val token = call.parameters["token"]
                val userId = JWTPayloadInteractor().getUserId(token!!)

                if (userId != null) {
                    call.respond(userId)
                }
                else
                    call.respond(HttpStatusCode.NotFound, "")
            }
        }
    }
}
