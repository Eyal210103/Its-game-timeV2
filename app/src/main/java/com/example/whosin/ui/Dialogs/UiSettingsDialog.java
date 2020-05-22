package com.example.whosin.ui.Dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosin.R;
import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.ImageResources;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.ViewModels.UserSharedViewModel;
import com.example.whosin.ui.Adapters.HomeImageAdapter;
import com.example.whosin.ui.Adapters.GroupsAdapter;
import com.example.whosin.ui.Adapters.UserMeetingsAdapter;
import com.example.whosin.ui.Groups.MyGroupsFragment;
import com.example.whosin.ui.findGroup.FindGroupFragment;

import java.util.ArrayList;

public class UiSettingsDialog extends DialogFragment {

    private SharedPreferences sharedPref;

    private UserSharedViewModel userSharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSharedViewModel = ViewModelProviders.of(getActivity()).get(UserSharedViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_home_ui_preferences, container, false);
         sharedPref = getActivity().getPreferences(getActivity().MODE_PRIVATE);

        User thisUser = CurrentUser.getInstance();

        RecyclerView recyclerViewShortCuts = root.findViewById(R.id.recyclerView_home_shortcuts);
        RecyclerView recyclerViewGroup = root.findViewById(R.id.recyclerview_home_groups_setting);
        RecyclerView recyclerViewMeetings = root.findViewById(R.id.recyclerView_home_meeting_settings);

        ArrayList<ImageResources> images = new ArrayList<>();

        images.add(new ImageResources(R.drawable.leadership,R.drawable.gradient_back_secondery,new MyGroupsFragment()));
        images.add(new ImageResources(R.drawable.find,R.drawable.gradient_back_alt,new FindGroupFragment()));
        images.add(new ImageResources(R.drawable.settings,R.drawable.gradient_back_blue,new MyGroupsFragment()));

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewShortCuts.setLayoutManager(horizontalLayoutManager);
        HomeImageAdapter homeImageAdapter = new HomeImageAdapter(images,getActivity(),getFragmentManager());
        recyclerViewShortCuts.setAdapter(homeImageAdapter);


        UserMeetingsAdapter meetingsAdapter = new UserMeetingsAdapter(FirebaseActions.loadUserMeetings(getActivity()).getValue(), getActivity().getApplicationContext(), this);
        recyclerViewMeetings.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMeetings.setAdapter(meetingsAdapter);

        GroupsAdapter groupAdapter = new GroupsAdapter(userSharedViewModel.getGroups().getValue(), thisUser, getActivity(), getFragmentManager(), "true");
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGroup.setAdapter(groupAdapter);

        return root;
    }
}
