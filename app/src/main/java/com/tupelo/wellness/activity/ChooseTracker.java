package com.tupelo.wellness.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.R;
import com.tupelo.wellness.SHealthIntegration.SetUpSHealth;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.helper.StepCountReader;
import com.tupelo.wellness.helper.WalkingPrefrence;
import com.tupelo.wellness.jawbone.oauth.OauthUtils;
import com.tupelo.wellness.jawbone.oauth.OauthWebViewActivity;

import org.apache.http.auth.AUTH;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import retrofit.http.POST;


public class ChooseTracker extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

    int[] mipmap = {R.mipmap.mymo, R.mipmap.fitbit, R.mipmap.google_fit, R.mipmap.jawbone,R.mipmap.garmin,R.mipmap.s_health};

    ArrayList device = new ArrayList<String>();
    String TAG = "ChooseTracker";
    FrameLayout previousLayout = null;
    //now start again with customized screen
    GoogleApiClient client;

    boolean showBackButton;

    SharedPreference sharedPreference;
    Button btnFitBit, btnJawbone, btnMymo, btnPadometer;
    private List<Constants.JawboneAuthScope> authScope;
    Activity activity;
    ImageView[] img = new ImageView[6];
    private HealthDataStore mStore;
    private StepCountReader mReporter;
    private long mCurrentStartTime;
    //private ActivityRootBinding binding;
//!! THIS SHOULD BE IN AN ANDROID KEYSTORE!! See https://developer.android.com/training/articles/keystore.html
    private static final String ALIAS_DEVICE_UUID = "deviceuuid";
    private static final String ALIAS_TOTAL_COUNT = "count";

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    private  HealthDataResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        mCurrentStartTime = StepCountReader.TODAY_START_UTC_TIME;

        showBackButton = getIntent().getBooleanExtra("showBackButton",false);

        //it will only be true when user have come from mydevice fragment

        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.tabanim_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        setSupportActionBar(toolbar);
        activity = this;


        sharedPreference = SharedPreference.getInstance(this);
        sharedPreference.putBoolean(Constants.IS_SIGNUP_COMPLETE, true);


        if(showBackButton)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

