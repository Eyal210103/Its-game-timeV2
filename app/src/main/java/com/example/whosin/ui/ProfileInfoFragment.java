package com.example.whosin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.ui.Activities.LoginActivity;
import com.example.whosin.ui.Dialogs.UiSettingsDialog;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileInfoFragment extends Fragment {


    private User user;

    public ProfileInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = CurrentUser.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        CircleImageView profile = root.findViewById(R.id.circleImageViewProfile);
        final TextView name = root.findViewById(R.id.textViewNameInfo);

        Glide.with(getActivity().getApplicationContext()).load(user.getImageUri()).into(profile);
        name.setText(user.getFullName());

        root.findViewById(R.id.textview_settings_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiSettingsDialog dialogFragment = new UiSettingsDialog();
                dialogFragment.show(getFragmentManager(),"UI");
            }
        });


        return root;
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent logScreen = new Intent(getActivity(), LoginActivity.class);
        startActivity(logScreen);
        getActivity().finish();
    }

}
