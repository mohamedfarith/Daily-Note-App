package com.example.farith.dailynotes.modelClass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class NoteClass(
    @ColumnInfo(name = "note_content")
    var noteContent: String?,
    @ColumnInfo(name = "date")
    var date: String?,
    @PrimaryKey(autoGenerate = false)
    var notificationId: Int?,
    @ColumnInfo(name = "reminder_time")
    var reminderTime: String?
) : Serializable {

}