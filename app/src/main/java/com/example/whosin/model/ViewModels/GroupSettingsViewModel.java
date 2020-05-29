package com.example.whosin.model.ViewModels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;

import java.util.ArrayList;

public class GroupSettingsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<User>> participants;

    public void init(Fragment context, Group group) {
        if (participants == null){
            participants= FirebaseActions.loadGroupsParticipants(group.getId(),context);
        }
    }
    public LiveData<ArrayList<User>> getParticipants() {
        return participants;
    }
}
