package org.traum.auth.useCases

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.Claim
import org.koin.core.component.KoinComponent

class JWTPayloadInteractor : KoinComponent {

    fun getUserId(x: String): String? = decodeJwt(x)["id"]?.asString()

    private fun decodeJwt(str: String): Map<String, Claim> = JWT.decode(str).claims
}