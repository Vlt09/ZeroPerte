package com.vlt.zeroperte.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.FoodDao
import com.vlt.zeroperte.data.source.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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
    fun insert_and_findById_returnsInsertedFood() = runTest {
        val food = sampleFood(name = "Lait")
        dao.insert(food)

        val all = dao.allFoods().first()
        assertEquals(1, all.size)

        val inserted = all.first()
        val found = dao.findById(inserted.id)

        assertNotNull(found)
        assertEquals("Lait", found?.name)
    }

    @Test
    fun findById_withUnknownId_returnsNull() = runTest {
        val found = dao.findById(999L)
        assertNull(found)
    }

    @Test
    fun allFoods_returnsEverythingInserted() = runTest {
        dao.insert(sampleFood(name = "A"))
        dao.insert(sampleFood(name = "B"))
        dao.insert(sampleFood(name = "C"))

        val results = dao.allFoods().first()

        assertEquals(3, results.size)
    }

    @Test
    fun allFoods_emitsNewValue_afterInsert() = runTest {
        dao.insert(sampleFood(name = "Initial"))
        assertEquals(1, dao.allFoods().first().size)

        dao.insert(sampleFood(name = "Ajoute"))

        assertEquals(2, dao.allFoods().first().size)
    }

    @Test
    fun delete_removesFood() = runTest {
        dao.insert(sampleFood(name = "AEffacer"))
        val inserted = dao.allFoods().first().first()

        dao.delete(inserted)

        assertTrue(dao.allFoods().first().isEmpty())
    }

    @Test
    fun update_modifiesExistingFood() = runTest {
        dao.insert(sampleFood(name = "AvantMaj", amount = 2))
        val inserted = dao.allFoods().first().first()

        val updated = inserted.copy(amount = 10)
        dao.update(updated)

        val result = dao.findById(inserted.id)
        assertEquals(10, result?.amount)
    }
}