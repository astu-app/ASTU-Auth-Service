package org.traum.auth.repositories

import org.traum.auth.enums.AvailableServices

interface IAuthOAuthRepository {
    fun getUserIdByOAuthIdAndService(oAuthId: String, service: AvailableServices): String

    fun containsUserId(userId: String): Boolean

    fun addAuthData(oAuthId: String, userId: String, service: AvailableServices): String
}