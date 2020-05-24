package com.example.whosin.ui.Groups;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.whosin.R;
import com.example.whosin.model.Listeners.DataLoadListener;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class CreateGroupDialog extends DialogFragment {

    private static final int PICK_IMAGE = 106;
    private Group newGroup;
    private EditText groupName;
    private RadioGroup sportsType;
    private EditText other;
    private boolean classBoolean;
    private User thisUser;
    private Uri imageUri;
    private Bitmap bitmap;
    private View root;

    private FirebaseDatabase database;
    private DatabaseReference myRefGroups , myRef;
    private StorageReference mStorageRef;
    private DialogFragment dialogFragment  = this;

    public CreateGroupDialog() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dialog_create_group, container, false);
        this.setCancelable(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.newGroup = new Group();

        this.thisUser = CurrentUser.getInstance();

        this.groupName = root.findViewById(R.id.editTextGroupName);
        this.sportsType = root.findViewById(R.id.radioGroup);
        this.other = root.findViewById(R.id.editTextOtherSport);

        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference().child("Users");
        this.myRefGroups = database.getReference().child("Groups");
        this.mStorageRef = FirebaseStorage.getInstance().getReference("Group Images");

        root.findViewById(R.id.buttonCreateGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateGroup();
            }
        });

        root.findViewById(R.id.buttonCancelCreation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancelGroup();
            }
        });

        root.findViewById(R.id.buttonSelectGroupImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalleryGroup();
            }
        });
        return root;
    }

    @SuppressLint("SetTextI18n")
    private void onClickCreateGroup() {
        this.newGroup = new Group();
        boolean isGood = false;
        TextView errors = root.findViewById(R.id.textViewErrors);

        String sport = "";
        if (!groupName.getText().toString().matches("")) {
            final String name = groupName.getText().toString();

            if (sportsType.getCheckedRadioButtonId() == R.id.radioButtonBasketball) {
                sport = "Basketball";
                isGood = true;
            } else if (sportsType.getCheckedRadioButtonId() == R.id.radioButtonSoccer) {
                sport = "Soccer";
                isGood = true;
            } else if (sportsType.getCheckedRadioButtonId() == R.id.radioButtonFootball) {
                sport = "American Football";
                isGood = true;
            } else if (sportsType.getCheckedRadioButtonId() == R.id.radioButtonOther) {
                if (!other.getText().toString().matches("")) {
                    sport = other.getText().toString();
                    isGood = true;
                } else {
                    errors.setText("Type The Kind Of Sports");
                }
            }
            if (isGood) {
                this.newGroup.setGroupName(name);
                this.newGroup.setSports(sport);
                final String groupImageURL = "https://www.liberaldictionary.com/wp-content/uploads/2018/11/null.png";
                final DatabaseReference ref = myRefGroups.push();
                newGroup.setId(ref.getKey());
                CircleImageView selectedImage = root.findViewById(R.id.group_ImageView);
                selectedImage.setDrawingCacheEnabled(true);
                selectedImage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) selectedImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                byte[] data = baos.toByteArray();

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Creating , Please Wait..");
                progressDialog.show();
                mStorageRef.child("Group").child(newGroup.getId()).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            mStorageRef.child("Group").child(newGroup.getId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        newGroup.setImage(task.getResult().toString());
                                    }else {
                                        Toast.makeText(getActivity(),"Photo Wont Upload" , Toast.LENGTH_SHORT).show();
                                        newGroup.setImage(groupImageURL);
                                    }
                                    ref.child("details").setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ref.child("Members").child(thisUser.getId()).setValue(thisUser.getId());
                                            myRef.child(thisUser.getId()).child("Groups").child(newGroup.getId()).setValue(newGroup.getId());
                                            DataLoadListener dataLoadListener = (DataLoadListener) getActivity();
                                            dataLoadListener.onGroupsLoaded();
                                            progressDialog.dismiss();
                                            dialogFragment.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        } else {
            errors.setText("Type");
        }
    }

    private void openGalleryGroup() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            this.imageUri = data.getData();
            CircleImageView selectedImage = root.findViewById(R.id.group_ImageView);
            selectedImage.setImageURI(this.imageUri);
            try {
                this.bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void onClickCancelGroup(){
        this.dismiss();
    }
}
