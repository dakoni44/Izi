package space.work.training.izi.mvvm.profile

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import space.work.training.izi.model.Chat
import space.work.training.izi.mvvm.posts.Img

class Converters {

    @TypeConverter
    fun restoreList(listOfString: String?): ArrayList<String?>? {
        return Gson().fromJson(listOfString, object : TypeToken<ArrayList<String?>?>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: ArrayList<String?>?): String? {
        return Gson().toJson(listOfString)
    }

    @TypeConverter
    fun fromImgToString(value: ArrayList<Img>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Img>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toImg(value: String): ArrayList<Img> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Img>>() {}.type
        return gson.fromJson(value, type)
    }
}