package space.work.training.izi.mvvm.chatList.chat

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ChatUser::class], version = 2, exportSchema = false)
@TypeConverters(ChatConverters::class)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

}