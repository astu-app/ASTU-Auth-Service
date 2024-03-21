package org.traum.auth.plugins

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.traum.auth.di.*
import org.traum.auth.enums.AvailableServices
import org.traum.auth.plugins.extensions.checkOr
import org.traum.auth.plugins.extensions.requireOr
import org.traum.auth.useCases.AppAuthInteractor

@Serializable
data class JWTLoginDTO(val login: String, val password: String)

@Serializable
data class JWTRegistrationDTO<T : Any>(val login: String, val password: String, val data: T)

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
        route("jwt/login") {
            post {
                val dto = call.receive<JWTLoginDTO>()
                val data = LoginData(dto.login, dto.password)
                val token = appAuthInteractor.loginBasic(data)
                call.respond(token)
            }
        }

        route("jwt/registration") {
            post {
                val dto = call.receive<JWTRegistrationDTO<UserData>>()
                val regData = RegistrationData(dto.login, dto.password)
                val token = appAuthInteractor.registrationBasic(regData, dto.data)
                call.respond(token)
            }
        }
    }

    oauthProviders.forEach { (name, provider) ->
        authentication {
            oauth("auth-oauth-$name") {
                urlProvider = { "https://localhost:50001/${name.lowercase()}/callback" }
                providerLookup = { provider }
                client = HttpClient(Apache)
            }
        }

        routing {
            route(name.lowercase()) {
                get("login") {
                    call.sessions.set(OAuthSession(false, mapOf()))
                    call.respondRedirect("callback")
                }

                get("registration") {
                    val id = call.request.queryParameters["userId"]!!
                    call.sessions.set(OAuthSession(true, mapOf("id" to id)))
                    call.respondRedirect("callback")
                }

                /**
                 * OAuth authorization result handler
                 */
                authenticate("auth-oauth-$name") {
                    get("callback") {
                        val session = call.sessions.get<OAuthSession>()
                        checkOr(session != null) {
                            call.respond(HttpStatusCode.BadRequest, "not found session")
                        }
                        call.request.headers.forEach { key, str ->
                            println(key)
                            str.forEach {
                                println(it)
                            }
                            println()
                        }
                        requireOr(services.containsKey(name)) {
                            call.sessions.clear<OAuthSession>()
                            call.respond(HttpStatusCode.NotImplemented, "not OAuth implemented service")
                        }
                        val principal: OAuthAccessTokenResponse? = call.authentication.principal()

                        checkOr(principal != null) {
                            call.sessions.clear<OAuthSession>()
                            call.respond(HttpStatusCode.Unauthorized, "principal is null")
                        }

                        val data = runCatching { services[name]!!.fetchData(principal) }.onFailure {
                            call.sessions.clear<OAuthSession>()
                            call.respondText(it.toString())
                        }.getOrNull()!!


                        val token = if (session.isRegistration) {
                            val userId = session.values?.get("id")
                            checkOr(userId != null) {
                                call.sessions.clear<OAuthSession>()
                                call.respond(HttpStatusCode.BadRequest, "")
                            }
                            appAuthInteractor.registrationOAuth(
                                OAuthRegistrationData(userId, data.id, AvailableServices.valueOf(name)),
                                UserData(data.name)
                            )
                        } else
                            appAuthInteractor.loginOAuth(OAuthLoginData(data.id, AvailableServices.valueOf(name)))
                        call.sessions.clear<OAuthSession>()
                        call.respond(token)
                    }
                }
            }
        }
    }
}