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

public class MeetingInfoViewModel extends ViewModel {
    private MutableLiveData<ArrayList<User>> howComing;

    public void init(Fragment context, Group group, ActiveMeeting meeting){
        if (howComing == null) {
            howComing = FirebaseActions.loadConfirmUsers(context, group, meeting);
        }
    }
    public LiveData<ArrayList<User>> getHowComing(){return  howComing;}
}
