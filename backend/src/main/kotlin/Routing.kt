package com.zeroperte

import com.zeroperte.Repository.FoodRepository
import com.zeroperte.model.Food
import com.zeroperte.model.foodAsTable
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val foodRepository by inject<FoodRepository>();

    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/foods"){
            val foodsList = foodRepository.foodsList
            call.respondText(
                contentType = ContentType.parse("text/html; charset=utf-8"),
                text = foodsList.foodAsTable()
            )
        }

    }
}