package com.vcads.vcads;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vcads.vcads.Fragment.RegisterFragment;

public class RegisterActivity extends AppCompatActivity {

    public static Intent newIntent(Context context){
        Intent i = new Intent(context, RegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.register_fragment_container);

        if(fragment==null){
            fragment = new RegisterFragment();

            fm.beginTransaction().add(R.id.register_fragment_container, fragment)
                    .commit();

        }
    }
}
