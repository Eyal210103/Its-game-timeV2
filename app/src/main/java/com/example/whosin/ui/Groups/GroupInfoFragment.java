package com.example.whosin.ui.Groups;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Listeners.GroupParticipantsLoadListener;
import com.example.whosin.model.Listeners.MeetingsLoadListener;
import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.ViewModels.GroupInfoViewModel;
import com.example.whosin.ui.Adapters.GroupMeetingsAdapter;
import com.example.whosin.ui.Adapters.MemberAdapter;
import com.example.whosin.ui.Meetings.SetMeetingDialogHour;
import com.example.whosin.ui.MembersDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupInfoFragment extends Fragment implements MeetingsLoadListener , GroupParticipantsLoadListener {


    private static final String TAG = "Group Info";
    private DatabaseReference myRefGroups;

    private ListView listView;
    private Group group;
    private User user;
    private View root;
    private GroupInfoViewModel groupInfoViewModel;
    private GroupMeetingsAdapter meetingAdapter;
    private RecyclerView recyclerViewMeeting;
    private GroupInfoViewModel mViewModel;
    private Fragment fragment;

    public GroupInfoFragment() {
        myRefGroups = FirebaseDatabase.getInstance().getReference().child("Groups");
        fragment = this;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        group = (Group) bundle.getSerializable("group");
        user = CurrentUser.getInstance();
        mViewModel = ViewModelProviders.of(getActivity()).get(GroupInfoViewModel.class);
        mViewModel.init(this,group);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String isUsers = (String) bundle.getSerializable("view");

        if (isUsers.equals("true")) {
            root = inflater.inflate(R.layout.fragment_group_info, container, false);
            loadIfUserIn();
        } else {
            root = inflater.inflate(R.layout.group_minimal_info_fragment, container, false);
            loadIfUserNotIn();
        }

        return root;
    }

    private void loadIfUserIn() {
        CircleImageView groupImage = root.findViewById(R.id.circleImageViewUserGroups);
        FloatingActionButton fab = root.findViewById(R.id.floatingActionButtonCreateMeet);
        FloatingActionButton fabMembers = root.findViewById(R.id.floatingActionButtonMembers);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFab();
            }
        });

        fabMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupSettingsFragment nextFrag = new GroupSettingsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("group", group);
                nextFrag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragments, nextFrag);
                fragmentTransaction.addToBackStack("true");
                fragmentTransaction.commit();
            }
        });

        groupInfoViewModel = ViewModelProviders.of(this).get(GroupInfoViewModel.class);
        groupInfoViewModel.init(this,group);

        final TextView name = root.findViewById(R.id.textViewUser_GroupName);

        Group current = mViewModel.getGroup().getValue();
        Glide.with(getActivity().getApplicationContext()).load(current.getImage()).into(groupImage);
        name.setText(current.getGroupName());

        recyclerViewMeeting = root.findViewById(R.id.active_meeting_view);
        meetingAdapter = new GroupMeetingsAdapter(getContext().getApplicationContext(),getFragmentManager(), groupInfoViewModel.getMeetings().getValue(), current);
        recyclerViewMeeting.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMeeting.setAdapter(meetingAdapter);

        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("group", current);
        chatFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container_chat , chatFragment).commit();


        ImageView bigChat = root.findViewById(R.id.imageViewBigChat);
        bigChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatFragment nextFrag = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("group", group);
                nextFrag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragments, nextFrag);
                fragmentTransaction.addToBackStack("true");
                fragmentTransaction.commit();
            }
        });
    }

    private void loadIfUserNotIn() {
        listView = root.findViewById(R.id.members_listView);

        final CircleImageView groupImage = root.findViewById(R.id.Info_group_image);
        CircleImageView sportsImage = root.findViewById(R.id.Info_group_sports);

        TextView name = root.findViewById(R.id.textViewInfoName);

        Glide.with(getActivity().getApplicationContext()).load(group.getImage()).into(groupImage);
        name.setText(group.getGroupName());

        String sports = group.getSports();
        if (sports.equals("Basketball")) {
            sportsImage.setImageResource(R.drawable.basketball);
        } else if (sports.equals("Soccer")) {
            sportsImage.setImageResource(R.drawable.soccer);
        }

        loadMembersList();

        Button joinButton = root.findViewById(R.id.buttonJoinThis);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG , user.toString());
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getId());
                ref.child("Members").child(user.getId()).setValue(user.getId());
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getId()).child("Groups").child(group.getId()).setValue(group.getId());
            }
        });

    }

    private void loadMembersList() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ids = new ArrayList<String>();
                final ArrayList<User> users = new ArrayList<User>();
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ids.add(postSnapshot.getValue(String.class));
                    }
                    users.clear();
                    for (String id : ids) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("details").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                users.add(dataSnapshot.getValue(User.class));
                                MemberAdapter adapter = new MemberAdapter(getContext().getApplicationContext(), R.layout.adapter_members, users);
                                listView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    listView.setDividerHeight(2);
                    Log.d("GET DATA", "+++++++++++++++++++++++++++++++++++++++++++++" + users.toString());
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myRefGroups.child(group.getId()).child("Members").addValueEventListener(valueEventListener);
    }

    private void onClickFab() {
        SetMeetingDialogHour dialog = new SetMeetingDialogHour();
        Bundle bundle = new Bundle();
        bundle.putSerializable("group" ,group);
        bundle.putSerializable("user" ,user);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(GroupInfoFragment.this, 1);
        dialog.show(getFragmentManager(), "Set Meeting");
    }

    private void onClickFabMembers() {
        MembersDialog dialog = new MembersDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("group", group);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(GroupInfoFragment.this, 2);
        dialog.show(getFragmentManager(), "Members");
    }

    @Override
    public void onMeetingsLoaded() {
        groupInfoViewModel.getMeetings().observe(this, new Observer<ArrayList<ActiveMeeting>>() {
            @Override
            public void onChanged(ArrayList<ActiveMeeting> activeMeetings) {
               meetingAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onGroupParticipantsLoaded() {

    }
}
