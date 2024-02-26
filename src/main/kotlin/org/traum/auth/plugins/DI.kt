package org.traum.auth.plugins

import io.ktor.server.application.*
import org.koin.core.context.startKoin
import org.traum.auth.di.initModule

fun Application.configureDI() {
    startKoin {
        modules(
            this@configureDI.initModule()
        )
    }
}