package org.traum.auth.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ServiceDbContext(private val db: Database) : IDbContext {

    object OAuth : Table("oauths") {
        val id: Column<UUID> = uuid("id").autoGenerate()
        val accountId: Column<UUID> = (uuid("account_id") references Account.id)

        val serviceId: Column<String> = text("service_id")
        val service: Column<String> = text("service")

        override val primaryKey = PrimaryKey(id)
    }

    object BasicAuth : Table("basic_auths") {
        val accountId: Column<UUID> = (uuid("account_id") references Account.id)

        val login: Column<String> = text("login")
        val salt: Column<String> = text("salt")
        val hash: Column<String> = text("hash")

        override val primaryKey = PrimaryKey(login)
    }

    object Account : Table("accounts") {
        val id: Column<UUID> = uuid("id").autoGenerate()

        val name: Column<String> = text("name")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction {
            SchemaUtils.create(OAuth, Account, BasicAuth)
        }
    }

    override fun <T> transaction(block: Transaction.() -> T): T {
        return transaction(db, block)
    }
}