package com.example.whosin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.whosin.R;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.ui.Adapters.MemberAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MembersDialog extends DialogFragment {


    private ListView listView;
    private DatabaseReference myRefGroups;
    private Group group;
    private View root;
    private DialogFragment df;

    public MembersDialog() {
        this.myRefGroups = FirebaseDatabase.getInstance().getReference().child("Groups");
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_members_view, container, false);
        listView = root.findViewById(R.id.dialog_members_listView);
        Bundle bundle = getArguments();
        this.group = (Group)bundle.getSerializable("group");
        df =this;
        loadMembersList();
        return root;
    }

    private void loadMembersList() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ids = new ArrayList<String>();
                final ArrayList<User> users = new ArrayList<User>();
                try {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
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

                    listView.setDividerHeight(1);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myRefGroups.child(group.getId()).child("Members").addValueEventListener(valueEventListener);
    }
}
