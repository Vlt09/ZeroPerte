package com.vlt.zeroperte.business

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.vlt.zeroperte.data.FoodRepository

class TestWorkerFactory(
    private val repository: FoodRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ZeroPerteNotifWorker::class.java.name ->
                ZeroPerteNotifWorker(appContext, workerParameters, repository)
            else -> null
        }
    }
}