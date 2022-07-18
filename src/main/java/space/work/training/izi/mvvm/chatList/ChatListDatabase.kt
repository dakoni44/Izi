package space.work.training.izi.mvvm.chatList

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class ChatListDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}