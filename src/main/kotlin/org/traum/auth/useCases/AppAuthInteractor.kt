package org.traum.auth.useCases

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.digest.SHA256
import io.ktor.util.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.traum.auth.di.*
import org.traum.auth.exception.LoginFailedException
import org.traum.auth.repositories.IAuthBasicRepository
import org.traum.auth.repositories.IAuthOAuthRepository
import java.util.*

class AppAuthInteractor : IAppAuthInteractor, KoinComponent {

    private val authOAuthRepository: IAuthOAuthRepository by inject()
    private val authBasicRepository: IAuthBasicRepository by inject()
    private val jwtCreator: JWTCreator<String, Tokens> by inject()

    /**
     * Authorizes the user based on the data
     */
    override suspend fun loginBasic(loginData: LoginData): Tokens {

        val (passwordHashInDb, salt) = runCatching {
            authBasicRepository.getPasswordAndSaltByLogin(loginData.login)
        }.getOrElse {
            throw LoginFailedException("Неправильный логин или пароль")
        }

        val passwordHash = createHash(loginData.password, salt)

        check(passwordHash == passwordHashInDb)

        val id: String = authBasicRepository.getUserIdByLogin(loginData.login)

        return jwtCreator.create(id)
    }

    /**
     * Registers a user based on registration data and user data
     */
    override suspend fun registrationBasic(registrationData: RegistrationData, userData: UUID): Tokens {
        val id = runCatching { authBasicRepository.getUserIdByLogin(registrationData.login) }
            .getOrElse {
                val salt = UUID.randomUUID().toString()
                val passwordHash = createHash(registrationData.password, salt)
                authBasicRepository.addAuthData(userData, registrationData.login, passwordHash, salt)
                userData.toString()
            }
        return jwtCreator.create(id)
    }

    /**
     * Authorizes the user
     */
    override suspend fun loginOAuth(loginData: OAuthLoginData): Tokens {
        ///todo add cache
        val id = authOAuthRepository.getUserIdByOAuthIdAndService(loginData.id, loginData.service)
        return jwtCreator.create(id)
    }

    /**
     * Registers a user
     */
    override suspend fun registrationOAuth(oAuthData: OAuthRegistrationData, userData: UUID): Tokens {
        runCatching {
            authOAuthRepository.getUserIdByOAuthIdAndService(oAuthData.serviceId, oAuthData.service)
        }.onFailure {
            authOAuthRepository.addAuthData(oAuthData.serviceId, userData.toString(), oAuthData.service)
            return jwtCreator.create(userData.toString())
        }
        TODO("Account is already registered")
    }

    private suspend fun createHash(password: String, salt: String): String =
        CryptographyProvider.Default.get(SHA256).hasher().hash((password + salt).encodeToByteArray())
            .encodeBase64()
}