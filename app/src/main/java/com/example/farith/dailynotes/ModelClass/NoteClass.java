package com.example.farith.dailynotes.ModelClass;

import android.provider.BaseColumns;

public class NoteClass implements BaseColumns {
    public final static String TABLE_NAME = "notes";

    public final static String ID = BaseColumns._ID;
    public final static String NOTE_CONTENT = "note_content";
    public static final String DATE = "date";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String REMINDER_TIME = "reminder_time";
}
