package com.example.farith.dailynotes.ui.interfaces;

import android.content.BroadcastReceiver;

public interface LongPressActionListener {
    void updateRecyclerView(int adapterPosition, String notificationID);

}
