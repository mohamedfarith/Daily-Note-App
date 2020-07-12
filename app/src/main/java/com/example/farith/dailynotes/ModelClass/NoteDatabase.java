package com.example.farith.dailynotes.ModelClass;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NoteEntityClass.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase database;

    public static NoteDatabase getDataBase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "Notes.db").build();
        }
        return database;
    }
}
