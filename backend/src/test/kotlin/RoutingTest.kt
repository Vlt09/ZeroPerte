import com.zeroperte.Repository.FoodRepository
import com.zeroperte.Repository.FoodTable
import com.zeroperte.configureRouting
import com.zeroperte.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

class RoutingTest {

    private lateinit var repository: FoodRepository

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:routingtest;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        repository = FoodRepository()
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(FoodTable)
        }
    }

    private fun ApplicationTestBuilder.configureTestApp() {
        application {
            install(Koin) {
                slf4jLogger()
                modules(module { single { repository } })
            }
            configureSerialization()
            configureRouting()
        }
    }

    private fun sampleFoodPostDto(
        name: String = "Yaourt",
        expiryDate: LocalDate = LocalDate(2099, 6, 15)
    ) = com.zeroperte.model.FoodPostDto(
        name = name,
        brand = "Danone",
        category = "Produit laitier",
        datePurchased = LocalDate(2026, 6, 1),
        expiryDate = expiryDate,
        comment = "A consommer rapidement",
        amount = 4
    )

    // ===== GET / =====

    @Test
    fun `GET racine retourne Hello World`() = testApplication {
        configureTestApp()
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, World!", response.bodyAsText())
    }

    // ===== GET /foods =====

    @Test
    fun `GET foods retourne liste vide si aucun aliment`() = testApplication {
        configureTestApp()
        val response = client.get("/foods") {
            accept(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun `GET foods retourne les aliments existants en JSON`() = testApplication {
        configureTestApp()
        repository.create(sampleFoodPostDto("Yaourt"))
        repository.create(sampleFoodPostDto("Lait"))

        val response = client.get("/foods") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Yaourt"))
        assertTrue(response.bodyAsText().contains("Lait"))
    }

    // ===== GET /foods/{id} =====

    @Test
    fun `GET foods id valide retourne l'aliment`() = testApplication {
        configureTestApp()
        val id = repository.create(sampleFoodPostDto("Lait"))

        val response = client.get("/foods/$id") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Lait"))
    }

    @Test
    fun `GET foods id inexistant retourne 404`() = testApplication {
        configureTestApp()
        val response = client.get("/foods/999") {
            accept(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET foods id non numérique retourne 400`() = testApplication {
        configureTestApp()
        val response = client.get("/foods/abc") {
            accept(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    // ===== GET /foods/name= =====

    @Test
    fun `GET foods par name retourne les aliments correspondants`() = testApplication {
        configureTestApp()
        repository.create(sampleFoodPostDto("Yaourt"))
        repository.create(sampleFoodPostDto("Lait"))

        val response = client.get("/foods/name=Yaourt") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Yaourt"))
        assertFalse(response.bodyAsText().contains("Lait"))
    }

    @Test
    fun `GET foods par name inconnu retourne liste vide`() = testApplication {
        configureTestApp()
        val response = client.get("/foods/name=Inconnu") {
            accept(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    // ===== GET /foods/category= =====

    @Test
    fun `GET foods par category retourne les aliments correspondants`() = testApplication {
        configureTestApp()
        repository.create(sampleFoodPostDto("Yaourt"))
        repository.create(
            sampleFoodPostDto("Jus").copy(category = "Boisson")
        )

        val response = client.get("/foods/category=Produit laitier") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Yaourt"))
        assertFalse(response.bodyAsText().contains("Jus"))
    }

    // ===== GET /foods/brand= =====

    @Test
    fun `GET foods par brand retourne les aliments correspondants`() = testApplication {
        configureTestApp()
        repository.create(sampleFoodPostDto("Yaourt"))
        repository.create(sampleFoodPostDto("Lait").copy(brand = "Lactel"))

        val response = client.get("/foods/brand=Danone") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Yaourt"))
        assertFalse(response.bodyAsText().contains("Lactel"))
    }

    // ===== GET /foods/expired= =====

    @Test
    fun `GET foods expired true retourne les aliments périmés`() = testApplication {
        configureTestApp()
        repository.create(sampleFoodPostDto(expiryDate = LocalDate(2020, 1, 1)))
        repository.create(sampleFoodPostDto("Lait", expiryDate = LocalDate(2099, 1, 1)))

        val response = client.get("/foods/expired=true") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("2020"))
        assertFalse(response.bodyAsText().contains("2099"))
    }

    @Test
    fun `GET foods expired false retourne les aliments non périmés`() = testApplication {
        configureTestApp()
        repository.create(sampleFoodPostDto(expiryDate = LocalDate(2020, 1, 1)))
        repository.create(sampleFoodPostDto("Lait", expiryDate = LocalDate(2099, 1, 1)))

        val response = client.get("/foods/expired=false") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertFalse(response.bodyAsText().contains("2020"))
        assertTrue(response.bodyAsText().contains("2099"))
    }

    // ===== GET /foods/expired=true&days= =====

    @Test
    fun `GET foods expiring dans N jours retourne les aliments concernés`() = testApplication {
        configureTestApp()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val inThreeDays = today.plus(3, DateTimeUnit.DAY)
        val inTenDays = today.plus(10, DateTimeUnit.DAY)

        repository.create(sampleFoodPostDto("Bientot", expiryDate = inThreeDays))
        repository.create(sampleFoodPostDto("PasEncoreD", expiryDate = inTenDays))

        val response = client.get("/foods/expired=true/days=5") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Bientot"))
        assertFalse(response.bodyAsText().contains("PasEncoreD"))
    }

    @Test
    fun `GET foods expiring avec expired false retourne liste vide`() = testApplication {
        configureTestApp()
        val response = client.get("/foods/expired=false&days=5") {
            accept(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    // ===== POST /foods =====

    @Test
    fun `POST foods avec formulaire complet retourne 201`() = testApplication {
        configureTestApp()
        val response = client.post("/foods") {
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            setBody(
                listOf(
                    "name" to "Pomme",
                    "brand" to "Bio",
                    "category" to "Fruit",
                    "datePurchased" to "2026-06-01",
                    "expiryDate" to "2026-06-20",
                    "comment" to "Vert",
                    "amount" to "6"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `POST foods avec champ obligatoire manquant retourne 400`() = testApplication {
        configureTestApp()
        val response = client.post("/foods") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(listOf("name" to "Pomme").formUrlEncode())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    // ===== PUT /foods/{id} =====

    @Test
    fun `PUT foods id valide retourne 200`() = testApplication {
        configureTestApp()
        val id = repository.create(sampleFoodPostDto())

        val response = client.put("/foods/$id") {
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            setBody(
                listOf(
                    "name" to "Yaourt nature",
                    "amount" to "2"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `PUT foods id inexistant retourne 400`() = testApplication {
        configureTestApp()
        val response = client.put("/foods/999") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(listOf("name" to "Fantome").formUrlEncode())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    // ===== DELETE /foods/{id} =====

    @Test
    fun `DELETE foods id valide retourne 204`() = testApplication {
        configureTestApp()
        val id = repository.create(sampleFoodPostDto())

        val response = client.delete("/foods/$id")

        assertEquals(HttpStatusCode.NoContent, response.status)
        assertNull(repository.findById(id))
    }

    @Test
    fun `DELETE foods id inexistant retourne 404`() = testApplication {
        configureTestApp()
        val response = client.delete("/foods/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE foods id non numérique retourne 400`() = testApplication {
        configureTestApp()
        val response = client.delete("/foods/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}