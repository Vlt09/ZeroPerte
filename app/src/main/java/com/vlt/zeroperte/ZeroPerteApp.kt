package com.vlt.zeroperte

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ZeroPerteApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() {
            Log.d("ZeroPerteApp", "workManagerConfiguration called, factory=$workerFactory")
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }

}