package com.example.whosin.ui.findGroup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.whosin.R;
import com.example.whosin.model.Listeners.GroupsLoadListener;
import com.example.whosin.model.Objects.SingleMeeting;
import com.example.whosin.model.ViewModels.FindViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.PlacePicker;

import java.io.File;

public class FindGroupFragment extends Fragment  implements GroupsLoadListener {
    private static final int PLACE_PICKER_CODE = 55;
    private static final int REQUEST_LOCATION = 1;
    //private static final String MAPBUNDLEKEY  = "MapViewBundleKey";
    private MapView mapView;
    private GoogleMap mMap;
    private FindViewModel viewModel;
    private double latitude = 0;
    private double longitude = 0;

    public FindGroupFragment() { }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FindViewModel.class);
        viewModel.init(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_find_group, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        this.mapView = root.findViewById(R.id.map_view);

        SharedPreferences googleBug = getActivity().getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getActivity().getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }

        initGoogleMap(savedInstanceState);
        final SeekBar seekBar = (SeekBar) root.findViewById(R.id.seekBarZoom);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != 0) {
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(progress/5f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress((int) (mMap.getCameraPosition().zoom*5));
            }
        });

        root.findViewById(R.id.buttonCanPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLocationSelect();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_CODE){
            AddressData addressData = (AddressData) data.getParcelableExtra(Constants.ADDRESS_INTENT);
            double lat = addressData.getLatitude();
            double lon = addressData.getLongitude();
            LatLng loc = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(loc).title("Meeting Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    private void onClickLocationSelect() {
        try {
            getLocation();
            Intent intent = new PlacePicker.IntentBuilder().onlyCoordinates(true)
                    .setLatLong(latitude, longitude)
                    .setMarkerDrawable(R.drawable.find)
                    .setFabColor(R.color.colorSecondary)
                    .showLatLong(true)
                    .build(this.getActivity());
            startActivityForResult(intent, PLACE_PICKER_CODE);
        } catch (Exception ignored) {
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = lat;
                longitude = longi;
            }
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
                    googleMap.setBuildingsEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Toast.makeText(getActivity(), "Map Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addSingleMarkers(){
        for (SingleMeeting sm : viewModel.getSingleMeetings().getValue()) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(sm.getSport());
            markerOptions.position(new LatLng(sm.getLat(),sm.getLon()));
            mMap.addMarker(markerOptions);
        }
    }


    @Override
    public void onGroupsLoaded() {
        addSingleMarkers();
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

}
