package com.vcads.vcads.Fragment.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vcads.vcads.Model.Vehicle;
import com.vcads.vcads.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Zaly on 3/11/2018.
 */

public class VehicleKeyDialogFragment extends DialogFragment {

    private static final String VEHICLE_ARGS = "VEHICLE_ARGS";
    private Vehicle mVehicle;
    private EditText mVehicleKeyEditText;
    private String mKey;
    private DatabaseReference mDatabaseReference;
    private final static String TAG = VehicleKeyDialogFragment.class.getSimpleName();

    public static final String EXTRA_CONFIRMATION = "com.android.dishpatch.dishpatch.boolean";
    public static final String EXTRA_LICENSE_PLATE = "com.android.dishpatch.dishpatch.license_plate";

    public static  VehicleKeyDialogFragment newInstance(Vehicle v)
    {
        Bundle args = new Bundle();
        args.putSerializable(VEHICLE_ARGS, v);
        VehicleKeyDialogFragment vehicleKeyDialogFragment = new VehicleKeyDialogFragment();
        vehicleKeyDialogFragment.setArguments(args);
        return vehicleKeyDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mVehicle = (Vehicle) getArguments().getSerializable(VEHICLE_ARGS);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_vehicle_key_layout,null);

        mVehicleKeyEditText = v.findViewById(R.id.vehicle_dialog_vehicle_key_edit_text);

        mVehicleKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mKey = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Enter Vehicle Key")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mKey.equals(mVehicle.getKey())){
                            verifyVehicle();
                        }else {
                            sendResult(Activity.RESULT_OK,false);

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,false);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode,Boolean isConfirmed)
    {
        if(getTargetFragment()==null)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONFIRMATION,isConfirmed);
        intent.putExtra(EXTRA_LICENSE_PLATE, mVehicle.getLicensePlate());
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    private void verifyVehicle(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("vehicle_ip").child(mVehicle.getLicensePlate().toUpperCase()).child("ip");

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ip = dataSnapshot.getValue(String.class);

                if(ip==null){
                    Log.v(TAG, "Authentication Failed");
                    sendResult(Activity.RESULT_OK,false);

                }else {
                    Log.v(TAG, ip);
                    verifyConnection(ip);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void verifyConnection(String ip){
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder().url("http://"+ip+":8080").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful())
                {
                    String json = response.body().string();
                    try {
                        JSONObject verifyObj = new JSONObject(json);
                        String license = verifyObj.getString("license_plate");

                        if(license.equals(mVehicle.getLicensePlate().toUpperCase())){
                            sendResult(Activity.RESULT_OK,true);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

}
