package org.traum.auth.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.traum.auth.useCases.AccountUseCase
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
        authenticate("auth-jwt") {
            route("user") {
                get("info") {
                    val principal =
                        call.principal<JWTPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val userId = principal.payload.getClaim("id").asString()

                    val userInfo = AccountUseCase().getUserInfo(userId)
                        ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(userInfo)
                }
            }
        }
        route("token/{token}") {
            get("userId") {
                val token = call.parameters["token"]
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                val userId = JWTPayloadInteractor().getUserId(token)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(userId)
            }
            get("userInfo") {
                val token = call.parameters["token"]
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                val userId = JWTPayloadInteractor().getUserId(token)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                val userInfo = AccountUseCase().getUserInfo(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(userInfo)
            }
        }
        get("users/info") {
            val result = runCatching {
                AccountUseCase().getAllUserInfo()
            }.onFailure {
                return@get call.respond(HttpStatusCode.InternalServerError)
            }.getOrThrow()
            call.respond(result)
        }
    }
}
