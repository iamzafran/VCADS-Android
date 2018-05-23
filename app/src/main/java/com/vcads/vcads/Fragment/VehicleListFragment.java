package com.vcads.vcads.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vcads.vcads.AddVehicleActivity;
import com.vcads.vcads.Fragment.DialogFragment.DeleteVehicleDialogFragment;
import com.vcads.vcads.Fragment.DialogFragment.VehicleKeyDialogFragment;
import com.vcads.vcads.LogInActivity;
import com.vcads.vcads.Model.Vehicle;
import com.vcads.vcads.Model.VehicleMake;
import com.vcads.vcads.Model.VehicleModel;
import com.vcads.vcads.Model.VehicleType;
import com.vcads.vcads.R;
import com.vcads.vcads.SharedPreferences.VCADSSHaredPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Zaly on 3/6/2018.
 */

public class VehicleListFragment extends Fragment {



    private Button mAddVehicleButton;
    private VehicleListAdapter mAdapter;
    private RecyclerView mVehicleListRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private static final String TAG = VehicleListFragment.class.getSimpleName();
    private static final int REQUEST_VEHICLE_CONFIRMATION = 0;
    private static final int REQUEST_VEHICLE_DELETE_CONFIRMATION = 1;
    private static final String VEHICLE_KEY_DIALOG = "VEHICLE_KEY_DIALOG";
    private String mCurrentActiveVehicle;


    private List<Vehicle> mVehicleList = new ArrayList<>();
    private HashMap<String, Vehicle> mVehicleHashMap = new HashMap<>();

