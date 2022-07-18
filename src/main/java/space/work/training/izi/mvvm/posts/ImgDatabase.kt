package space.work.training.izi.mvvm.posts

import androidx.room.Database
import androidx.room.RoomDatabase
import space.work.training.izi.mvvm.posts.newImgs.ImgNew

@Database(entities = [Img::class, ImgNew::class], version = 3, exportSchema = false)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

}