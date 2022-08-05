package space.work.training.izi.notifications

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import space.work.training.izi.Constants.Companion.CONTENT_TYPE
import space.work.training.izi.Constants.Companion.SERVER_KEY

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun pushNotification(@Body notification: Sender): Response<ResponseBody>
}