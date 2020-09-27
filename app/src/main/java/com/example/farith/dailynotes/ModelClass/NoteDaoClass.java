package com.example.farith.dailynotes.ModelClass;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import static android.icu.text.MessagePattern.ArgType.SELECT;

@Dao
public interface NoteDaoClass {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(NoteEntityClass entityClass);


    @Query("SELECT * FROM " + NoteClass.TABLE_NAME + " ORDER BY " + NoteClass.DATE + " DESC")
    List<NoteEntityClass> readValues();


    @Query("UPDATE " + NoteClass.TABLE_NAME + " SET "
            + NoteClass.NOTE_CONTENT + "=:note,"
            + NoteClass.DATE + "=:currentTime,"
            + NoteClass.REMINDER_TIME + "=:reminderTime,"
            + NoteClass.NOTIFICATION_ID + "=:notificationId"
            + " WHERE " + NoteClass.DATE + "=:previousTime")
    void updateValue(String note, String previousTime, String currentTime, String reminderTime, String notificationId);


    @Query(" DELETE FROM " + NoteClass.TABLE_NAME + " WHERE " + NoteClass.NOTE_CONTENT + "=:note")
    void deleteValue(String note);


    @Query("UPDATE layoutStatus SET status =:status WHERE statusId = 1")
    void updateStatusData(String status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStatusData(StatusEntityClass statusEntityClass);

    @Query("SELECT * FROM layoutStatus WHERE statusId=1")
    StatusEntityClass readStatusData();
}
