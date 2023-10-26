package org.traum.auth.di

import io.ktor.server.auth.*

class OAuth2Providers(private val value: Map<String, OAuthServerSettings.OAuth2ServerSettings>) :
    Map<String, OAuthServerSettings.OAuth2ServerSettings> by value {
    constructor(vararg pairs: Pair<String, OAuthServerSettings.OAuth2ServerSettings>) : this(pairs.toMap())
}