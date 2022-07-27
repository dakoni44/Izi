package space.work.training.izi.mvvm.chatList

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ChatListUsers::class], version = 1, exportSchema = false)
@TypeConverters(ChatListConverters::class)
abstract class ChatListDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}