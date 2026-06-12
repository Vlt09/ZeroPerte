package com.zeroperte.Repository

import com.zeroperte.model.Food
import com.zeroperte.model.FoodDto
import kotlinx.datetime.LocalDateTime
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
        datePurchased = LocalDateTime(2026, 6, 1, 10, 0),
        expiryDate = LocalDateTime(2026, 6, 15, 0, 0),
        comment = "A consommer rapidement",
        amount = 4
    )

    @Test
    fun `create insère un aliment et retourne un id`() {
        val id = repository.create(sampleFood())

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
        val id = repository.create(sampleFood())

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
        val id = repository.create(sampleFood())

        val deletedCount = repository.delete(id)

        assertEquals(1, deletedCount)
        assertNull(repository.findById(id))
    }

    @Test
    fun `delete sur un id inexistant retourne 0`() {
        val deletedCount = repository.delete(999L)

        assertEquals(0, deletedCount)
    }
}