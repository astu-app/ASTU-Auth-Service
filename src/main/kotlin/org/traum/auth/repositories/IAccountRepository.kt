package org.traum.auth.repositories

import org.traum.auth.UserInfo

interface IAccountRepository<UserData> {
    suspend fun addAccountData(userData: UserData): String

    suspend fun getAccountInfo(userId: String): UserInfo?

    suspend fun getAllAccountInfo(): List<UserInfo>
}