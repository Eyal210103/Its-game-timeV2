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
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosin.R;
import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.ui.Meetings.MeetingInfoFragment;
import com.example.whosin.ui.Meetings.SetMeetingDialogHour;

import java.util.ArrayList;

public class GroupMeetingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 5;
    private static final int TYPE_OTHER = 6;
    private ArrayList<ActiveMeeting> meetings;
    private Context mContext;
    private Fragment fragment;
    private  Group group;

    public GroupMeetingsAdapter(Context mContext, Fragment fragment, ArrayList<ActiveMeeting> meetings , Group group) {
        this.meetings = new ArrayList<>();
        this.meetings.add(0,new ActiveMeeting());
        this.meetings.addAll(1,meetings);
        this.mContext = mContext;
        this.fragment = fragment;
        this.meetings = meetings;
        this.group = group;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_group_meetings, parent, false);
            return new MeetingsViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpater_create_meeting_group, parent, false);
            return new MeetingsCreateViewHolder(view);
        }
    }


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ActiveMeeting activeMeeting = meetings.get(position);
        if (getItemViewType(position) == TYPE_NORMAL) {
            ((MeetingsViewHolder) holder).textViewDay.setText("" + activeMeeting.getDay());
            ((MeetingsViewHolder) holder).textViewHour.setText(String.format("%02d:%02d", activeMeeting.getHour(), activeMeeting.getMinute()));
            switch (activeMeeting.getMonth()) {
                case 1:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Jan");
                    break;

                case 2:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Feb");
                    break;

                case 3:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Mar");
                    break;

                case 4:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Apr");
                    break;

                case 5:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("May");
                    break;

                case 6:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Jun");
                    break;

                case 7:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Jul");
                    break;

                case 8:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Aug");
                    break;

                case 9:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Sep");
                    break;

                case 10:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Oct");
                    break;

                case 11:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Nov");
                    break;

                case 12:
                    ((MeetingsViewHolder) holder).textViewMonth.setText("Dec");
                    break;
            }

            ((MeetingsViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MeetingInfoFragment nextFrag = new MeetingInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("meeting", activeMeeting);
                    bundle.putParcelable("group", group);
                    nextFrag.setArguments(bundle);
                    fragment.getFragmentManager().beginTransaction()
                            .replace((R.id.container_fragments), nextFrag)
                            .addToBackStack("true")
                            .commit();
                }
            });
        }else {
            ((MeetingsCreateViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetMeetingDialogHour dialog = new SetMeetingDialogHour();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group" ,group);
                    dialog.setArguments(bundle);
                    dialog.setTargetFragment(fragment, 1);
                    dialog.show(fragment.getFragmentManager(), "Set Meeting");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0) {
            return TYPE_NORMAL;
        } else {
            return TYPE_OTHER;
        }
    }

    class MeetingsViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDay , textViewMonth , textViewHour;
        public MeetingsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textView_month_group_adapter);
            textViewDay = itemView.findViewById(R.id.textView_day_group_adapter);
            textViewHour = itemView.findViewById(R.id.textView_hour_group_adpter);
        }
    }
    class MeetingsCreateViewHolder extends RecyclerView.ViewHolder {
        public MeetingsCreateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
