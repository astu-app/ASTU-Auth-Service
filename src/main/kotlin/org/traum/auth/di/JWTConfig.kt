package org.traum.auth.di

data class JWTConfig(val audience: String, val domain: String, val realm: String, val secret: String)
