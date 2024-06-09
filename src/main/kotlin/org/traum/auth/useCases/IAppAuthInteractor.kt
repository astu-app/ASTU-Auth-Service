package org.traum.auth.useCases

import org.traum.auth.di.*
import java.util.*

interface IAppAuthInteractor :
    IAuthInteractor<Tokens, UUID, LoginData, RegistrationData, OAuthLoginData, OAuthRegistrationData>