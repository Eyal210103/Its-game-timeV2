package com.example.whosin.model.ViewModels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.MeetingToGroup;
import com.example.whosin.model.Objects.Group;

import java.util.ArrayList;


public class UserSharedViewModel extends ViewModel {
    private MutableLiveData<ArrayList<MutableLiveData<Group>>> groups;
    private MutableLiveData<ArrayList<MeetingToGroup>> meetings;
    Activity context;

    public void init (Activity context) {
        this.context = context;
    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getGroups(){
        if (groups == null){
            groups = FirebaseActions.loadUserGroups(context);
        }
        return groups;
    }
    public LiveData<ArrayList<MeetingToGroup>> getMeetings(){
        if (meetings == null) {
            meetings = FirebaseActions.loadUserMeetings(context);
        }
        return meetings;
    }
    public void setGroups(ArrayList<MutableLiveData<Group>> groups){
        this.groups.setValue(groups);
    }

}
