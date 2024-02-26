package org.traum.auth.repositories


interface IAuthBasicRepository {
    fun getPasswordAndSaltByLogin(login: String): Pair<String, String>

    fun getUserIdByLogin(login: String): String

    fun containsLogin(login: String): Boolean

    fun addAuthData(userId: String, login: String, passwordHash: String, salt: String): String
}

