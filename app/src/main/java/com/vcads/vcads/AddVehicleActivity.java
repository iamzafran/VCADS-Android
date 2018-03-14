package com.vcads.vcads;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vcads.vcads.Fragment.AddVehicleFragment;

public class AddVehicleActivity extends AppCompatActivity {

    public static Intent newIntent(Context context){
        Intent i = new Intent(context, AddVehicleActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.add_vehicle_fragment_container);

        if(fragment==null){
            fragment = AddVehicleFragment.newInstance();

            fm.beginTransaction().add(R.id.add_vehicle_fragment_container,fragment)
                    .commit();
        }
    }
}
