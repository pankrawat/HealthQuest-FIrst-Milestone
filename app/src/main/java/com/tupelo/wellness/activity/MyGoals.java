package com.tupelo.wellness.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.R;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.xw.repo.BubbleSeekBar;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin1 on 6/9/17.
 */

public class MyGoals extends Fragment {


    private RelativeLayout stepView;
    private TextView stepTxt;
    private View stepview;
    private BubbleSeekBar seekBar;
    private RelativeLayout myGoalSquare;
    private TextView smallStep;
    private TextView steps;
    private RelativeLayout caloriesView;
    private View view2;
    private TextView caloriesTxt;
    private View view1;
    private BubbleSeekBar seekBarCalories;
    private RelativeLayout myGoalSquare1;
    private TextView smallCalories;
    private TextView calories;
    private RelativeLayout distanceView;
    private View view3;
    private TextView distanceTxt;
    private View view4;
    private BubbleSeekBar seekBarDistance;
    private RelativeLayout myGoalSquare3;
    private TextView smallDistance;
    private TextView distance;
    private RelativeLayout floorView;
    private View view5;
    private TextView floorTxt;
    private View view6;
    private BubbleSeekBar seekBarFloor;
    private RelativeLayout myGoalSquare2;
    private TextView smallFloor;
    private TextView floors;
    private Button save;
    View view;
    SharedPreferences editor;
    RelativeLayout step_layout,calorie_layout,distance_layout,floor_layout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.activity_mygoals, container, false);
        initView(view);
         editor= getActivity().getSharedPreferences("companyprefs",0);

        seekBar.getConfigBuilder().secondTrackColor(Color.parseColor(AppTheme.getInstance().colorPrimary)).build();
        seekBarCalories.getConfigBuilder().secondTrackColor(Color.parseColor(AppTheme.getInstance().colorPrimary)).build();
        seekBarDistance.getConfigBuilder().secondTrackColor(Color.parseColor(AppTheme.getInstance().colorPrimary)).build();
        seekBarFloor.getConfigBuilder().secondTrackColor(Color.parseColor(AppTheme.getInstance().colorPrimary)).build();
        save.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        getUser_Goal();
        //[active-calories, steps, floors-climbed, totaldistance]

        Set<String> actdatashow=editor.getStringSet("deviceset",new HashSet<String>());
       // actdatashow.remove("active-calories");
       // actdatashow.remove("totaldistance");
        if(!actdatashow.contains("steps")){
            step_layout.setVisibility(View.GONE);
        }
        if(!actdatashow.contains("active-calories")){
            calorie_layout.setVisibility(View.GONE);
        }
        if(!actdatashow.contains("totaldistance")){
            distance_layout.setVisibility(View.GONE);
        }
        if(!actdatashow.contains("floors-climbed")){
            floor_layout.setVisibility(View.GONE);
        }

        //SharedPreferences sp= getActivity().getSharedPreferences("MyGoals", Context.MODE_PRIVATE);
//        steps.setText(sp.getString("steps","10000"));
//        seekBar.setProgress(Integer.valueOf(sp.getString("steps","10000")));
//        distance.setText(sp.getString("distance","7000"));
//        seekBarDistance.setProgress(Integer.valueOf(sp.getString("distance","7000")));
//        calories.setText(sp.getString("calories","550"));
//        seekBarCalories.setProgress(Integer.valueOf(sp.getString("calories","550")));
//        floors.setText(sp.getString("floors","15"));
//        seekBarFloor.setProgress(Integer.valueOf(sp.getString("floors","15")));

           seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                           steps.setText(String.valueOf(progress));

            }
            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        seekBarCalories.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                calories.setText(String.valueOf(progress));

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        seekBarDistance.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

                    distance.setText(String.valueOf(progress));



            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        seekBarFloor.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                floors.setText(String.valueOf(progress));

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckConnection.isConnection(getActivity())) {
                   final ProgressDialog pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Please wait...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("responseeee",response);
                            pDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("#error")) {
                                    SharedPreferences.Editor mygoals = getActivity().getSharedPreferences("MyGoals", Context.MODE_PRIVATE).edit();
                                    mygoals.putString("steps", steps.getText().toString());
                                    mygoals.putString("calories", calories.getText().toString());
                                    mygoals.putString("distance", distance.getText().toString());
                                    mygoals.putString("floors", floors.getText().toString());
                                    mygoals.commit();
                                    Toast.makeText(getActivity(), "Goals Updated Successfully.", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getActivity(), "Something Went Wrong, Please Try Again.", Toast.LENGTH_SHORT).show();

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            DbAdapter dbAdapter = DbAdapter.getInstance(getActivity());
                            Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);
                            String sessionId = "", userId = "", corpId = "";
                            if (cursor.getCount() > 0) {
                                sessionId = cursor.getString(0);
                                userId = cursor.getString(1);
                                corpId = cursor.getString(3);
                            }
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("method", "userV2.save_usergoal");
                            Helper helper = new Helper();
                            String time_stamp = helper.getUnixTimestamp();
                            map.put("hash", helper.getHashValue(Constants.API_KEY, Constants.DOMAIN, time_stamp, "userV2.save_usergoal"));
                            map.put("domain_name", Constants.DOMAIN);
                            map.put("domain_time_stamp", time_stamp);
                            map.put("nonce", time_stamp);
                            map.put("sessid", sessionId);
                            map.put("userid", userId);
                            map.put("corpid", corpId);
