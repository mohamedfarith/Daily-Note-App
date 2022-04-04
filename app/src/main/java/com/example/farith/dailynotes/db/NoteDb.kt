package com.example.farith.dailynotes.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import android.util.Log
import com.example.farith.dailynotes.modelClass.NoteClass
import com.example.farith.dailynotes.modelClass.NotesDatabaseList
import java.util.*

class NoteDb(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "onCreate: $SQL_CREATE_ENTRIES")
        Log.d(TAG, "onCreate: $SQL_LAYOUT_STATUS")
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_LAYOUT_STATUS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: $SQL_DELETE_ENTRIES")
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_STATUS)
        onCreate(db)
    }

    fun insertStatusData(db: SQLiteDatabase?, data: String?) {
        var db = db
        db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("layoutStatus", data)
        db.replace("status", null, contentValues)
    }

    fun readStatusData(db: SQLiteDatabase?): String {
        var db = db
        db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM status WHERE statusId = 1", null)
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(
                TAG,
                "readStatusData: the layout status is " + cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "layoutStatus"
                    )
                )
            )
            return cursor.getString(cursor.getColumnIndexOrThrow("layoutStatus"))
        }
        return "true"
    }

    fun updateStatusData(db: SQLiteDatabase?, value: String?) {
        var db = db
        db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("layoutStatus", value)
        db.update("status", contentValues, "statusId = 1 ", null)
    }

    //The data is first time inserted in the onCreate of the Main Activity
    fun insertData(
        db: SQLiteDatabase?,
        data: String?,
        time: String?,
        notificationId: String?,
        reminderTime: String?
    ) {
        var db = db
        db = writableDatabase
        val values = ContentValues()
        values.put(NoteClass.NOTE_CONTENT, data)
        values.put(NoteClass.DATE, time)
        values.put(NoteClass.NOTIFICATION_ID, notificationId)
        values.put(NoteClass.REMINDER_TIME, reminderTime)
        db.insert(NoteClass.TABLE_NAME, null, values)
    }

    fun readValues(db: SQLiteDatabase?): ArrayList<NotesDatabaseList> {
        var db = db
        val notesDatabaseList = ArrayList<NotesDatabaseList>()
        db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM " + NoteClass.TABLE_NAME + " ORDER BY " + NoteClass.DATE + " DESC",
            null
        )
        if (cursor == null) {
        } else {
            while (cursor.moveToNext()) {
                val notes = cursor.getString(cursor.getColumnIndexOrThrow(NoteClass.NOTE_CONTENT))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(NoteClass.DATE))
                val notificationId =
                    cursor.getString(cursor.getColumnIndexOrThrow(NoteClass.NOTIFICATION_ID))
                val reminderTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(NoteClass.REMINDER_TIME))
                val list = NotesDatabaseList()
                list.notes = notes
                list.date = date
                list.notificationID = notificationId
                list.reminder = reminderTime
                notesDatabaseList.add(list)
                Log.d(
                    TAG,
                    "readValues: from the database: edited date from db " + list.date + " edited notes from db " + list.notes + " notification id from db " + list.notificationID + " Reminder time from db" + reminderTime
                )
            }
        }
        return notesDatabaseList
    }

    //Whenever the note is added, the value is updated and it happens in NoteActivity
    fun updateDb(
        db: SQLiteDatabase?,
        note: String,
        previousTime: String?,
        currentTime: String?,
        notificationId: String?,
        reminderTime: String?
    ) {
        var db = db
        Log.d(TAG, "updateDb: $note previous time$previousTime current time")
        db = writableDatabase
        val values = ContentValues()
        if (!TextUtils.isEmpty(note)) {
            values.put(NoteClass.NOTE_CONTENT, note)
            values.put(NoteClass.DATE, currentTime)
            values.put(NoteClass.NOTIFICATION_ID, notificationId)
            values.put(NoteClass.REMINDER_TIME, reminderTime)
            Log.d(
                TAG,
                "updateDb: content of ContentValues " + values.getAsString(NoteClass.NOTE_CONTENT) + values.getAsString(
                    NoteClass.DATE
                )
            )
            db.update(NoteClass.TABLE_NAME, values, NoteClass.DATE + "=" + previousTime, null)
        }
    }

    fun deleteRowInDb(db: SQLiteDatabase?, adapterPosition: Int, data: String?) {
        var db = db
        val notesDatabaseList = ArrayList<NotesDatabaseList>()
        Log.d(TAG, "deleteRowInDb: $adapterPosition")
        db = writableDatabase
        db.execSQL("DELETE FROM " + NoteClass.TABLE_NAME + " WHERE " + NoteClass.NOTE_CONTENT + " = " + "'" + data + "'")
        db = readableDatabase
        val cursor = db.query(NoteClass.TABLE_NAME, null, null, null, null, null, null)
        if (cursor == null) {
        } else {
            while (cursor.moveToNext()) {
                val notes = cursor.getString(cursor.getColumnIndexOrThrow(NoteClass.NOTE_CONTENT))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(NoteClass.DATE))
                val list = NotesDatabaseList()
                list.notes = notes
                list.date = date
                notesDatabaseList.add(list)
                Log.d(
                    TAG,
                    "readValues: from the database after delete" + list.date + " " + list.notes
                )
            }
        }
    }

    companion object {
        var DATABASE_NAME = "Notes.db"
        var DATABASE_VERSION = 1
        var SQL_CREATE_ENTRIES = "CREATE TABLE " + NoteClass.TABLE_NAME + "(" + NoteClass.ID + " " +
                "INTEGER PRIMARY KEY AUTOINCREMENT," + NoteClass.NOTE_CONTENT + " " + "TEXT, " + NoteClass.DATE + " TEXT, " + NoteClass.NOTIFICATION_ID + " TEXT, " + NoteClass.REMINDER_TIME + " TEXT)"
        var SQL_LAYOUT_STATUS =
            "CREATE TABLE status ( statusId INTEGER PRIMARY KEY AUTOINCREMENT , layoutStatus TEXT )"
        var SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + NoteClass.TABLE_NAME
        var SQL_DELETE_STATUS = "DROP TABLE IF EXISTS status"
        private const val TAG = "Notes Database"
    }
}