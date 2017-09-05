package com.tupelo.wellness.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.tupelo.wellness.R;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.helper.WalkingPrefrence;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends Activity {
    // HashMap<String,Class<?>> hash = new HashMap<>();
    DbAdapter dbAdapter;
    Activity activity;
    boolean moveForward = true;
    String TAG = SplashScreen.class.getName();
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreference sharedPreference;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      showDialog();
       /* Intent intent = getIntent();
        if(intent.hasExtra("check")){
            String check = intent.getStringExtra("check");
            if(check.equals("1")){
                Toast.makeText(activity, "11111111", Toast.LENGTH_SHORT).show();
                showDialog();
            }else if(check.equals("0")){
                Toast.makeText(activity, "22222222222", Toast.LENGTH_SHORT).show();
                showDialog();
            }

        }
*/


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);



      //  Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);

    }
    public void showDialog() {

//        openPopup = true;
//        setContentView(R.layout.popup);

        RelativeLayout promptView = (RelativeLayout) getLayoutInflater().inflate(R.layout.popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this)
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

    @Override
    protected void onPause() {
        super.onPause();
        moveForward = false;

        Log.e(TAG, "splash screen pause " + moveForward);
    }

    @Override
    protected void onResume() {
        super.onResume();
        moveForward = true;

        Log.e(TAG, "splash screen resume " + moveForward);


        sharedPreference = SharedPreference.getInstance(SplashScreen.this);

        dbAdapter = DbAdapter.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);


        String expiry = prefs.getString("expiry", "");

        boolean alreadyLoggedIn = prefs.getBoolean("DownloadedImages", false);


        Calendar c = Calendar.getInstance();

        Date date = c.getTime();
        long dateUser = date.getTime();
        long dateServer = Long.MAX_VALUE;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date2 = format.parse(expiry);
            dateServer = date2.getTime();

            Log.e(TAG, "phone current date is " + dateUser + " expiry date is " + dateServer + " date server exact " + expiry +" date server intermin is "+ date2.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }


        // boolean alreadyLoggedIn = false;

        if (alreadyLoggedIn) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(SplashScreen.this, R.color.colorPrimaryDark));
            }

            SharedPreferences prefs2 = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
            String selectedDevice = prefs2.getString("selectedDevice", "");
            if (selectedDevice.isEmpty())
                continueSplashCourse();
            else if (dateServer < dateUser)
               // continueSplashCourse();
              Helper.logoutVolley(SplashScreen.this, pDialog);
            else
                continueSplashCourse();
        } else {



            activity = this;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "splash screen " + moveForward);
                    if (moveForward) {
                        Intent intent = new Intent(activity, CompanyCodeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }, SPLASH_TIME_OUT);

        }

    }

    private void continueSplashCourse() {

        SharedPreferences prefs = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
        //  File f = new File("/mnt/sdcard/mainscreens1.jpg");
        ImageView mImgView1 = (ImageView) this.findViewById(R.id.splash_image);
        String splashImage = prefs.getString("splashImage", "");
        if (!splashImage.isEmpty()) {
            Bitmap bmp = Helper.stringToBitmap(splashImage);
            mImgView1.setImageBitmap(bmp);
        }
        activity = this;

        String color = prefs.getString("colorcode", "1");
        AppTheme app = AppTheme.getInstance();
        app.setAppTheme(Integer.parseInt(color));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(AppTheme.getInstance().colorPrimaryDark));
        }
        Log.e(TAG, "the corp id is" + prefs.getString("corpid", ""));
        String companyName = prefs.getString("corpname", "");
        String grp_alias = prefs.getString("grp_alias", "Select Division");
        String clustr_alias = prefs.getString("clustr_alias", "Select Group");
        String deviceSupport = prefs.getString("deviceSupport", "");
        String groupings = prefs.getString("groupings", "");
        String programName = prefs.getString("programeName", "");
        String corpid = prefs.getString("corpid", "");
        String corpcode = prefs.getString("corpcode", "");
        String clustrmode = prefs.getString("clustrmode", "");
        String grpmode = prefs.getString("grpmode", "");
        String prgrm_edate = prefs.getString("prgrm_edate", "");
        String prgrm_sdate = prefs.getString("prgrm_sdate", "");


        //  System.out.println("Current time => " + c.getTime());


        String roleId = prefs.getString("roleId", "");
        WalkingPrefrence walkingPrefrence = WalkingPrefrence.getInstance();
        walkingPrefrence.setWalkingPrefrence(prgrm_edate,prgrm_sdate,companyName, deviceSupport, groupings, programName, corpid, grp_alias, clustr_alias, color, corpcode, clustrmode, grpmode, roleId);
