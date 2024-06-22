package org.traum.auth.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.traum.auth.di.*
import org.traum.auth.useCases.AppAuthInteractor
import java.util.*

@Serializable
data class JWTLoginDTO(val login: String, val password: String)

@Serializable
data class JWTRegistrationDTO(val login: String, val password: String, val userId: String)

data class OAuthSession(val isRegistration: Boolean, val values: Map<String, String>?)

fun Application.configureSecurity() {
    val oauthProviders by inject<OAuth2Providers>()
    val services by inject<Services>()
    val jwtRegistration by inject<JWTRegistration>()
    val appAuthInteractor = AppAuthInteractor()

    with(jwtRegistration) {
        authentication {
            this.call("auth-jwt")
        }
    }

    routing {
        post("jwt/login") {
            val dto = call.receive<JWTLoginDTO>()
            val data = LoginData(dto.login, dto.password)
            kotlin.runCatching {
                val token = appAuthInteractor.loginBasic(data)
                call.respond(token)
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, "Неправильный логин или пароль")
            }
        }

        post("jwt/registration") {
            val dto = call.receive<JWTRegistrationDTO>()
            val regData = RegistrationData(dto.login, dto.password)
            val token = appAuthInteractor.registrationBasic(regData, UUID.fromString(dto.userId))
            call.respond(token)
        }
    }

//    oauthProviders.forEach { (name, provider) ->
//        authentication {
//            oauth("auth-oauth-$name") {
//                urlProvider = { "https://localhost:50001/${name.lowercase()}/callback" }
//                providerLookup = { provider }
//                client = HttpClient(Apache)
//            }
//        }
//
//        routing {
//            route(name.lowercase()) {
//                get("login") {
//                    call.sessions.set(OAuthSession(false, mapOf()))
//                    call.respondRedirect("callback")
//                }
//
//                /**
//                 * OAuth authorization result handler
//                 */
//                authenticate("auth-oauth-$name") {
//                    get("registration") {
//                        val header = call.request.headers[HttpHeaders.Authorization]
//                        val id = JWTPayloadInteractor().getUserId(header!!)
//                        call.sessions.set(OAuthSession(true, mapOf("id" to id!!)))
//                        call.respondRedirect("callback")
//                    }
//
//                    get("callback") {
//                        val session = call.sessions.get<OAuthSession>()
//                        checkOr(session != null) {
//                            call.respond(HttpStatusCode.BadRequest, "not found session")
//                        }
//                        call.request.headers.forEach { key, str ->
//                            println(key)
//                            str.forEach {
//                                println(it)
//                            }
//                            println()
//                        }
//                        requireOr(services.containsKey(name)) {
//                            call.sessions.clear<OAuthSession>()
//                            call.respond(HttpStatusCode.NotImplemented, "not OAuth implemented service")
//                        }
//                        val principal: OAuthAccessTokenResponse? = call.authentication.principal()
//
//                        checkOr(principal != null) {
//                            call.sessions.clear<OAuthSession>()
//                            call.respond(HttpStatusCode.Unauthorized, "principal is null")
//                        }
//
//                        val data = runCatching { services[name]!!.fetchData(principal) }.onFailure {
//                            call.sessions.clear<OAuthSession>()
//                            call.respondText(it.toString())
//                        }.getOrNull()!!
//
//
//                        val token = if (session.isRegistration) {
//                            val userId = session.values?.get("id")
//                            checkOr(userId != null) {
//                                call.sessions.clear<OAuthSession>()
//                                call.respond(HttpStatusCode.BadRequest, "")
//                            }
//                            appAuthInteractor.registrationOAuth(
//                                OAuthRegistrationData(userId, data.id, AvailableServices.valueOf(name)),
//                                UUID.fromString(userId)
//                            )
//                        } else
//                            appAuthInteractor.loginOAuth(OAuthLoginData(data.id, AvailableServices.valueOf(name)))
//                        call.sessions.clear<OAuthSession>()
//                        call.respond(token)
//                    }
//                }
//            }
//        }
//    }
}