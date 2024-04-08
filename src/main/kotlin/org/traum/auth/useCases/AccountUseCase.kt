package org.traum.auth.useCases

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.traum.auth.UserInfo
import org.traum.auth.di.UserData
import org.traum.auth.repositories.IAccountRepository

class AccountUseCase : IAccountUseCase, KoinComponent {
    private val accountRepository: IAccountRepository<UserData> by inject()

    override suspend fun getUserInfo(userId: String): UserInfo? = accountRepository.getAccountInfo(userId)
    override suspend fun getAllUserInfo(): List<UserInfo> = accountRepository.getAllAccountInfo()
}