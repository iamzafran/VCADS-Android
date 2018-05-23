package com.vcads.vcads.Fragment;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.print.PageRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vcads.vcads.AlertMapActivity;
import com.vcads.vcads.Model.Alert;
import com.vcads.vcads.R;
import com.vcads.vcads.SharedPreferences.VCADSSHaredPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zaly on 3/14/2018.
 */

public class AlertFragment extends Fragment {

    public static AlertFragment newInsatance(){
        return new AlertFragment();
    }

    private RecyclerView mAlertListRecyclerView;
    private AlertListAdapter mAlertListAdapter;
    private List<Alert> mAlertList = new ArrayList<>();

    private DatabaseReference mMyLocationReference;
    private DatabaseReference mAlertReference;
    private String mLicensePlate;
    private GeoFire mGeoFire;
    private GeoLocation mGeoLocation;
    private GeoQuery mGeoQuery;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.0.187:8080");
        } catch (URISyntaxException e) {
            Log.e(TAG, "URI Syntax Exception ", e);
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, (String) args[0]);
        }
    };

    private Emitter.Listener onNewWarning = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject alertObject = (JSONObject) args[0];
            Log.v(TAG, alertObject.toString());
            try {
            double latitude = alertObject.getDouble("latitude");
            double longitude = alertObject.getDouble("longitude");
            double distance = alertObject.getDouble("distance");
            String key = null;
            Alert alert = new Alert("Accident", new GeoLocation(latitude, longitude), (float) distance, key);
            key = alertObject.getString("key");
            alert.setAlertId(key);

            for(Alert a: mAlertList){

                if(a.getAlertId().equals(key)){
                    return;
                }
            }
            mAlertList.add(0,alert);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mAlertListRecyclerView.getAdapter()!=null){
                        mAlertListRecyclerView.getAdapter().notifyDataSetChanged();
                    }

                }
            });
        }
    };

    private static final String TAG = AlertFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket.on("news", onConnect);
        mSocket.on("new_warning", onNewWarning);
        mSocket.connect();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alert, container, false);
        mAlertListRecyclerView = v.findViewById(R.id.alert_list_recycler_view);
        mLicensePlate = VCADSSHaredPreferences.getLicensePlate(getActivity());

        if(mLicensePlate!=null){
            Log.v(TAG, mLicensePlate);
            mAlertListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAlertListAdapter = new AlertListAdapter();
            mAlertListRecyclerView.setAdapter(mAlertListAdapter);
            mAlertListAdapter = new AlertListAdapter();
            mAlertListRecyclerView.setAdapter(mAlertListAdapter);

            //bindGeoFire();

        }else {

            Log.v(TAG, "NO LICENSE_PLATE");
        }





        return v;
    }

    private void bindGeoFire(){

        mMyLocationReference = FirebaseDatabase.getInstance().getReference("vehicle_location").child(mLicensePlate).child("l");
        mAlertReference = FirebaseDatabase.getInstance().getReference("warning_location");


        mMyLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){
                    return;
                }
                double latitude = (double) dataSnapshot.child("0").getValue();
                double longitude = (double) dataSnapshot.child("1").getValue();
                mGeoLocation = new GeoLocation(latitude, longitude);

                if(mGeoQuery==null){
                    mGeoFire = new GeoFire(mAlertReference);

                  mGeoQuery = mGeoFire.queryAtLocation(mGeoLocation, 1.0);

                    mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            float[] distance = new float[2];
                            Location.distanceBetween(mGeoQuery.getCenter().latitude, mGeoQuery.getCenter().longitude, location.latitude, location.longitude, distance);
                            Log.v(TAG, key+" distance: "+ distance[0]+"m");
                            Alert a = new Alert("Accident", location, distance[0], key);
                            mAlertList.add(0,a);
                            mAlertListRecyclerView.getAdapter().notifyItemChanged(0);
                        }

                        @Override
                        public void onKeyExited(String key) {
                            for(Alert a: mAlertList){

                                if(a.getAlertId().equals(key)){
                                    mAlertList.remove(a);
                                }

                            }
                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });

                }else {


                    mGeoQuery.setCenter(mGeoLocation);
                    updateLocation();
                    mAlertListRecyclerView.getAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private void updateLocation(){

                for(Alert a: mAlertList){
                    float[] distance = new float[2];
                    Location.distanceBetween(mGeoQuery.getCenter().latitude, mGeoQuery.getCenter().longitude, a.getLatitude(), a.getLongitude(), distance);
                    a.setDistance(distance[0]);
                }

            }
        });



    }

    private void getWarnings(){

    }


    private class AlertListAdapter extends RecyclerView.Adapter<AlertListViewHolder>{


        @Override
        public AlertListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View v = inflater.inflate(R.layout.list_item_alert,parent,false);

            return new AlertListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AlertListViewHolder holder, int position) {

            holder.bindVehicleList(mAlertList.get(position));
        }

        @Override
        public int getItemCount() {
            return mAlertList.size();
        }
    }


    private class AlertListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mAlertInfoTextView;
        private TextView mDistanceTextView;

        private Alert mAlert;

        public AlertListViewHolder(View itemView) {
            super(itemView);

            mAlertInfoTextView = (TextView) itemView.findViewById(R.id.alert_text_view);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.distance_and_heading_text_view);

            itemView.setOnClickListener(this);

        }

        public void bindVehicleList(Alert alert)
        {
            mAlert = alert;
            mAlertInfoTextView.setText("Warning! Accident Ahead");
            mDistanceTextView.setText(mAlert.getDistance()+"m");


        }

        @Override
        public void onClick(View v) {

            Intent i = AlertMapActivity.newIntent(getActivity(), mAlert);
            startActivity(i);

        }
    }


}
