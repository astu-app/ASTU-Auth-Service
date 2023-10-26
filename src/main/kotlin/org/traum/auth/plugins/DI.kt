package org.traum.auth.plugins

import io.ktor.server.application.*
import org.traum.auth.di.initModule
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(
            this@configureDI.initModule()
        )
    }
}