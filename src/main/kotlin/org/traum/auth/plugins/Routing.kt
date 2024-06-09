package org.traum.auth.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.traum.auth.repositories.IAuthBasicRepository

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        unhandled { call ->
            call.respondText("\uD83D\uDC7A")
        }
    }

    val authBasicRepository by inject<IAuthBasicRepository>()

    routing {
        authenticate("auth-jwt") {
            get("token-validate") {
                val principal =
                    call.principal<JWTPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val userId = principal.payload.getClaim("id").asString()
                call.respond(userId)
            }
        }
        get("login/{login}") {
            val login = call.parameters["login"]
            val result = authBasicRepository.containsLogin(login!!)
            call.respond(result)
        }
        get("ping") {
            call.respondText("pong")
        }
    }
}
