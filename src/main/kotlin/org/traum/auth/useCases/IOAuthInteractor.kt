package org.traum.auth.useCases

/**
 * Oauth Authorization Interface
 */
interface IOAuthInteractor<AuthResult, LoginData, RegistrationData, userData> {

    /**
     * Authorizes the user
     */
    fun loginOAuth(loginData: LoginData): AuthResult

    /**
     * Registers a user
     */
    fun registrationOAuth(oAuthData: RegistrationData, userData: userData): AuthResult
}