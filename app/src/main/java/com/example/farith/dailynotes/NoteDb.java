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
            "INTEGER PRIMARY KEY AUTOINCREMENT," + NoteClass.NOTE_CONTENT + " " + "TEXT, "+NoteClass.DATE +" TEXT)";

    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + NoteClass.TABLE_NAME;
    private static final String TAG = "Notes Database";

    public NoteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: " + SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public ArrayList<NotesDatabaseList> readValues(SQLiteDatabase db) {
        ArrayList<NotesDatabaseList> notesDatabaseList = new ArrayList<>();
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
                Log.d(TAG, "readValues: from the database " + list.date+" "+list.notes);
            }
        }
        return notesDatabaseList;
    }

    public void updateDb(SQLiteDatabase db,String note,String adapterPosition,String currentTime) {
        int position = Integer.parseInt(adapterPosition);
        Log.d(TAG, "updateDb: "+note+" "+adapterPosition);
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(note)) {
            values.put(NoteClass.NOTE_CONTENT, note);
            values.put(NoteClass.DATE,currentTime);
            Log.d(TAG, "updateDb: content of ContentValues "+values.getAsString(NoteClass.NOTE_CONTENT)+values.getAsString(NoteClass.DATE));
            db.update(NoteClass.TABLE_NAME, values, NoteClass.ID + "="+position, null);
        }
    }
    public void deleteRowInDb(SQLiteDatabase db,int adapterPosition){
        Log.d(TAG, "deleteRowInDb: "+adapterPosition);
        db = getWritableDatabase();
        db.delete(NoteClass.TABLE_NAME,NoteClass.ID+"=?",new String[]{String.valueOf(adapterPosition+1)});
    }
}
