package com.vcads.vcads.Fragment;


import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vcads.vcads.Model.Alert;
import com.vcads.vcads.R;
import com.vcads.vcads.SharedPreferences.VCADSSHaredPreferences;

/**
 * Created by Zaly on 3/20/2018.
 */

public class AlertMapFragment extends SupportMapFragment {

    private GoogleMap mMap;
    private Alert mAlert;
    private DatabaseReference mMyLocationReference;
    private DatabaseReference mAlertReference;
    private String mLicensePlate;
    private LatLng myPoint;
    private LatLng mAlertPoint;
    private int margin;
    private static final String ALERT_ARGS = "com.vcads.vcads.AlertMapFragment.ALERT_ARGS";


    public static AlertMapFragment newInstance(Alert alert){

        AlertMapFragment fragment = new AlertMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ALERT_ARGS, alert);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mAlert = (Alert) getArguments().getSerializable(ALERT_ARGS);
        mLicensePlate = VCADSSHaredPreferences.getLicensePlate(getActivity());
        margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                getLocationFromFirebase();
            }
        });
    }

    private void getLocationFromFirebase(){
        mMyLocationReference = FirebaseDatabase.getInstance().getReference("vehicle_location").child(mLicensePlate).child("l");
        mAlertReference = FirebaseDatabase.getInstance().getReference("warning_location").child(mAlert.getAlertId()).child("l");


        mMyLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double latitude = (double) dataSnapshot.child("0").getValue();
                double longitude = (double) dataSnapshot.child("1").getValue();

                updateUI(latitude, longitude);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void updateUI(double latitude, double longitude){

        mAlertPoint = new LatLng(mAlert.getLatitude(), mAlert.getLongitude());
        myPoint = new LatLng(latitude, longitude);

        MarkerOptions myPointMarker = new MarkerOptions().position(myPoint);

        MarkerOptions mAlertMarker = new MarkerOptions().position(mAlertPoint);

        mMap.clear();



        mMap.addMarker(myPointMarker);
        mMap.addMarker(mAlertMarker);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(mAlertPoint)
                .build();


        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.moveCamera(update);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
