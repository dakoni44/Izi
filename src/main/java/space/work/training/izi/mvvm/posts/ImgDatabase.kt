package space.work.training.izi.mvvm.posts

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.posts.newImgs.ImgNew
import space.work.training.izi.mvvm.profile.Converters

@Database(entities = [Img::class, ImgNew::class], version = 3, exportSchema = false)
@TypeConverters(HomeImgConverter::class)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

}