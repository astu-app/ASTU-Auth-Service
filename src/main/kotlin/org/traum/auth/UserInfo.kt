package org.traum.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(val userId: String, val name: String)