package com.tupelo.wellness.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonArray;
import com.pubnub.api.Pubnub;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompanyCodeActivity extends AppCompatActivity implements View.OnClickListener {

    Activity activity;
    TextView tv_question_mark;
    Context context = CompanyCodeActivity.this;
    EditText et_company_code;
    private ProgressDialog pDialog;
    String sessid;
    static String TAG = "foooooooooooooo CompanyCodeActivity";
    String regId;
    private final Pubnub pubnub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_code);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(CompanyCodeActivity.this, R.color.colorPrimaryDark));
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        activity = this;
        tv_question_mark = (TextView) findViewById(R.id.tv_question_mark);
        et_company_code = (EditText) findViewById(R.id.et_company_code);
        tv_question_mark.setOnClickListener(onClickListener);
        //   List<NameValuePair> list = new ArrayList<>();
        //   list.add(new BasicNameValuePair("companycode", et_company_code.getText().toString()));
//        new IonCall(activity, "corpchallenge.getcompanyconfig", CompanyCodeActivity.this).setBodyParameter(list).start();

        Button downloadImages = (Button) this.findViewById(R.id.downloadImages);
        downloadImages.setOnClickListener(this);
        // sendNotification();
    }

    private void showDialog() {

//        openPopup = true;
//        setContentView(R.layout.popup);

        RelativeLayout promptView = (RelativeLayout) getLayoutInflater().inflate(R.layout.popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(promptView);
        final AlertDialog alertDialog = builder.create();
        Button ok = (Button) promptView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog();
        }
    };


    public void connectForSessId() {

        if (et_company_code.getText().toString().isEmpty()) {
          //  Toast.makeText(CompanyCodeActivity.this, "The field cannot be empty", Toast.LENGTH_LONG).show();
            customdialog("The field cannot be empty");
            return;
        }


        pDialog.show();
        String url = Constants.BASE_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        if (result != null && !result.isEmpty()) {
                            try {
                                JSONObject json = new JSONObject(result);
                                JSONObject jsonObject = json.getJSONObject("#data");
                                sessid = jsonObject.getString("sessid");
                                downloadCompanyTheme();
                                Log.d("jsondata", jsonObject.toString());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customdialog("Slow connection Please Try again later");

                        // Toast.makeText(CompanyCodeActivity.this, R.string.network_not_connected, Toast.LENGTH_LONG).show();
                        pDialog.hide();
                        Log.d("ERROR", "error => " + error.getMessage());
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("method", "system.connect");
                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);

    }


    private void downloadCompanyTheme() {
        String url = Constants.BASE_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        Log.d("Result.....", response);
                        try {
                            JSONObject companypref=new JSONObject(response).optJSONObject("#data").getJSONObject("companyPrefs");
                            SharedPreferences.Editor editor= getSharedPreferences("companyprefs",0).edit();
                            editor.putString("distance",companypref.getString("distance"));

                            JSONArray actdataShow=new JSONObject(response).optJSONObject("#data").getJSONArray("actdataShow");
                            ArraySet<String> arr=new ArraySet<>();

                            for(int i=0;i<actdataShow.length();i++){
                                JSONObject actdata= actdataShow.getJSONObject(i);
                                if(actdata.getInt("value")==1){
                                    arr.add(actdata.getString("device"));
                                }
                            }
                            editor.putStringSet("deviceset",arr);
                            Log.e("arrrrrr",arr.toString());
                            editor.commit();

                            String corpid = new JSONObject(response).optJSONObject("#data").optString("corpid");
                            Log.e(TAG, corpid);
                            if (corpid != null & !corpid.isEmpty()) {
                                register();
                                //   sendNotification();

                                Intent intent = new Intent(CompanyCodeActivity.this, ConfigureActivity.class);
                                intent.putExtra("json", response);
                                startActivity(intent);
                                finish();

                            } else {

                                String message = new JSONObject(response).optJSONObject("#data").optString("#message");
                                if (message.equalsIgnoreCase("2"))
                                  //  Toast.makeText(CompanyCodeActivity.this, "Company Code expired. Please contact your program manager.", Toast.LENGTH_LONG).show();
                                    customdialog("Company Code expired. Please contact your program manager.");
                                else
                                    customdialog("Company code does not exist.");
                                   // Toast.makeText(CompanyCodeActivity.this, "Company code does not exist.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e1) {
                            customdialog("Error");
                           // Toast.makeText(CompanyCodeActivity.this, "Error", Toast.LENGTH_LONG).show();

                            e1.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customdialog("Connection error you are on a slow connection Please try again Later");

                      //  Toast.makeText(CompanyCodeActivity.this, "Connection error you are on a slow connection Please try again Later", Toast.LENGTH_LONG).show();
                        pDialog.hide();
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                final Helper helper = new Helper();
                final String time_stamp = helper.getUnixTimestamp();
                final String method = "corpchallenge.getcompanyconfig";
                params.put("hash", helper.getHashValue(Constants.API_KEY, Constants.DOMAIN, time_stamp, method));
                params.put("domain_name", Constants.DOMAIN);
                params.put("domain_time_stamp", time_stamp);
                params.put("nonce", time_stamp);
                params.put("sessid", sessid);
                params.put("method", method);
                params.put("companycode", et_company_code.getText().toString());
                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);


    }



  public void  customdialog(String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
              //  Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                /*finish();*/
            }
        });



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().toString().equalsIgnoreCase("downloadImages")) {

            connectForSessId();


        }
    }
public void popup(){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");

    alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            Toast.makeText(CompanyCodeActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
        }
    });

    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    });

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
}

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void register() {
        if (checkPlayServices()) {
            //   gcm = GoogleCloudMessaging.getInstance(this);

            try {
                regId = FirebaseInstanceId.getInstance().getToken();
                Log.e(TAG, "token is " + regId);

                //  regId = getRegistrationId(context);
            } catch (Exception e) {
                e.printStackTrace();
            }

           /* if (regId.isEmpty()) {

            } else {
                toastUser("Registration ID already exists: " + regId);
            }*/

            registerInBackground();

        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }
    }


    /*  public void toastUser(String message)
      {
          Toast.makeText(context,message,Toast.LENGTH_LONG).show();
      }*/
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String msg;
                try {
                    regId = FirebaseInstanceId.getInstance().getToken();
                    //  regId = gcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID: " + regId;

                    sendRegistrationId(regId);

                    storeRegistrationId(context, regId);
                    Log.i(TAG, msg);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e(TAG, msg);
                }
                return msg;
            }
        }.execute(null, null, null);
    }


    private String getRegistrationId(Context context) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences("Token", Context.MODE_PRIVATE);
        String registrationId = prefs.getString("token", "");
        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }

    private void sendRegistrationId(String regId) {
        pubnub.enablePushNotificationsOnChannel(
                et_company_code.getText().toString().trim(),
                regId);
    }

    private void storeRegistrationId(Context context, String regId) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", regId);
        editor.apply();
    }


}
