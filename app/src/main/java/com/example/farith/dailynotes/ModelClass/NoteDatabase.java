package com.example.farith.dailynotes.ModelClass;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.json.JSONStringer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {NoteEntityClass.class, StatusEntityClass.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    public abstract NoteDaoClass getNoteDaoClass();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public void insertData(NoteEntityClass noteEntityClass) {
        NoteDatabase.databaseWriteExecutor.execute(() -> {
            getNoteDaoClass().insertData(noteEntityClass);
        });
    }

    public void insertStatusData(StatusEntityClass statusEntityClass) {
        NoteDatabase.databaseWriteExecutor.execute(() -> getNoteDaoClass().insertStatusData(statusEntityClass));
    }

    public MutableLiveData<List<NoteEntityClass>> readValues(MutableLiveData<List<NoteEntityClass>> liveData) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        MutableLiveData<List<NoteEntityClass>> finalLiveData = liveData;
        NoteDatabase.databaseWriteExecutor.execute(() -> finalLiveData.postValue(getNoteDaoClass().readValues()));
        return liveData;
    }

    public void updateValue(String note, String previousTime, String currentTime, String reminderTime, String notificationId) {
        NoteDatabase.databaseWriteExecutor.execute(() -> getNoteDaoClass().updateValue(note, previousTime, currentTime, reminderTime, notificationId));
    }

    public void deleteValue(String date) {
        NoteDatabase.databaseWriteExecutor.execute(() -> getNoteDaoClass().deleteValue(date));
    }

    public void updateStatusData(String statusData) {
        NoteDatabase.databaseWriteExecutor.execute(() -> getNoteDaoClass().updateStatusData(statusData));
    }

    public MutableLiveData<StatusEntityClass> readStatusData(MutableLiveData<StatusEntityClass> statusLiveData) {
        if (statusLiveData == null) {
            statusLiveData = new MutableLiveData<>();
        }
        MutableLiveData<StatusEntityClass> finalStatusLiveData = statusLiveData;
        NoteDatabase.databaseWriteExecutor.execute(() -> finalStatusLiveData.postValue(getNoteDaoClass().readStatusData()));
        return statusLiveData;
    }


}
