package com.zeroperte

import com.zeroperte.Repository.FoodRepository
import com.zeroperte.Service.FoodService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

internal val main = module {
    single { FoodRepository() }
    single { FoodService() }
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(main)
    }
}
