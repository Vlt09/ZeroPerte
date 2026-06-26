package com.zeroperte.Repository

import com.zeroperte.model.Food
import com.zeroperte.model.FoodDto
import com.zeroperte.model.FoodPostDto
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toStdlibInstant
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.*

class FoodRepositoryTest {

    private lateinit var repository: FoodRepository

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        repository = FoodRepository()
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(FoodTable)
        }
    }

    private fun sampleFood(name: String = "Yaourt") = Food(
        id = 0,
        name = name,
        brand = "Danone",
        category = "Produit laitier",
        datePurchased = LocalDate(2026, 6, 1),
        expiryDate = LocalDate(2026, 6, 15),
        comment = "A consommer rapidement",
        amount = 4
    )

    private fun sampleFoodPostDto(name: String = "Yaourt") = FoodPostDto(
        name = name,
        brand = "Danone",
        category = "Produit laitier",
        datePurchased = LocalDate(2026, 6, 1),
        expiryDate = LocalDate(2026, 6, 15),
        comment = "A consommer rapidement",
        amount = 4
    )

    @Test
    fun `create insère un aliment et retourne un id`() {
        val id = repository.create(sampleFoodPostDto())

        assertNotNull(id)

        val saved = repository.findById(id)
        assertNotNull(saved)
        assertEquals("Yaourt", saved.name)
        assertEquals("Danone", saved.brand)
        assertEquals("Produit laitier", saved.category)
        assertEquals(4, saved.amount)
    }

    @Test
    fun `findById retourne null si l'aliment n'existe pas`() {
        val result = repository.findById(999L)
        assertNull(result)
    }

    @Test
    fun `update modifie uniquement les champs renseignés`() {
        val id = repository.create(sampleFoodPostDto())

        val dto = FoodDto(
            name = "Yaourt nature",
            brand = null,
            category = null,
            comment = "Ouvert hier",
            amount = 2,
            datePurchased = null,
            expiryDate = null
        )

        val updated = repository.update(id, dto)

        assertNotNull(updated)
        assertEquals("Yaourt nature", updated.name)
        assertEquals("Danone", updated.brand)
        assertEquals("Produit laitier", updated.category)
        assertEquals("Ouvert hier", updated.comment)
        assertEquals(2, updated.amount)
    }

    @Test
    fun `update sur un id inexistant ne crée rien et retourne null`() {
        val dto = FoodDto(
            name = "Fantome",
            brand = null,
            category = null,
            comment = null,
            amount = null,
            datePurchased = null,
            expiryDate = null
        )

        val result = repository.update(999L, dto)

        assertNull(result)
    }

    @Test
    fun `delete supprime un aliment existant`() {
        val id = repository.create(sampleFoodPostDto())

        val deletedCount = repository.delete(id)

        assertEquals(1, deletedCount)
        assertNull(repository.findById(id))
    }

    @Test
    fun `delete sur un id inexistant retourne 0`() {
        val deletedCount = repository.delete(999L)

        assertEquals(0, deletedCount)
    }

    // ===== findByName =====

    @Test
    fun `findByName retourne les aliments correspondants`() {
        repository.create(sampleFoodPostDto("Yaourt"))
        repository.create(sampleFoodPostDto("Yaourt"))
        repository.create(sampleFoodPostDto("Lait"))

        val result = repository.findByName("Yaourt")

        assertEquals(2, result.size)
        assertTrue(result.all { it.name == "Yaourt" })
    }

    @Test
    fun `findByName retourne liste vide si aucun aliment ne correspond`() {
        repository.create(sampleFoodPostDto("Lait"))

        val result = repository.findByName("Yaourt")

        assertTrue(result.isEmpty())
    }

// ===== findByCategory =====

    @Test
    fun `findByCategory retourne les aliments de la catégorie`() {
        repository.create(sampleFoodPostDto())
        repository.create(sampleFoodPostDto())
        repository.create(sampleFoodPostDto("Lait").copy(category = "Boisson"))

        val result = repository.findByCategory("Produit laitier")

        assertEquals(2, result.size)
        assertTrue(result.all { it.category == "Produit laitier" })
    }

    @Test
    fun `findByCategory retourne liste vide si catégorie inconnue`() {
        repository.create(sampleFoodPostDto())

        val result = repository.findByCategory("Inconnu")

        assertTrue(result.isEmpty())
    }

