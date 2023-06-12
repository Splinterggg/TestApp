package com.leon.myapplication.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.leon.myapplication.data.BootCountDao
import com.leon.myapplication.data.BootCountDatabase
import com.leon.myapplication.data.BootEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BootCompletedBroadcastReceiver :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            CoroutineScope(Dispatchers.IO).launch {
                BootCountDatabase.getInstance(context).getBootEventDao().insertBootCount(BootEntity(System.currentTimeMillis()))
            }
        }
    }
}
