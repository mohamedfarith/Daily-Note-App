package com.example.farith.dailynotes.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.farith.dailynotes.R
import com.example.farith.dailynotes.ui.MainActivity

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", -1)
        val notes = intent.getStringExtra("notes")
        val newIntent = Intent(context, MainActivity::class.java)
        newIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            newIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //creating notification channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationChannel =
                NotificationChannel("1", "notes", NotificationManager.IMPORTANCE_HIGH)
            //cutomizing the notification thing in version>26
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //creating notification
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.daily_note_app_icon)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notification: NotificationCompat.Builder? = null
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, "1")
        } else {
            NotificationCompat.Builder(context)
        }
        notification.setContentTitle("Daily Note App")
            .setContentText("You have added a remainder for a note ").setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("You have added a remainder for a note ' $notes '")
            )
            .setSmallIcon(R.drawable.ic_sticky_note_png)
            .setSound(soundUri)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
        notificationManager.notify(notificationId, notification.build())
    }
}