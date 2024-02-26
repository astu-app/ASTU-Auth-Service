package org.traum.auth.di

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.Module
import org.koin.dsl.module
import org.traum.auth.enums.AvailableServices
import org.traum.auth.repositories.*
import org.traum.auth.services.YandexOAuthService

/**
 * Registering Objects for Dependency Injection
 */
fun Application.initModule(): Module {
    val oAuthClients = OAuthClients(
        AvailableServices.Yandex.name to OAuthClient(
            System.getenv("YANDEX_CLIENT_ID"),
            System.getenv("YANDEX_CLIENT_SECRET")
        ),
        AvailableServices.Google.name to OAuthClient(
            System.getenv("GOOGLE_CLIENT_ID"),
            System.getenv("GOOGLE_CLIENT_SECRET")
        )
    )

    val jwtConfig = JWTConfig(
        audience = environment.config.property("jwt.audience").getString(),
        domain = environment.config.property("jwt.domain").getString(),
        realm = environment.config.property("jwt.realm").getString(),
        secret = environment.config.property("jwt.secret").getString()
    )

    val jwtRegistration = JWTRegistration {
        jwt(it) {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.domain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtConfig.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    val jwtCreator = JWTCreator<String, Tokens> {
        val jwt = JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.domain)
            .withClaim("id", it)
            .sign(
                Algorithm.HMAC256(jwtConfig.secret)
            )
        Tokens(jwt.toString(), jwt.toString())
    }

    val database = Database.connect(
        "jdbc:postgresql://localhost:5432/test",
        driver = "org.postgresql.Driver",
        "admin",
        "admin"
    )

    val providers = OAuth2Providers(
        AvailableServices.Google.name.let { name ->
            val client = oAuthClients.getOrElse(name) { throw Exception("not defined service: $name") }
            name to OAuthServerSettings.OAuth2ServerSettings(
                name = name,
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                requestMethod = HttpMethod.Post,
                clientId = client.id,
                clientSecret = client.secret,
                defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
            )
        },
        AvailableServices.Yandex.name.let { name ->
            val client = oAuthClients.getOrElse(name) { throw Exception("not defined service: $name") }
            name to OAuthServerSettings.OAuth2ServerSettings(
                name = AvailableServices.Yandex.name,
                authorizeUrl = "https://oauth.yandex.ru/authorize",
                accessTokenUrl = "https://oauth.yandex.ru/token",
                requestMethod = HttpMethod.Post,
                clientId = client.id,
                clientSecret = client.secret,
            )
        }
    )

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    return module {
        single { oAuthClients }

        single { providers }

        single { httpClient }

        single { jwtCreator }

        single { jwtRegistration }

        single { database }

        single(createdAtStart = true) { ServiceDbContext(get()) }
        single<IAuthOAuthRepository> { AuthOAuthRepository() }
        single<IAccountRepository<UserData>> { AccountRepository() }
        single<IAuthBasicRepository> { AuthBasicRepository() }

        single {
            Services(
                AvailableServices.Yandex.name to YandexOAuthService()
            )
        }
    }
}