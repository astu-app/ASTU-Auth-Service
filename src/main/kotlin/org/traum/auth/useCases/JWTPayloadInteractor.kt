package org.traum.auth.useCases

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.Claim
import org.koin.core.component.KoinComponent

class JWTPayloadInteractor : KoinComponent {
    fun getUserId(x: String): String? = decodeJwt(x)["id"]?.asString()
    private fun decodeJwt(str: String): Map<String, Claim> = JWT.decode(removeBearer(str)).claims

    private fun removeBearer(str: String): String {
        if (str.trim().startsWith("Bearer "))
            return str.trim().removePrefix("Bearer ").trim()
        return str
    }
}