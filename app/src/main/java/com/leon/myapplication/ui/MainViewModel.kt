package com.leon.myapplication.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.leon.myapplication.R
import com.leon.myapplication.data.BootCountDao
import com.leon.myapplication.data.BootCountDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext val applicationContext: Context,
) :
    ViewModel() {

    val bootEntities: StateFlow<String> = BootCountDatabase.getInstance(applicationContext).getBootEventDao().getAllBootEntities().map {
        if (it.isEmpty()) {
            applicationContext.getString(R.string.no_boot_events_detected)
        } else {
            var text = ""
            it.forEachIndexed { index, element ->
                text += "${index + 1} - ${element.time}\n"
            }
            text
        }
    }.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
        started = SharingStarted.Eagerly,
        initialValue = "",
    )

    fun runNotificationWorker(hasNotificationPermissionGranted: Boolean) {
        if (hasNotificationPermissionGranted) {
            val periodicWorkRequest =
                PeriodicWorkRequestBuilder<BootCountNotificationWorker>(15, TimeUnit.MINUTES)
                    .build()

            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    "BootNotificationWorker",
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    periodicWorkRequest,
                )
        }
    }
}
