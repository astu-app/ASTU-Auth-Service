package org.traum.auth.di

import org.traum.auth.services.OAuthService
import org.traum.auth.services.OAuthUserDataResponse

class Services(private val value: Map<String, OAuthService<OAuthUserDataResponse>>) :
    Map<String, OAuthService<OAuthUserDataResponse>> by value {
    constructor(vararg pairs: Pair<String, OAuthService<OAuthUserDataResponse>>) : this(pairs.toMap())
}