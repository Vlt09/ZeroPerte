package com.zeroperte

import com.zeroperte.Repository.FoodRepository
import com.zeroperte.model.Food
import com.zeroperte.model.foodAsRow
import com.zeroperte.model.foodAsTable
import com.zeroperte.model.getMemperPropertiesString
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import reactor.util.function.Tuple7

fun Application.configureRouting() {
    val foodRepository by inject<FoodRepository>();

    routing {
        staticResources("/zeroperte_ui", "zeroperte_ui")

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

        get("/foods/{id?}"){
            val idAsText = call.parameters["id"]
            if (idAsText == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val food = foodRepository.findById(idAsText.toLong())

                if (food == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respondText(
                    contentType = ContentType.parse("text/html; charset=utf-8"),
                    text = food.foodAsRow()
                )

            }catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/food"){
            val formContent = call.receiveParameters()

            // Instead of having declared list in which we have one line per Food properties, we use reflection
            val params = Food.getMemberPropertiesString().associateWith { d -> (formContent[d] ?: "") }

            if (params.entries.any { it.value.isEmpty() }) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            /*try {

            }*/
        }


    }
}