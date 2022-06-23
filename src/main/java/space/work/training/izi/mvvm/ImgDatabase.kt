package com.social.world.tracy.mvvm.kotlin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Img::class], version = 1, exportSchema = false)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

    companion object {
        @Volatile
        private var instance: ImgDatabase? = null

        fun getDatabase(context: Context): ImgDatabase {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    ImgDatabase::class.java, "img_database"
                ).build()
                instance = inst
                return inst
            }
        }
    }
}