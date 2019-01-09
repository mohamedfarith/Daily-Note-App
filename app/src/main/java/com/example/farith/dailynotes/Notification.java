package com.example.farith.dailynotes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class Notification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notes = intent.getStringExtra("notes");
        String adapterPosition = intent.getStringExtra("position");
        Intent newIntent = new Intent(context, NoteActivity.class);
        newIntent.putExtra("notes", notes);
        newIntent.putExtra("position", adapterPosition);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //creating notification channel

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("1","notes",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //creating notification
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.daily_note_app_icon);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "1")
                .setContentTitle("Daily Note App")
                .setContentText("You have added a remainder for a note " ).setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have added a remainder for a note ' "+notes+" '"))
                .setSmallIcon(R.drawable.ic_sticky_note_png)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(100, notification.build());
    }

}
