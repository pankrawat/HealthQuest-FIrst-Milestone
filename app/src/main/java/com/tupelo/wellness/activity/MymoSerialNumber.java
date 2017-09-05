package com.tupelo.wellness.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.tupelo.wellness.R;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;

public class MymoSerialNumber extends AppCompatActivity {

    EditText et_mymo_serial;
    String mymo_serial;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_3);


        sharedPreference = SharedPreference.getInstance(MymoSerialNumber.this);
        //btnPlayStore = (ImageButton) findViewById(R.id.btnPlayStore);
        //
        et_mymo_serial = (EditText) this.findViewById(R.id.et_mymo_serial);

        String devType = sharedPreference.getString("devType", "");
        if(devType.equalsIgnoreCase("101")) {
            mymo_serial = sharedPreference.getString("devices", "");
            Helper helper = new Helper();
            mymo_serial = String.valueOf(helper.HMM_serialDecodePM(Long.parseLong(mymo_serial)));
            et_mymo_serial.setText(mymo_serial);
        }




    }


    public void showNext() {
        //getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE).edit().putString(Constants.MYMO_SERIAL, mymo_serial).commit();

        DbAdapter dbAdapter = DbAdapter.getInstance(MymoSerialNumber.this);
        ContentValues contentValues = new ContentValues();
        contentValues.put("login", "true");
        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_MYMO);
        dbAdapter.insertQuery(DbAdapter.TABLE_NAME_MYMO, contentValues);


        Intent intent = new Intent(MymoSerialNumber.this, TabActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_MYMO);
        startActivity(intent);
        finish();

    }

/*    public void goToNext(View view) {


        if (et_mymo_serial.getText().toString().trim().length() == 10) {
            mymo_serial = et_mymo_serial.getText().toString().trim().toString();
            if (CheckConnection.isConnection(this))
                new AddDeviceAsync(this).execute(mymo_serial);
            else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        } else if(et_mymo_serial.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.enter_serial), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.enter_valid_serial), Toast.LENGTH_SHORT).show();
        }


    }*/
}