/*

        TextView header = (TextView) this.findViewById(R.id.tv_choose);
        header.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(AppTheme.getInstance().colorPrimaryDark));
        }


        try {
            JSONArray deviceSupports = new JSONArray(WalkingPrefrence.getInstance().deviceSupport);
              device = new ArrayList<String>();
            Log.e(TAG,deviceSupports.toString());
            for(int i=0;i<deviceSupports.length();i++)
            {
                if(deviceSupports.getJSONObject(i).getInt("value") == 1 && !(deviceSupports.getJSONObject(i).getString("device").equalsIgnoreCase("Health Kit")))
                {
                  device.add(deviceSupports.getJSONObject(i).getString("device"));
                }
            }
         //   device = new JSONArray("[mymo,healthkit]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        img[0] = (ImageView) findViewById(R.id.img1);
        img[1] = (ImageView) findViewById(R.id.img2);
        img[2] = (ImageView) findViewById(R.id.img3);
        img[3] = (ImageView) findViewById(R.id.img4);
        img[4] = (ImageView) findViewById(R.id.img5);
        img[5] = (ImageView) findViewById(R.id.img6);

        String deviceArray[] = new String[device.size()];
        deviceArray = (String [])device.toArray(deviceArray);
        Log.e(TAG,device.toString());
        for(int i=0; i<deviceArray.length; i++){
            switch (deviceArray[i]){

                case "mymo":
                    chechIfSelected("mymo",img[i]);
                    img[i].setImageResource(mipmap[0]);
                    img[i].setTag("mymo");
                    break;

                case "fitbit":
                    chechIfSelected("fitbit",img[i]);
                    img[i].setImageResource(mipmap[1]);
                    img[i].setTag("fitbit");
                    break;

                case "Google Fit":
                    chechIfSelected("Google Fit",img[i]);
                    img[i].setImageResource(mipmap[2]);
                    img[i].setTag("Google Fit");
                    break;

                case "jawbone":
                    chechIfSelected("jawbone",img[i]);
                    img[i].setImageResource(mipmap[3]);
                    img[i].setTag("jawbone");
                    break;

                case "Garmin":
                    chechIfSelected("Garmin",img[i]);
                    img[i].setImageResource(mipmap[4]);
                    img[i].setTag("Garmin");
                    break;

                case "S Health":
                    chechIfSelected("S Health", img[i]);
                    img[i].setImageResource(mipmap[5]);
                    img[i].setTag("S Health");
                    break;
            }

        }






    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        Log.e(TAG, "the item id is " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
    private void chechIfSelected(String myDevice,ImageView trackerImage) {
        SharedPreferences prefs = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String selectedDevice = prefs.getString("selectedDeviceGlobal", "");
        if(!selectedDevice.isEmpty() && selectedDevice.equalsIgnoreCase(myDevice))
        {

            RelativeLayout parent = (RelativeLayout) trackerImage.getParent();
            FrameLayout selectorImage = (FrameLayout) parent.getChildAt(1);
            selectorImage = AppTheme.changeBackgroundColor(selectorImage);
            selectorImage.setVisibility(View.VISIBLE);
            if(previousLayout == null)
                previousLayout = selectorImage;
        }


        //the two keys for device are needed because on click the selecdDevice will change and when the user comes again to
        //choose tracker he will see the selected device selected even in the case when the has not gone to tab activity
        //for example if you select jawbone and do not login, then in selectedDevice key there will be jawbone,
        //but in selectedDeviceglobal will have jawbone only when the user succesfully submited the device details and
        //has gone to tabactivity

    }



    public void openRespectiveTracker(View view) {
        Intent intent;

        ImageView trackerImage = (ImageView) view;
        String tag = trackerImage.getTag().toString();

        RelativeLayout parent = (RelativeLayout) view.getParent();
        FrameLayout selectorImage = (FrameLayout) parent.getChildAt(1);
        if(previousLayout != null)
        {
            previousLayout.setVisibility(View.INVISIBLE);
        }
        selectorImage = AppTheme.changeBackgroundColor(selectorImage);
        selectorImage.setVisibility(View.VISIBLE);
        previousLayout = selectorImage;
      //  selectorImage.setBackgroundColor(Color.WHITE);
    /*    selectorImage.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);
        selectorImage.setVisibility(View.VISIBLE);*/
        /*

        RelativeLayout view2 = (RelativeLayout)view;
        ImageView trackerImage = (ImageView) view2.getChildAt(0);

        String tag = trackerImage.getTag().toString();

        ImageView trackerSelectorImage = (ImageView) view2.getChildAt(1);
        trackerSelectorImage.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);
*/


        if(!tag.isEmpty()) {

            Log.e(TAG,"clicked value is " + tag);



            SharedPreferences.Editor editor = getSharedPreferences("MyProfile", MODE_PRIVATE).edit();
            editor.putString("selectedDevice",view.getTag().toString());
            editor.commit();

           switch (tag) {
                case "mymo":
                    intent = new Intent(activity, MymoOnBoard.class);
                    startActivity(intent);
                  //  finish();
                    break;
                case "fitbit":
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Constants.FITBIT_lOGIN_URL));
//                    List<ResolveInfo> browserList = getPackageManager().queryIntentActivities(intent, 0);
//                    intent.setPackage(browserList.get(0).activityInfo.packageName);
                    startActivity(intent);

                        //finish();
                    break;
               case "S Health":
                  SetUpSHealth.getInstance().init(activity,0);
