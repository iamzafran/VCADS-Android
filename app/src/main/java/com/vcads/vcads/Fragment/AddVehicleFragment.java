package com.vcads.vcads.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vcads.vcads.LogInActivity;
import com.vcads.vcads.Model.VehicleModel;
import com.vcads.vcads.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

public class AddVehicleFragment extends Fragment {

    private EditText mLicensePlateEditText;
    private AutoCompleteTextView mVehicleModelAutoComplete;
    private EditText mVehicleKeyEditText;
    private EditText mConfirmVehicleKeyEditText;
    private Button mAddVehicleButton;
    private static final String TAG = AddVehicleFragment.class.getCanonicalName();
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private HashMap<String, VehicleModel> vehicleModelMap = new HashMap<>();

    private String mLicensePlate;
    private String mVehicleKey;
    private String mModel;
    private String mConfirmKey;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    private String URL = "https://afternoon-reaches-20046.herokuapp.com/";



    public static AddVehicleFragment newInstance(){
        return new AddVehicleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_vehicle, container, false);

        mLicensePlateEditText = v.findViewById(R.id.add_vehicle_license_plate_edit_text);
        mVehicleModelAutoComplete = v.findViewById(R.id.add_vehicle_model_auto_complete);
        mVehicleKeyEditText = v.findViewById(R.id.add_vehicle_vehicle_key_edit_text);
        mConfirmVehicleKeyEditText = v.findViewById(R.id.add_vehicle_confirm_vehicle_key_edit_text);
        mAddVehicleButton = v.findViewById(R.id.add_vehicle_add_button);



        mAutoCompleteAdapter = new AutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);

        mVehicleModelAutoComplete.setAdapter(mAutoCompleteAdapter);

        mLicensePlateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLicensePlate = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mVehicleKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mVehicleKey = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mConfirmVehicleKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mConfirmKey = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAddVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    addVehicle();
                }
            }
        });

        mVehicleModelAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mModel = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
         mUser = mAuth.getCurrentUser();

        if(mUser==null){
            loginactivity();
        }
    }

    private void loginactivity(){
        Intent i = LogInActivity.newIntent(getActivity());
        startActivity(i);
        getActivity().finish();
    }

    private boolean validateForm(){

        if(mLicensePlate.isEmpty()|| mVehicleKey.isEmpty() || mModel.isEmpty() || mConfirmKey.isEmpty()){
            Toast.makeText(getActivity(), "Incomplete form", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!mVehicleKey.equals(mConfirmKey)){
            Toast.makeText(getActivity(), "Key does not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addVehicle(){
        OkHttpClient client = new OkHttpClient();

        String addUserUrl = URL + "vehicle/api/adduservehicle/";

        VehicleModel model = vehicleModelMap.get(mModel);
        int modelId = model.getModelId();

        RequestBody requestBody = new FormBody.Builder()
                .add("uuid", mUser.getUid())
                .add("license_plate", mLicensePlate)
                .add("model",modelId+"")
                .add("key", mConfirmKey)
                .build();
        Request request =  new Request.Builder().url(addUserUrl).post(requestBody).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if(response.isSuccessful())
                    {
                        String responseStr = response.body().string();

                        Log.v(TAG,"OK");
                        Log.v(TAG,responseStr);
                        if(responseStr.equals("Vehicle added to user")){
                            getActivity().finish();
                        }

                    }
                } catch (IOException e) {
                    Log.e(TAG,"Exception caught ",e);
                }
            }
        });

    }

    public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

        ArrayList<String> vehicleModels;

        public AutoCompleteAdapter(Context context, int textViewResourceId){
            super(context, textViewResourceId);
            vehicleModels = new ArrayList<>();


        }

        @Override
        public int getCount(){
            return vehicleModels.size();
        }

        @Override
        public String getItem(int index){
            return vehicleModels.get(index);
        }


        @Override
        public Filter getFilter(){

            Filter myFilter = new Filter(){

                @Override
                protected FilterResults performFiltering(CharSequence constraint){
                    FilterResults filterResults = new FilterResults();
                    if(constraint != null) {
                        // A class that queries a web API, parses the data and returns an ArrayList<Style>
//
                        try {

                            vehicleModels = new GetVehicleModels().execute(new String[]{constraint.toString()}).get();
                        }
                        catch(Exception e) {
//                        Log.e("myException", e.getMessage());
                        }
                        // Now assign the values and count to the FilterResults object
                        filterResults.values = vehicleModels;
                        filterResults.count = vehicleModels.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if(results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }

            };

            return myFilter;

        }


        private class GetVehicleModels extends AsyncTask<String, Void, ArrayList<String>> {

            ArrayList<String> modelNames = new ArrayList<>();

            @Override
            protected ArrayList<String> doInBackground(String... params) {

                Log.v(TAG, params[0]);
                String query = params[0];
                OkHttpClient client = new OkHttpClient();
                String request_url = "https://afternoon-reaches-20046.herokuapp.com/vehicle/api/model/autocomplete/"+query;
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


                return modelNames;
            }

            @Override
            protected void onPostExecute(ArrayList<String> result) {

            }

            private void populateList(String responseBody) throws JSONException{

                JSONArray modelsArray = new JSONArray(responseBody);

                for(int i=0; i<modelsArray.length(); i++){
                    JSONObject vehicleModelObject  = modelsArray.getJSONObject(i);
                    int model_id = vehicleModelObject.getInt("id");
                    String model = vehicleModelObject.getString("vehicle_model");


                    VehicleModel vm = new VehicleModel(model_id,model);

                    modelNames.add(vm.getModel());
                    vehicleModelMap.put(vm.getModel(), vm);
                }

            }

        }



    }
}
