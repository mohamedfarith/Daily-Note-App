package com.example.farith.dailynotes.ModelClass;


import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = NoteClass.TABLE_NAME)
public class NoteEntityClass {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    int id;

    @ColumnInfo(name = NoteClass.NOTE_CONTENT)
    public String notes;

    @ColumnInfo(name = NoteClass.DATE)
    public String date;

    @ColumnInfo(name = NoteClass.NOTIFICATION_ID)
    public String notificationID;

    @ColumnInfo(name = NoteClass.REMINDER_TIME)
    public String reminder;

    NoteEntityClass(String mDate, String mNotes, String mReminder, String mNotificationId) {
        this.date = mDate;
        this.notes = mNotes;
        this.notificationID = mNotificationId;
        this.reminder = mReminder;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
