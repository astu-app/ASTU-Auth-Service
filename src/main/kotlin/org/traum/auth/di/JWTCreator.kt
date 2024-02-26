package org.traum.auth.di

fun interface JWTCreator<UserCredentials, OutputData> {
    fun create(userCredentials: UserCredentials): OutputData
}