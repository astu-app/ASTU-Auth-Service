package org.traum.auth.services

import io.ktor.server.auth.*

/**
 * Service for working with OAuth services
 */
interface OAuthService<RegistrationData> {
    /**
     * Fetch user data from the OAuth service
     */
    suspend fun fetchData(oAuthToken: OAuthAccessTokenResponse): RegistrationData
}