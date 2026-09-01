package com.vlt.zeroperte.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vlt.zeroperte.business.ZeroPerteNotifWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(@param:ApplicationContext private val appContext: Context) : ViewModel() {
    private var workManager: WorkManager = WorkManager.getInstance(appContext)
    private val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()
    private val workName = "ZeroPerteWorker"


    fun runPeriodicWorkRequestInitialDelay() {

        val workRequest = PeriodicWorkRequestBuilder<ZeroPerteNotifWorker>(
            repeatInterval = 8,
            TimeUnit.HOURS
        )
            .setInitialDelay(20, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest)

    }

    fun cancelWorker() {
        workManager.cancelUniqueWork(workName)
    }

    fun runOneTimeWorkRequest() {
        val workRequest = OneTimeWorkRequestBuilder<ZeroPerteNotifWorker>()
            .build()

        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            workRequest)
    }

}