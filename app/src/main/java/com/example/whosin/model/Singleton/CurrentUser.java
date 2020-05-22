package com.example.whosin.model.Singleton;

import com.example.whosin.model.Objects.User;
import com.google.firebase.auth.FirebaseAuth;

public class CurrentUser {
    private static User instance = null;

    private CurrentUser() {
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
            instance.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            instance.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            instance.setImageUri(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            instance.setFullName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            instance.setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
        return instance;
    }

    public static void logout() {
        instance = null;
    }

    public static void updateInstance(){
        instance.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        instance.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        instance.setImageUri(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        instance.setFullName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        instance.setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
    }
}
