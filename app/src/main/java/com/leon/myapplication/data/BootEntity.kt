package com.leon.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("bootEntity")
data class BootEntity(
    @PrimaryKey val time: Long,
)
