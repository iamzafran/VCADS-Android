package com.vcads.vcads.Fragment.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vcads.vcads.Model.Vehicle;
import com.vcads.vcads.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Zaly on 4/24/2018.
 */

public class DeleteVehicleDialogFragment extends DialogFragment {

    private static final String VEHICLE_ARGS = DeleteVehicleDialogFragment.class.getSimpleName()+"VEHICLE_ARGS";
    public static final String EXTRA_VEHICLE_ID = DeleteVehicleDialogFragment.class.getSimpleName()+"EXTRA_VEHICLE_KEY";

    private TextView mDeleteVehicleTextView;
    private Vehicle mVehicle;

    public static DeleteVehicleDialogFragment newInstance(Vehicle v)
    {
        Bundle args = new Bundle();
        args.putSerializable(VEHICLE_ARGS, v);
        DeleteVehicleDialogFragment vehicleDeleteDialogFragment = new DeleteVehicleDialogFragment();
        vehicleDeleteDialogFragment.setArguments(args);
        return vehicleDeleteDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mVehicle = (Vehicle) getArguments().getSerializable(VEHICLE_ARGS);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_vehicle_fragment,null);

        String text = getString(R.string.confirm_delete)+" "+mVehicle.getLicensePlate();


        mDeleteVehicleTextView = v.findViewById(R.id.dialog_vehicle_delete_text_view);
        mDeleteVehicleTextView.setText(text);


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Delete Confirmation")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,mVehicle.getId());

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
    }

    private void sendResult(int resultCode,int vehicle_id)
    {
        if(getTargetFragment()==null)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_VEHICLE_ID, vehicle_id);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


}
