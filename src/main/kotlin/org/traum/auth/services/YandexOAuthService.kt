package org.traum.auth.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Service for working with Yandex OAuth service
 */
class YandexOAuthService : OAuthService<OAuthUserDataResponse>, KoinComponent {
    /**
     * User data in the DTO form
     */
    @Serializable
    data class UserDataDTO(
        val id: String,
        @SerialName("default_email") val defaultEmail: String,
        @SerialName("first_name") val firstName: String
    )

    private val client: HttpClient by inject()

    /**
     * Fetch user data from the OAuth service
     */
    override suspend fun fetchData(oAuthToken: OAuthAccessTokenResponse): OAuthUserDataResponse {
        require(oAuthToken is OAuthAccessTokenResponse.OAuth2)

        val response = client.get("https://login.yandex.ru/info") {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${oAuthToken.accessToken}")
            }
            parameters {
                append("format", "json")
            }
        }

        check(response.status.isSuccess()) { response.status.toString() }

        val data: UserDataDTO = response.body()

        return OAuthUserDataResponse(data.id, data.firstName)
    }
}