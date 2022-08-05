package space.work.training.izi.mvvm.posts

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import space.work.training.izi.mvvm.posts.newImgs.ImgNew

@Database(entities = [ImgHome::class, ImgNew::class], version = 2, exportSchema = false)
@TypeConverters(HomeImgConverter::class)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

}