package com.vcads.vcads.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vcads.vcads.MainActivity;
import com.vcads.vcads.R;
import com.vcads.vcads.RegisterActivity;

/**
 * Created by Zaly on 3/4/2018.
 */

public class LogInFragment extends Fragment {

    private TextView registerTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLogInButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String email;
    private String password;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_in_layout, container, false);

        registerTextView = v.findViewById(R.id.register_text_view);
        mEmailEditText = v.findViewById(R.id.log_in_email_edit_text);
        mPasswordEditText = v.findViewById(R.id.log_in_password_edit_text);
        mLogInButton = v.findViewById(R.id.log_in_button);

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent i = RegisterActivity.newIntent(getActivity());
              startActivity(i);
            }
        });

        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyInput()){

                    signInUser();
                }

            }
        });

        return v;
    }

    private boolean verifyInput() {

        if(email == null || password == null){
            Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();

        if(mUser!=null){
            Intent i = MainActivity.newIntent(getActivity());
            startActivity(i);
            getActivity().finish();
        }

    }

    private void signInUser(){

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mUser = mAuth.getCurrentUser();

                    Intent i = MainActivity.newIntent(getActivity());
                    startActivity(i);
                    getActivity().finish();

                }else{
                    Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
