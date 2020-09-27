package com.example.farith.dailynotes;

import android.app.Application;

import com.example.farith.dailynotes.ModelClass.NoteRepository;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NoteRepository.getDaoClass(this);

    }
}
