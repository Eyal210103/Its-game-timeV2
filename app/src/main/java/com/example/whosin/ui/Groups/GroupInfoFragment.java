package com.example.whosin.ui.Groups;


import android.annotation.SuppressLint;
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
import com.example.whosin.model.Listeners.DataLoadListener;
import com.example.whosin.model.Listeners.GroupParticipantsLoadListener;
import com.example.whosin.model.Listeners.MeetingsLoadListener;
import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.ViewModels.GroupInfoViewModel;
import com.example.whosin.ui.Adapters.GroupMeetingsAdapter;
import com.example.whosin.ui.Adapters.MemberAdapter;
import com.example.whosin.ui.Meetings.MeetingInfoFragment;
import com.example.whosin.ui.Meetings.SetMeetingDialogHour;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupInfoFragment extends Fragment implements MeetingsLoadListener, GroupParticipantsLoadListener, DataLoadListener {


    private static final String TAG = "Group Info";
    private DatabaseReference myRefGroups;

    private ListView listView;
    private Group group;
    private User user;
    private View root;
    private GroupInfoViewModel groupInfoViewModel;
    private GroupMeetingsAdapter meetingAdapter;
    private RecyclerView recyclerViewMeeting;

    CircleImageView groupImage;
    TextView name;
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
        groupInfoViewModel = ViewModelProviders.of(this).get(GroupInfoViewModel.class);
        groupInfoViewModel.init(this, group.getId());

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
        groupImage = root.findViewById(R.id.circleImageViewUserGroups);
        name = root.findViewById(R.id.textViewUser_GroupName);

        groupImage.setOnClickListener(new View.OnClickListener() {
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


        updateUi();


        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("group", group);
        chatFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container_chat, chatFragment).commit();

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

        recyclerViewMeeting = root.findViewById(R.id.active_meeting_view);
       // meetingAdapter = new GroupMeetingsAdapter(getContext().getApplicationContext(), this, groupInfoViewModel.getMeetings().getValue(), group);
        loadMeetings();

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
                Log.d(TAG, user.toString());
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
        bundle.putSerializable("group", group);
        bundle.putSerializable("user", user);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(GroupInfoFragment.this, 1);
        dialog.show(getFragmentManager(), "Set Meeting");
    }

    public void updateUi() {
        name.setText(group.getGroupName());
        Glide.with(getActivity()).load(group.getImage()).into(groupImage);
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

    @Override
    public void onGroupsLoaded() {
        groupInfoViewModel.getGroup().observe(this, new Observer<Group>() {
            @Override
            public void onChanged(Group group1) {
                try {
                    group = group1;
                    updateUi();
                } catch (Exception e) {

                }
            }
        });
    }

    FirebaseRecyclerAdapter adapter;

    public void loadMeetings() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Groups").child(group.getId())
                .child("ActiveMeeting");

//        FirebaseRecyclerOptions<ActiveMeeting> options =
//                new FirebaseRecyclerOptions.Builder<ActiveMeeting>().setQuery(query, new SnapshotParser<ActiveMeeting>() {
//                    @NonNull
//                    @Override
//                    public ActiveMeeting parseSnapshot(@NonNull DataSnapshot snapshot) {
//                        ActiveMeeting activeMeeting =snapshot.child(snapshot.getKey()).getValue(ActiveMeeting.class);
//                        return activeMeeting;
//                    }
//                }).build();

        FirebaseRecyclerOptions<ActiveMeeting> options =
                new FirebaseRecyclerOptions.Builder<ActiveMeeting>().setQuery(query, ActiveMeeting.class).build();

        adapter = new FirebaseRecyclerAdapter<ActiveMeeting, RecyclerView.ViewHolder>(options) {
            private static final int TYPE_NORMAL = 5;
            private static final int TYPE_OTHER = 6;

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == TYPE_NORMAL) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_group_meetings, parent, false);
                    return new GroupMeetingsAdapter.MeetingsViewHolder(view);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpater_create_meeting_group, parent, false);
                    return new GroupMeetingsAdapter.MeetingsCreateViewHolder(view);
                }
            }


            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ActiveMeeting model) {
                final ActiveMeeting activeMeeting = getSnapshots().get(position);
                if (getItemViewType(position) == TYPE_NORMAL) {
                    ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewDay.setText("" + activeMeeting.getDay());
                    ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewHour.setText(String.format("%02d:%02d", activeMeeting.getHour(), activeMeeting.getMinute()));
                    switch (activeMeeting.getMonth()) {
                        case 1:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Jan");
                            break;

                        case 2:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Feb");
                            break;

                        case 3:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Mar");
                            break;

                        case 4:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Apr");
                            break;

                        case 5:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("May");
                            break;

                        case 6:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Jun");
                            break;

                        case 7:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Jul");
                            break;

                        case 8:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Aug");
                            break;

                        case 9:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Sep");
                            break;

                        case 10:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Oct");
                            break;

                        case 11:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Nov");
                            break;

                        case 12:
                            ((GroupMeetingsAdapter.MeetingsViewHolder) holder).textViewMonth.setText("Dec");
                            break;
                    }

                    ((GroupMeetingsAdapter.MeetingsViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MeetingInfoFragment nextFrag = new MeetingInfoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("meeting", activeMeeting);
                            bundle.putParcelable("group", group);
                            nextFrag.setArguments(bundle);
                            fragment.getFragmentManager().beginTransaction()
                                    .replace((R.id.container_fragments), nextFrag)
                                    .addToBackStack("true")
                                    .commit();
                        }
                    });
                } else {
                    ((GroupMeetingsAdapter.MeetingsCreateViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SetMeetingDialogHour dialog = new SetMeetingDialogHour();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("group", group);
                            dialog.setArguments(bundle);
                            dialog.setTargetFragment(fragment, 1);
                            dialog.show(fragment.getFragmentManager(), "Set Meeting");
                        }
                    });
                }
            }


            @Override
            public int getItemCount() {
                return getSnapshots().size();
            }

            @Override
            public int getItemViewType(int position) {
                if (position != 0) {
                    return TYPE_NORMAL;
                } else {
                    return TYPE_OTHER;
                }
            }

            class MeetingsViewHolder extends RecyclerView.ViewHolder {
                public TextView textViewDay, textViewMonth, textViewHour;

                public MeetingsViewHolder(@NonNull View itemView) {
                    super(itemView);
                    textViewMonth = itemView.findViewById(R.id.textView_month_group_adapter);
                    textViewDay = itemView.findViewById(R.id.textView_day_group_adapter);
                    textViewHour = itemView.findViewById(R.id.textView_hour_group_adpter);
                }
            }

            class MeetingsCreateViewHolder extends RecyclerView.ViewHolder {
                public MeetingsCreateViewHolder(@NonNull View itemView) {
                    super(itemView);
                }
            }
        };
        recyclerViewMeeting.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMeeting.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
