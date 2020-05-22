package com.example.whosin.ui.Groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchGroupFragment extends Fragment {

    ArrayList<Group> groups;
    FirebaseDatabase database;
    DatabaseReference myRefGroups, myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText name;
    private RecyclerView recyclerView;
    private User user;
    private FirebaseRecyclerAdapter adapter;

    public SearchGroupFragment() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Users");
        myRefGroups = database.getReference().child("Groups");
        this.user = CurrentUser.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search_group, container, false);

        this.database = FirebaseDatabase.getInstance();
        this.myRefGroups = database.getReference();//.child("Groups");
        this.recyclerView = root.findViewById(R.id.groupFoundList);
        this.name = root.findViewById(R.id.editGroupName);
        this.loadGroups();
        return root;
    }

//    private void loadGroups() {
//        DatabaseReference myRef = database.getReference().child("Groups");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final ArrayList<Group> groupsObj = new ArrayList<Group>();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Group group = postSnapshot.child("details").getValue(Group.class);
//                    groupsObj.add(group);
//                    adapter = new GroupsAdapter(groupsObj , user,getContext().getApplicationContext(),getFragmentManager(),"false");
//                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

    private void loadGroups() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Groups")
                .limitToLast(50);

        FirebaseRecyclerOptions<Group> options =
                new FirebaseRecyclerOptions.Builder<Group>().setQuery(query, new SnapshotParser<Group>() {
                    @NonNull
                    @Override
                    public Group parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot.child("details").getValue(Group.class);
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<Group, GroupsViewHolder>(options) {
            @NonNull
            @Override
            public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_groups,parent,false);
                return new GroupsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GroupsViewHolder holder, int position, @NonNull final Group model) {
                Group group = model;

                Glide.with(getActivity()).load(group.getImage()).into(holder.circleImageView);
                holder.textViewName.setText(group.getGroupName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GroupInfoFragment nextFrag = new GroupInfoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("group" ,model);
                        bundle.putString("view" , "false");
                        nextFrag.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace((R.id.container_fragments), nextFrag);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }
        };

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);
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
class GroupsViewHolder extends RecyclerView.ViewHolder {
    TextView textViewName;
    CircleImageView circleImageView;

    public GroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.list_groupName);
        circleImageView = itemView.findViewById(R.id.groupLogo);
    }
}

