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
    private Fragment context;

    public void init (Fragment context, String id){
        this.context = context;
        this.group = FirebaseActions.getGroupById(id, context);
        meetings = FirebaseActions.loadGroupMeetingsWithHolder(context, id);

    }

    public LiveData<ArrayList<ActiveMeeting>> getMeetings(){
       // sort(meetings.getValue());
        return meetings;
    }
    public LiveData<Group> getGroup() {
        return group;
    }
    public LiveData<ArrayList<User>> getParticipants() {
        if (participants != null) {
            participants = FirebaseActions.loadGroupsParticipants(group.getValue(), context);
        }
        return participants;
    }

    private void sort(ArrayList<ActiveMeeting> meetings) {
        ArrayList<ActiveMeeting> m = new ArrayList<ActiveMeeting>();
        for (int i = 1; i <13; i++) {
            ArrayList<ActiveMeeting> month = new ArrayList<ActiveMeeting>();
            for (int j = 0; j < meetings.size(); j++) {
                if (meetings.get(j).getMonth() == i){
                    month.add(meetings.get(j));
                    meetings.remove(j);
                }
            }
            for (int j = 0; j < month.size(); j++) {
                for (int k = 1; k < 32; k++) {
                    if (month.get(j).getDay() == k){
                        m.add(month.get(j));
                        month.remove(j);
                    }
                }
            }
        }
        this.meetings.setValue(m);
    }
}
