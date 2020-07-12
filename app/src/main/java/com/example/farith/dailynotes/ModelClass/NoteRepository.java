package com.example.farith.dailynotes.ModelClass;

import android.content.Context;

import androidx.lifecycle.ViewModel;

public class NoteRepository {
    NoteDaoClass daoClass;

    public static void getAllNotes(Context context){
        NoteDatabase.getDataBase(context);
    }

}
