package com.leon.myapplication

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun Context.checkPermissions(vararg permissions: String): Boolean {
    return listOf(
        permissions.forEach {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        },
    ).any()
}