//
                            map.put("steps", steps.getText().toString());
                            map.put("calories", calories.getText().toString());
                            if(editor.getString("distance","").equals("km"))
                            {
                                Integer dis=Integer.valueOf(distance.getText().toString())*1000;
                                map.put("distance", distance.getText().toString());

                            }else {
                                Integer dis=Integer.valueOf(distance.getText().toString())*1609;
                                map.put("distance", distance.getText().toString());

                            }
                            map.put("floors", floors.getText().toString());
                            Log.e("Param", "params are " + map.toString());

                            return map;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(stringRequest);

                }else{
                    Toast.makeText(getActivity(), "Some Network Error Occured, Please Try Again!!", Toast.LENGTH_SHORT).show();

                }

            }
        });
        return view;
    }


    private void initView(View view) {
        step_layout=(RelativeLayout)view.findViewById(R.id.step_layout);
        calorie_layout=(RelativeLayout)view.findViewById(R.id.calories_layout);
        distance_layout=(RelativeLayout)view.findViewById(R.id.distance_layout);
        floor_layout=(RelativeLayout)view.findViewById(R.id.floor_layout);
        stepView = (RelativeLayout) view.findViewById(R.id.step_view);
        stepTxt = (TextView) view.findViewById(R.id.step_txt);
        stepview = (View) view.findViewById(R.id.view);
        seekBar = (BubbleSeekBar) view.findViewById(R.id.seek_bar);
        myGoalSquare = (RelativeLayout) view.findViewById(R.id.my_goal_square);
        smallStep = (TextView) view.findViewById(R.id.small_step);
        steps = (TextView) view.findViewById(R.id.steps);
        caloriesView = (RelativeLayout) view.findViewById(R.id.calories_view);
        view2 = (View) view.findViewById(R.id.view2);
        caloriesTxt = (TextView) view.findViewById(R.id.calories_txt);
        view1 = (View) view.findViewById(R.id.view1);
        seekBarCalories = (BubbleSeekBar) view.findViewById(R.id.seek_bar_calories);
        myGoalSquare1 = (RelativeLayout) view.findViewById(R.id.my_goal_square1);
        smallCalories = (TextView) view.findViewById(R.id.small_calories);
        calories = (TextView) view.findViewById(R.id.calories);
        distanceView = (RelativeLayout) view.findViewById(R.id.distance_view);
        view3 = (View) view.findViewById(R.id.view3);
        distanceTxt = (TextView) view.findViewById(R.id.distance_txt);
        view4 = (View) view.findViewById(R.id.view4);
        seekBarDistance = (BubbleSeekBar) view.findViewById(R.id.seek_bar_distance);
        myGoalSquare3 = (RelativeLayout) view.findViewById(R.id.my_goal_square3);
        smallDistance = (TextView) view.findViewById(R.id.small_distance);
        distance = (TextView) view.findViewById(R.id.distance);
        floorView = (RelativeLayout) view.findViewById(R.id.floor_view);
        view5 = (View) view.findViewById(R.id.view5);
        floorTxt = (TextView) view.findViewById(R.id.floor_txt);
        view6 = (View) view.findViewById(R.id.view6);
        seekBarFloor = (BubbleSeekBar) view.findViewById(R.id.seek_bar_floor);
        myGoalSquare2 = (RelativeLayout) view.findViewById(R.id.my_goal_square2);
        smallFloor = (TextView) view.findViewById(R.id.small_floor);
        floors = (TextView) view.findViewById(R.id.floors);
        save=(Button)view.findViewById(R.id.save);
    }



