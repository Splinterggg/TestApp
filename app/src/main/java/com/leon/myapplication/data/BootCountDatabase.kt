package com.leon.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BootEntity::class], version = 1)
abstract class BootCountDatabase : RoomDatabase() {
    abstract fun getBootEventDao(): BootCountDao

    companion object {

        private var instance: BootCountDatabase? = null

        fun getInstance(context: Context): BootCountDatabase {
            return instance ?: buildDatabase(context)
        }

        private fun buildDatabase(context: Context): BootCountDatabase {
            val db = Room.databaseBuilder(context, BootCountDatabase::class.java, "boot-count-db")
                .build()
            instance = db
            return db
        }
    }
}
