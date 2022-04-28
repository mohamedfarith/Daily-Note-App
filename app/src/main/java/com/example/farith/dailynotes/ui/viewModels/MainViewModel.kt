package com.example.farith.dailynotes.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farith.dailynotes.modelClass.NoteClass
import com.example.farith.dailynotes.ui.repository.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    private lateinit var noteListData: MutableLiveData<List<NoteClass>>
    private lateinit var transferNote: NoteClass

    fun getNoteLiveData(): MutableLiveData<List<NoteClass>> {
        if (!::noteListData.isInitialized) {
            noteListData = MutableLiveData<List<NoteClass>>()
        }
        return noteListData
    }


    fun getNotesList() {
        viewModelScope.launch(Dispatchers.IO) {
            val notesList = BaseRepository.getNotesListFromLocal()
            viewModelScope.launch(Dispatchers.Main) {
                noteListData.value = notesList
            }

        }

    }

    fun insertNotes(noteData: NoteClass) {
        viewModelScope.launch(Dispatchers.IO) {
            BaseRepository.insertNote(noteData)
        }
    }

    fun updateNotes(noteData: NoteClass) {
        viewModelScope.launch(Dispatchers.IO) {
            BaseRepository.updateNotes(noteData)
        }
    }

    fun transferNote(note: NoteClass) {
        this.transferNote = note
    }

    fun getTransferredNote(): NoteClass {
        return transferNote
    }


}