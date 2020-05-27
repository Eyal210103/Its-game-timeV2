package com.example.whosin.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosin.R;
import com.example.whosin.model.ImageResources;
import com.example.whosin.model.Listeners.DataLoadListener;
import com.example.whosin.model.Listeners.UserMeetingsLoadListener;
import com.example.whosin.model.MeetingToGroup;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.Singleton.Fragments;
import com.example.whosin.model.ViewModels.HomeViewModel;
import com.example.whosin.model.ViewModels.UserSharedViewModel;
import com.example.whosin.ui.Adapters.GroupsAdapter;
import com.example.whosin.ui.Adapters.HomeImageAdapter;
import com.example.whosin.ui.Adapters.UserMeetingsAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements DataLoadListener, UserMeetingsLoadListener {

    private User thisUser;
    private GroupsAdapter groupAdapter;
    private UserMeetingsAdapter meetingsAdapter;
    private DialogFragment loading;
    private Fragment fragment;

    private UserSharedViewModel userSharedViewModel;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerViewGroups;
    private static String TAG = "HOME FRAGMENT";
    FirebaseRecyclerAdapter adapter;

   //TODO Fix initial Lists Display
    public HomeFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userSharedViewModel = ViewModelProviders.of(getActivity()).get(UserSharedViewModel.class);
        meetingsAdapter = new UserMeetingsAdapter(userSharedViewModel.getMeetings().getValue(),getActivity(),this);
        groupAdapter = new GroupsAdapter(userSharedViewModel.getGroups().getValue(), thisUser, getActivity(), getFragmentManager(), "true");
        thisUser = CurrentUser.getInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //userSharedViewModel.init(getActivity());

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView buttons = root.findViewById(R.id.recyclerview_home_buttons);
        ArrayList<ImageResources> images = new ArrayList<>();

        images.add(new ImageResources(R.drawable.leadership,R.drawable.gradient_back_secondery, Fragments.getMyGroupsFragment()));
        images.add(new ImageResources(R.drawable.find,R.drawable.gradient_back_alt,Fragments.getFindGroupFragment()));
        images.add(new ImageResources(R.drawable.settings,R.drawable.gradient_back_blue,Fragments.getSettingFragment()));
        buttons.setNestedScrollingEnabled(false);
        buttons.setLayoutManager(horizontalLayoutManager);
        HomeImageAdapter homeImageAdapter = new HomeImageAdapter(images,getActivity(),getFragmentManager());
        buttons.setAdapter(homeImageAdapter);


        final RecyclerView recyclerViewMeetings = root.findViewById(R.id.recyclerview_home_meetings);
        recyclerViewMeetings.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMeetings.setAdapter(meetingsAdapter);

        recyclerViewGroups = root.findViewById(R.id.recyclerview_home_groups);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGroups.setAdapter(groupAdapter);
        return root;
    }

    @Override
    public void onGroupsLoaded() {
        try {
            userSharedViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<Object>() {
                @Override
                public void onChanged(Object o) {
                    groupAdapter.notifyDataSetChanged();

                }
            });
        }catch (Exception e){}
    }

    @Override
    public void onUserMeetingsLoaded() {
        try {
            userSharedViewModel.getMeetings().observe(getViewLifecycleOwner(), new Observer<ArrayList<MeetingToGroup>>() {
                @Override
                public void onChanged(ArrayList<MeetingToGroup> meetingToGroups) {
                    meetingsAdapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e){}

}

}