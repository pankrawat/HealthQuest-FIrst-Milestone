package com.tupelo.wellness.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;

public class MymoSerialFragment extends Fragment {

    EditText et_mymo_serial;
    String mymo_serial;
    SharedPreference sharedPreference;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tutorial_3, container, false);

        sharedPreference = SharedPreference.getInstance(getActivity());
        //btnPlayStore = (ImageButton) findViewById(R.id.btnPlayStore);
        //
        et_mymo_serial = (EditText) v.findViewById(R.id.et_mymo_serial);

        String devType = sharedPreference.getString("devType", "");
        if(devType.equalsIgnoreCase("101")) {
            mymo_serial = sharedPreference.getString("devices", "");
            Helper helper = new Helper();
            mymo_serial = String.valueOf(helper.HMM_serialDecodePM(Long.parseLong(mymo_serial)));
            et_mymo_serial.setText(mymo_serial);
        }

        sharedPreference.putString("mymo_serial_change",mymo_serial);

        et_mymo_serial.addTextChangedListener(new TextWatcher() {



            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text

                sharedPreference.putString("mymo_serial_change",cs+"");



            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });



        return v;
    }



/*
    public void goToNext(View view) {


        if (et_mymo_serial.getText().toString().trim().length() == 10) {
            mymo_serial = et_mymo_serial.getText().toString().trim().toString();
            if (CheckConnection.isConnection(getActivity()))
                new AddDeviceAsync(getActivity()).execute(mymo_serial);
            else {
                Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        } else if(et_mymo_serial.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.enter_serial), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.enter_valid_serial), Toast.LENGTH_SHORT).show();
        }


    }*/
    

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
