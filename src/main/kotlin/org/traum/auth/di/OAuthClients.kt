package org.traum.auth.di

class OAuthClients(private val value: Map<String, OAuthClient>) : Map<String, OAuthClient> by value {
    constructor(vararg pairs: Pair<String, OAuthClient>) : this(pairs.toMap())
}
