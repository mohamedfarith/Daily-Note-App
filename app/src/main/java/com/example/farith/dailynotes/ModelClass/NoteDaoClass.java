package com.example.farith.dailynotes.ModelClass;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import static android.icu.text.MessagePattern.ArgType.SELECT;

@Dao
public interface NoteDaoClass {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(NoteEntityClass entityClass);


    @Query("SELECT * FROM " + NoteClass.TABLE_NAME + " ORDER BY " + NoteClass.DATE + " DESC")
    void readValues();


    @Query("UPDATE " + NoteClass.TABLE_NAME + " SET "
            + NoteClass.NOTE_CONTENT + "=:note,"
            + NoteClass.DATE + "=:currentTime,"
            + NoteClass.REMINDER_TIME + "=:reminderTime,"
            + NoteClass.NOTIFICATION_ID + "=:notificationId"
            + " WHERE " + NoteClass.DATE + "=:previousTime")
    void updateValue(String note, String previousTime, String currentTime, String reminderTime, String notificationId);


    @Query(" DELETE FROM " + NoteClass.TABLE_NAME + " WHERE " + NoteClass.DATE + "=:date")
    void deleteValue(String date);


    @Query("UPDATE status SET layoutStatus =:status WHERE statusId = 1")
    void updateStatusData(String status);

    @Query("SELECT * FROM status WHERE statusId=1")
    String readStatusData();
}
