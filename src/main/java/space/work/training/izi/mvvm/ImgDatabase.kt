package space.work.training.izi.mvvm

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Img::class], version = 1, exportSchema = false)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

}