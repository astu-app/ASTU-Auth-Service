package org.traum.auth.di

import kotlinx.serialization.Serializable
import org.traum.auth.enums.AvailableServices

data class LoginData(val login: String, val password: String)

data class OAuthLoginData(val id: String, val service: AvailableServices)

data class OAuthRegistrationData(val id: String, val serviceId: String, val service: AvailableServices)

data class RegistrationData(val login: String, val password: String)

@Serializable
data class UserData(val name: String)