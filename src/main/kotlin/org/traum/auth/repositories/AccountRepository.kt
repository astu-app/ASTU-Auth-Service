package org.traum.auth.repositories

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.traum.auth.UserInfo
import org.traum.auth.di.UserData
import java.util.UUID

class AccountRepository : IAccountRepository<UserData>, KoinComponent {
    private val serviceDbContext: ServiceDbContext by inject()

    override suspend fun addAccountData(userData: UserData): String {
        val account = ServiceDbContext.Account
        return serviceDbContext.transaction {
            val id = account.insert {
                it[name] = userData.name
            } get account.id
            id
        }.toString()
    }

    override suspend fun getAccountInfo(userId: String): UserInfo? {
        val account = ServiceDbContext.Account
        val userUUID = UUID.fromString(userId)
        return serviceDbContext.transaction {
            account.select {
                account.id eq userUUID
            }.singleOrNull()
        }?.run { UserInfo(userId, this[account.name]) }
    }

    override suspend fun getAllAccountInfo(): List<UserInfo> {
        val account = ServiceDbContext.Account
        return serviceDbContext.transaction {
            account.selectAll().toList()
        }.map {
            UserInfo(it[account.id].toString(), it[account.name])
        }
    }
}