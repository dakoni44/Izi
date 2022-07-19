package space.work.training.izi.mvvm.chatList.chat

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import space.work.training.izi.model.Chat

class ChatConverters {

    @TypeConverter
    fun fromCountryLangList(value: ArrayList<Chat>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Chat>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCountryLangList(value: String): ArrayList<Chat> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Chat>>() {}.type
        return gson.fromJson(value, type)
    }
}
