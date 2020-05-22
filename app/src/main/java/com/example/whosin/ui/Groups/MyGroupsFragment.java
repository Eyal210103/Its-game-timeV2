package com.example.whosin.ui.Groups;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosin.R;
import com.example.whosin.model.Listeners.DataLoadListener;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.ViewModels.UserSharedViewModel;
import com.example.whosin.ui.Adapters.GroupsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MyGroupsFragment extends Fragment implements DataLoadListener {

    private static final int PICK_IMAGE = 102;

    private GroupsAdapter adapter;
    private User user;
    private UserSharedViewModel userSharedViewModel;

    public MyGroupsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSharedViewModel = ViewModelProviders.of(getActivity()).get(UserSharedViewModel.class);
        userSharedViewModel.init(getActivity());
        user = CurrentUser.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_groups, container, false);
        userSharedViewModel.init(getActivity());

        RecyclerView recyclerView = view.findViewById(R.id.listViewGroups);

        FloatingActionButton fabJoinGroup = view.findViewById(R.id.floatingActionButtonJoinGroup);
        fabJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapFragment();
            }
        });

        FloatingActionButton fabCreateGroup = view.findViewById(R.id.floatingActionButtonCreate);
        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFabCreateGroup();
            }
        });

        adapter = new GroupsAdapter(userSharedViewModel.getGroups().getValue(), user, getActivity(), getFragmentManager(), "true");
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        userSharedViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<ArrayList<Group>>() {
            @Override
            public void onChanged(ArrayList<Group> meetingToGroups) {
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void swapFragment() {
        SearchGroupFragment nextFrag = new SearchGroupFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), nextFrag)
                .addToBackStack(null)
                .commit();
    }

    @SuppressLint("SetTextI18n")
    private void onClickFabCreateGroup() {
        CreateGroupDialog dialog = new CreateGroupDialog();
        dialog.setTargetFragment(MyGroupsFragment.this, 2);
        dialog.show(getFragmentManager(), "Create Group");
    }

    @Override
    public void onGroupsLoaded() {
        try {
            userSharedViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<ArrayList<Group>>() {
                @Override
                public void onChanged(ArrayList<Group> meetingToGroups) {
                    adapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e){}
    }
}