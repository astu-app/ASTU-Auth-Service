package org.traum.auth.repositories

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.traum.auth.enums.AvailableServices
import java.util.UUID

class AuthOAuthRepository : IAuthOAuthRepository, KoinComponent {
    private val serviceDbContext: ServiceDbContext by inject()


    override fun getUserIdByOAuthIdAndService(oAuthId: String, service: AvailableServices): String {
        val oAuth = ServiceDbContext.OAuth
        return serviceDbContext.transaction {
            oAuth.select(oAuth.serviceId.eq(oAuthId) and oAuth.service.eq(service.name))
                .single()[oAuth.accountId].toString()
        }
    }

    override fun containsUserId(userId: String): Boolean {
        val oAuth = ServiceDbContext.OAuth
        return serviceDbContext.transaction {
            oAuth.select(oAuth.accountId.eq(UUID.fromString(userId)))
                .any()
        }
    }

    override fun addAuthData(oAuthId: String, userId: String, service: AvailableServices): String {
        val oAuth = ServiceDbContext.OAuth
        return serviceDbContext.transaction {
            val id = oAuth.insert {
                it[serviceId] = oAuthId
                it[accountId] = UUID.fromString(userId)
                it[this.service] = service.name
            } get oAuth.id
            id.toString()
        }
    }
}