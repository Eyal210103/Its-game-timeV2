package com.example.whosin.ui.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.MeetingToGroup;
import com.example.whosin.ui.Meetings.MeetingInfoFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMeetingsAdapter extends RecyclerView.Adapter<UserMeetingsAdapter.ViewHolder> {

    public ArrayList<MeetingToGroup> mMeetings;
    private Context mContext;
    private Fragment fragment;

    public UserMeetingsAdapter(ArrayList<MeetingToGroup> mMeetings, Context mContext, Fragment fragment) {
        this.mMeetings = mMeetings;
        this.mContext = mContext;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_meetings,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        MeetingToGroup activeMeeting = mMeetings.get(position);
        try {
            Glide.with(mContext).load(activeMeeting.getGroup().getImage()).into(holder.circleImageView);
            holder.textViewGroupName.setText(activeMeeting.getGroup().getGroupName());
            holder.textViewHour.setText(String.format("%02d:%02d", activeMeeting.getMeeting().getHour(), activeMeeting.getMeeting().getMinute()));
            holder.textViewDate.setText(String.format("%02d.%02d.%02d", activeMeeting.getMeeting().getDay(), activeMeeting.getMeeting().getMonth(), activeMeeting.getMeeting().getYear()));
        }
        catch (Exception e){
            Glide.with(mContext).load(R.color.colorAlt).into(holder.circleImageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingInfoFragment nextFrag = new MeetingInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("group" , mMeetings.get(position).getGroup());
                bundle.putParcelable("meeting" , mMeetings.get(position).getMeeting());
                nextFrag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragment.getFragmentManager().beginTransaction();
                fragmentTransaction.replace((R.id.container_fragments), nextFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }



    public void setMeetings(ArrayList<MeetingToGroup> meetingToGroups) {
        mMeetings = meetingToGroups;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDate , textViewHour ,textViewGroupName;
        CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textView_date_user_m_adapter);
            textViewHour = itemView.findViewById(R.id.textView_hour_user_m_adapter);
            textViewGroupName = itemView.findViewById(R.id.textView_group_name_user_m_adapter);
            circleImageView = itemView.findViewById(R.id.civ_user_m_adapter);
        }
    }
}
