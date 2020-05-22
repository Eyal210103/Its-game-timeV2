package com.example.whosin.model.ViewModels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;

import java.util.ArrayList;

public class GroupInfoViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ActiveMeeting>> meetings;
    private MutableLiveData<Group> group;
    private MutableLiveData<ArrayList<User>> participants;

    public void init (Fragment context, Group group){
        meetings = FirebaseActions.loadGroupMeeting(context,group);
        this.group= new MutableLiveData<Group>();
        this.group.setValue(group);
        participants= FirebaseActions.loadGroupsParticipants(group,context);
    }

    public LiveData<ArrayList<ActiveMeeting>> getMeetings(){
        return meetings;
    }
    public LiveData<Group> getGroup() {
        return group;
    }
    public LiveData<ArrayList<User>> getParticipants() {
        return participants;
    }

}
