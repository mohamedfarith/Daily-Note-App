package com.example.farith.dailynotes.ui

import android.app.Application
import com.example.farith.dailynotes.db.RoomInstance

class NoteApplication() : Application() {
    override fun onCreate() {
        super.onCreate()
        RoomInstance.initialize(this)

    }
}