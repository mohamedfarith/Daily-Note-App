package com.example.farith.dailynotes;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;


public class Notification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notes = intent.getStringExtra("notes");
        String notificationId = intent.getStringExtra("position");
        Intent newIntent = new Intent(context, NoteActivity.class);
        newIntent.putExtra("notes", notes);
        newIntent.putExtra("position", notificationId);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(notificationId), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //creating notification channel

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationChannel notificationChannel = new NotificationChannel("1", "notes", NotificationManager.IMPORTANCE_HIGH);
            //cutomizing the notification thing in version>26
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //creating notification
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.daily_note_app_icon);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(context, "1");
        } else {

            notification = new NotificationCompat.Builder(context);
        }
        notification.setContentTitle("Daily Note App")
                .setContentText("You have added a remainder for a note ").setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have added a remainder for a note ' " + notes + " '"))
                .setSmallIcon(R.drawable.ic_sticky_note_png)
                .setSound(soundUri)
                .setLargeIcon(bitmap)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);


        notificationManager.notify(Integer.parseInt(notificationId), notification.build());
    }

}
