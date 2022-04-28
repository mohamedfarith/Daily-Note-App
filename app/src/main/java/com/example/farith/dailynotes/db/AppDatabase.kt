package com.example.farith.dailynotes.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.farith.dailynotes.modelClass.NoteClass

@Database(entities = [NoteClass::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun resultsDao(): DaoClass

}