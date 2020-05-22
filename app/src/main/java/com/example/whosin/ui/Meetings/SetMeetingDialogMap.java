
package com.example.whosin.ui.Meetings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.whosin.R;
import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.GPSTracker;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.PlacePicker;

public class SetMeetingDialogMap extends DialogFragment {

    private static final int PLACE_PICKER_CODE = 988;


    private Button submit;
    private MapView mapView;
    private GoogleMap mMap;
    private boolean isOpen;

    private double lat;
    private double lon;

    private int[] date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialod_set_meeting_map, container, false);
        mapView = root.findViewById(R.id.mapViewSetMeeting);
        final Bundle bundle =getArguments();
        final Group group = (Group)bundle.getSerializable("group");
        final User user = (User)bundle.getSerializable("user");
        lat = 0;
        lon= 0;
        isOpen = false;
        date = (int[]) getArguments().getSerializable("date");

        Switch switchIs = root.findViewById(R.id.switch_allow_to_join);
        switchIs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked){
                   isOpen = true;
               }
               else  {
                   isOpen = false;
               }
            }
        });
        Button selectLocation = root.findViewById(R.id.buttonSelectPlace);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLocationSelect();
            }
        });
        submit = root.findViewById(R.id.button_set_meeting);
        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                ActiveMeeting meeting = new ActiveMeeting();
                meeting.setYear(date[0]);
                meeting.setMonth(date[1]);
                meeting.setDay(date[2]);
                meeting.setHour(date[3]);
                meeting.setMinute(date[4]);
                meeting.setLatLon(lat,lon);
                meeting.setOpen(isOpen);
                getDialog().dismiss();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getId()).child("ActiveMeeting").push();
                meeting.setId(reference.getKey());
                reference.setValue(meeting);

                if (isOpen){
                    FirebaseDatabase.getInstance().getReference().child("OpenMeetings").child("ActiveMeeting").push().setValue(meeting.getId());
                }

                MeetingInfoFragment nextFrag = new MeetingInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.putSerializable("group" , group);
                bundle.putSerializable("meeting" ,meeting);
                nextFrag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragments , nextFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        initGoogleMap(savedInstanceState);
        return root;
    }


    private void onClickLocationSelect(){
        try {
            GPSTracker gpsTracker = new GPSTracker(getActivity());
            Intent intent = new PlacePicker.IntentBuilder().onlyCoordinates(true)
                    .setLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude())
                    .setMarkerDrawable(R.drawable.find)
                    .setFabColor(R.color.colorSecondary)
                    .showLatLong(true)
                    .build(this.getActivity());
            startActivityForResult(intent, PLACE_PICKER_CODE);
        }catch (Exception ignored){ }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_CODE){
            AddressData addressData = (AddressData) data.getParcelableExtra(Constants.ADDRESS_INTENT);
            lat = addressData.getLatitude();
            lon = addressData.getLongitude();
            LatLng loc = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(loc).title("Meeting Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Toast.makeText(getActivity(), "Map Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    private void swapToGroupFragment() {

    }
}
