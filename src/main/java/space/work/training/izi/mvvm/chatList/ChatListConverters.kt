package space.work.training.izi.mvvm.chatList

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import space.work.training.izi.model.User

class ChatListConverters {

    @TypeConverter
    fun fromChatListList(value: ArrayList<User>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<User>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toChatListList(value: String): ArrayList<User> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<User>>() {}.type
        return gson.fromJson(value, type)
    }

}