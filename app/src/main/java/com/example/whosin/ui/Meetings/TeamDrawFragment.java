package com.example.whosin.ui.Meetings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.whosin.R;
import com.example.whosin.model.Objects.User;

import java.util.ArrayList;

public class TeamDrawFragment extends Fragment {

    private View root;
    private ArrayList<User> users;

    public TeamDrawFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_team_draw, container, false);

        return root;
    }
}
