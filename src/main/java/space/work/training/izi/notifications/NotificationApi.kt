package space.work.training.izi.notifications

import android.app.Notification
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import space.work.training.izi.Constants.Companion.CONTENT_TYPE
import space.work.training.izi.Constants.Companion.SERVER_KEY

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY","Content_type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun pushNotification(@Body notification: Sender):retrofit2.Response<ResponseBody>
}