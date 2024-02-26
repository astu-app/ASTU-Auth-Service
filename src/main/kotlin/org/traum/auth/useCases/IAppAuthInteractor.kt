package org.traum.auth.useCases

import org.traum.auth.di.*

interface IAppAuthInteractor :
    IAuthInteractor<Tokens, UserData, LoginData, RegistrationData, OAuthLoginData, OAuthRegistrationData>