package com.social.world.tracy.mvvm.kotlin

import androidx.room.Database
import androidx.room.RoomDatabase
import space.work.training.izi.mvvm.ImgDao

@Database(entities = [Img::class], version = 1, exportSchema = false)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

}