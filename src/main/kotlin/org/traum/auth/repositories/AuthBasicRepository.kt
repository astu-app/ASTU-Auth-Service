package org.traum.auth.repositories

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class AuthBasicRepository : IAuthBasicRepository, KoinComponent {
    private val serviceDbContext: ServiceDbContext by inject()

    override fun getPasswordAndSaltByLogin(login: String): Pair<String, String> {
        val basicAuth = ServiceDbContext.BasicAuth
        return serviceDbContext.transaction {
            val resultRow = basicAuth.select(basicAuth.login.eq(login))
                .single()
            resultRow[basicAuth.hash] to resultRow[basicAuth.salt]
        }
    }

    override fun getUserIdByLogin(login: String): String {
        val basicAuth = ServiceDbContext.BasicAuth
        return serviceDbContext.transaction {
            basicAuth.select(basicAuth.login.eq(login))
                .single()[basicAuth.accountId].toString()
        }
    }

    override fun containsLogin(login: String): Boolean {
        val basicAuth = ServiceDbContext.BasicAuth
        return serviceDbContext.transaction {
            basicAuth.select(basicAuth.login.eq(login))
                .any()
        }
    }

    override fun addAuthData(userId: String, login: String, passwordHash: String, salt: String): String {
        val basicAuth = ServiceDbContext.BasicAuth
        return serviceDbContext.transaction {
            basicAuth.insert {
                it[this.salt] = salt
                it[accountId] = UUID.fromString(userId)
                it[this.hash] = passwordHash
                it[this.login] = login
            } get basicAuth.login
        }
    }
}