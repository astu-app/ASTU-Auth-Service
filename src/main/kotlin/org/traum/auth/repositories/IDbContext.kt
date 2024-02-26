package org.traum.auth.repositories

import org.jetbrains.exposed.sql.Transaction

interface IDbContext {

    fun <T> transaction(block: Transaction.() -> T): T
}