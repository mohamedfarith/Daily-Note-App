package com.example.farith.dailynotes.db

import android.app.Application
import androidx.room.Room

object RoomInstance {

    private lateinit var roomDatabase: AppDatabase


    fun initialize(application: Application) {
        if (!::roomDatabase.isInitialized)
            roomDatabase =
                Room.databaseBuilder(application, AppDatabase::class.java, "NoteDb").build();
    }

    fun getRoomInstance(): AppDatabase {
        return roomDatabase
    }

}