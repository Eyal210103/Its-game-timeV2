package com.example.whosin.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Objects.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private ArrayList<User> participants;
    private Context context;


    public ParticipantAdapter(@NonNull ArrayList<User> participants, Context context) {
        this.participants = participants;
        this.context = context;
    }

    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_members,parent,false);
        return new ParticipantViewHolder(view);
    }

    public void add(User user){
        participants.add(user);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        User user = participants.get(position);
        holder.textViewListMemberName.setText(user.getFullName());
        holder.textViewMemberEmail.setText(user.getEmail());
        Glide.with(context).load(user.getImageUri()).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    class ParticipantViewHolder extends RecyclerView.ViewHolder {
        TextView textViewListMemberName , textViewMemberEmail;
        CircleImageView profileImage;
        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewListMemberName = itemView.findViewById(R.id.textViewListMemberName);
            textViewMemberEmail = itemView.findViewById(R.id.textViewMemberEmail);
            profileImage = itemView.findViewById(R.id.circleImageView_participant_image);
        }
    }
}
