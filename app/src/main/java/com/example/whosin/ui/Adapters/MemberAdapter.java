package com.example.whosin.ui.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private int resource;

    public MemberAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context,resource,objects);
        mContext = context;
        this.resource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name;
        String email;
        String url;
        if (getItem(position) == null){
             FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
             name = user.getDisplayName();
             email = user.getEmail();
             url = user.getPhotoUrl().toString();
        }else {
             name = getItem(position).getFullName();
             email = getItem(position).getEmail();
             url = getItem(position).getImageUri();
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(resource, parent, false);

        TextView textViewName = convertView.findViewById(R.id.textViewListMemberName);
        TextView textViewEmail = convertView.findViewById(R.id.textViewMemberEmail);
        CircleImageView circleImageView = convertView.findViewById(R.id.circleImageView_participant_image);
        textViewName.setText(name);
        textViewEmail.setText(email);
        Glide.with(mContext).load(Uri.parse(url)).into(circleImageView);
        return convertView;
    }
}
