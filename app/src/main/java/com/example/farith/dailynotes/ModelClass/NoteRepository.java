package com.example.farith.dailynotes.ModelClass;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.room.Room;

public class NoteRepository extends ViewModel {
    private static NoteDatabase database;


    private static NoteDatabase getDataBase(Application application) {
        database = Room.databaseBuilder(application, NoteDatabase.class, "NotesDb").build();
        return database;
    }

    public static NoteDaoClass getDaoClass(Application application) {
        if (database == null)
            return getDataBase(application).getNoteDaoClass();
        return database.getNoteDaoClass();
    }

    public static NoteDatabase getInstance() {
        if (database == null) {
            try {
                throw new Exception("Reference not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return database;
    }



}