    public static VehicleListFragment newInstance() {
        VehicleListFragment fragment = new VehicleListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mCurrentActiveVehicle = VCADSSHaredPreferences.getLicensePlate(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vehicle_list, container, false);

        mAddVehicleButton = v.findViewById(R.id.vehicle_list_add_vehicle_button);

        mAddVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVehicle();
            }
        });
        mVehicleListRecyclerView = v.findViewById(R.id.vehicle_list_recycler_view);
        mVehicleListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();

        if(mUser==null){
            loginactivity();
        }

        mVehicleList.clear();
        new GetUserVehicleTask().execute();

    }

    @Override
    public void onStop() {
        super.onStop();
        mVehicleList.clear();

    }

    private void loginactivity(){
        Intent i = LogInActivity.newIntent(getActivity());
        startActivity(i);
        getActivity().finish();
    }


    private void addVehicle(){
        Intent i = AddVehicleActivity.newIntent(getActivity());
        startActivity(i);
    }

    private void updateUI()
    {
        mAdapter = new VehicleListAdapter(mVehicleList);
        mVehicleListRecyclerView.setAdapter(mAdapter);
    }



    private class GetUserVehicleTask extends AsyncTask<Void,Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();
            String request_url = "https://afternoon-reaches-20046.herokuapp.com/vehicle/api/getuservehicle/"+mUser.getUid();
            Request request = new Request.Builder().url(request_url).build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                {

                    String responseBody = response.body().string();
                    Log.v(TAG,responseBody);

                    try {
                        populateList(responseBody);
                    }catch (JSONException e) {
                        Log.v(TAG,e.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
        }

        private void populateList(String response) throws JSONException {
            JSONArray vehicleArray = new JSONArray(response);

            for(int i=0; i<vehicleArray.length(); i++){
                JSONObject vehicleObject = vehicleArray.getJSONObject(i);
                int vehicleId =  vehicleObject.getInt("id");
                String license_plate = vehicleObject.getString("license_plate");
                String key = vehicleObject.getString("key");
                JSONObject modelObject = vehicleObject.getJSONObject("vehicle_model");
                int modelId = modelObject.getInt("id");
                String vehicle_model = modelObject.getString("vehicle_model");

                JSONObject typeObject = modelObject.getJSONObject("vehicle_type");
                int typeId = typeObject.getInt("id");
                String vehicle_type = typeObject.getString("vehicle_type");
                VehicleType type = new VehicleType(typeId, vehicle_type);

                JSONObject makeObject = modelObject.getJSONObject("vehicle_make");
                int makeId = makeObject.getInt("id");
                String vehicle_make = makeObject.getString("vehicle_make");
                VehicleMake make = new VehicleMake(makeId, vehicle_make);

                VehicleModel model = new VehicleModel(modelId, vehicle_model, type, make);

                Vehicle vehicle = new Vehicle(vehicleId, model, license_plate, key);

                mVehicleList.add(vehicle);
                mVehicleHashMap.put(vehicle.getLicensePlate(),vehicle);

            }
        }
    }







    private class VehicleListAdapter extends RecyclerView.Adapter<VehicleListViewHolder>{

        private List<Vehicle> mVehicleList = new ArrayList<>();
        public VehicleListAdapter(List<Vehicle> trackList)
        {
            mVehicleList = trackList;
        }
        @Override
        public VehicleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View v = inflater.inflate(R.layout.list_item_vehicle,parent,false);

            return new VehicleListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(VehicleListViewHolder holder, int position) {

            holder.bindVehicleList(mVehicleList.get(position));
        }

        @Override
        public int getItemCount() {
            return mVehicleList.size();
        }
    }


    private class VehicleListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView mVehicleModelTextView;
        private TextView mLicensePlateTextView;
        private Button mActivateButton;

        private Vehicle mVehicle;

        public VehicleListViewHolder(View itemView) {
            super(itemView);

            mVehicleModelTextView = (TextView) itemView.findViewById(R.id.vehicle_list_item_vehicle_info_text_view);
            mLicensePlateTextView = (TextView) itemView.findViewById(R.id.vehicle_list_item_license_plate_text_view);
            mActivateButton = (Button) itemView.findViewById(R.id.vehicle_list_item_activate_button);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void bindVehicleList(Vehicle vehicle)
        {

            mVehicle = vehicle;

            String text = mVehicle.getVehicleModel().getMake().getMake()+", "+mVehicle.getVehicleModel().getModel();

            mVehicleModelTextView.setText(text);
            mLicensePlateTextView.setText(mVehicle.getLicensePlate());

            Log.v(TAG, "Is active"+mVehicle.isActive()+"");

            mActivateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getFragmentManager();

                    Log.v(TAG, mVehicle.isActive()+"is active");
                    if(!mVehicle.isActive()){
                        VehicleKeyDialogFragment dialog = VehicleKeyDialogFragment.newInstance(mVehicle);
                        dialog.setTargetFragment(VehicleListFragment.this, REQUEST_VEHICLE_CONFIRMATION);
                        dialog.show(fm, VEHICLE_KEY_DIALOG);

                    }else if(mVehicle.isActive()){
                        VCADSSHaredPreferences.setPrefLicensePlate(getActivity(), null);
                        VCADSSHaredPreferences.setVehicleIPAddress(getActivity(), null);

                        mVehicle.setActive(false);
                        for(Vehicle v: mVehicleList){
                            if(v.getLicensePlate().equals(mVehicle.getLicensePlate())){
                                v.setActive(false);
                            }
                        }
                        mActivateButton.setText(R.string.deactivate);
                        mVehicleListRecyclerView.getAdapter().notifyDataSetChanged();

                    }

                }
            });

            if(mCurrentActiveVehicle!=null){

                if(mCurrentActiveVehicle.equals(mVehicle.getLicensePlate())){
                    mVehicle.setActive(true);
                    Log.v(TAG, "EQUALS CURRENT");
                    mActivateButton.setText(R.string.deactivate);
                }

            }

            if(mVehicle.isActive()){
                mActivateButton.setText(R.string.deactivate);
            }


        }

        @Override
        public void onClick(View v) {


        }

        @Override
        public boolean onLongClick(View view) {
            Log.v(TAG, mVehicle.getId()+"");
            FragmentManager fm = getFragmentManager();


            DeleteVehicleDialogFragment dialog = DeleteVehicleDialogFragment.newInstance(mVehicle);
            dialog.setTargetFragment(VehicleListFragment.this, REQUEST_VEHICLE_DELETE_CONFIRMATION);
            dialog.show(fm, VEHICLE_KEY_DIALOG);


            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!= Activity.RESULT_OK){
            return;
        }

        if(requestCode==REQUEST_VEHICLE_CONFIRMATION){
            boolean confirmed = data.getBooleanExtra(VehicleKeyDialogFragment.EXTRA_CONFIRMATION, false);
            final String license_plate = data.getStringExtra(VehicleKeyDialogFragment.EXTRA_LICENSE_PLATE);
            final String key = data.getStringExtra(VehicleKeyDialogFragment.EXTRA_KEY);
            Log.v(TAG, confirmed+"");

            if(confirmed){
                verifyVehicle(license_plate, key);

            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Vehicle Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }else if(requestCode==REQUEST_VEHICLE_DELETE_CONFIRMATION){
            int vehicle_id = data.getIntExtra(DeleteVehicleDialogFragment.EXTRA_VEHICLE_ID,-1);
            deleteVehicle(vehicle_id);
            Log.v(TAG, "Delete vehicle id : "+vehicle_id );
        }
    }

    private void setIsActive(Vehicle vehicle)
    {

        vehicle.setActive(true);
        VCADSSHaredPreferences.setPrefLicensePlate(getActivity(),vehicle.getLicensePlate().toUpperCase());
        Vehicle currentVehicle = mVehicleHashMap.get(mCurrentActiveVehicle);
        if(currentVehicle!=null){
            currentVehicle.setActive(false);
        }
        mCurrentActiveVehicle = vehicle.getLicensePlate();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVehicleListRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }


    private void deleteVehicle(final int vehicle_id)
    {
        OkHttpClient client = new OkHttpClient();
        String request_url = "https://afternoon-reaches-20046.herokuapp.com/vehicle/api/deleteuservehicle/";

        RequestBody formBody = new FormBody.Builder()
                .add("vehicle_id", vehicle_id+"")
                .build();
        Request request = new Request.Builder().url(request_url).post(formBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.v(TAG, "Succesfully deleted");

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful())
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Succesfully deleted", Toast.LENGTH_SHORT).show();

                            for(Vehicle v : mVehicleList){
                                if(v.getId()==vehicle_id){
                                    mVehicleList.remove(v);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }

            }
        });
    }

    private void verifyVehicle(final String license_plate, final String key){
       DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("vehicle_ip").child(license_plate.toUpperCase()).child("ip");

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ip = dataSnapshot.getValue(String.class);

                if(ip==null){
                    Log.v(TAG, "Authentication Failed");

                }else {
                    Log.v(TAG, ip);
                    verifyConnection(ip, license_plate, key);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void verifyConnection(final String ip, final String license_plate, String key){
        OkHttpClient client = new OkHttpClient();
        Log.v(TAG, "VERIFY CONNECTION");
        final RequestBody requestBody = new FormBody.Builder().add("key", key).build();
        Request request = new Request.Builder().url("http://"+ip+":8080").post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful())
                {
                    VCADSSHaredPreferences.setVehicleIPAddress(getActivity(), ip);
                    String json = response.body().string();
                    Log.v(TAG, json);
                    try {
                        JSONObject verifyObj = new JSONObject(json);
                        String license = verifyObj.getString("license_plate");

                        if(license.equals(license_plate)){
                            Vehicle vehicle = mVehicleHashMap.get(license_plate);
                                getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), license_plate+" Succesfully Authenticated!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            setIsActive(vehicle);

                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), license_plate+" Authentication Failed", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(getActivity(), "Vehicle authentication failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



}
