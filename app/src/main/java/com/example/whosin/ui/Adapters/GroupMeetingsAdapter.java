package com.example.whosin.ui.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosin.R;
import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.ui.Meetings.MeetingInfoFragment;

import java.util.ArrayList;

public class GroupMeetingsAdapter extends RecyclerView.Adapter<GroupMeetingsAdapter.ViewHolder> {

    private ArrayList<ActiveMeeting> meetings;
    private Context mContext;
    private FragmentManager fragmentManager;
    private  Group group;

    public GroupMeetingsAdapter(Context mContext, FragmentManager fragmentManager, ArrayList<ActiveMeeting> meetings , Group group) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.meetings = meetings;
        this.group = group;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_group_meetings,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ActiveMeeting activeMeeting = meetings.get(position);
        holder.textViewDay.setText(""+activeMeeting.getDay());
        holder.textViewHour.setText(String.format("%02d:%02d",activeMeeting.getHour(),activeMeeting.getMinute()));
        switch(activeMeeting.getMonth()){
            case 1:
                holder.textViewMonth.setText("Jan");
                break;

            case 2:
                holder.textViewMonth.setText("Feb");
                break;

            case 3:
                holder.textViewMonth.setText("Mar");
                break;

            case 4:
                holder.textViewMonth.setText("Apr");
                break;

            case 5:
                holder.textViewMonth.setText("May");
                break;

            case 6:
                holder.textViewMonth.setText("Jun");
                break;

            case 7:
                holder.textViewMonth.setText("Jul");
                break;

            case 8:
                holder.textViewMonth.setText("Aug");
                break;

            case 9:
                holder.textViewMonth.setText("Sep");
                break;

            case 10:
                holder.textViewMonth.setText("Oct");
                break;

            case 11:
                holder.textViewMonth.setText("Nov");
                break;

            case 12:
                holder.textViewMonth.setText("Dec");
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingInfoFragment nextFrag = new MeetingInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("meeting",  activeMeeting);
                bundle.putParcelable("group",  group);
                nextFrag.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace((R.id.container_fragments), nextFrag)
                        .addToBackStack("true")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
         public TextView textViewDay , textViewMonth , textViewHour;
         public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textView_month_group_adapter);
            textViewDay = itemView.findViewById(R.id.textView_day_group_adapter);
            textViewHour = itemView.findViewById(R.id.textView_hour_group_adpter);
        }
    }

}
