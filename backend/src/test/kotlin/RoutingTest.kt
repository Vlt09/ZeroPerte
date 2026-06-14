import com.zeroperte.Repository.FoodRepository
import com.zeroperte.configureRouting
import com.zeroperte.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoutingTest {

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:routingtest;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(com.zeroperte.Repository.FoodTable)
        }
    }

    private fun ApplicationTestBuilder.configureTestApp() {
        application {
            install(Koin) {
                slf4jLogger()
                modules(module {
                    single { FoodRepository() }
                })
            }
            configureSerialization()
            configureRouting()
        }
    }

    @Test
    fun `GET racine retourne Hello World`() = testApplication {
        configureTestApp()

        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, World!", response.bodyAsText())
    }

    @Test
    fun `GET foods retourne une table vide si aucun aliment`() = testApplication {
        configureTestApp()

        val response = client.get("/foods")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().startsWith("<table rules=\"all\">"))
    }

    @Test
    fun `GET foods retourne les aliments existants`() = testApplication {
        configureTestApp()

        // insertion directe via le repository pour préparer la donnée
        val repository = FoodRepository()
        repository.create(
            com.zeroperte.model.FoodPostDto(
                name = "Yaourt",
                brand = "Danone",
                category = "Laitier",
                datePurchased = LocalDate(2026, 6, 1),
                expiryDate = LocalDate(2026, 6, 15),
                comment = null,
                amount = 4
            )
        )

        val response = client.get("/foods")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Yaourt"))
        assertTrue(response.bodyAsText().contains("Danone"))
    }

    @Test
    fun `GET foods avec id valide retourne l'aliment`() = testApplication {
        configureTestApp()

        val repository = FoodRepository()
        val id = repository.create(
            com.zeroperte.model.FoodPostDto(
                name = "Lait",
                brand = "Lactel",
                category = "Laitier",
                datePurchased = LocalDate(2026, 6, 1),
                expiryDate = LocalDate(2026, 6, 10),
                comment = null,
                amount = 1
            )
        )

        val response = client.get("/foods/$id")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Lait"))
    }

    @Test
    fun `GET foods avec id inexistant retourne 404`() = testApplication {
        configureTestApp()

        val response = client.get("/foods/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET foods avec id non numerique retourne 400`() = testApplication {
        configureTestApp()

        val response = client.get("/foods/abc")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST food avec formulaire complet retourne 201 et un id`() = testApplication {
        configureTestApp()

        val response = client.post("/food") {
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            setBody(
                listOf(
                    "id" to "0",
                    "name" to "Pomme",
                    "brand" to "Bio",
                    "category" to "Fruit",
                    "datePurchased" to "2026-06-01T10:00",
                    "expiryDate" to "2026-06-20T00:00",
                    "comment" to "Vert",
                    "amount" to "6"
                ).formUrlEncode()
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `POST food avec champ manquant retourne 400`() = testApplication {
        configureTestApp()

        val response = client.post("/food") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "name" to "Pomme",
                    // champs manquants
                ).formUrlEncode()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}