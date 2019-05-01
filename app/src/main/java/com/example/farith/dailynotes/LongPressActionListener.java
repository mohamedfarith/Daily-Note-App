package com.example.farith.dailynotes;

import android.content.BroadcastReceiver;

public interface LongPressActionListener {
    void updateRecyclerView(int adapterPosition, String notificationID);

}
