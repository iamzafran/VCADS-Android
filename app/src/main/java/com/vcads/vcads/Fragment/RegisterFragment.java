package com.vcads.vcads.Fragment;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vcads.vcads.LogInActivity;
import com.vcads.vcads.R;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Zaly on 3/4/2018.
 */

public class RegisterFragment extends Fragment {

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private String email;
    private String password;
    private String confirmPassword;
    private String URL = "https://afternoon-reaches-20046.herokuapp.com/";

    private static final String TAG = RegisterFragment.class.getName();


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_layout, container, false);



        mEmailEditText = v.findViewById(R.id.register_email_edit_text);
        mPasswordEditText = v.findViewById(R.id.register_password_edit_text);
        mConfirmPasswordEditText = v.findViewById(R.id.register_password_confirm_edit_text);
        mRegisterButton = v.findViewById(R.id.register_button);

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

        mConfirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPassword = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInput()){
                    registerUser();
                }
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
    }

    private void registerUser() {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mUser = mAuth.getCurrentUser();
                    addUserToDatabase();
                }else{
                    Toast.makeText(getActivity(), "Registration Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean checkInput(){

        if(email==null){
            Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(confirmPassword)){
            Toast.makeText(getActivity(), "Passwords not equal", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addUserToDatabase(){
        OkHttpClient client = new OkHttpClient();

        String addUserUrl = URL + "user/api/adduser";

        RequestBody requestBody = new FormBody.Builder()
                .add("uuid", mUser.getUid())
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
                        if(responseStr.equals("Ok")){
                            Intent i = LogInActivity.newIntent(getActivity());
                            startActivity(i);
                            getActivity().finish();
                        }

                    }
                } catch (IOException e) {
                    Log.e(TAG,"Exception caught ",e);
                }
            }
        });

    }

}

