package com.leon.myapplication.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.leon.myapplication.R
import com.leon.myapplication.data.BootCountDatabase
import javax.inject.Inject

class BootCountNotificationWorker @Inject constructor(
    private val context: Context,
    workerParams: WorkerParameters,
) :
    CoroutineWorker(
        context,
        workerParams,
    ) {

    var notificationChannelCreated = false

    override suspend fun doWork(): Result {
        if (!notificationChannelCreated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        postNotification(getBootInfoFromDB())

        return Result.success()
    }

    private fun getBootInfoFromDB(): String {
        val bootEventList =
            BootCountDatabase.getInstance(context).getBootEventDao().getAllBootCounts()
        return when {
            bootEventList.isEmpty() -> "No boots detected"
            bootEventList.size == 1 -> {
                "The boot was detected with the timestamp = ${bootEventList.first().time}"
            }

            else -> {
                val delta =
                    bootEventList.last().time - bootEventList[bootEventList.lastIndex - 1].time
                "Last boots time delta = $delta"
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun postNotification(notificationText: String) {
        val builder = NotificationCompat.Builder(
            this.context,
            this.context.getString(R.string.channel_id),
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.boot_tracker_notification_title))
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this.applicationContext)) {
            notify(context.getString(R.string.notification_id).toInt(), builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                context.getString(R.string.channel_id),
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.channel_description)
            }
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationChannelCreated = true
    }
}
