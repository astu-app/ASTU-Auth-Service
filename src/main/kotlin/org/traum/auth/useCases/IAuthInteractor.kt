package org.traum.auth.useCases

interface IAuthInteractor<AuthResult, UserData, BasicLoginData, BasicRegistrationData, OAuthLoginData, OAuthRegistrationData> :
    IBasicAuthInteractor<AuthResult, BasicLoginData, BasicRegistrationData, UserData>,
    IOAuthInteractor<AuthResult, OAuthLoginData, OAuthRegistrationData, UserData>

