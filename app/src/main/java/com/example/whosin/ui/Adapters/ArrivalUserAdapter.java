package com.example.whosin.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Objects.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArrivalUserAdapter extends RecyclerView.Adapter<ArrivalUserAdapter.ViewHolder>{

    private ArrayList<User> users;
    private Context mContext;

    public ArrivalUserAdapter(ArrayList<User> users , Context mContext) {
        this.users = users;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_how_coming_meeting,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        Glide.with(mContext).load(user.getImageUri()).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return users.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.civ_profile_image_recycler);
        }
    }
}
