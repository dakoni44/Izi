package space.work.training.izi.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import space.work.training.izi.BaseActivity
import kotlin.random.Random


const val channelId = "izi_not_kokosanel"
const val channelName = "space.work.tr.izi.notif"

class FirebaseMessaging : FirebaseMessagingService() {

    fun generateNotification(title: String, message: String) {

        val notifID= Random.nextInt()
        val intent = Intent(this, BaseActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent, PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(space.work.training.izi.R.drawable.izi)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notification)
    }

    /*   fun getRemoteView(title: String, message: String): RemoteViews {
           val remoteView =
               RemoteViews("space.work.training.izi.notifications", R.layout.chat_notification)
           remoteView.setTextViewText(space.work.training.izi.R.id.title, title)
           remoteView.setTextViewText(space.work.training.izi.R.id.message, message)
           remoteView.setImageViewResource(R.id.logo, R.drawable.izi)

           return remoteView
       }*/

    override fun onMessageReceived(@NonNull message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            generateNotification(
                message.notification!!.title!!,
                message.notification!!.body!!
            )
        }
    }


    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            updateToken(s)
        }
    }

    private fun updateToken(tokenRefresh: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(tokenRefresh)
        reference.child(user!!.uid).setValue(token)
    }

}