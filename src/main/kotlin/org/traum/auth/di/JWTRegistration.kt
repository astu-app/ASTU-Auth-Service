package org.traum.auth.di

import io.ktor.server.auth.*

fun interface JWTRegistration {
    fun AuthenticationConfig.call(name: String)
}