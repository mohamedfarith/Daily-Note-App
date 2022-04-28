package com.example.farith.dailynotes.db

import androidx.room.*
import com.example.farith.dailynotes.modelClass.NoteClass

@Dao
interface DaoClass {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(noteData: NoteClass)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(noteData: NoteClass)

    @Query("SELECT * FROM notes ORDER BY date DESC")
    suspend fun getNotesList():List<NoteClass>

    @Query("DELETE FROM notes WHERE  notes.note_content = :data")
    suspend fun deleteRow(data:String)

}