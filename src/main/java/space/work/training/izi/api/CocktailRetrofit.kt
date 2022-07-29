package space.work.training.izi.api

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.work.training.izi.Constants
import java.lang.reflect.Type

class CocktailRetrofit {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.COCKTAIL_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(CocktailApi::class.java)
        }
    }
}