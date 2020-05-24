package com.example.whosin.ui.Groups;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Listeners.GroupParticipantsLoadListener;
import com.example.whosin.model.Listeners.MeetingsLoadListener;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.ViewModels.GroupInfoViewModel;
import com.example.whosin.ui.Adapters.MemberAdapter;
import com.example.whosin.ui.Adapters.ParticipantAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class GroupSettingsFragment extends Fragment implements GroupParticipantsLoadListener, MeetingsLoadListener {

    private static final int PICK_IMAGE = 1012;
    private GroupInfoViewModel mViewModel;
    private StorageReference mStorageRef;
    private Group group;
    private DatabaseReference myRefGroups , myRef;
    private CircleImageView groupImage;
    private User user;
    private GroupInfoViewModel groupInfoViewModel;
    private ParticipantAdapter adapter;
    private RecyclerView participant;
    private ListView listView;
    private Bitmap bitmap;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = CurrentUser.getInstance();
        group = getArguments().getParcelable("group");
        groupInfoViewModel = ViewModelProviders.of(this).get(GroupInfoViewModel.class);
        groupInfoViewModel.init(this,group);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.group_settings_fragment, container, false);
        group = groupInfoViewModel.getGroup().getValue();

        this.mStorageRef = FirebaseStorage.getInstance().getReference("Group Images");
        groupImage = root.findViewById(R.id.circleImageView_group_setting);
        Glide.with(getActivity()).load(group.getImage()).into(groupImage);

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryGroup();
            }
        });
//
//        participant =  root.findViewById(R.id.RecyclerView_part_group_settings);
//        adapter = new ParticipantAdapter(new ArrayList<User>(),getActivity());
//        participant.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        participant.setAdapter(adapter);

        listView = root.findViewById(R.id.listView_temp);

        final EditText editText = root.findViewById(R.id.editText_group_settings);
        editText.setText(group.getGroupName());
        Button saveButton = root.findViewById(R.id.button_group_save_changes);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().matches("")){
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getId()).child("details").child("groupName").setValue(editText.getText().toString());
                }else {
                    Toast.makeText(getActivity(),"Please Type",Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadMembersList();

        return root;
    }


    private void openGalleryGroup() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            groupImage.setImageURI(imageUri);
            try {
                this.bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            changePhoto(imageUri);
        }
    }

    private void changePhoto(Uri imageUri){


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data = baos.toByteArray();

        mStorageRef.child("Group").child(group.getId()).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mStorageRef.child("Group").child(group.getId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getId()).child("details").child("image").setValue(task.getResult().toString());
                                Toast.makeText(getActivity(),"Photo Upload Succeed" , Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(),"Photo Wont Upload" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(GroupInfoViewModel.class);
        mViewModel.init(this,group);
    }

    @Override
    public void onGroupParticipantsLoaded() {
        groupInfoViewModel.getParticipants().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                adapter = new ParticipantAdapter(users,getActivity());
                participant.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onMeetingsLoaded() {

    }

    private void loadMembersList() {
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ids = new ArrayList<>();
                final ArrayList<User> users = new ArrayList<>();
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
        FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getId()).child("Members").addValueEventListener(valueEventListener);
    }
}
