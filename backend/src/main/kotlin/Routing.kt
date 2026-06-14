package com.zeroperte

import com.zeroperte.Repository.FoodRepository
import com.zeroperte.model.Food
import com.zeroperte.model.FoodPostDto
import com.zeroperte.model.foodAsRow
import com.zeroperte.model.foodAsTable
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import org.koin.ktor.ext.inject
import kotlin.reflect.full.primaryConstructor

internal val LOGGER = KtorSimpleLogger("com.zeroperte.RoutingLogger")

fun Application.configureRouting() {
    val foodRepository by inject<FoodRepository>();

    routing {
        staticResources("static", "static")
        staticResources("/zeroperte_ui", "zeroperte_ui")


        get("/") {
            call.respondText("Hello, World!")
        }
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/foods"){
            val foodsList = foodRepository.allFoods()
            call.respond(
                foodsList
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

                call.respond(food)

            }catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/foods"){
            val formContent = call.receiveParameters()

            // Instead of having declared list in which we have one line per Food properties, we use reflection
            val params = Food.getMemberPropertiesString().associateWith { d -> (formContent[d] ?: "") }

            try {

                val constructor = FoodPostDto::class.primaryConstructor!!
                val args = constructor.parameters.associateWith { p ->
                    val rawValue = params[p.name] ?: ""
                    Food.converters[p.name]?.invoke(rawValue)
                }

                val foodDto = constructor.callBy(args)
                val id = foodRepository.create(foodDto)

                call.respond(HttpStatusCode.Created, id)
                LOGGER.info("food has been created with id $id: $foodDto")


            } catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
            }
        }

        put("/food/{id}"){
            val formContent = call.receiveParameters()

        }

    }
}