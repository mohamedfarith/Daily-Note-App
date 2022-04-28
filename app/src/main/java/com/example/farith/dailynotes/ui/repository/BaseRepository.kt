package com.example.farith.dailynotes.ui.repository

import com.example.farith.dailynotes.db.RoomInstance
import com.example.farith.dailynotes.modelClass.NoteClass

object BaseRepository {


    suspend fun getNotesListFromLocal(): List<NoteClass> {
        return RoomInstance.getRoomInstance().resultsDao().getNotesList()
    }

    suspend fun updateNotes(noteData: NoteClass) {
        RoomInstance.getRoomInstance().resultsDao().updateNote(noteData = noteData)
    }

    suspend fun insertNote(noteData: NoteClass) {
        RoomInstance.getRoomInstance().resultsDao().insertNote(noteData = noteData)
    }
}