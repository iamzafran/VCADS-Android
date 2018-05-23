package com.vcads.vcads.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.vcads.vcads.R;
import com.vcads.vcads.SharedPreferences.VCADSSHaredPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Zaly on 4/24/2018.
 */

public class CollisionWarningFragment extends Fragment {

    private static final String TAG = CollisionWarningFragment.class.getSimpleName();
    private Socket mSocket;
    private ImageView mArrowUpImageView;
    private ImageView mArrowUpRightImageView;
    private ImageView mArrowRightImageView;
    private ImageView mArrowDownRightImageView;
    private ImageView mArrowDownImageView;
    private ImageView mArrowDownLeftImageView;
    private ImageView mArrowLeftImageView;
    private ImageView mArrowUpLeftImageView;


    private Emitter.Listener onCollisionWarning = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject collisionObj = (JSONObject) args[0];
            Log.v(TAG, "P = "+collisionObj.toString());
            try {
                double p = collisionObj.getDouble("likelihood");
                final int aoa = collisionObj.getInt("angle_of_attack");
                if(p>0.5){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetDrawable();
                            alertDriver(aoa);
                        }
                    });

                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetDrawable();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    public static CollisionWarningFragment newInstance(){
        return new CollisionWarningFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectSocket();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_collision_warning_layout, container, false);
        mArrowUpImageView = v.findViewById(R.id.fragment_collision_arrow_up);
        mArrowUpRightImageView = v.findViewById(R.id.fragment_collision_arrow_up_right);
        mArrowRightImageView = v.findViewById(R.id.fragment_collision_arrow_right);
        mArrowDownRightImageView = v.findViewById(R.id.fragment_collision_arrow_down_right);
        mArrowDownImageView = v.findViewById(R.id.fragment_collision_arrow_down);
        mArrowDownLeftImageView = v.findViewById(R.id.fragment_collision_arrow_down_left);
        mArrowLeftImageView = v.findViewById(R.id.fragment_collision_arrow_left);
        mArrowUpLeftImageView = v.findViewById(R.id.fragment_collision_arrow_up_left);
        return v;

    }

    private void connectSocket(){
        String ipAddress = VCADSSHaredPreferences.getVehicleIPAddress(getActivity());
        if(ipAddress!=null){
            try {
                mSocket = IO.socket("http://"+ipAddress+":8080");
                mSocket.on("collision", onCollisionWarning);
                mSocket.connect();
            } catch (URISyntaxException e) {
                Log.e(TAG, "URI Syntax Exception ", e);
            }
        }else {
            Toast.makeText(getActivity(), "Vehicle Not Connected", Toast.LENGTH_SHORT).show();
        }

    }

    private void alertDriver(int heading){


        if((heading >= 359) || (heading <= 10)){
            //rear collision
            Drawable drawable = getActivity().getDrawable(R.drawable.arrow_danger_down);
            mArrowDownImageView.setImageDrawable(drawable);
        }else if((heading >= 80) && (heading <=100)){
            //right collision
            Drawable drawable = getActivity().getDrawable(R.drawable.arrow_danger_right);
            mArrowRightImageView.setImageDrawable(drawable);
        }else if((heading>=170) && (heading<=190)){
            //front collision
            Drawable drawable = getActivity().getDrawable(R.drawable.arrow_danger_up);
            mArrowUpImageView.setImageDrawable(drawable);
        }else if((heading>=260)&&(heading<=280)){
            //left
            Drawable drawable = getActivity().getDrawable(R.drawable.arrow_danger_left);
            mArrowLeftImageView.setImageDrawable(drawable);
        }


    }

    private void resetDrawable(){

        Drawable d = getActivity().getDrawable(R.drawable.arrow_up);
        mArrowUpImageView.setImageDrawable(d);
        d = getActivity().getDrawable(R.drawable.arrow_up_right);
        mArrowUpRightImageView.setImageDrawable(d);
        d = getActivity().getDrawable(R.drawable.arrow_right);
        mArrowRightImageView.setImageDrawable(d);
        d = getActivity().getDrawable(R.drawable.arrow_down_right);
        mArrowDownRightImageView.setImageDrawable(d);
        d = getActivity().getDrawable(R.drawable.arrow_down);
        mArrowDownImageView.setImageDrawable(d);
        d = getActivity().getDrawable(R.drawable.arrow_down_left);
        mArrowDownLeftImageView.setImageDrawable(d);
        d = getActivity().getDrawable(R.drawable.arrow_left);
        mArrowLeftImageView.setImageDrawable(d);
        d = getActivity().getDrawable(R.drawable.arrow_up_left);
        mArrowUpLeftImageView.setImageDrawable(d);
    }





}
