package com.tupelo.wellness.async;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.bean.CaloriesBean;
import com.tupelo.wellness.bean.DistanceBean;
import com.tupelo.wellness.bean.FloorBean;
import com.tupelo.wellness.bean.StepsBean;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.network.NetworkCall;
import com.tupelo.wellness.parcer.Jsonparser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Exchanger;

import cz.msebera.android.httpclient.Header;

//import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 27-09-2015.
 */
public class FitbitTupeloAsync extends AsyncTask<String, Void, String> {

    private Context context;
    String TAG = FitbitTupeloAsync.class.getSimpleName();
    GetterSetter getterSetter;
    Jsonparser jsonparser = null;
    String sessionId = "", userId = "", corpId = "", message = "";
    Cursor cursor;
    DbAdapter dbAdapter;
    ArrayList<CaloriesBean> fitbitCaloriesArray;
    ArrayList<StepsBean> stepsArray;
    ArrayList<FloorBean> fitbitfloorArray=new ArrayList<>();
    ArrayList<DistanceBean> fitbitDistanceArray;

    public FitbitTupeloAsync(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ((TabActivity) context).showProgress(true);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            dbAdapter = DbAdapter.getInstance(context);
            cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

            if (cursor.getCount() > 0) {
                sessionId = cursor.getString(0);
                userId = cursor.getString(1);
                corpId = cursor.getString(3);
            }

            if (params[0].equalsIgnoreCase(DbAdapter.TABLE_NAME_FITBIT)) {
                syncFitbit(params[1],params[2], params[3], params[4]);
                syncDashboard();
            } else if (params[0].equalsIgnoreCase(DbAdapter.TABLE_NAME_JAWBONE)) {
                syncJawbone(params[1]);
                syncDashboard();
            } else if (params[0].equalsIgnoreCase(DbAdapter.TABLE_NAME_MYMO)) {
                syncDashboard();
            } else if (params[0].equalsIgnoreCase(DbAdapter.TABLE_NAME_GARMIN)) {
                syncDashboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

/*        if(context instanceof TabActivity)
            ((TabActivity)context).swipeRefreshDashBoard(false);*/
        Log.e(TAG,"the value of string returned is " + s);
        ((TabActivity) context).showProgress(false);
        if (s.equalsIgnoreCase("0")) {
            ((TabActivity) context).setDataToShow(getterSetter);
        } else {
            ((TabActivity) context).showError();
        }

    }

    private void syncFitbit(String URL, String USER_ID,String TOKEN_TYPE, String ACCESS_TOKEN) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(URL);
            request.addHeader("Authorization", TOKEN_TYPE + " " + ACCESS_TOKEN);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String jsonString = null;
            jsonString = httpclient.execute(request, handler);
//            Log.d("fitbit jsonstring", jsonString);

            if (jsonString != null) {

                Jsonparser jsonparser1 = new Jsonparser(jsonString);
               stepsArray = jsonparser1.getFitbitArray();


                getfitbitcaloriesdata(USER_ID,ACCESS_TOKEN,TOKEN_TYPE);
//                Log.d("tupelo steps update", response);
            } else {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getfitbitcaloriesdata(final String fitbit_userid, final String fitbit_token, final String token_type){
        DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        HashMap<String, String> map= new HashMap<>();
        map.put("Authorization",token_type+" "+fitbit_token);
        String url="https://api.fitbit.com/1/user/"+fitbit_userid+"/activities/tracker/calories/date/"+dateFormat.format(date)+"/7d.json";
        StringRequest stringRequest= new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("volley result.....",response);
                Jsonparser jsonparser1 = new Jsonparser(response);
                 fitbitCaloriesArray = jsonparser1.getFitbitCaloriesArray();
//
           //     String respons = new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepsArray,0.00f);
                getfitbitdistacnedata(fitbit_userid,fitbit_token,token_type);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> map= new HashMap<>();
                map.put("Authorization",token_type+" "+fitbit_token);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void getfitbitdistacnedata(final String fitbit_userid, final String fitbit_token, final String token_type){
        DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        HashMap<String, String> map= new HashMap<>();
        map.put("Authorization",token_type+" "+fitbit_token);
        String url="https://api.fitbit.com/1/user/"+fitbit_userid+"/activities/tracker/distance/date/"+dateFormat.format(date)+"/7d.json";

        StringRequest stringRequest= new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("volley result.....",response);
                Jsonparser jsonparser1 = new Jsonparser(response);
                fitbitDistanceArray = jsonparser1.getFitbitDistanceArray();
                getfitbitfloordata(fitbit_userid,fitbit_token,token_type);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> map= new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");

                map.put("Authorization",token_type+" "+fitbit_token);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getfitbitfloordata(final String fitbit_userid, final String fitbit_token, final String token_type){
        DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        final String imei = new Helper().getImei(context);
//
        final String serial = "666974626974", apitype = "1001";

        cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_FITBIT);

        final String apitoken="";
        if (cursor.getCount() > 0) {
            apitoken = cursor.getString(3);
        }
        AsyncHttpClient client= new AsyncHttpClient();
        client.addHeader("Authorization",token_type+" "+fitbit_token);
        String url="https://api.fitbit.com/1/user/"+fitbit_userid+"/activities/tracker/floors/date/today/7d.json";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("response......",new String(responseBody));


                String string= new String(responseBody);
                try {
                    Jsonparser jsonparser1 = new Jsonparser(string);
                    fitbitfloorArray = jsonparser1.getFitbitFloorArray();



                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                              String respons = new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepsArray,fitbitCaloriesArray,fitbitDistanceArray,fitbitfloorArray);
                            //  Log.d("Fitbitsave to mymo server",respons);
                        }
                    });
                    thread.start();
                }catch (Exception error){
                    error.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("response......",new String(responseBody));
                String string= new String(responseBody);
                try {
                    JSONObject jsonObject= new JSONObject(string);
                    if(jsonObject.has("errors")){
                        String respons = new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepsArray,fitbitCaloriesArray,fitbitDistanceArray);

                    }
                }catch (Exception error){
                    error.printStackTrace();
                }


            }


                }
        );


