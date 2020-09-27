package com.example.farith.dailynotes.ModelClass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class DatabaseViewModel extends ViewModel {

    MutableLiveData<List<NoteEntityClass>> liveData;
    MutableLiveData<StatusEntityClass> statusLiveData;


    public MutableLiveData<List<NoteEntityClass>> readValues() {
        return NoteRepository.getInstance().readValues(liveData);
    }

    public MutableLiveData<StatusEntityClass> getStatusData() {
        return NoteRepository.getInstance().readStatusData(statusLiveData);
    }

}