// ===== findByBrand =====

    @Test
    fun `findByBrand retourne les aliments de la marque`() {
        repository.create(sampleFoodPostDto())
        repository.create(sampleFoodPostDto("Lait").copy(brand = "Lactel"))

        val result = repository.findByBrand("Danone")

        assertEquals(1, result.size)
        assertEquals("Danone", result[0].brand)
    }

    @Test
    fun `findByBrand retourne liste vide si marque inconnue`() {
        val result = repository.findByBrand("Inconnue")

        assertTrue(result.isEmpty())
    }

// ===== findExpired =====

    @Test
    fun `findExpired true retourne uniquement les aliments périmés`() {
        repository.create(sampleFoodPostDto().copy(expiryDate = LocalDate(2020, 1, 1))) // périmé
        repository.create(sampleFoodPostDto().copy(expiryDate = LocalDate(2099, 1, 1))) // valide

        val result = repository.findExpired(true)

        assertEquals(1, result.size)
        assertEquals(LocalDate(2020, 1, 1), result[0].expiryDate)
    }

    @Test
    fun `findExpired false retourne uniquement les aliments non périmés`() {
        repository.create(sampleFoodPostDto().copy(expiryDate = LocalDate(2020, 1, 1))) // périmé
        repository.create(sampleFoodPostDto().copy(expiryDate = LocalDate(2099, 1, 1))) // valide

        val result = repository.findExpired(false)

        assertEquals(1, result.size)
        assertEquals(LocalDate(2099, 1, 1), result[0].expiryDate)
    }

    @Test
    fun `findExpired retourne liste vide si aucun aliment périmé`() {
        repository.create(sampleFoodPostDto().copy(expiryDate = LocalDate(2099, 1, 1)))

        val result = repository.findExpired(true)

        assertTrue(result.isEmpty())
    }

// ===== findExpiring =====

    @Test
    fun `findExpiring retourne les aliments expirant dans le délai`() {
        val today = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val inThreeDays = today.plus(3, kotlinx.datetime.DateTimeUnit.DAY)
        val inTenDays = today.plus(10, kotlinx.datetime.DateTimeUnit.DAY)

        repository.create(sampleFoodPostDto().copy(expiryDate = inThreeDays))
        repository.create(sampleFoodPostDto().copy(expiryDate = inTenDays))

        val result = repository.findExpiring(5)

        assertEquals(1, result.size)
        assertEquals(inThreeDays, result[0].expiryDate)
    }

    @Test
    fun `findExpiring retourne liste vide si aucun aliment n'expire dans le délai`() {
        val today = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val inTenDays = today.plus(10, kotlinx.datetime.DateTimeUnit.DAY)

        repository.create(sampleFoodPostDto().copy(expiryDate = inTenDays))

        val result = repository.findExpiring(3)

        assertTrue(result.isEmpty())
    }

    // == findByMultipleFilter ==

    @Test
    fun `findByMultipleFilter should find by name`() {
        repository.create(sampleFoodPostDto())

        val result = repository.findByMultipleFilter(
            mapOf("name" to "Yaourt")
        )

        assertEquals(1, result.size)
        assertEquals("Yaourt", result.first().name)
    }

    @Test
    fun `findByMultipleFilter should find by brand`() {
        repository.create(sampleFoodPostDto())

        val result = repository.findByMultipleFilter(
            mapOf("brand" to "Danone")
        )

        assertEquals(1, result.size)
        assertEquals("Danone", result.first().brand)
    }

    @Test
    fun `findByMultipleFilter should find by category`() {
        repository.create(sampleFoodPostDto())

        val result = repository.findByMultipleFilter(
            mapOf("category" to "Produit laitier")
        )

        assertEquals(1, result.size)
    }

    @Test
    fun `findByMultipleFilter should combine filters`() {
        repository.create(sampleFoodPostDto("Ble"))
        repository.create(sampleFoodPostDto())


        val result = repository.findByMultipleFilter(
            mapOf(
                "brand" to "Danone",
                "name" to "Yaourt"
            )
        )

        assertEquals(1, result.size)
        assertEquals("Yaourt", result.first().name)
    }

    @Test
    fun `findByMultipleFilter should return empty list`() {
        repository.create(sampleFoodPostDto())

        val result = repository.findByMultipleFilter(
            mapOf("brand" to "Unknown")
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByMultipleFilter should filter by expiryDate`() {
        val today = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val inThreeDays = today.plus(3, kotlinx.datetime.DateTimeUnit.DAY)
        val inTenDays = today.plus(10, kotlinx.datetime.DateTimeUnit.DAY)

        repository.create(sampleFoodPostDto().copy(expiryDate = inThreeDays))


        val result = repository.findByMultipleFilter(
            mapOf("expiryDate" to "3")
        )

        assertTrue(result.all {
            it.expiryDate > today
        })
    }
}