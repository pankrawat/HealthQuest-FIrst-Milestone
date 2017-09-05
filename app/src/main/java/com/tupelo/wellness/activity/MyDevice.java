package com.tupelo.wellness.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;
import com.tupelo.wellness.SHealthIntegration.SetUpSHealth;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;

import java.util.Map;

//import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 09-09-2015.
 */
public class MyDevice extends Fragment {

    Activity activity;
    String selectedDevice;
    SetUpSHealth sHealth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_my_device, container, false);

        ImageView image = (ImageView)v.findViewById(R.id.forwardbutton);
        Drawable myDrawable = image.getDrawable();
        myDrawable.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);
        image.setImageDrawable(myDrawable);


        image.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setTitle("Switch to new device");
                alertDialogBuilder.setMessage("Warning! If you proceed, today's steps will be overwritten with the steps from the new device.");

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(getActivity(),ChooseTracker.class);
                        intent.putExtra("showBackButton",true);
                        startActivity(intent);

                    }
                });

                alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                // create alert dialog

                return false;
            }
        });


        int[] mipmap = {R.mipmap.mymo_icon, R.mipmap.fitbit_icon, R.mipmap.google_icon, R.mipmap.jawbone_icon,R.mipmap.garmin_icon,R.mipmap.s_health_icon};


        SharedPreferences prefs = getActivity().getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        selectedDevice = prefs.getString("selectedDevice", "");

        TextView mymoSerial = (TextView) v.findViewById(R.id.mymoserial);
        ImageView img = (ImageView) v.findViewById(R.id.device);

        SharedPreference sharedPreference = SharedPreference.getInstance(getActivity());

        TextView selectedvalue = (TextView) v.findViewById(R.id.selectedvalue);
        selectedvalue.setText(selectedDevice.toUpperCase());

        switch (selectedDevice){

            case "mymo":
                img.setImageResource(mipmap[0]);
                mymoSerial.setVisibility(View.VISIBLE);


                String devType = sharedPreference.getString("devType", "");
                if(devType.equalsIgnoreCase("101")) {
                    String mymo_serial = sharedPreference.getString("devices", "");
                    Helper helper = new Helper();
                    mymo_serial = String.valueOf(helper.HMM_serialDecodePM(Long.parseLong(mymo_serial)));
                    mymoSerial.setText(mymo_serial);
                }


                break;
            case "fitbit":
                img.setImageResource(mipmap[1]);
                break;
            case "Google Fit":
                img.setImageResource(mipmap[2]);
                break;
            case "jawbone":
                img.setImageResource(mipmap[3]);
                break;
            case "Garmin":
                img.setImageResource(mipmap[4]);
                break;
            case "S Health":
                img.setImageResource(mipmap[5]);
                break;
        }


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((TabActivity)getActivity()).menu=menu;
        if (selectedDevice.equals("S Health")) {
            if (menu.hasVisibleItems())
                menu.clear();
            inflater.inflate(R.menu.menu_shealth, menu);
        }
        else{
            if (menu.hasVisibleItems())
                menu.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == (R.id.action_menu_shealth)) {
            sHealth = SetUpSHealth.getInstance();
            HealthPermissionManager pmsManager = new HealthPermissionManager(sHealth.mStore);
            try {
                pmsManager.requestPermissions(sHealth.mKeySet, activity).setResultListener(mPermissionListener);
            } catch (Exception e) {

            }
        }
        return true;
    }

    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                @Override
                public void onResult(HealthPermissionManager.PermissionResult result) {
                    Log.d(Constants.SHEALTH_TAG, "Permission callback is received.");
                    Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        showPermissionAlarmDialog();
                    }
                }
            };

    private void showPermissionAlarmDialog() {
        if (activity.isFinishing()) {
            return;
        }

        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("Notice");
        alert.setMessage("All permissions should be acquired");
        alert.setPositiveButton("OK", null);
        alert.show();
    }
}


