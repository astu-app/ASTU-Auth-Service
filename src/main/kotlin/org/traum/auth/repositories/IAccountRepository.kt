package org.traum.auth.repositories

interface IAccountRepository<UserData> {
    fun addAccountData(userData: UserData): String
}