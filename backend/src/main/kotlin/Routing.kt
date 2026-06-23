package com.zeroperte

import com.zeroperte.Repository.FoodRepository
import com.zeroperte.model.Food
import com.zeroperte.model.FoodDto
import com.zeroperte.model.FoodPostDto
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.KtorSimpleLogger
import org.koin.ktor.ext.inject
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

internal val LOGGER = KtorSimpleLogger("com.zeroperte.RoutingLogger")

internal val filterPropertyGetRoute = listOf<String>(
    "name",
    "category",
    "brand"
)

/**
 * Converts form parameters into a DTO instance using reflection.
 *
 * Instead of manually mapping each field, this function dynamically retrieves
 * the properties of [Food] and the constructor parameters of the provided [dtoConstructor],
 * then uses [Food.converters] to convert each raw string value to its expected type.
 *
 * This generic approach allows the same function to be reused across multiple DTO types
 * (e.g. [FoodPostDto], [FoodDto]), avoiding duplication for each form submission.
 *
 * @param T The DTO type to instantiate.
 * @param formContent The form parameters received from the HTTP request.
 * @param dtoConstructor The primary constructor of the target DTO class, obtained via reflection.
 * @return An instance of [T] populated with the converted form values.
 *
 * @throws IllegalArgumentException If a required field is missing or cannot be converted.
 * @throws NullPointerException If the provided constructor is null.
 *
 * @see Food.getMemberPropertiesString
 * @see Food.converters
 */
internal fun <T> formContentToFoodDto(formContent: Parameters, dtoConstructor: KFunction<T>): T{
    // Instead of having declared list in which we have one line per Food properties, we use reflection
    val params = Food.getMemberPropertiesString().associateWith { d -> (formContent[d] ?: "") }

    val args = dtoConstructor.parameters.associateWith { p -> // Associate each data name field to param from form
        val rawValue = params[p.name] ?: ""
        Food.converters[p.name]?.invoke(rawValue)
    }

    return dtoConstructor.callBy(args)
}
private suspend fun getParameterFromURL(call: RoutingCall, parameterName: String): String?{
    val foodEntityProperty = call.parameters[parameterName]

    if (foodEntityProperty == null) {
        call.respond(HttpStatusCode.BadRequest)
        return null
    }
    return foodEntityProperty

}

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

        route("/foods"){
            route("/{id}") {
                get{
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

                put{
                    val formContent = call.receiveParameters()
                    val idAsText = call.parameters["id"]

                    if (idAsText == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@put
                    }

                    try {
                        val constructor = FoodDto::class.primaryConstructor!!

                        val foodDto = formContentToFoodDto<FoodDto>(formContent, constructor)
                        val updatedFood = foodRepository.update(idAsText.toLong(), foodDto)

                        if (updatedFood == null) {
                            call.respond(HttpStatusCode.BadRequest)
                            return@put
                        }

                        call.respond(HttpStatusCode.OK, updatedFood.id)
                        LOGGER.info("food with id $updatedFood.id has been updated : $foodDto")


                    } catch (ex: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
                    } catch (ex: IllegalStateException) {
                        call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
                    }
                }

                delete{
                    val idAsText = call.parameters["id"]

                    if (idAsText == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@delete
                    }

                    try {
                        val id = foodRepository.delete(idAsText.toLong())

                        if (id == 0){
                            call.respond(HttpStatusCode.NotFound)
                            return@delete
                        }

                        LOGGER.info("food has been deleted : $id")
                        call.respond(HttpStatusCode.NoContent)

                    }catch (ex: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

            }

            get{
                val foodsList = foodRepository.allFoods()

                LOGGER.info("Get all foods : ${foodsList.size} has been found")
                call.respond(
                    foodsList
                )
            }

            get("/name={name}") {
                val foodEntityName = getParameterFromURL(call, "name") ?: return@get

                try {
                    val foodListByName = foodRepository.findByName(foodEntityName)

                    LOGGER.info("$foodEntityName has been found ${foodListByName.size}")
                    call.respond(foodListByName)

                }catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("/category={category}") {
                val foodEntityCategory = getParameterFromURL(call, "category") ?: return@get

                try {
                    val foodListByName = foodRepository.findByCategory(foodEntityCategory)

                    LOGGER.info("$foodEntityCategory has been found ${foodListByName.size}")
                    call.respond(foodListByName)

                }catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("/brand={brand}") {
                val foodEntityBrand = getParameterFromURL(call, "brand") ?: return@get

                try {
                    val foodListByName = foodRepository.findByBrand(foodEntityBrand)

                    LOGGER.info("$foodEntityBrand has been found ${foodListByName.size}")
                    call.respond(foodListByName)

                }catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("/expired={expired}") {
                val expiredBoolString = getParameterFromURL(call, "expired") ?: return@get

                try {
                    val expired = expiredBoolString.toBoolean()
                    val expiredFoodList = foodRepository.findExpired(expired)

                    LOGGER.info("${expiredFoodList.size} expired food has been found")
                    call.respond(expiredFoodList)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("/expired={expired}&days={days}") {
                val expiredBoolString = getParameterFromURL(call, "expired") ?: return@get
                val days = getParameterFromURL(call, "days") ?: return@get

                try {
                    val expired = expiredBoolString.toBoolean()
                    if (!expired){
                        LOGGER.warn("Expired days bool is false")
                        return@get
                    }

                    val expiredFoodList = foodRepository.findExpiring(days.toInt())

                    LOGGER.info("${expiredFoodList.size} expired food has been found")
                    call.respond(expiredFoodList)

                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post{
                val formContent = call.receiveParameters()

                try {
                    // Use reflection to avoid writing all data properties for FoodPostDto
                    val constructor = FoodPostDto::class.primaryConstructor!!

                    val foodDto = formContentToFoodDto(formContent, constructor)
                    val id = foodRepository.create(foodDto)

                    call.respond(HttpStatusCode.Created, id)
                    LOGGER.info("food has been created with id $id: $foodDto")


                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
                }
            }
        }
    }
}