package space.work.training.izi.mvvm.chat.room_v_firebase

import androidx.room.Database
import androidx.room.RoomDatabase
import space.work.training.izi.mvvm.chat.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class ChatListDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}