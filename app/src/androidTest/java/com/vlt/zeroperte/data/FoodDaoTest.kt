package com.vlt.zeroperte.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDao
import com.vlt.zeroperte.data.source.AppDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class FoodDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: FoodDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.foodDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    private fun sampleFood(
        name: String = "Yaourt nature",
        brand: String? = "Danone",
        category: String = "frais",
        amount: Int = 4,
        datePurchased: LocalDate = LocalDate.now().minusDays(2),
        expiryDate: LocalDate = LocalDate.now().plusDays(5),
        comment: String? = null
    ) = Food(
        name = name,
        brand = brand,
        category = category,
        amount = amount,
        datePurchased = datePurchased,
        expiryDate = expiryDate,
        comment = comment
    )

    @Test
    fun insert_and_findById_returnsInsertedFood() {
        val food = sampleFood(name = "Lait")
        dao.insert(food)

        val all = dao.allFoods()
        assertEquals(1, all.size)

        val inserted = all.first()
        val found = dao.findById(inserted.id)

        assertNotNull(found)
        assertEquals("Lait", found?.name)
    }

    @Test
    fun findById_withUnknownId_returnsNull() {
        val found = dao.findById(999L)
        assertNull(found)
    }

    @Test
    fun findByName_matchesExactName() {
        dao.insert(sampleFood(name = "Compote pomme"))
        dao.insert(sampleFood(name = "Compote poire"))

        val results = dao.findByName("Compote pomme")

        assertEquals(1, results.size)
        assertEquals("Compote pomme", results.first().name)
    }

    @Test
    fun findByName_withNoMatch_returnsEmptyList() {
        dao.insert(sampleFood(name = "Compote pomme"))

        val results = dao.findByName("Chocolat")

        assertTrue(results.isEmpty())
    }

    @Test
    fun findByCategory_returnsOnlyMatchingCategory() {
        dao.insert(sampleFood(name = "Poisson", category = "surgelé"))
        dao.insert(sampleFood(name = "Riz", category = "sec"))
        dao.insert(sampleFood(name = "Légumes", category = "surgelé"))

        val results = dao.findByCategory("surgelé")

        assertEquals(2, results.size)
        assertTrue(results.all { it.category == "surgelé" })
    }

    @Test
    fun findByBrand_returnsOnlyMatchingBrand() {
        dao.insert(sampleFood(name = "Yaourt", brand = "Danone"))
        dao.insert(sampleFood(name = "Fromage blanc", brand = "Yoplait"))

        val results = dao.findByBrand("Danone")

        assertEquals(1, results.size)
        assertEquals("Yaourt", results.first().name)
    }

    @Test
    fun findExpired_currentBehavior_returnsNonExpiredFoods() {
        dao.insert(sampleFood(name = "Perime", expiryDate = LocalDate.now().minusDays(3)))
        dao.insert(sampleFood(name = "Valide", expiryDate = LocalDate.now().plusDays(3)))

        val results = dao.findExpired()

        assertEquals(1, results.size)
        assertEquals("Valide", results.first().name)
    }

    @Test
    fun findExpiringSinceDays_returnsFoodsWithinRange() {
        dao.insert(sampleFood(name = "DansTroisJours", expiryDate = LocalDate.now().plusDays(3)))
        dao.insert(sampleFood(name = "DansDixJours", expiryDate = LocalDate.now().plusDays(10)))

        val results = dao.findExpiringSinceDays(5)

        assertEquals(1, results.size)
        assertEquals("DansTroisJours", results.first().name)
    }

    @Test
    fun allFoods_returnsEverythingInserted() {
        dao.insert(sampleFood(name = "A"))
        dao.insert(sampleFood(name = "B"))
        dao.insert(sampleFood(name = "C"))

        val results = dao.allFoods()

        assertEquals(3, results.size)
    }

    @Test
    fun delete_removesFood() {
        dao.insert(sampleFood(name = "AEffacer"))
        val inserted = dao.allFoods().first()

        dao.delete(inserted)

        assertTrue(dao.allFoods().isEmpty())
    }

    @Test
    fun update_modifiesExistingFood() {
        dao.insert(sampleFood(name = "AvantMaj", amount = 2))
        val inserted = dao.allFoods().first()

        val updated = inserted.copy(amount = 10)
        dao.update(updated)

        val result = dao.findById(inserted.id)
        assertEquals(10, result?.amount)
    }
}