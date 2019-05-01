package com.example.farith.dailynotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;

public class CancelNotification extends BroadcastReceiver {
    private static final String TAG = "CancelNotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive:  is called and im becoming mad");
        String notificationID = intent.getStringExtra("cancel_noti");
        Intent newIntent = new Intent(context, Notification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(notificationID), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