//
//                   long endTime = System.currentTimeMillis();
//                   long startTime = endTime - 24*60*60*1000;
//                   HealthDataResolver.Filter filter = HealthDataResolver.Filter.and(HealthDataResolver.Filter.greaterThanEquals(HealthConstants.Exercise.START_TIME, startTime),
//                           HealthDataResolver.Filter.lessThanEquals(HealthConstants.Exercise.END_TIME, endTime));
//
//                   HealthDataResolver.ReadRequest request1 = new HealthDataResolver.ReadRequest.Builder()
//                           .setDataType(HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE)
//                           .setProperties(new String[]{HealthConstants.FloorsClimbed.FLOOR})
//                           .setFilter(filter)
//                           .build();
//                    mResolver.read(request1).setResultListener(new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
//                        @Override
//                        public void onResult(HealthDataResolver.ReadResult result) {
//                            Cursor c = null;
//                            try {
//                                c = result.getResultCursor();
//                                if (c != null) {
//                                    while (c.moveToNext()) {
//                                        String data = String.valueOf(c.getInt(c.getColumnIndex("floor")));
//                                        Integer data = c.getInt(c.getColumnIndex(HealthConstants.StepCount.CALORIE));
//
//                                            if (data != null) {
//                                             If data is not null, it means there is LIVE_DATA
//                                             Refer an example in API Reference’ HealthConstants.Exercise interface
//                                             and decompress live data
//                                        byte[] data = c.getBlob(c.getColumnIndex(HealthConstants.FloorsClimbed.FLOOR));
//                                        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
//                                        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
//                                        BufferedReader bf = new BufferedReader(new
//                                                InputStreamReader(gzipInputStream, "UTF-8"));
//                                        String outStr = "";
//                                        String line;
//                                        while ((line=bf.readLine())!=null) {
//                                            outStr += line;
//                                        }
//                                        JSONArray array= new JSONArray(outStr);
//                                        for(int i=0;i<array.length();i++){
//                                            JSONObject jsonObject=array.getJSONObject(i);
//                                            Date date= new Date(jsonObject.getLong("start_time"));
//                                            SimpleDateFormat format= new SimpleDateFormat("dd/MM/yy HH:mm:ss z");
//                                            Log.e("dateeee",format.format(date));
//                                        }
//                                    }
//                                       // Log.e(".APP_TAG", ""+data);
//
//
////                                        else {
////                                            Log.e(".APP_TAG", "there is no live data.");
////                                        }
//                                    }
//                                }
//                            } catch(Exception e) {
//                                Log.e("MainActivity.APP_TAG", e.getClass().getName() + " - " + e.getMessage());
//                            } finally {
//                                if (c != null) {
//                                    c.close();
//                                }
//                      }
//                      }
//                      });
                   Log.e("S Health", " is selected ...");
                   break;
                case "Google Fit":
                    if(new Helper().isAppInstalled(activity)) {
                        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("login", "true");
                        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PEDOMETER);
                        dbAdapter.insertQuery(DbAdapter.TABLE_NAME_PEDOMETER, contentValues);

                        intent = new Intent(activity, TabActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constants.SERVICE, Constants.PADOMETER);
                        startActivity(intent);

                       // new VerifyDataTask().execute();
                     //   finish();
                    } else {
                        Intent intent1 = new Intent(activity, GoogleFitHelpActivity.class);
                        startActivity(intent1);
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity)
//                            .setMessage("To use Pedometer, you need to install Google Fit first.")
//                            .setCancelable(false)
//                            .setPositiveButton(R.string.accept, new Dialog.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            // Mark this version as read.
//
//                                            // Close dialog
//                                            dialogInterface.dismiss();
//                                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                                            intent.setData(Uri.parse(Constants.GOOGLE_FIT_MARKET_LINK));
//                                            startActivity(intent);
//                                        }
//                                    })
//                            .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int which) {
//                                            // Close the activity as they have declined
//                                            // the EULA
//                                            dialogInterface.dismiss();
//                                        }
//
//                                    });
//                    AlertDialog alertDialog = builder1.create();
//                    alertDialog.show();
                    }
                    break;
                case "jawbone":
                    authScope  = new ArrayList<Constants.JawboneAuthScope>();
                    authScope.add(Constants.JawboneAuthScope.MOVE_READ);
                    Uri.Builder builder = OauthUtils.setOauthParameters(Constants.CLIENT_ID, Constants.OAUTH_CALLBACK_URL, authScope);
                    intent = new Intent(activity, OauthWebViewActivity.class);
                    intent.putExtra(Constants.AUTH_URI, builder.build());
                    startActivity(intent);
                  //  finish();
                    break;
               case "Garmin":
                   String sessionId = "", userId = "";

                   DbAdapter dbAdapter = DbAdapter.getInstance(this);
                   Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);
                   if (cursor.getCount() > 0) {
                       sessionId = cursor.getString(0);
                       userId = cursor.getString(1);
                       getGarmin(activity,sessionId,userId);
                   }
                   break;
            }

        }
    }
    private static void getGarmin(final Context context,  final String sessionId, final String userId) {
        final String TAG = "Get Garmin Token";
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObject = jsonObject.getJSONObject("#data");
                            String status = jsonObject.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                String url = jsonObject.getString("auth_url");
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                List<ResolveInfo> browserList = context.getPackageManager().queryIntentActivities(intent, 0);
                                intent.setPackage(browserList.get(0).activityInfo.packageName);
                                intent.putExtra(Constants.SERVICE, "garmin");
                                Log.d(TAG, "result =>" + url);
                                context.startActivity(intent);
                            }
                            else {
                                Intent  intentR = new Intent(context, TabActivity.class);
                                intentR.putExtra(Constants.SERVICE, "garmin");
                                context.startActivity(intentR);
                            }
                            DbAdapter dbAdapter = DbAdapter.getInstance(context);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("login", "true");
                            dbAdapter.deleteAll(DbAdapter.TABLE_NAME_GARMIN);
                            dbAdapter.insertQuery(DbAdapter.TABLE_NAME_GARMIN, contentValues);
                        }catch (JSONException e){
                            Toast.makeText(context,e.getMessage() , Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error 3=> " + error.getMessage());
                        //pDialog.dismiss();
                        Toast.makeText(context, R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();

                String method = "corpchallenge.requesttoken_garmin";
                params.clear();

                params.put("method", method);
                params.put("sessid", sessionId);
                params.put("userid", userId);
                params = new Helper().addRequiredParams(params, method);

                Log.e(TAG, "params are " + params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }



//    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
//
//        SensorRequest request = new SensorRequest.Builder()
//                .setDataSource( dataSource )
//                .setDataType( dataType )
//                .setSamplingRate( 3, TimeUnit.SECONDS )
//                .build();
//
//        Fitness.SensorsApi.add(client, request, new OnDataPointListener() {
//            @Override
//            public void onDataPoint(DataPoint dataPoint) {
//                for( final Field field : dataPoint.getDataType().getFields() ) {
//                    final Value value = dataPoint.getValue( field );
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        })
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            Log.e( "GoogleFit", "SensorApi successfully added" );
//                        }
//                    }
//                });
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SetUpSHealth.getInstance().destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == 1 ) {
            if( resultCode == RESULT_OK ) {
                if( !client.isConnecting() && !client.isConnected() ) {
                    client.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE )
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();

//        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
//            @Override
//            public void onResult(DataSourcesResult dataSourcesResult) {
//                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
//                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
//                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
//                    }
//                }
//            }
//        };

//        Fitness.SensorsApi.findDataSources(client, dataSourceRequest)
//                .setResultCallback(dataSourcesResultCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        try {
            result.startResolutionForResult( ChooseTracker.this, 1 );
        } catch(IntentSender.SendIntentException e ) {
            e.printStackTrace();
        }
    }


    class VerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            long total = 0;


            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(client, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if (totalSet != null) {
                    total = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                }
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }


            Log.i(TAG, "Total steps: " + total);
            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new FetchCalorieAsync().execute();

        }
    }

    private class FetchCalorieAsync extends AsyncTask<Object, Object, Float> {
        protected Float doInBackground(Object... params) {
            Float total = 0.00f;
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(client, DataType.TYPE_CALORIES_EXPENDED);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if (totalSet != null) {
                    total = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                }
            } else {
                Log.w(TAG, "There was a problem getting the calories.");
            }
            return total;
        }


        @Override
        protected void onPostExecute(Float aLong) {
            super.onPostExecute(aLong);

            //Total calories burned for that day
            Log.i(TAG, "Total calories: " + aLong);

        }
    }















            //fitbit data







    public void onLoggedIn() {
        Intent intent = new Intent(this,TabActivity.class);
        startActivity(intent);
    }




}