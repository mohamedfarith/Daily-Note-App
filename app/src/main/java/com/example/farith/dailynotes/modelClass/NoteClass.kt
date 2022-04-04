package com.example.farith.dailynotes.modelClass

import android.provider.BaseColumns

object NoteClass : BaseColumns {
    const val TABLE_NAME = "notes"
    const val ID = BaseColumns._ID
    const val NOTE_CONTENT = "note_content"
    const val DATE = "date"
    const val NOTIFICATION_ID = "notification_id"
    const val REMINDER_TIME = "reminder_time"
}