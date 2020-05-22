package com.example.whosin.model.ViewModels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.Objects.SingleMeeting;

import java.util.ArrayList;

public class FindViewModel extends ViewModel {

    private MutableLiveData<ArrayList<SingleMeeting>> singleMeetings;

    public void init(Fragment context) {
        singleMeetings = FirebaseActions.loadAllSingleMeetings(context);
    }

    public LiveData<ArrayList<SingleMeeting>> getSingleMeetings() {
        return singleMeetings;
    }
}