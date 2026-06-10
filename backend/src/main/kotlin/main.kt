package com.zeroperte

import com.zeroperte.config.DbConfig
import io.ktor.server.engine.*
import io.ktor.server.application.*
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        configureExposed()
        configureHttp()
        configureRouting()
        configureSerialization()
        configureStatusPages()
        configureKoin()

    }.start(wait = true)


    //io.ktor.server.netty.EngineMain.main(args)
}
