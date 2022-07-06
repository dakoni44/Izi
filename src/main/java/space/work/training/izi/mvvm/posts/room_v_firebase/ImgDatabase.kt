package space.work.training.izi.mvvm.posts.room_v_firebase

import androidx.room.Database
import androidx.room.RoomDatabase
import space.work.training.izi.mvvm.posts.Img

@Database(entities = [Img::class], version = 1, exportSchema = false)
abstract class ImgDatabase : RoomDatabase() {

    abstract fun imgDao(): ImgDao

}