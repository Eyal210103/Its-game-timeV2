package com.example.whosin.ui.Meetings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.whosin.model.Objects.Group;
import com.example.whosin.R;
import com.example.whosin.model.Objects.User;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Arrays;

public class SetMeetingDialogHour extends DialogFragment {



    TimePicker timePicker;
    DatePicker datePicker;
    Button submit;

    //values to get
    int year;
    int month;
    int day;
    int hour;
    int minute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_create_meeting_hour, container, false);

        timePicker = root.findViewById(R.id.timepicker);
        datePicker = root.findViewById(R.id.calendarView);

        submit = root.findViewById(R.id.buttonSubmitDate);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = datePicker.getYear();
                month = datePicker.getMonth()+1;
                day = datePicker.getDayOfMonth();
                hour = timePicker.getHour();
                minute = timePicker.getMinute();
                Bundle bundle = new Bundle();
                bundle.putSerializable("date" ,new int[]{year, month, day, hour, minute} );
                bundle.putSerializable("group",(Group)getArguments().getSerializable("group"));
                bundle.putSerializable("user",(User)getArguments().getSerializable("user"));
                SetMeetingDialogMap dialog = new SetMeetingDialogMap();
                dialog.setArguments(bundle);
                dialog.setTargetFragment(SetMeetingDialogHour.this, 3);
                dialog.show(getFragmentManager(), "Set Meeting");
                getDialog().dismiss();
                Log.d("date", Arrays.toString(new int[]{year, month, day, hour, minute}));

            }
        });
        return root;
    }

}
