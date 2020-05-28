package com.example.whosin.ui.Meetings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosin.R;
import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.GPSTracker;
import com.example.whosin.model.Listeners.ArrivalUserListener;
import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.ViewModels.MeetingInfoViewModel;
import com.example.whosin.ui.Adapters.ArrivalUserAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.PlacePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlacePicker;

public class
MeetingInfoFragment extends Fragment implements ArrivalUserListener {


    private static int PLACE_PICKER_CODE = 1;

    private ActiveMeeting meeting;
    private Group group;
    private User user;
    private TextView timerTextView;
    private RecyclerView howsComing;
    private ArrivalUserAdapter arrivalUserAdapter;

    private MeetingInfoViewModel meetingInfoViewModel;

    private ArrayList<User> users;
    private int month;
    private int day;
    private long millSec;
    private double lat, currentLat;
    private double lon, currentLon;

    private MapView mapView;
    private GoogleMap mMap;

    private long mTimeLeftInMillis;
    private DatabaseReference reference;

    public MeetingInfoFragment() {
        users = new ArrayList<User>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meetingInfoViewModel = ViewModelProviders.of(this).get(MeetingInfoViewModel.class);
        this.user = CurrentUser.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_meeting_info, container, false);

        Bundle bundle = getArguments();
        this.meeting = (ActiveMeeting) bundle.getSerializable("meeting");
        this.group = (Group) bundle.getSerializable("group");
        this.reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getId());
        meetingInfoViewModel.init(this, group, meeting);

        SharedPreferences googleBug = getActivity().getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getActivity().getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }

        int year1 = meeting.getYear();
        month = meeting.getMonth() - 1;
        day = meeting.getDay();
        int hour1 = meeting.getHour();
        int minutes1 = meeting.getMinute();

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        long diffYear = year1 - currentYear;
        long diffMonth = month - currentMonth;
        long diffDay = day - currentDay;
        long diffHour = hour1 - currentHour;
        long diffMin = minutes1 - currentMin;
        this.millSec = getReminderDate();


        int year = meeting.getYear();
        month = meeting.getMonth() - 1;
        day = meeting.getDay();
        int hour = meeting.getHour();
        int minutes = meeting.getMinute();


        this.timerTextView = root.findViewById(R.id.textViewTimerTillMeeting);
        this.howsComing = root.findViewById(R.id.hows_coming_recycler);

        loadWhoComing();

        Button coming = root.findViewById(R.id.buttonImComingTFragment);
        coming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImComing();
            }
        });

        FloatingActionButton fabSpotify = root.findViewById(R.id.floatingActionButtonSpotify);
        fabSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFabSpotify();
            }
        });

        FloatingActionButton fabChooseLoc = root.findViewById(R.id.floatingActionButtonSelectLocation);
        fabChooseLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickfabLocation();
            }
        });

        updateCountDownText();
        startTimer();

        this.mapView = root.findViewById(R.id.mapViewMeeting);

        initGoogleMap(savedInstanceState);

        TextView streetView = root.findViewById(R.id.textView_street);
       // streetView.setText(getStreetName(meeting.getLat(), meeting.getLon()));
        return root;
    }

    private void startTimer() {
        CountDownTimer mCountDownTimer = new CountDownTimer(millSec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimeLeftInMillis = 0;
            }
        };
        mCountDownTimer.start();
    }

    private void updateCountDownText() {

        String timeLeftFormatted = "";

        if (mTimeLeftInMillis <= 0) {
            timeLeftFormatted = "Have Fun!";
        } else {
            int hours = (int) ((mTimeLeftInMillis / 1000) / 3600);
            int minutes = (int) (((mTimeLeftInMillis / 1000) % 3600) / 60);
            int seconds = (int) ((mTimeLeftInMillis / 1000) % 60);
            TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis);
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
        timerTextView.setText(timeLeftFormatted);
    }

    private long getReminderDate() {
        Date currentTime = new Date();
        Date end;
        if (currentTime.getMonth() <= month) {
            end = new Date(currentTime.getYear(), month, day);
        } else {
            end = new Date(currentTime.getYear() + 1, month, day);
        }
        return end.getTime() - currentTime.getTime();
    }

    private void onClickImComing() {
        FirebaseActions.confirmUserArrival(this.group, this.meeting);
    }

    private void loadWhoComing() {
        this.arrivalUserAdapter = new ArrivalUserAdapter(meetingInfoViewModel.getHowComing().getValue(), getActivity());
        howsComing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        //       howsComing.setLayoutManager(new GridLayoutManager(getActivity(),4));
        howsComing.setAdapter(arrivalUserAdapter);
    }

    private void onClickFabSpotify() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://open.spotify.com/playlist/3BJND41AmrWKXjcVvp1qdY?si=Mc6c1zbPSYmDtgD2Mr0SOA"));
        getActivity().startActivity(i);
    }

    private void onClickfabLocation() {
        try {
            GPSTracker gpsTracker = new GPSTracker(getActivity());
            Intent intent = new PlacePicker.IntentBuilder().onlyCoordinates(true)
                    .setLatLong(gpsTracker.getLatitude(), gpsTracker.getLongitude())
                    .setMarkerDrawable(R.drawable.find)
                    .setFabColor(R.color.colorSecondary)
                    .showLatLong(true)
                    .build(this.getActivity());
            startActivityForResult(intent, PLACE_PICKER_CODE);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_CODE) {
            AddressData addressData = (AddressData) data.getParcelableExtra(Constants.ADDRESS_INTENT);
            lat = addressData.getLatitude();
            lon = addressData.getLongitude();
            StringBuilder strReturnedAddress = new StringBuilder();
            List<Address> addresses = addressData.getAddressList();
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                }
                Toast.makeText(getActivity(), strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();
            }
            meeting.setLatLon(lat, lon);
            reference.child("ActiveMeeting").child(meeting.getId()).child("lat").setValue(lat);
            reference.child("ActiveMeeting").child(meeting.getId()).child("lon").setValue(lon);
            LatLng loc = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(loc).title(strReturnedAddress.toString()));
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
                try {
                    LatLng loc = new LatLng(meeting.getLat(), meeting.getLon());
                    MarkerOptions markerOptions = new MarkerOptions().position(loc).title("Meeting Location");
                    //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(urlToBitmap()));
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(loc)
                            .zoom(17).build();
                    //Zoom in and animate the camera.
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (Exception ignored) {
                }
            }
        });
    }

//    private String getStreetName(double lat, double lon) {
//        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
//
//            if (addresses != null) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder();
//                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
//                }
//                Toast.makeText(getActivity(), strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();
//                return strReturnedAddress.toString();
//            } else {
//                return "No Address returned!";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Canont get Address!";
//        }
//    }

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

    @Override
    public void onUserLoaded() {
        arrivalUserAdapter.notifyDataSetChanged();
    }
}
