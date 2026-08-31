package com.vlt.zeroperte.business

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.vlt.zeroperte.MainActivity
import com.vlt.zeroperte.R
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.domain.FoodStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltWorker
class ZeroPerteNotifWorker @AssistedInject constructor(@Assisted private val appContext: Context,
                                                       @Assisted params: WorkerParameters,
                                                       private val repository: FoodRepository
                                                       ): CoroutineWorker(appContext, params) {

    private val notificationChannelId = "ZeroPerteNotificationChannelId"

    private fun createNotificationChannel() {

        val notificationChannel = NotificationChannel(
            notificationChannelId,
            "ZeroPerteWorker",
            NotificationManager.IMPORTANCE_DEFAULT,
        )

        val notificationManager: NotificationManager? =
            getSystemService(
                applicationContext,
                NotificationManager::class.java
            )

        notificationManager?.createNotificationChannel(
            notificationChannel
        )
    }

    private fun createNotification() : Notification {
        createNotificationChannel()

        val mainActivityIntent = Intent(
            applicationContext,
            MainActivity::class.java
        )

        var pendingIntentFlag by Delegates.notNull<Int>()
        pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE

        val mainActivityPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            mainActivityIntent,
            pendingIntentFlag)


        return NotificationCompat.Builder(
            applicationContext,
            notificationChannelId
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText("Vous avez des aliments qui vont expiré ou le sont déjà attention !")
            .setContentIntent(mainActivityPendingIntent)
            .setAutoCancel(true)
            .build()
    }

    override suspend fun doWork(): Result {

        val foods = repository.getAllFoods()

        val list = foods.map { foods ->
                        foods.filter { f ->
                            FoodStatusCalculator.fromExpiryDate(f.expiryDate) != FoodStatus.Edible }
                            }.first()

        if (list.isNotEmpty() && ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(0, createNotification())
            }
        }

        Log.i("Notif", "Success")
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            0, createNotification()
        )
    }


}