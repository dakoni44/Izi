package space.work.training.izi.notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.work.training.izi.Constants.Companion.BASE_URL

class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationApi::class.java)
        }
    }
}