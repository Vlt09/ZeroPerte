package com.zeroperte

import com.zeroperte.model.foodAsTable
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/foods"){
            call.respondText(
                contentType = ContentType.parse("text/html; charset=utf-8"),
                text = "Food"
            )
        }

    }
}