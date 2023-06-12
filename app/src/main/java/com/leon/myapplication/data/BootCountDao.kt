package com.leon.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BootCountDao {
    @Query("SELECT * FROM bootEntity")
    fun getAllBootEntities(): Flow<List<BootEntity>>

    @Query("SELECT * FROM bootEntity")
    fun getAllBootCounts(): List<BootEntity>

    @Insert
    suspend fun insertBootCount(bootCountEntity: BootEntity)
}
