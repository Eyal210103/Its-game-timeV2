package com.example.whosin.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.ViewModels.GroupMinimalInfoViewModel;
import com.example.whosin.ui.Adapters.MemberAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMinimalInfoFragment extends Fragment {

    private GroupMinimalInfoViewModel mViewModel;
    private View root;
    private ListView listView;
    private Group group;
    private User user;


    public static GroupMinimalInfoFragment newInstance() {
        return new GroupMinimalInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = CurrentUser.getInstance();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.group_minimal_info_fragment, container, false);

        Bundle bundle = getArguments();
        group = (Group) bundle.getSerializable("group");
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupMinimalInfoViewModel.class);
        // TODO: Use the ViewModel
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
        DatabaseReference myRefGroups = FirebaseDatabase.getInstance().getReference().child("Groups");
        myRefGroups.child(group.getId()).child("Members").addValueEventListener(valueEventListener);
    }


}
