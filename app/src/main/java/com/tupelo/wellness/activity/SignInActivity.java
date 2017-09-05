package com.tupelo.wellness.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubnub.api.Pubnub;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.helper.WalkingPrefrence;
import com.tupelo.wellness.parcer.Jsonparser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * SignInActivity class
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_username, et_password;
    public ImageView btn_signIn;
    Fonts fonts;
    static String TAG = SignInActivity.class.getSimpleName();
    GoogleCloudMessaging gcm;
    String regId;
    Context context = SignInActivity.this;
    SharedPreference spMain;
    private final Pubnub pubnub = new Pubnub(Constants.PUBLISH_KEY,Constants.SUBSCRIBE_KEY);

    private ProgressDialog pDialog;
    int isUserName;
    int sendToSelectDev = 1, sendToSaveCouncil = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
        fonts = new Fonts(this);



        spMain = spMain.getInstance(context);
        //write here thrte code to rtegister with pubnub push






        RelativeLayout myLayout = (RelativeLayout) this.findViewById(R.id.login_background);


        AppTheme app = AppTheme.getInstance();
        myLayout.setBackgroundColor(Color.parseColor(app.colorPrimary));
        String companyName = WalkingPrefrence.getInstance().companyName;
      /*  TextView mImgView1 = (TextView) this.findViewById(R.id.client_logo);
        mImgView1.setText(companyName);*/

        TextView program_name = (TextView) this.findViewById(R.id.program_name);
        program_name.setText(WalkingPrefrence.getInstance().programeName);


        String statusBarColor = AppTheme.getInstance().colorPrimaryDark;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }

        initViews();
    }

    private void initViews() {


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        /*try {
            isUserName = new JSONObject(spMain.getString(Constants.APP_CREDENTIALS, "")).optJSONObject("#data").optInt("isUsername");
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        SharedPreferences prefs = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
        try {
            isUserName = Integer.parseInt(prefs.getString("isUsername", ""));
        }catch (Exception e){
            e.printStackTrace();
        }

        et_username = (EditText) findViewById(R.id.et_si_username);
        if (isUserName == 1) {
            et_username.setHint(getResources().getString(R.string.username));
        } else {
            et_username.setHint(getResources().getString(R.string.email));
        }
        et_password = (EditText) findViewById(R.id.et_si_password);
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                    return true;
                }
                return false;
            }
        });
        TextView tv_forgot = (TextView) findViewById(R.id.txtVuForgot);
        TextView txtVuSignup = (TextView) findViewById(R.id.txtVuSignup);
        txtVuSignup.setOnClickListener(this);

        tv_forgot.setOnClickListener(this);
     //   tv_forgot.setTypeface(fonts.getTypefaceNormal());
        btn_signIn = (ImageView) findViewById(R.id.btn_login);
        btn_signIn.setOnClickListener(this);

        Drawable myDrawable = btn_signIn.getDrawable();
        myDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        btn_signIn.setImageDrawable(myDrawable);


        //colored white again, because in My device screen we are using the same icon.
        //so if user comes here from my device screen we will see the changed icon in My device

    }

    private void login() {
        new Helper().hidekeyboard(this, et_password);
        if (Validation()) {
            if (CheckConnection.isConnection(SignInActivity.this)) {
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();
            //    new SingInAsync(SignInActivity.this, false).execute(username, password);
                sessionIdCheck("login",username, password);

            } else {
                new Helper().showNoInternetToast(SignInActivity.this);
            }
        }
    }

    private void sessionIdCheck(final String... eclipseParams) {
        pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG,"session id responce" + response);
                            JSONObject json = new JSONObject(response);
                            JSONObject jsonObject = json.getJSONObject("#data");
                            if(eclipseParams[0].equalsIgnoreCase("login"))
                                 signInVolley(jsonObject.getString("sessid"), eclipseParams[1], eclipseParams[2]);
                            else if(eclipseParams[0].equalsIgnoreCase("forgot"))
                                forgotPasswordVolley(jsonObject.getString("sessid"),eclipseParams[1]);
                            
                        } catch (JSONException j) {
                            j.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(SignInActivity.this, R.string.network_not_connected , Toast.LENGTH_SHORT).show();

                        Log.e("ERROR", "error 1=> " + error.getMessage());
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                String method = "system.connect";
                params.put("method", method);

                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);


    }

    public void forgotPasswordVolley(final String... eclipseParams )
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        Log.e(TAG,"Response is " + response);


                        int index = response.indexOf("{");
                        Log.e(TAG,"index is " + index);
                        if(index != -1)
                        response = response.substring(index);
                         // doing this because in case of right user the api responce is SMTP Error: Could not authenticate. line {"#error":false,"#data":true}
                        Log.e(TAG,"response is " + response);
                        String result;

                        if (!response.equals("Connection Timeout")) {
                            Jsonparser jsonparser = new Jsonparser(response);
                            if (jsonparser.forgetPassword())
                                result = "0";
                            else
                                result  =  "1";
                        }
                        else
                            result = "-3";


                        if (result.equals("-3")) {
                            Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.universal_err), Toast.LENGTH_SHORT).show();
                        } else {
                               afterResetPass(result);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error 3=> " + error.getMessage());
                        pDialog.hide();
                        Toast.makeText(SignInActivity.this, R.string.network_not_connected , Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String method = "userV2.resetpwd", mSessId = "";
                mSessId = eclipseParams[0];
                params = new Helper().addRequiredParams(params,method);
                params.put("method", method);
                params.put("usernameid", eclipseParams[1]);
                params.put("sessid", mSessId);
                 Log.e(TAG,"params are "+params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);

    }

    private void signInVolley(final String sessid, final String username, final String password) {

        String url = Constants.BASE_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        boolean is_social = false;

                        Log.e("sign in response", response);
                        Jsonparser jsonparser = new Jsonparser(response);
                        String result = jsonparser.getError();
                        if (result.equals("0")) {
                            jsonparser.setTupeloSignIn(SignInActivity.this, "login");
                        }

                        if (result.equals("0")) {




                            saveDataToSharedPrefrence(response);




                            try {
                                JSONObject data = (new JSONObject(response)).optJSONObject("#data");
                                JSONObject users = data.optJSONObject("user");
                                int groupSet = users.optInt("groupSet");

                                if (groupSet == sendToSaveCouncil) {
                                    afterSuccessLogin(sendToSaveCouncil);
                                } else {
                                    afterSuccessLogin(sendToSelectDev);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (result.equals("2")) {
                            if (!is_social)
                                Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.wrongusernamepass), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.userregistered), Toast.LENGTH_LONG).show();
                        } else if (result.equals("1")) {
                            Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.login_server_err), Toast.LENGTH_SHORT).show();
                        } else if (result.equals("-3")) {
                            Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.universal_err), Toast.LENGTH_SHORT).show();
                        }else if (result.equals("3")) {
                            Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.not_allowed), Toast.LENGTH_SHORT).show();
                        }else if (result.equals("4")) {
                            Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.not_allowed_Activate), Toast.LENGTH_SHORT).show();
                        } else if (result.equals("5")) {
                            Toast.makeText(SignInActivity.this, SignInActivity.this.getString(R.string.account_blocked), Toast.LENGTH_SHORT).show();
                        }


                        //          return message;

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(SignInActivity.this, R.string.network_not_connected , Toast.LENGTH_SHORT).show();

                        Log.e("ERROR", "error => " + error.getMessage());
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                
                String method = "userV2.login_challengeV2";
                Map<String, String> params = new HashMap<String, String>();
                Helper helper = new Helper();
                String time_stamp = helper.getUnixTimestamp();
                params.put("hash", helper.getHashValue(Constants.API_KEY, Constants.DOMAIN, time_stamp, method));
                params.put("domain_name", Constants.DOMAIN);
                params.put("domain_time_stamp", time_stamp);
                params.put("nonce", time_stamp);
                params.put("method", method);
                params.put("sessid", sessid);
                params.put("username", username);
                params.put("corpid", WalkingPrefrence.getInstance().corpId);
                params.put("password", password);

                Log.e(TAG,params.toString());

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);

    }

    @Override
    public void onBackPressed() {
       // new Helper().setActivityBackAnimation(SignInActivity.this);
        goToLoginOrRegister(new View(SignInActivity.this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.txtVuForgot:
                showForgetpasswordDialog();
                break;
            case R.id.txtVuSignup:
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }
    public void saveDataToSharedPrefrence(String response)
    {
        try {
            JSONObject user = new JSONObject(response).getJSONObject("#data").getJSONObject("user");

            String sessionid = new JSONObject(response).getJSONObject("#data").getString("sessid");

            SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor1 = pref1.edit();
            editor1.putString("userId", user.getString("userId")); // Storing string
            editor1.commit();




            String username = user.getString("userName");
            String userFname = user.getString("userFname");
            String userLname = user.getString("userLname");
            String userCountry = user.getString("userCountry");
            String userCity = user.getString("userCity");
            String userbirthDate = user.getString("userbirthDate");
            String userWeight = user.getString("userWeight");
            String userHeight = user.getString("userHeight");
            String userGender = user.getString("userGender");
            String heightpref = user.getString("heightPref");

            String userHeightEnteredSIUnit = user.getString("heightPref");

            String userWeightEnteredSIUnit = user.getString("weightPref");

            String userAvatarURL = user.getString("userAvatarURL");
            String userZip =  user.getString("userZip");
            String finalGender;
            if (userGender.equalsIgnoreCase("male") || userGender.equalsIgnoreCase("0"))
                finalGender = "0";
            else
                finalGender = "1";


            SharedPreferences.Editor editor = getSharedPreferences("MyProfile", MODE_PRIVATE).edit();

            userHeight = userHeightEnteredSIUnit.contains("ft")?Helper.getFeetFromCMS(userHeight):userHeight;

            userWeight = userWeightEnteredSIUnit.contains("lbs")?getlbsFromKg(userWeight):userWeight;

            Log.e(TAG,userWeight + " " +userHeight);

            editor.putString("userName",username);
            editor.putString("userAvatarURL",userAvatarURL);
            editor.putString("userFname",userFname);
            editor.putString("userLname",userLname);
            editor.putString("userCity",userCity);
            editor.putString("userWeight",userWeight);
            editor.putString("userHeight",userHeight);
            editor.putString("userGender",finalGender);
            editor.putString("userCountry",userCountry);
            editor.putString("userbirthDate",userbirthDate);
            editor.putString("heightPref", heightpref);
            editor.putString("sessid", sessionid);
            if(userZip == null || userZip.equalsIgnoreCase("null"))
                userZip = "";
            editor.putString("zipCode",userZip);
            Log.e(TAG,"gender is " + userGender + userFname);
                editor.commit();


        }
        catch (JSONException j)
        {
            j.printStackTrace();
        }

    }

    private String getlbsFromKg(String userWeight) {

        double weight = Double.parseDouble(userWeight);
 //       Math.round((weight / 2.20462) *10.0) / 10.0
        return Math.round((weight*2.20462)*10.0) / 10.0 + " lbs";

    }



    private boolean Validation() {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (username.equals("")) {
            Toast.makeText(this, getString(R.string.empty_user_name_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.equals("")) {
            Toast.makeText(this, getString(R.string.empty_pass_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void afterSuccessLogin(int choices) {
        Intent intent = new Intent(Constants.KILL_ACTIVITY);
        sendBroadcast(intent);
        if (choices == sendToSaveCouncil) {
            Intent intent2 = new Intent(SignInActivity.this, SaveCouncilActivity.class);
            startActivity(intent2);
        } else {
            Intent intent2 = new Intent(SignInActivity.this, ChooseTracker.class);
            startActivity(intent2);
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }

    public void afterResetPass(String response) {
        Log.e(TAG,"i have" + response);

        if (response.equals("1"))
            Toast.makeText(this, getString(R.string.wrngusernameemail), Toast.LENGTH_LONG).show();
        else if(response.equals("0"))
            Toast.makeText(this, getString(R.string.rightusernameemail), Toast.LENGTH_LONG).show();


    }

    private void showForgetpasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            builder=new AlertDialog.Builder(SignInActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        else
            builder=new AlertDialog.Builder(SignInActivity.this,android.R.style.Theme_Dialog);
        builder.setTitle(getString(R.string.pleaseenter));
        View view = SignInActivity.this.getLayoutInflater().inflate(R.layout.forget_password,null);
        final EditText editText=(EditText)view.findViewById(R.id.etUsername);

        new Helper().showKeyBoard(SignInActivity.this);
        builder.setView(view);

        builder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Helper().hidekeyboard(SignInActivity.this,editText);
                if(CheckConnection.isConnection(SignInActivity.this)){
                    String username=editText.getText().toString().trim();
                    if(!username.equals("")){
                        //new ForgetPasswordAsync(SignInActivity.this).execute(username);
                        sessionIdCheck("forgot",username);
                    }
                    else
                        Toast.makeText(SignInActivity.this, getString(R.string.plzprovideusernameid), Toast.LENGTH_SHORT).show();}
                else
                    new Helper().showNoInternetToast(SignInActivity.this);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Helper().hidekeyboard(SignInActivity.this,editText);
                dialog.dismiss();
            }
        });
  builder.show();
    }

    public void goToLoginOrRegister(View view) {
        Intent intent = new Intent(SignInActivity.this,LoginOrRegister.class);
        startActivity(intent);
        finish();
    }



    //push pubnub func





}
