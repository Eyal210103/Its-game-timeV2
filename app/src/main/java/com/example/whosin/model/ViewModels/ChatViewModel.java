package com.example.whosin.model.ViewModels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.MessageChat;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<ArrayList<MessageChat>> messages;


    public void init (Fragment context, Group group){
        messages = FirebaseActions.loadMessages(context,group);
    }
    public LiveData<ArrayList<MessageChat>> getMessages(){
        return messages;
    }
}
