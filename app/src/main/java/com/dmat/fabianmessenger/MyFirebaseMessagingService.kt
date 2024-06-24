package com.dmat.fabianmessenger

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelID = "notification_channel"
const val channelName = "com.dmat.fabianmessenger"

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null){
            genNotification("title", "message")
        }
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews{
        val remoteView = RemoteViews("com.dmat.fabianmessenger1", R.layout.custom_notification)
        remoteView.setTextViewText(R.id.notif_title, title)
        remoteView.setTextViewText(R.id.notif_description, message)

        return remoteView
    }

    fun genNotification(title: String, message: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 342, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.oh)
            .setContentTitle("TITLE")
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            notifManager.createNotificationChannel(notifChannel)
        }

        notifManager.notify(342, builder.build())

    }

}