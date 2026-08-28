package com.vlt.zeroperte.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vlt.zeroperte.business.ZeroPerteNotifWorker
import com.vlt.zeroperte.data.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private lateinit var workManager: WorkManager
    private val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()
    private val workName = "ZeroPerteWorker"


    fun runPeriodicWorkRequestInitialDelay() {

        val workRequest = PeriodicWorkRequestBuilder<ZeroPerteNotifWorker>(
            repeatInterval = 1,
            TimeUnit.DAYS
        )
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest)

    }

    fun cancelWorker() {
        workManager.cancelUniqueWork(workName)
    }

}