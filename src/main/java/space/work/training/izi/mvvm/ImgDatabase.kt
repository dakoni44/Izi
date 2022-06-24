package com.social.world.tracy.mvvm.kotlin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Img::class], version = 1, exportSchema = false)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

}