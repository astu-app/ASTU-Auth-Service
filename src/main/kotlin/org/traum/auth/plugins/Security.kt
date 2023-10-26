package org.traum.auth.plugins

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.traum.auth.di.OAuth2Providers
import org.traum.auth.di.Services
import org.traum.auth.plugins.extensions.checkOr
import org.traum.auth.plugins.extensions.requireOr
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val oauthProviders by inject<OAuth2Providers>()
    val services by inject<Services>()

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
                            call.respondText("${it.id}\n ${it.name}")
                        }.onFailure {
                            call.respondText(it.toString())
                        }
                    }
                }
            }
        }
    }
}