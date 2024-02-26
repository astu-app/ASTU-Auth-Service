package org.traum.auth.repositories

import org.jetbrains.exposed.sql.insert
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.traum.auth.di.UserData

class AccountRepository : IAccountRepository<UserData>, KoinComponent {
    private val serviceDbContext: ServiceDbContext by inject()

    override fun addAccountData(userData: UserData): String {
        val account = ServiceDbContext.Account
        return serviceDbContext.transaction {
            val id = account.insert {
                it[name] = userData.name
            } get account.id
            id
        }.toString()
    }
}