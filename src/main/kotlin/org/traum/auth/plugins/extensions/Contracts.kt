package org.traum.auth.plugins.extensions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun requireOr(value: Boolean, action: () -> Unit) {
    contract {
        returns() implies value
    }
    if (!value)
        action()
}

@OptIn(ExperimentalContracts::class)
inline fun checkOr(value: Boolean, action: () -> Unit) {
    contract {
        returns() implies value
    }
    if (!value)
        action()
}