//    private void getUser_Goal(){
//        if (CheckConnection.isConnection(getActivity())) {
//            final ProgressDialog pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.e("responseeee",response);
//                    pDialog.dismiss();
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        if(!jsonObject.getBoolean("#error")){
//                            JSONObject object= jsonObject.getJSONObject("#data");
//                            JSONObject dailygoal= object.getJSONObject("dailygoal");
//                            steps.setText(String.valueOf(dailygoal.getInt("steps")));
//                            seekBar.setProgress(dailygoal.getInt("steps"));
//                            distance.setText(String.valueOf(dailygoal.getInt("distance")));
//                            seekBarDistance.setProgress(dailygoal.getInt("distance"));
//                            calories.setText(String.valueOf(dailygoal.getInt("calories")));
//                            seekBarCalories.setProgress(dailygoal.getInt("calories"));
//                            floors.setText(String.valueOf(dailygoal.getInt("floors")));
//                            seekBarFloor.setProgress(dailygoal.getInt("floors"));
//                        }else{
//                            Toast.makeText(getActivity(), "Some Went Wrong, Please Try Again!!", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    pDialog.dismiss();
//
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    DbAdapter dbAdapter = DbAdapter.getInstance(getActivity());
//                    Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);
//                    String sessionId = "", userId = "", corpId = "";
//                    if (cursor.getCount() > 0) {
//                        sessionId = cursor.getString(0);
//                        userId = cursor.getString(1);
//                        corpId = cursor.getString(3);
//                    }
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put("method", "userV2.get_usergoal");
//                    Helper helper = new Helper();
//                    String time_stamp = helper.getUnixTimestamp();
//                    map.put("hash", helper.getHashValue(Constants.API_KEY, Constants.DOMAIN, time_stamp, "userV2.get_usergoal"));
//                    map.put("domain_name", Constants.DOMAIN);
//                    map.put("domain_time_stamp", time_stamp);
//                    map.put("nonce", time_stamp);
//                    map.put("sessid", sessionId);
//                    map.put("userid", userId);
//                    map.put("corpid", corpId);
////
////
//                    Log.e("Param", "params are " + map.toString());
//
//                    return map;
//                }
//            };
//            AppController.getInstance().addToRequestQueue(stringRequest);
//
//        }else{
//            Toast.makeText(getActivity(), "Some Network Error Occured, Please Try Again!!", Toast.LENGTH_SHORT).show();
//
//        }
//    }

    private void getUser_Goal(){
        SharedPreferences mygoals = getActivity().getSharedPreferences("MyGoals", Context.MODE_PRIVATE);

        steps.setText(String.valueOf(mygoals.getString("steps","10000")));
        seekBar.setProgress(Integer.valueOf(mygoals.getString("steps","10000")));
        //seekBarDistance.getConfigBuilder().progress(Integer.valueOf(mygoals.getString("distance","7000")));
        calories.setText(mygoals.getString("calories","550"));
        seekBarCalories.setProgress(Integer.valueOf(mygoals.getString("calories","550")));
        floors.setText(mygoals.getString("floors","15"));
        seekBarFloor.setProgress(Integer.valueOf(mygoals.getString("floors","15")));

        if(editor.getString("distance","").equals("km"))
        {
            TextView total_distance_txt=(TextView)view.findViewById(R.id.distance_total_txt);
            total_distance_txt.setText("80");
            smallDistance.setText("km");
            seekBarDistance.getConfigBuilder().min(0).max(80).build();
            Log.e("valueeeee",""+Integer.valueOf(mygoals.getString("distance","7000"))/1000);
            seekBarDistance.setProgress(Integer.valueOf(mygoals.getString("distance","7000"))/1000);
            distance.setText(String.valueOf(seekBarDistance.getProgress()));
        }else
        {
           TextView total_distance_txt=(TextView)view.findViewById(R.id.distance_total_txt);
            total_distance_txt.setText("50");
            smallDistance.setText("mi");
            seekBarDistance.getConfigBuilder().min(0).max(50).build();
            seekBarDistance.setProgress(Double.valueOf(Integer.valueOf(mygoals.getString("distance","7000"))*0.000621371).intValue());
            distance.setText(String.valueOf(seekBarDistance.getProgress()));

        }
    }
}
