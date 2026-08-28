package com.vlt.zeroperte.business

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.vlt.zeroperte.data.FakeFoodRepository
import com.vlt.zeroperte.data.model.Food
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ZeroPerteNotifWorkerTest {

    private lateinit var context: Context
    private lateinit var repository: FakeFoodRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = FakeFoodRepository()
    }

    @After
    fun tearDown() {
        // Clean notif to not render next test false
        NotificationManagerCompat.from(context).cancelAll()
    }

    private fun sampleFood(
        name: String = "Yaourt",
        expiryDate: LocalDate
    ) = Food(
        name = name,
        brand = null,
        category = null,
        amount = 1,
        datePurchased = LocalDate.now().minusDays(2),
        expiryDate = expiryDate,
        comment = null
    )

    private suspend fun buildAndRunWorker(): ListenableWorker.Result {
        val worker = TestListenableWorkerBuilder<ZeroPerteNotifWorker>(context)
            .setWorkerFactory(TestWorkerFactory(repository))
            .build()

        return worker.doWork()
    }

    @Test
    fun no_food_needing_alert_returns_success_and_sends_no_notification() = runTest {
        repository.setFoods(
            listOf(sampleFood(expiryDate = LocalDate.now().plusDays(30))) // statut OK
        )

        val result = buildAndRunWorker()

        assertEquals(ListenableWorker.Result.success(), result)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val activeNotifications = notificationManager.activeNotifications
        assertTrue(activeNotifications.isEmpty())
    }

    @Test
    fun expired_food_returns_success_and_sends_notification() = runTest {
        repository.setFoods(
            listOf(sampleFood(expiryDate = LocalDate.now().minusDays(1))) // expiré
        )

        val result = buildAndRunWorker()

        assertEquals(ListenableWorker.Result.success(), result)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val activeNotifications = notificationManager.activeNotifications
        assertTrue(activeNotifications.any { it.id == 0 })
    }

    @Test
    fun soon_expiring_food_sends_notification() = runTest {
        repository.setFoods(
            listOf(sampleFood(expiryDate = LocalDate.now().plusDays(2))) // bientôt expiré
        )

        val result = buildAndRunWorker()

        assertEquals(ListenableWorker.Result.success(), result)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val activeNotifications = notificationManager.activeNotifications
        assertTrue(activeNotifications.any { it.id == 0 })
    }
}