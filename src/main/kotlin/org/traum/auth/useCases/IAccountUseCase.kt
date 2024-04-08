package org.traum.auth.useCases

import org.traum.auth.UserInfo

interface IAccountUseCase {
    suspend fun getUserInfo(userId: String): UserInfo?
    suspend fun getAllUserInfo(): List<UserInfo>
}