/*

            String myClass = prefs.getString("class","");
            if(!myClass.isEmpty())
            {
             //   Class<?> cls = hash.get(myClass);
            //    startActivity(new Intent(SplashScreen.this,cls));

            }
*/


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.e(TAG, "splash screen " + moveForward);
                if (moveForward) {
                    try {
                        Log.e("Get Auth Token", " table fit bit is present" + dbAdapter.isLogin(DbAdapter.TABLE_NAME_FITBIT));

                        Intent i = null;
                        Log.e(TAG, "checking weather the table is fit bit");
                        if (dbAdapter.isLogin(DbAdapter.TABLE_NAME_FITBIT)) {

                            Log.e(TAG, "yes it is fitbit");
                            Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_FITBIT);
                            if (cursor.getCount() > 0) {
                                String expiry = cursor.getString(4);
                                Long d = Long.valueOf(expiry);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(d);
                                DateFormat format = DateFormat.getDateTimeInstance();
                                //SimpleDateFormat sdf = new SimpleDateFormat(format + "");
                                String da = format.format(calendar.getTime());
                                Date date = null;
                                try {
                                    date = format.parse(da);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date curDate = Calendar.getInstance().getTime();
                                if (date != null) {
                                    switch (date.compareTo(curDate)) {
                                        case -1:
                                        case 0:
                                            i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(Constants.FITBIT_lOGIN_URL));
                                            List<ResolveInfo> browserList = getPackageManager().queryIntentActivities(i, 0);
                                            i.setPackage(browserList.get(0).activityInfo.packageName);
                                            break;
                                        case 1:
                                            i = new Intent(SplashScreen.this, TabActivity.class);
                                            i.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_FITBIT);
                                    }
                                }
                            }


                        } else if (dbAdapter.isLogin(DbAdapter.TABLE_NAME_JAWBONE)) {
                            i = new Intent(SplashScreen.this, TabActivity.class);
                            i.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_JAWBONE);
                        } else if (dbAdapter.isLogin(DbAdapter.TABLE_NAME_MYMO)) {
                            i = new Intent(SplashScreen.this, TabActivity.class);
                            i.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_MYMO);
                        } else if (dbAdapter.isLogin(DbAdapter.TABLE_NAME_GARMIN)) {
                            i = new Intent(SplashScreen.this, TabActivity.class);
                            i.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_GARMIN);
                        } else if (dbAdapter.isLogin(DbAdapter.TABLE_NAME_PEDOMETER)) {
                            i = new Intent(SplashScreen.this, TabActivity.class);
                            i.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_PEDOMETER);
                        } else if (dbAdapter.isLogin(DbAdapter.TABLE_NAME_SHEALTH)) {
                            i = new Intent(SplashScreen.this, TabActivity.class);
                            i.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_SHEALTH);

                        } else if (dbAdapter.isLogin(DbAdapter.TABLE_NAME_TUPELO)) {
                            if (sharedPreference.getBoolean(Constants.IS_SIGNUP_COMPLETE, false)) {
                                Log.e(TAG, "no it is not fitbit");
                                i = new Intent(SplashScreen.this, ChooseTracker.class);
                            } else {
                                i = new Intent(SplashScreen.this, SaveCouncilActivity.class);
                            }
                        } else {
                            i = new Intent(SplashScreen.this, LoginOrRegister.class);
                        }
                        startActivity(i);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // close this activity
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }
}