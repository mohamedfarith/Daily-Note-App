package com.example.farith.dailynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.farith.dailynotes.ModelClass.NoteClass;
import com.example.farith.dailynotes.ModelClass.NotesDatabaseList;

import java.util.ArrayList;

public class NoteDb extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "Notes.db";
    public static int DATABASE_VERSION = 1;

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + NoteClass.TABLE_NAME + "(" + NoteClass.ID + " " +
            "INTEGER PRIMARY KEY AUTOINCREMENT," + NoteClass.NOTE_CONTENT + " " + "TEXT, " + NoteClass.DATE + " TEXT)";
    public static String SQL_LAYOUT_STATUS = "CREATE TABLE status ( statusId INTEGER PRIMARY KEY AUTOINCREMENT , layoutStatus TEXT )";

    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + NoteClass.TABLE_NAME;
    public static String SQL_DELETE_STATUS = "DROP TABLE IF EXISTS status";
    private static final String TAG = "Notes Database";

    public NoteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: " + SQL_CREATE_ENTRIES);
        Log.d(TAG, "onCreate: " + SQL_LAYOUT_STATUS);
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_LAYOUT_STATUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_STATUS);
        onCreate(db);
    }

    public void insertStatusData(SQLiteDatabase db, String data) {
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("layoutStatus", data);
        db.replace("status", null, contentValues);
    }

    public String readStatusData(SQLiteDatabase db) {
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM status WHERE statusId = 1", null);
        if (!cursor.equals(null)&& cursor.moveToFirst()) {
            Log.d(TAG, "readStatusData: the layout status is " + cursor.getString(cursor.getColumnIndex("layoutStatus")));
            String status = cursor.getString(cursor.getColumnIndex("layoutStatus"));
            return status;
        }
        return "true";
    }
    public void updateStatusData(SQLiteDatabase db,String value){
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("layoutStatus",value);
        db.update("status",contentValues,"statusId = 1 ",null);
    }

    public void insertData(SQLiteDatabase db, String data, String time) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteClass.NOTE_CONTENT, data);
        values.put(NoteClass.DATE, time);
        db.insert(NoteClass.TABLE_NAME, null, values);
    }

    public ArrayList<NotesDatabaseList> readValues(SQLiteDatabase db) {
        ArrayList<NotesDatabaseList> notesDatabaseList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + NoteClass.TABLE_NAME + " ORDER BY " + NoteClass.DATE + " DESC", null);
        if (cursor.equals(null)) {

        } else {
            while (cursor.moveToNext()) {
                String notes = cursor.getString(cursor.getColumnIndex(NoteClass.NOTE_CONTENT));
                String date = cursor.getString(cursor.getColumnIndex(NoteClass.DATE));
                NotesDatabaseList list = new NotesDatabaseList();
                list.setNotes(notes);
                list.setDate(date);
                notesDatabaseList.add(list);
                Log.d(TAG, "readValues: from the database " + list.date + " " + list.notes);
            }
        }
        return notesDatabaseList;
    }

    public void updateDb(SQLiteDatabase db, String note, String previousTime, String currentTime) {
        Log.d(TAG, "updateDb: " + note + " " + previousTime);
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(note)) {
            values.put(NoteClass.NOTE_CONTENT, note);
            values.put(NoteClass.DATE, currentTime);
            Log.d(TAG, "updateDb: content of ContentValues " + values.getAsString(NoteClass.NOTE_CONTENT) + values.getAsString(NoteClass.DATE));
            db.update(NoteClass.TABLE_NAME, values, NoteClass.DATE+ "=" + previousTime, null);
        }
    }

    public void deleteRowInDb(SQLiteDatabase db, int adapterPosition, String data) {
        ArrayList<NotesDatabaseList> notesDatabaseList = new ArrayList<>();
        Log.d(TAG, "deleteRowInDb: " + adapterPosition);
        db = getWritableDatabase();
        db.execSQL("DELETE FROM " + NoteClass.TABLE_NAME + " WHERE " + NoteClass.NOTE_CONTENT + " = " + "'" + data + "'");
        db = getReadableDatabase();
        Cursor cursor = db.query(NoteClass.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.equals(null)) {

        } else {
            while (cursor.moveToNext()) {
                String notes = cursor.getString(cursor.getColumnIndex(NoteClass.NOTE_CONTENT));
                String date = cursor.getString(cursor.getColumnIndex(NoteClass.DATE));
                NotesDatabaseList list = new NotesDatabaseList();
                list.setNotes(notes);
                list.setDate(date);
                notesDatabaseList.add(list);
                Log.d(TAG, "readValues: from the database after delete" + list.date + " " + list.notes);
            }
        }
    }
}
