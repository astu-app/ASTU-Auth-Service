package org.traum.auth.useCases

/**
 * Basic authorization interface, e.g. password-based
 */
interface IBasicAuthInteractor<AuthResult, LoginData, RegistrationData, userData> {

    /**
     * Authorizes the user based on the data
     */
    suspend fun loginBasic(loginData: LoginData): AuthResult

    /**
     * Registers a user based on registration data and user data
     */
    suspend fun registrationBasic(registrationData: RegistrationData, userData: userData): AuthResult
}