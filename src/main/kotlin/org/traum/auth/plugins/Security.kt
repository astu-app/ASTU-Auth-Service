package org.traum.auth.plugins

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.traum.auth.di.*
import org.traum.auth.enums.AvailableServices
import org.traum.auth.plugins.extensions.checkOr
import org.traum.auth.plugins.extensions.requireOr
import org.traum.auth.useCases.AppAuthInteractor

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
            get {
                val login = call.request.queryParameters["login"].toString()
                val password = call.request.queryParameters["password"].toString()
                val data = LoginData(login, password)
                val token = appAuthInteractor.loginBasic(data)
                call.respond(token)
            }
        }

        route("jwt/registration") {
            get {
                val login = call.request.queryParameters["login"].toString()
                val password = call.request.queryParameters["password"].toString()
                val regData = RegistrationData(login, password)
                val userData = UserData("name")
                val token = appAuthInteractor.registrationBasic(regData, userData)
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
                    call.respondRedirect("callback")
                }

                get("registration") {
                    call.respondRedirect("callback")
                }

                /**
                 * OAuth authorization result handler
                 */
                authenticate("auth-oauth-$name") {
                    get("callback") {
                        requireOr(services.containsKey(name)) {
                            call.respond(HttpStatusCode.NotImplemented, "not OAuth implemented service")
                        }
                        val principal: OAuthAccessTokenResponse? = call.authentication.principal()

                        checkOr(principal != null) {
                            call.respond(HttpStatusCode.Unauthorized, "principal is null")
                        }

                        runCatching { services[name]!!.fetchData(principal) }.onSuccess {
                            val token = appAuthInteractor.registrationOAuth(
                                OAuthRegistrationData("", it.id, AvailableServices.valueOf(name)),
                                UserData(it.name)
                            )
                            call.respond(token)
                        }.onFailure {
                            call.respondText(it.toString())
                        }
                    }
                }
            }
        }
    }
}