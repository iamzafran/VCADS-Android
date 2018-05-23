package com.vcads.vcads;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vcads.vcads.Fragment.AlertMapFragment;
import com.vcads.vcads.Model.Alert;

public class AlertMapActivity extends AppCompatActivity {

    private static final String EXTRA_ALERT = "com.vcads.vcads.AlertMapActivity.EXTRA_ALERT";

    public static Intent newIntent(Context context, Alert alert){
        Intent i = new Intent(context, AlertMapActivity.class);
        i.putExtra(EXTRA_ALERT, alert);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_map);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.alert_map_fragment_container);

        Alert alert;
        alert = (Alert) getIntent().getSerializableExtra(EXTRA_ALERT);

        if(fragment==null){
            fragment = AlertMapFragment.newInstance(alert);

            fm.beginTransaction().add(R.id.alert_map_fragment_container, fragment).commit();
        }
    }
}
