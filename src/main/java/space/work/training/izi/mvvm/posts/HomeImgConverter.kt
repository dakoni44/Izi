package space.work.training.izi.mvvm.posts

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import space.work.training.izi.model.Img

class HomeImgConverter {

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