//        StringRequest stringRequest= new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("volley result.....",response);
//                if(response.contains("activities-tracker")) {
//                    Jsonparser jsonparser1 = new Jsonparser(response);
//                    fitbitfloorArray = jsonparser1.getFitbitFloorArray();
//                }
//                else {
//                }
//                    String imei = new Helper().getImei(context);
////
//                    String serial = "666974626974", apitoken = "", apitype = "1001";
//
//                    cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_FITBIT);
//                    if (cursor.getCount() > 0) {
//                        apitoken = cursor.getString(3);
//                    }
//
//                     String respons = new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepsArray,fitbitCaloriesArray,fitbitDistanceArray,fitbitfloorArray);
//            Log.d("Fitbitsave to mymo server",respons);
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String,String> map= new HashMap<>();
//                map.put("Authorization",token_type+" "+fitbit_token);
//                return map;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    private void syncJawbone(String jsonString) {
        if (jsonString != null) {

            Jsonparser jsonparser = new Jsonparser(jsonString);
            ArrayList<StepsBean> stepsArray = jsonparser.getJawboneArray();

            String imei = new Helper().getImei(context);

            String serial = "6a6177626f6e65", apitoken = "", apitype = "1002";

            cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_JAWBONE);
            if (cursor.getCount() > 0) {
                apitoken = cursor.getString(0);
            }

            String response = new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepsArray);
//            Log.d("tupelo steps update", response);
        } else {
//            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
    }

    private void syncDashboard() {
        String updatetimezone = new NetworkCall().setTimeZoneForUser(userId, sessionId, TimeZone.getDefault().getID());
//        Log.d("timezoneresponse", updatetimezone);

        Calendar calendar = Calendar.getInstance();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        //date= "2015-01-15";

        String response1 = new NetworkCall().getPersonalDashboard(sessionId, userId, corpId, date);
//        Log.d("dashboard", response1);

        jsonparser = new Jsonparser(response1);
        message = jsonparser.getErrorCodePersonalDetail();

//        Log.d("message", message);

        getterSetter = jsonparser.getDashboardInfo(context);
    }
}