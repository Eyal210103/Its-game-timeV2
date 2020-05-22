package com.example.whosin.model.ViewModels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.MeetingToGroup;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<ArrayList<MeetingToGroup>> meetings;

    public void init (Activity context){
        if (meetings == null){
            meetings = FirebaseActions.loadUserMeetings(context);
        }
    }

    public LiveData<ArrayList<MeetingToGroup>> getMeetings(){
        return meetings;
    }
}
