package com.example.farith.dailynotes.receivers

import android.app.PendingIntent
import android.app.AlarmManager
import android.content.*
import android.util.Log

class CancelNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive:  is called and im becoming mad")
        val notificationID = intent.getStringExtra("cancel_noti")
        val newIntent = Intent(context, Notification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID!!.toInt(),
            newIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private const val TAG = "CancelNotification"
    }
}