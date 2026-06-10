package com.zeroperte

import com.zeroperte.Repository.FoodRepository
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

internal val main = module {
    single { FoodRepository() }
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(main)
    }
}
