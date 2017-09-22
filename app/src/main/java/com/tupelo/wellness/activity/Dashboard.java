package com.tupelo.wellness.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.repacked.stringtemplate.v4.ST;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.j256.ormlite.stmt.query.In;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Transformation;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.CFlipper;
import com.tupelo.wellness.DrawCanvasDot;
import com.tupelo.wellness.FitnessHelper;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.GetAuthToken;
import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.R;
import com.tupelo.wellness.RoundCornerTransformation;
import com.tupelo.wellness.SHealthIntegration.SetUpSHealth;
import com.tupelo.wellness.async.FitbitTupeloAsync;
import com.tupelo.wellness.bean.CaloriesBean;
import com.tupelo.wellness.bean.DistanceBean;
import com.tupelo.wellness.bean.InfoStreamBean;
import com.tupelo.wellness.bean.StepsBean;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.jawbone.oauth.OauthUtils;
import com.tupelo.wellness.jawbone.oauth.OauthWebViewActivity;
import com.tupelo.wellness.network.NetworkCall;
import com.tupelo.wellness.parcer.Jsonparser;
import com.tupelo.wellness.view.CircleAngleAnimation;
import com.tupelo.wellness.view.NewTextView;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;



/**
 * Created by Abhishek Singh Arya on 09-09-2015.
 */
public class Dashboard extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final int MY_SOCKET_TIMEOUT_MS = 50000;
    private static GoogleApiClient fitnessClient;
    public DrawCanvasDot dot;
    Context context;
    String TAG = Dashboard.class.getSimpleName();
    ProgressDialog pDialog;
    boolean isRefresh = false;
    String USER_ID, TOKEN_TYPE, EXPIRES_IN, ACCESS_TOKEN;
    TextView tv_steps_count, tv_yesterday, tv_best_single, tv_total, tv_yesterday_steps, tv_best_steps, tv_total_steps;
    View v;
    int size;
    CFlipper cFlipper;
    LinearLayout dotLayout;
    int count = 0;
    String service;
    int i = 0;
    SharedPreference sharedPreference;
    FitnessHelper fitnessHelper;
    ArrayList<StepsBean> padoSteps = new ArrayList<StepsBean>();
    public SwipeRefreshLayout swipeRefreshLayout;
    private Fonts fonts;
    private float initialX;
    private boolean authInProgress = false;
    String payload_title, payload_text, payload_type;
    ArrayList<CaloriesBean> caloriesBeanArrayList = new ArrayList<>();
    ArrayList<DistanceBean> distanceBeanArrayList = new ArrayList<>();
    private LinearLayout caloriesLayout;
    private ImageView yesterdayCalImg;
    private NewTextView yesterdayCal, step_label;
    private NewTextView yesterdayCalTxt;
    private ImageView bestCalImg;
    private NewTextView bestCal;
    private NewTextView bestCalTxt;
    private ImageView totalCalImg;
    private NewTextView totalCal;
    private NewTextView totalCalTxt;
    private LinearLayout distanceLayout;
    private ImageView yesterdayDistanceImg;
    private NewTextView yesterdayDistance;
    private NewTextView yesterdayDistanceTxt;
    private ImageView bestDistanceImg;
    private NewTextView bestDistance;
    private NewTextView bestDistanceTxt;
    private ImageView totalDistanceImg;
    private NewTextView totalDistance;
    private NewTextView totalDistanceTxt;
    private LinearLayout floorLayout, stepLayout;
    private ImageView yesterdayFloorImg;
    private NewTextView yesterdayFloor;
    private NewTextView yesterdayFloorTxt;
    private ImageView bestFloorImg;
    private NewTextView bestFloor;
    private NewTextView bestFloorTxt;
    private ImageView totalFloorImg;
    private NewTextView totalFloor;
    private NewTextView totalFloorTxt, unit;
    private TextView yesterday_cal_statictxt;
    private DonutProgress ringProgressBar;
    private ImageView left, right, img;
    static int slide = 0;
    private View view;
    private GetterSetter getterSetter = new GetterSetter();
    SharedPreferences editor;
    Calendar cal;
    Date now;
    long startTime,endTime;
    DateFormat dateFormat,timeformat;
    DataReadRequest readdistanceRequest,readCaloriesRequest;
    private static HashMap<String, Integer> getMoveEventsListRequestParams() {
        HashMap<String, Integer> queryHashMap = new HashMap<String, Integer>();

        //uncomment to add as needed parameters
//        queryHashMap.put("date", "<insert-date>");
//        queryHashMap.put("page_token", "<insert-page-token>");
//        queryHashMap.put("start_time", "<insert-time>");
//        queryHashMap.put("end_time", "<insert-time>");
//        queryHashMap.put("updated_after", "<insert-time>");

        return queryHashMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_dashboard, container, false);
        initView(v);
        setHasOptionsMenu(false);



        final Transformation transformation = new RoundCornerTransformation(8, 5);
        RelativeLayout relativeTop = (RelativeLayout) v.findViewById(R.id.relativeTop);
        relativeTop.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
         editor= getActivity().getSharedPreferences("companyprefs",0);


        context = getActivity();
        initViews();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        /*
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.plz_wt));
        dialog.setCancelable(true);*/
            fonts = new Fonts(context);
        sharedPreference = SharedPreference.getInstance(context);
        sharedPreference.putBoolean(Constants.IS_SIGNUP_COMPLETE, true);
        Bundle bundle = this.getArguments();
        service = bundle.getString(Constants.SERVICE);
        left.setVisibility(View.INVISIBLE);

        Log.e(TAG, "the service is 1 " + service);

        if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_MYMO)) {
            syncDashboardFromMymo();
        } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_GARMIN)) {
            syncDashboardFromGarmin();
        } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_JAWBONE)) {
            IonJawbone();
        } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_SHEALTH)) {
            SetUpSHealth.getInstance().init(getActivity(), 1);
        } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_PEDOMETER)) {
            if (new Helper().isAppInstalled(getActivity())) {
                if (CheckConnection.isConnection(context)) {
                    fitnessHelper = FitnessHelper.getInstance((TabActivity) context);
                    fitnessHelper.fitnessClientBuilder();
                    showProgressBar(true);

                } else {

                    swipeRefreshLayout.setRefreshing(false);
                    new Helper().showNoInternetToast(context);
                }

            } else {
                DbAdapter dbAdapter = DbAdapter.getInstance(context);
                dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PEDOMETER);
                sharedPreference.remove(Constants.IS_RECORDING_STARTED);
                Intent intent = new Intent(context, ChooseTracker.class);
                context.startActivity(intent);
                ((TabActivity) context).finish();
            }
        } else {
            service = DbAdapter.TABLE_NAME_FITBIT;
            GetAuthToken getAuthToken = (GetAuthToken) bundle.getSerializable(Constants.GET_AUTH_TOKEN);
            USER_ID = getAuthToken.getUSER_ID();
            TOKEN_TYPE = getAuthToken.getTOKEN_TYPE();
            EXPIRES_IN = getAuthToken.getEXPIRES_IN();
            ACCESS_TOKEN = getAuthToken.getACCESS_TOKEN();
            syncDashboardFromFitbit();
        }

       final Set<String> actdataset= editor.getStringSet("deviceset", new HashSet<String>());
               // actdataset.remove("totaldistance");
              //  actdataset.remove("active-calories");
                // actdataset.remove("floors-climbed");

        Log.d("actdatashow......", actdataset.toString());
        //[active-calories, steps, floors-climbed, totaldistance]
            if(actdataset.size()==1){
                right.setVisibility(View.INVISIBLE);
                left.setVisibility(View.INVISIBLE);
            }

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slide == 0) {
                    if(actdataset.contains("active-calories")) {
                        slide = 1;
                        left.setVisibility(View.VISIBLE);

                        caloriesLayout.setVisibility(View.VISIBLE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.GONE);
                        floorLayout.setVisibility(View.GONE);
                        img.setBackgroundResource(R.mipmap.calories);
                        step_label.setText("CALORIES");
                        unit.setText("");
                        tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_calories()).intValue()));
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Double.valueOf(tv_steps_count.getText().toString()).intValue());
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                    }else if(actdataset.contains("totaldistance"))
                    {
                        left.setVisibility(View.VISIBLE);

                        slide = 2;
                        step_label.setText("DISTANCE");
                        img.setBackgroundResource(R.mipmap.distance);
                        if(editor.getString("distance","").equals("km"))
                        {
                            unit.setText("km");
                            String km = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getToday_distance()) / 1000);
                            tv_steps_count.setText(String.valueOf(km));
                        }else
                        {
                            unit.setText("mi");
                            String km = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getToday_distance()) *0.000621371);
                            tv_steps_count.setText(String.valueOf(km));
                        }
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Double.valueOf(tv_steps_count.getText().toString()).intValue());
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                        caloriesLayout.setVisibility(View.GONE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.VISIBLE);
                        floorLayout.setVisibility(View.GONE);
                    }else if(actdataset.contains("floors-climbed")){
                        left.setVisibility(View.VISIBLE);
                        right.setVisibility(View.INVISIBLE);

                        slide = 3;
                        step_label.setText("FLOORS");
                        img.setBackgroundResource(R.mipmap.stairs);
                        unit.setText("");

                        tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_floors()).intValue()));
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Double.valueOf(tv_steps_count.getText().toString()).intValue());
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                        caloriesLayout.setVisibility(View.GONE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.GONE);
                        floorLayout.setVisibility(View.VISIBLE);
                       // right.setVisibility(View.INVISIBLE);
                    }
                    else {
                        right.setVisibility(View.INVISIBLE);
                    }
                } else if (slide == 1) {
                    if(actdataset.contains("totaldistance")) {
                        left.setVisibility(View.VISIBLE);

                        slide = 2;
                        step_label.setText("DISTANCE");
                        img.setBackgroundResource(R.mipmap.distance);
                        if (editor.getString("distance", "").equals("km")) {
                            unit.setText("km");
                            String km = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getToday_distance()) / 1000);
                            tv_steps_count.setText(String.valueOf(km));
                        } else {
                            unit.setText("mi");
                            String km = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getToday_distance()) * 0.000621371);
                            tv_steps_count.setText(String.valueOf(km));
                        }
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Double.valueOf(tv_steps_count.getText().toString()).intValue());
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                        caloriesLayout.setVisibility(View.GONE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.VISIBLE);
                        floorLayout.setVisibility(View.GONE);
                    }else if(actdataset.contains("floors-climbed"))
                    {
                        left.setVisibility(View.VISIBLE);
                        right.setVisibility(View.INVISIBLE);

                        slide = 3;
                        step_label.setText("FLOORS");
                        img.setBackgroundResource(R.mipmap.stairs);
                        unit.setText("");

                        tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_floors()).intValue()));
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Double.valueOf(tv_steps_count.getText().toString()).intValue());
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                        caloriesLayout.setVisibility(View.GONE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.GONE);
                        floorLayout.setVisibility(View.VISIBLE);
                        //right.setVisibility(View.INVISIBLE);
                    } else {
                        right.setVisibility(View.INVISIBLE);
                    }
                } else if (slide == 2) {
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.INVISIBLE);

                    slide = 3;
                    step_label.setText("FLOORS");
                    img.setBackgroundResource(R.mipmap.stairs);
                    unit.setText("");

                    tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_floors()).intValue()));
                    CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Double.valueOf(tv_steps_count.getText().toString()).intValue());
                    animation.setDuration(1000);
                    ringProgressBar.startAnimation(animation);
                    caloriesLayout.setVisibility(View.GONE);
                    stepLayout.setVisibility(View.GONE);
                    distanceLayout.setVisibility(View.GONE);
                    floorLayout.setVisibility(View.VISIBLE);
                    right.setVisibility(View.INVISIBLE);

                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //right.setVisibility(View.VISIBLE);

                if (slide == 1) {
                    right.setVisibility(View.VISIBLE);
                    slide = 0;
                    step_label.setText("STEPS");
                    img.setBackgroundResource(R.mipmap.steps);
                    unit.setText("");

                    left.setVisibility(View.INVISIBLE);

                    caloriesLayout.setVisibility(View.GONE);
                    stepLayout.setVisibility(View.VISIBLE);
                    distanceLayout.setVisibility(View.GONE);
                    floorLayout.setVisibility(View.GONE);
                    tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_steps()).intValue()));
                    CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Integer.valueOf(tv_steps_count.getText().toString()));
                    animation.setDuration(1000);
                    ringProgressBar.startAnimation(animation);
                } else if (slide == 2) {
                    if(actdataset.contains("active-calories")) {
                        right.setVisibility(View.VISIBLE);

                        slide = 1;
                        step_label.setText("CALORIES");
                        img.setBackgroundResource(R.mipmap.calories);
                        unit.setText(" ");

                        tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_calories()).intValue()));
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Integer.valueOf(tv_steps_count.getText().toString()));
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                        caloriesLayout.setVisibility(View.VISIBLE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.GONE);
                        floorLayout.setVisibility(View.GONE);
                    }else if(actdataset.contains("steps"))
                    {
                        right.setVisibility(View.VISIBLE);

                        slide = 0;
                        step_label.setText("STEPS");
                        img.setBackgroundResource(R.mipmap.steps);
                        unit.setText("");

                        // left.setVisibility(View.INVISIBLE);

                        caloriesLayout.setVisibility(View.VISIBLE);
                        stepLayout.setVisibility(View.VISIBLE);
                        distanceLayout.setVisibility(View.GONE);
                        floorLayout.setVisibility(View.GONE);
                        tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_steps()).intValue()));
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Integer.valueOf(tv_steps_count.getText().toString()));
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                    }

                } else if (slide == 3) {
                    if(actdataset.contains("totaldistance")) {
                        right.setVisibility(View.VISIBLE);

                        slide = 2;
                        step_label.setText("DISTANCE");
                        img.setBackgroundResource(R.mipmap.distance);
                        SharedPreferences editor = getActivity().getSharedPreferences("companyprefs", 0);

                        if (editor.getString("distance", "").equals("km")) {
                            unit.setText("km");
                            String km = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getToday_distance()) / 1000);
                            tv_steps_count.setText(String.valueOf(km));
                        } else {
                            unit.setText("mi");
                            String km = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getToday_distance()) * 0.000621371);
                            tv_steps_count.setText(String.valueOf(km));
                        }
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Double.valueOf(tv_steps_count.getText().toString()).intValue());
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);

                        caloriesLayout.setVisibility(View.GONE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.VISIBLE);
                        floorLayout.setVisibility(View.GONE);
                    } else if(actdataset.contains("active-calories"))
                    {
                        right.setVisibility(View.VISIBLE);

                        slide = 1;
                        step_label.setText("CALORIES");
                        img.setBackgroundResource(R.mipmap.calories);
                        unit.setText(" ");

                        tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_calories()).intValue()));
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Integer.valueOf(tv_steps_count.getText().toString()));
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                        caloriesLayout.setVisibility(View.VISIBLE);
                        stepLayout.setVisibility(View.GONE);
                        distanceLayout.setVisibility(View.GONE);
                        floorLayout.setVisibility(View.GONE);
                    }else if(actdataset.contains("steps"))
                    {
                        right.setVisibility(View.VISIBLE);

                        slide = 0;
                        step_label.setText("STEPS");
                        img.setBackgroundResource(R.mipmap.steps);
                        unit.setText("");

                       // left.setVisibility(View.INVISIBLE);

                        caloriesLayout.setVisibility(View.GONE);
                        stepLayout.setVisibility(View.VISIBLE);
                        distanceLayout.setVisibility(View.GONE);
                        floorLayout.setVisibility(View.GONE);
                        tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_steps()).intValue()));
                        CircleAngleAnimation animation = new CircleAngleAnimation(ringProgressBar, Integer.valueOf(tv_steps_count.getText().toString()));
                        animation.setDuration(1000);
                        ringProgressBar.startAnimation(animation);
                    }
                }

            }

        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public void showDialog1() {

//        openPopup = true;
//        setContentView(R.layout.popup);

        RelativeLayout promptView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.announcement, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(promptView);
        final AlertDialog alertDialog = builder.create();

        TextView title = (TextView) promptView.findViewById(R.id.congo);
        title.setText(payload_title.toString());


        TextView des = (TextView) promptView.findViewById(R.id.dis);
        des.setText(payload_text.toString());


        LinearLayout ok = (LinearLayout) promptView.findViewById(R.id.ok);
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


    public void showDialog2() {

//        openPopup = true;
//        setContentView(R.layout.popup);

        RelativeLayout promptView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.winner, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(promptView);
        final AlertDialog alertDialog = builder.create();

        TextView title = (TextView) promptView.findViewById(R.id.congo);
        title.setText(payload_title.toString());
        TextView des = (TextView) promptView.findViewById(R.id.dis);
        des.setText(payload_text.toString());

        ImageView ok = (ImageView) promptView.findViewById(R.id.close);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        ImageView fb = (ImageView) promptView.findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callShareIntent(getActivity(), payload_text, payload_title);
            }
        });


        ImageView tw = (ImageView) promptView.findViewById(R.id.tw);
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callShareIntent(getActivity(), payload_text, payload_title);
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

//    private void Jawbone() {
//        DbAdapter dbAdapter = DbAdapter.getInstance(context);
//        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_JAWBONE);
//        if (cursor.getCount() > 0) {
//            String ACCESS_TOKEN = cursor.getString(0);
//            if (ACCESS_TOKEN != null) {
//                ApiManager.getRequestInterceptor().setAccessToken(ACCESS_TOKEN);
//            }
//        }
//        ApiManager.getRestApiInterface().getMoveEventsList(
//                Constants.API_VERSION_STRING,
//                getMoveEventsListRequestParams(),
//                genericCallbackListener);
//    }

    //    private void refreshJawboneToken() {
//        DbAdapter dbAdapter = DbAdapter.getInstance(context);
//        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_JAWBONE);
//        if (cursor.getCount() > 0) {
//            String ACCESS_TOKEN = cursor.getString(0);
//            if (ACCESS_TOKEN != null) {
//                ApiManager.getRequestInterceptor().setAccessToken(ACCESS_TOKEN);
//            }
//        }
//        ApiManager.getRestApiInterface().getRefreshToken(
//                Constants.API_VERSION_STRING,
//                Constants.CLIENT_SECRET_KEY,
//                call);
//    }
    public static void callShareIntent(Activity activity, String shareContent, String subject) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        activity.startActivity(Intent.createChooser(sharingIntent, "Refer Friend via"));
    }

    private void syncDashboardFromFitbit() {

        Log.e(TAG, "sync db fitbit inside");
        //Calendar calendar = Calendar.getInstance();
        //String date = calendar.get(Calendar.YEAR) + "-" + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        if (CheckConnection.isConnection(context)) {
            new FitbitTupeloAsync(context).execute(DbAdapter.TABLE_NAME_FITBIT, "https://api.fitbit.com/1/user/" + USER_ID + "/activities/tracker/steps/date/today/7d.json", USER_ID, TOKEN_TYPE, ACCESS_TOKEN);
           // getInfoStreamData();
            //new InfoStreamAsync(context).execute();
        } else {

            swipeRefreshLayout.setRefreshing(false);
            new Helper().showNoInternetToast(context);
        }
    }

    private void syncDashboardFromMymo() {
        if (CheckConnection.isConnection(context)) {
            new FitbitTupeloAsync(context).execute(DbAdapter.TABLE_NAME_MYMO);
          ///  getInfoStreamData();
            //new InfoStreamAsync(context).execute();
        } else {

            swipeRefreshLayout.setRefreshing(false);
            new Helper().showNoInternetToast(context);
        }
    }

    private void syncDashboardFromGarmin() {
        if (CheckConnection.isConnection(context)) {
            new FitbitTupeloAsync(context).execute(DbAdapter.TABLE_NAME_GARMIN);
          //  getInfoStreamData();
            //new InfoStreamAsync(context).execute();
        } else {

            swipeRefreshLayout.setRefreshing(false);
            new Helper().showNoInternetToast(context);
        }
    }

    public void syncDashboardFromPado() {
        if (CheckConnection.isConnection(context)) {
            new FitbitTupeloAsync(context).execute(DbAdapter.TABLE_NAME_MYMO);
            // fitnessHelper.getdata();

          //  getInfoStreamData();
            //new InfoStreamAsync(context).execute();
        } else {

            swipeRefreshLayout.setRefreshing(false);
            new Helper().showNoInternetToast(context);
        }
    }

    private void syncDashboardFromJawbone(String jsonString) {
        if (CheckConnection.isConnection(context)) {
            new FitbitTupeloAsync(context).execute(DbAdapter.TABLE_NAME_JAWBONE, jsonString);

          //  getInfoStreamData();
            // new InfoStreamAsync(context).execute();
        } else {

            swipeRefreshLayout.setRefreshing(false);
            new Helper().showNoInternetToast(context);
        }
    }

    public void syncDashboardFromSHealth() {
        if (CheckConnection.isConnection(context)) {
            new FitbitTupeloAsync(context).execute(DbAdapter.TABLE_NAME_SHEALTH);

           // getInfoStreamData();
        } else {

            swipeRefreshLayout.setRefreshing(false);
            new Helper().showNoInternetToast(context);
        }
    }

    private void initViews() {
        step_label = (NewTextView) v.findViewById(R.id.steps_label);
        left = (ImageView) v.findViewById(R.id.left);
        right = (ImageView) v.findViewById(R.id.right);
        ringProgressBar = (DonutProgress) v.findViewById(R.id.ringbar);
        tv_steps_count = (TextView) v.findViewById(R.id.tv_steps_count);
        stepLayout = (LinearLayout) v.findViewById(R.id.linearLayout2);
        ColorFilter filter = new PorterDuffColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_OUT);

        ImageView step_yesterday = (ImageView) v.findViewById(R.id.iv_yesterday);
        ImageView best_single_steps = (ImageView) v.findViewById(R.id.iv_best_single);
        ImageView total_steps = (ImageView) v.findViewById(R.id.iv_total_steps);
        step_yesterday.setColorFilter(filter);
        best_single_steps.setColorFilter(filter);
        total_steps.setColorFilter(filter);
        //  tv_steps_count.setTypeface(fonts.getTypefaceNormal());

        //  tv_steps_count.setTypeface(fonts.getTypefaceNormal());

        tv_yesterday = (TextView) v.findViewById(R.id.tv_yesterday);
        //  tv_yesterday.setTypeface(fonts.getTypefaceBold());

        tv_best_single = (TextView) v.findViewById(R.id.tv_best_single);

        //  tv_best_single.setTypeface(fonts.getTypefaceBold());

        tv_total = (TextView) v.findViewById(R.id.tv_total);

        //  tv_total.setTypeface(fonts.getTypefaceBold());

        tv_yesterday_steps = (TextView) v.findViewById(R.id.tv_yesterday_steps);
        //  tv_yesterday_steps.setTypeface(fonts.getTypefaceLite());
        tv_yesterday_steps.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        tv_best_steps = (TextView) v.findViewById(R.id.tv_best_steps);
        // tv_best_steps.setTypeface(fonts.getTypefaceLite());
        tv_best_steps.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        tv_total_steps = (TextView) v.findViewById(R.id.tv_total_steps);
        //  tv_total_steps.setTypeface(fonts.getTypefaceLite());
        tv_total_steps.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        cFlipper = (CFlipper) v.findViewById(R.id.flipper);
        cFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return true;
            }
        });
        cFlipper.setReference(this);

        dotLayout = (LinearLayout) v.findViewById(R.id.dotLayout);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayoutDash);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void startRecording() {
        if (sharedPreference.getBoolean(Constants.IS_RECORDING_STARTED, false)) {
            getPadometerSteps();
        } else {
            sharedPreference.putBoolean(Constants.IS_RECORDING_STARTED, true);
            fitnessHelper.subscribeRecording();
        }
    }

    public void getPadometerSteps() {
        pDialog.show();
        long startTime, endTime;
        Calendar calendar = Calendar.getInstance();
        endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        calendar.add(Calendar.HOUR_OF_DAY, -(calendar.get(Calendar.HOUR_OF_DAY)));
        calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE)));
        calendar.add(Calendar.SECOND, -(calendar.get(Calendar.SECOND)));
        startTime = calendar.getTimeInMillis();

//        Log.d("st " + startTime, "et " + endTime);
        if (CheckConnection.isConnection(context)) {
            new PadometerDistanceAsync().execute();
        } else {

            swipeRefreshLayout.setRefreshing(false);

            new Helper().showNoInternetToast(context);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_OAUTH) {
            fitnessHelper.setAuthInProgress(false);
            if (resultCode == Activity.RESULT_OK) {
                if (!fitnessHelper.isConnecting() && !fitnessHelper.isConnected()) {
                    fitnessHelper.connect();
                }
            } else {
                DbAdapter dbAdapter = DbAdapter.getInstance(context);
                dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PEDOMETER);
                Intent intent = new Intent(context, ChooseTracker.class);
                context.startActivity(intent);
                ((TabActivity) context).finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
        //dialog.dismiss();
    }

    public void setDataToList(GetterSetter getterSetter) {
        this.getterSetter = getterSetter;
        if (slide == 0) {


            ringProgressBar.setMax(Integer.valueOf(getterSetter.getTotal_steps()));
            ringProgressBar.setProgress(Integer.valueOf(getterSetter.getToday_steps()));
            tv_steps_count.setText(getterSetter.getToday_steps());

        } else if (slide == 1) {
            ringProgressBar.setMax(Double.valueOf(getterSetter.getTotal_calories()).intValue());
            ringProgressBar.setProgress(Double.valueOf(getterSetter.getToday_calories()).intValue());
            tv_steps_count.setText(String.valueOf(Double.valueOf(getterSetter.getToday_calories()).intValue()));
        } else if (slide == 2) {
            ringProgressBar.setMax(Double.valueOf(getterSetter.getTotal_distance()).intValue());
            ringProgressBar.setProgress(Double.valueOf(getterSetter.getToday_distance()).intValue());
            tv_steps_count.setText(String.valueOf(Integer.valueOf(getterSetter.getToday_distance())));
        } else if (slide == 3) {
            ringProgressBar.setMax(Integer.valueOf(getterSetter.getTotal_floors()));
            ringProgressBar.setProgress(Integer.valueOf(getterSetter.getToday_floors()));
            tv_steps_count.setText(getterSetter.getToday_floors());
        }
        swipeRefreshLayout.setRefreshing(true);
        tv_yesterday_steps.setText(getterSetter.getYesterday_steps());
        tv_best_steps.setText(getterSetter.getBest_single());
        tv_total_steps.setText(getterSetter.getTotal_steps());
        SharedPreferences editor= getActivity().getSharedPreferences("companyprefs",0);

        if(editor.getString("distance","").equals("km"))
        {
            String ydistance = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getYesterday_distance()) / 1000);
            String bdistance = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getBest_single_distance()) / 1000);
            String tdistance = new DecimalFormat("#").format(Double.valueOf(getterSetter.getTotal_distance()) / 1000);

            yesterdayDistance.setText(ydistance);
            bestDistance.setText(bdistance);
            totalDistance.setText(tdistance);
        }else
        {
            String ydistance = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getYesterday_distance()) *0.000621371);
            String bdistance = new DecimalFormat("#.##").format(Double.valueOf(getterSetter.getBest_single_distance())*0.000621371);
            String tdistance = new DecimalFormat("#").format(Double.valueOf(getterSetter.getTotal_distance()) *0.000621371);

            yesterdayDistance.setText(ydistance);
            bestDistance.setText(bdistance);
            totalDistance.setText(tdistance);
        }
        yesterdayCal.setText(String.valueOf(Double.valueOf(getterSetter.getYesterday_calories()).intValue()));
        bestCal.setText(String.valueOf(Double.valueOf(getterSetter.getBest_single_calories()).intValue()));
        totalCal.setText(String.valueOf(Double.valueOf(getterSetter.getTotal_calories()).intValue()));



        yesterdayFloor.setText(getterSetter.getYesterday_floors());
        bestFloor.setText(getterSetter.getBest_single_floors());
        totalFloor.setText(getterSetter.getTotal_floors());


        tv_steps_count.setTypeface(fonts.getTypefaceBold());
        tv_yesterday_steps.setTypeface(fonts.getTypefaceNormal());
        tv_best_steps.setTypeface(fonts.getTypefaceNormal());
        tv_total_steps.setTypeface(fonts.getTypefaceNormal());

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }


    private void initView(View v) {
        yesterday_cal_statictxt=(TextView)v.findViewById(R.id.yesterday_cal_statictxt);
        img = (ImageView) v.findViewById(R.id.img);
        unit = (NewTextView) v.findViewById(R.id.unit);
        caloriesLayout = (LinearLayout) v.findViewById(R.id.calories_layout);
        yesterdayCalImg = (ImageView) v.findViewById(R.id.yesterday_cal_img);
        ColorFilter filter = new PorterDuffColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_OUT);
        yesterdayCalImg.setColorFilter(filter);
        yesterdayCal = (NewTextView) v.findViewById(R.id.yesterday_cal);
        yesterdayCal.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        yesterday_cal_statictxt.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        yesterdayCalTxt = (NewTextView) v.findViewById(R.id.yesterday_cal_txt);
        bestCalImg = (ImageView) v.findViewById(R.id.best_cal_img);
        bestCalImg.setColorFilter(filter);
        bestCal = (NewTextView) v.findViewById(R.id.best_cal);
        bestCal.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        bestCalTxt = (NewTextView) v.findViewById(R.id.best_cal_txt);
        totalCalImg = (ImageView) v.findViewById(R.id.total_cal_img);
        totalCalImg.setColorFilter(filter);
        totalCal = (NewTextView) v.findViewById(R.id.total_cal);
        totalCal.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        totalCalTxt = (NewTextView) v.findViewById(R.id.total_cal_txt);
        distanceLayout = (LinearLayout) v.findViewById(R.id.distance_layout);
        yesterdayDistanceImg = (ImageView) v.findViewById(R.id.yesterday_distance_img);
        yesterdayDistanceImg.setColorFilter(filter);
        yesterdayDistance = (NewTextView) v.findViewById(R.id.yesterday_distance);
        yesterdayDistance.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        yesterdayDistanceTxt = (NewTextView) v.findViewById(R.id.yesterday_distance_txt);
        bestDistanceImg = (ImageView) v.findViewById(R.id.best_distance_img);
        bestDistanceImg.setColorFilter(filter);
        bestDistance = (NewTextView) v.findViewById(R.id.best_distance);
        bestDistance.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        bestDistanceTxt = (NewTextView) v.findViewById(R.id.best_distance_txt);
        totalDistanceImg = (ImageView) v.findViewById(R.id.total_distance_img);
        totalDistanceImg.setColorFilter(filter);
        totalDistance = (NewTextView) v.findViewById(R.id.total_distance);
        totalDistance.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        totalDistanceTxt = (NewTextView) v.findViewById(R.id.total_distance_txt);
        floorLayout = (LinearLayout) v.findViewById(R.id.floor_layout);
        yesterdayFloorImg = (ImageView) v.findViewById(R.id.yesterday_floor_img);
        yesterdayFloorImg.setColorFilter(filter);
        yesterdayFloor = (NewTextView) v.findViewById(R.id.yesterday_floor);
        yesterdayFloor.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        yesterdayFloorTxt = (NewTextView) v.findViewById(R.id.yesterday_floor_txt);
        bestFloorImg = (ImageView) v.findViewById(R.id.best_floor_img);
        bestFloorImg.setColorFilter(filter);
        bestFloor = (NewTextView) v.findViewById(R.id.best_floor);
        bestFloor.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        bestFloorTxt = (NewTextView) v.findViewById(R.id.best_floor_txt);
        totalFloorImg = (ImageView) v.findViewById(R.id.total_floor_img);
        totalFloorImg.setColorFilter(filter);
        totalFloor = (NewTextView) v.findViewById(R.id.total_floor);
        totalFloor.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        totalFloorTxt = (NewTextView) v.findViewById(R.id.total_floor_txt);
        view = (View) v.findViewById(R.id.view);
        view.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
    }

    public void showError() {
        Toast.makeText(context, "Some Network Error Occured, Please Try Again!!", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setCanvas(ArrayList<InfoStreamBean> arrayList) {
        cFlipper.removeAllViews();
        size = arrayList.size();
//        Log.d("array size", size + "");
        dot = new DrawCanvasDot(context, null);
        if (arrayList.size() > 0) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
            for (int i = 0; i < arrayList.size(); i++) {

                // final RelativeLayout relativeLayout = new RelativeLayout(context);
                final CardView cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.flipper, null, false);
                final RelativeLayout relativeLayout = (RelativeLayout) cardView.findViewById(R.id.relativeLayout);
                final TextView tv_title = (TextView) relativeLayout.findViewById(R.id.tv_title);
                final TextView tv_message = (TextView) relativeLayout.findViewById(R.id.tv_message);

                tv_title.setText(arrayList.get(i).getTitle());
                setTextViewHTML(tv_message, arrayList.get(i).getMessage());

                final ImageView img = (ImageView) relativeLayout.findViewById(R.id.img);
                img.setImageResource(R.mipmap.default_img);
                String imagePaths = arrayList.get(i).getPhotourl();
                if (!(imagePaths == null || imagePaths.equals(""))) {
                    Ion.with(img)
                            .placeholder(R.mipmap.default_img)
                            .error(R.mipmap.default_img)
                            .load(imagePaths);
                }
                cFlipper.addView(cardView);
                count++;
                if (count == size) {
                    startViewFlipping();
                }
            }
        }
    }

    /*         Ion.with(getActivity()).load(imagePaths).withBitmap()
                         .placeholder(R.mipmap.default_img)
                         .error(R.mipmap.default_img)
                         .transform(new RoundedTransformation())
                         .intoImageView(img);*/
    private void startViewFlipping() {
        dotLayout.addView(dot);

        if (cFlipper.getChildCount() > 1) {
            enableFlipMode();
        }
    }

    private void enableFlipMode() {
        cFlipper.setFlipInterval(6000);
        cFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.in_right));
        cFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.out_left));
        cFlipper.startFlipping();
    }

    @Override
    public void onRefresh() {

        Log.e("swipe refresh", "on refresh dash board");
        if (CheckConnection.isConnection(context)) {
            swipeRefreshLayout.setColorSchemeResources(R.color.ColorPrimary);
            swipeRefreshLayout.setRefreshing(true);
            isRefresh = true;

            Log.e(TAG, "the service is " + service);
            if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_MYMO)) {
                syncDashboardFromMymo();
            } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_GARMIN)) {
                syncDashboardFromGarmin();
            } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_FITBIT)) {
                Log.e(TAG, "inside fitbit");
                syncDashboardFromFitbit();
            } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_JAWBONE)) {
                IonJawbone();
            } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_SHEALTH)) {
                SetUpSHealth.getInstance().init(getActivity(), 1);
            } else if (service.equalsIgnoreCase(DbAdapter.TABLE_NAME_PEDOMETER)) {
                if (new Helper().isAppInstalled(getActivity())) {
                    if (CheckConnection.isConnection(context)) {
                        fitnessHelper = FitnessHelper.getInstance((TabActivity) context);
                        fitnessHelper.fitnessClientBuilder();
                        showProgressBar(true);
                      //  getInfoStreamData();
                        //new InfoStreamAsync(context).execute();
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        new Helper().showNoInternetToast(context);
                    }
                } else {
                    DbAdapter dbAdapter = DbAdapter.getInstance(context);
                    dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PEDOMETER);
                    sharedPreference.remove(Constants.IS_RECORDING_STARTED);
                    Intent intent = new Intent(context, ChooseTracker.class);
                    context.startActivity(intent);
                    ((TabActivity) context).finish();
                }
            }
            getInfoStreamData();

        } else {
            new Helper().showNoInternetToast(context);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (size > 1) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    TabActivity.ViewPagerAdapter.pager.requestDisallowInterceptTouchEvent(true);
                    initialX = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    TabActivity.ViewPagerAdapter.pager.requestDisallowInterceptTouchEvent(false);
                    float finalX = motionEvent.getX();
                    if (initialX > finalX) {

                        Log.e(TAG, "showing next");
                        if (cFlipper.getDisplayedChild() == size - 1)
                            break;
                        cFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.in_right));
                        cFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.out_left));
                        cFlipper.showNext();
                    } else {
                        Log.e(TAG, "showing previous");
                        if (cFlipper.getDisplayedChild() == 0)
                            break;
                        cFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.in_left));
                        cFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.out_right));
                        cFlipper.showPrevious();
                    }

                    break;
            }
        }
        return true;
    }

//    private Callback genericCallbackListener = new Callback<Object>() {
//        @Override
//        public void success(Object object, Response response) {
////            Log.e("jawbone moves json", "api call successful, json output: " + object.toString());
//            try {
//                Gson gson = new Gson();
//                String jsonString = gson.toJson(object);
//                Jsonparser jsonparser = new Jsonparser(jsonString);
//                if (jsonparser.getJawboneError()) {
//                    syncDashboardFromJawbone(jsonString);
//                } else {
////                    refreshJawboneToken();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void failure(RetrofitError retrofitError) {
////            Log.e("jawbone err moves json", "api call failed, error message: " + retrofitError.getMessage());
//            Toast.makeText(context, retrofitError.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    };
//    private Callback call = new Callback<Object>() {
//        @Override
//        public void success(Object o, Response response) {
////            Log.e("refresh", "api call successful, json output: " + o.toString());
//
//        }
//
//        @Override
//        public void failure(RetrofitError retrofitError) {
////            Log.e("refresh fail", "api call failed, error message: " + retrofitError.getMessage());
//
//        }
//    };

    public void IonJawbone() {
        try {
            final DbAdapter dbAdapter = DbAdapter.getInstance(context);
            Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_JAWBONE);
            if (cursor.getCount() > 0) {
                String ACCESS_TOKEN = cursor.getString(0);
                if (ACCESS_TOKEN != null) {
                    Ion.with(context)
                            .load("https://jawbone.com/nudge/api/v.1.1/users/@me/moves")
                            .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                            .addHeader("Accept", "application/json")
                            .asString()
                            .setCallback(new FutureCallback<String>() {

                                @Override
                                public void onCompleted(Exception e, String jsonString) {
                                    if (jsonString != null) {
//                                        Log.d("result", jsonString);
                                        Jsonparser jsonparser = new Jsonparser(jsonString);
                                        if (jsonparser.getJawboneError()) {
                                            syncDashboardFromJawbone(jsonString);
                                        } else {
                                            dbAdapter.deleteAll(DbAdapter.TABLE_NAME_JAWBONE);
                                            List<Constants.JawboneAuthScope> authScope = new ArrayList<Constants.JawboneAuthScope>();
                                            authScope.add(Constants.JawboneAuthScope.MOVE_READ);

                                            Uri.Builder builder = OauthUtils.setOauthParameters(Constants.CLIENT_ID, Constants.OAUTH_CALLBACK_URL, authScope);

                                            Intent intent = new Intent(context, OauthWebViewActivity.class);
                                            intent.putExtra(Constants.AUTH_URI, builder.build());
                                            startActivity(intent);
                                            ((TabActivity) context).finish();
//                                            refreshJawboneToken();
                                        }
                                    }
                                }
                            });
                }
            }
        } catch (Exception e) {

        }
    }

    //    public void IonJawboneRefresh() {
//        try {
//            DbAdapter dbAdapter = DbAdapter.getInstance(context);
//            Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_JAWBONE);
//            if (cursor.getCount() > 0) {
//                String ACCESS_TOKEN = cursor.getString(0);
//                String REFRESH_TOKEN = cursor.getString(0);
//                if (ACCESS_TOKEN != null) {
//                    Ion.with(context)
//                            .load("https://jawbone.com/auth/oauth2/token?grant_type=refresh_token")
//                            .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
//                            .addHeader("Accept", "application/json")
//                            .setBodyParameter("client_id", Constants.CLIENT_ID)
//                            .setBodyParameter(Constants.CLIENT_SECRET, Constants.CLIENT_SECRET_KEY)
//                            .setBodyParameter("refresh_token", REFRESH_TOKEN)
//                            .asString()
//                            .setCallback(new FutureCallback<String>() {
//
//                                @Override
//                                public void onCompleted(Exception e, String jsonString) {
//                                    if(jsonString != null) {
////                                        Log.d("result", jsonString);
////                                        Jsonparser jsonparser = new Jsonparser(jsonString);
////                                        if (jsonparser.getJawboneError()) {
////                                            syncDashboardFromJawbone(jsonString);
////                                        } else {
////                                            refreshJawboneToken();
////                                        }
//                                    }
//                                }
//                            });
//                }
//            }
//        } catch (Exception e){
//
//        }
//    }
    public void showProgressBar(boolean show) {
        if (!isRefresh) {
            if (show) {
                i++;
            } else {
                i--;
            }
            if (i > 0) {
                if (pDialog != null && !pDialog.isShowing()) {
                    pDialog.show();
                }
            } else if (i == 0) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();

                    boolean isShowedSwipeShowcaseView = sharedPreference.getBoolean(Constants.SWIPE_SHOWCASE, false);
                    if (isShowedSwipeShowcaseView == false) {
                        showSwipeShowcaseView();
                    }
                }
            }
        }
    }

    public void showSwipeShowcaseView() {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.swipe_showcase);
        dialog.show();
        Button btnD = (Button) dialog.findViewById(R.id.btnD);
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sharedPreference.putBoolean(Constants.SWIPE_SHOWCASE, true);
            }
        });
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        Log.e(TAG, "the span is " + span.toString());
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", span.getURL());
                startActivity(intent);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html) {

        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        Log.e(TAG, "html is" + html);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setLinksClickable(true);
        text.setLinkTextColor(Color.YELLOW);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void getInfoStreamData() {
        pDialog.hide();
        if (CheckConnection.isConnection(getActivity())) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("daily goal", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("#error")) {
                        JSONObject object = jsonObject.getJSONObject("#data");
                        JSONObject dailygoal = object.getJSONObject("dailygoal");
                        SharedPreferences.Editor mygoals = getActivity().getSharedPreferences("MyGoals", Context.MODE_PRIVATE).edit();
                        mygoals.putString("steps", dailygoal.getString("steps"));
                        mygoals.putString("calories", dailygoal.getString("calories"));
                        mygoals.putString("distance", dailygoal.getString("distance"));
                        mygoals.putString("floors", String.valueOf(dailygoal.getInt("floors")));
                        mygoals.commit();
                    } else {
                        Toast.makeText(getActivity(), "Some Went Wrong, Please Try Again!!", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
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
                map.put("method", "userV2.get_usergoal");
                Helper helper = new Helper();
                String time_stamp = helper.getUnixTimestamp();
                map.put("hash", helper.getHashValue(Constants.API_KEY, Constants.DOMAIN, time_stamp, "userV2.get_usergoal"));
                map.put("domain_name", Constants.DOMAIN);
                map.put("domain_time_stamp", time_stamp);
                map.put("nonce", time_stamp);
                map.put("sessid", sessionId);
                map.put("userid", userId);
                map.put("corpid", corpId);
//
//
                Log.e("Param", "params are " + map.toString());

                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }else

    {
        Toast.makeText(getActivity(), "Some Network Error Occured, Please Try Again!!", Toast.LENGTH_SHORT).show();

    }


    }




    public class PadometerAsync extends AsyncTask<Long, Void, Void> {
        String sessionId, userId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar(true);
        }

        @Override
        protected Void doInBackground(Long... params) {
            padoSteps = fitnessHelper.getStepsCountByBucket(params[0], params[1]);

            DbAdapter dbAdapter = DbAdapter.getInstance(context);
            Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);
            // fitnessHelper.getGoogleDistance();
            if (cursor.getCount() > 0) {
                sessionId = cursor.getString(0);
                userId = cursor.getString(1);
            }
            String imei = new Helper().getImei(context);
            //String serial = "7065646f6d657465720d0a", apitoken = "", apitype = "1003";
            String serial = "676f6f676c65666974", apitoken = "", apitype = "1003";


            String response = new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, padoSteps, caloriesBeanArrayList, distanceBeanArrayList);

            Log.d("Google fit data add to mymoserver", response);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            syncDashboardFromPado();
            showProgressBar(false);
            pDialog.hide();
            Log.d("Dashboard", "On progress Dashboard");

        }
    }

    public class PadometerDistanceAsync extends AsyncTask<Long, Void, Void> {
        String sessionId, userId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgressBar(true);
        }

        @Override
        protected Void doInBackground(Long... params) {

            // caloriesBeanArrayList = fitnessHelper.getdata();

            final long startTime, endTime;
            Calendar calendar = Calendar.getInstance();
            endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -6);
            calendar.add(Calendar.HOUR_OF_DAY, -(calendar.get(Calendar.HOUR_OF_DAY)));
            calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE)));
            calendar.add(Calendar.SECOND, -(calendar.get(Calendar.SECOND)));
            startTime = calendar.getTimeInMillis();

            final java.text.DateFormat dateFormat = DateFormat.getDateInstance();
            final java.text.DateFormat timeformat = DateFormat.getTimeInstance();
            Log.e("History", "Range Start: " + dateFormat.format(startTime));
            Log.e("History", "Range End: " + dateFormat.format(endTime));
            DataReadRequest readdistanceRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

                    Fitness.HistoryApi.readData(fitnessHelper.fitnessClient, readdistanceRequest).setResultCallback(new ResultCallback<DataReadResult>() {
                        @Override
                        public void onResult(DataReadResult dataReadResult) {
                            if (dataReadResult.getBuckets().size() > 0) {
                                Log.e("DataSet.size(): ",
                                        "" + dataReadResult.getBuckets().size());
                                for (Bucket bucket : dataReadResult.getBuckets()) {
                                    List<DataSet> dataSets = bucket.getDataSets();
                                    for (DataSet dataSet : dataSets) {
                                        Log.e("dataSet.dataType: ", "" + dataSet.getDataType().getName());
                                        if (!dataSet.getDataPoints().isEmpty()) {
                                            for (DataPoint dp : dataSet.getDataPoints()) {
                                                Log.e("first date", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

                                                DistanceBean distanceBean = new DistanceBean();
                                                String field_value = "";

                                                String msg = "dataPoint: "
                                                        + "type: " + dp.getDataType().getName() + "\n"
                                                        + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                                                        + ", fields: [";

                                                for (Field field : dp.getDataType().getFields()) {
                                                    msg += field.getName() + "=" + dp.getValue(field) + " ";
                                                    field_value = "" + dp.getValue(field);

                                                }
                                                distanceBean.setDate(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                                                distanceBean.setDistance(field_value);
                                                distanceBeanArrayList.add(distanceBean);
                                                msg += "]";
                                                Log.e("msggggggggggg ,......", msg);
                                            }
                                            Log.e("msggggggggggg ,......", "secondd");

                                        } else {
                                            Log.e("else part","elseeeee");
                                            DistanceBean distanceBean = new DistanceBean();
                                            distanceBean.setDistance("0");
                                            distanceBeanArrayList.add(distanceBean);

                                        }
                                    }
                                }
                            } else if (dataReadResult.getDataSets().size() > 0) {
                                Log.e("dataSet.size(): ", "" + dataReadResult.getDataSets().size());
                                for (DataSet dataSet : dataReadResult.getDataSets()) {
                                    Log.e("dataType: ", "" + dataSet.getDataType().getName());

                                    for (DataPoint dp : dataSet.getDataPoints()) {
                                        DistanceBean distanceBean = new DistanceBean();
                                        String field_value = "";

                                        String msg = "dataPoint: "
                                                + "type: " + dp.getDataType().getName() + "\n"
                                                + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                                                + ", fields: [";

                                        for (Field field : dp.getDataType().getFields()) {
                                            msg += field.getName() + "=" + dp.getValue(field) + " ";
                                            field_value = "" + dp.getValue(field);

                                        }
                                        distanceBean.setDate(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                                        distanceBean.setDistance(field_value);
                                        distanceBeanArrayList.add(distanceBean);

                                        msg += "]";
                                        Log.e("msggggggggggg ,......", msg);
                                    }
                                }
                            }

                        }
                    });
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    new PadometerCaloriesAsync().execute();

                }
            });

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


                //new PadometerCaloriesAsync().execute();
            //showProgressBar(false);

        }
    }

    public class PadometerCaloriesAsync extends AsyncTask<Long, Void, ArrayList<CaloriesBean>> {
        String sessionId, userId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // showProgressBar(false);
        }

        @Override
        protected ArrayList<CaloriesBean> doInBackground(Long... params) {
            final long startTime, endTime;
            Calendar calendar = Calendar.getInstance();
            endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -6);
            calendar.add(Calendar.HOUR_OF_DAY, -(calendar.get(Calendar.HOUR_OF_DAY)));
            calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE)));
            calendar.add(Calendar.SECOND, -(calendar.get(Calendar.SECOND)));
            startTime = calendar.getTimeInMillis();
            dateFormat = DateFormat.getDateInstance();
            timeformat = DateFormat.getTimeInstance();




            Log.e("History", "Range Start: " + dateFormat.format(startTime));
            Log.e("History", "Range End: " + dateFormat.format(endTime));

            readCaloriesRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            Fitness.HistoryApi.readData(FitnessHelper.fitnessClient, readCaloriesRequest).setResultCallback(new ResultCallback<DataReadResult>() {
                @Override
                public void onResult(DataReadResult dataReadResult) {
                    if (dataReadResult.getBuckets().size() > 0) {
                        Log.e("DataSet.size(): ",
                                "" + dataReadResult.getBuckets().size());
                        for (Bucket bucket : dataReadResult.getBuckets()) {
                            List<DataSet> dataSets = bucket.getDataSets();

                                for (DataSet dataSet : dataSets) {
                                    Log.e("dataSet.dataType: ", "" + dataSet.getDataType().getName());
                                    if (!dataSet.getDataPoints().isEmpty()) {
                                        for (DataPoint dp : dataSet.getDataPoints()) {
                                            Log.e("first date", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                            CaloriesBean stepsBean = new CaloriesBean();
                                            String field_value = "";
                                            String msg = "dataPoint: "
                                                    + "type: " + dp.getDataType().getName() + "\n"
                                                    + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                                                    + ", fields: [";
                                            for (Field field : dp.getDataType().getFields()) {
                                                msg += field.getName() + "=" + dp.getValue(field) + " ";
                                                field_value = "" + dp.getValue(field);
                                            }
                                            stepsBean.setDate(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                            stepsBean.setSteps(field_value);
                                            caloriesBeanArrayList.add(stepsBean);
                                            msg += "]";
                                            Log.e("msggggggggggg ,......", msg);
                                        }
                                    } else {
                                        CaloriesBean stepsBean = new CaloriesBean();
                                        stepsBean.setSteps("0");
                                        caloriesBeanArrayList.add(stepsBean);
                                    }
                                }
                        }
                    } else if (dataReadResult.getDataSets().size() > 0) {
                        Log.e("DataSet.size(): ",
                                "" + dataReadResult.getBuckets().size());
                        for (Bucket bucket : dataReadResult.getBuckets()) {
                            List<DataSet> dataSets = bucket.getDataSets();
                            for (DataSet dataSet : dataSets) {
                               // Log.e("dataSet.dataType: ", "" + dataSet.getDataType().getName());
                                if (!dataSet.getDataPoints().isEmpty()) {

                                    for (DataPoint dp : dataSet.getDataPoints()) {
                                        CaloriesBean stepsBean = new CaloriesBean();
                                        String field_value = "";
                                        String msg = "dataPoint: "
                                                + "type: " + dp.getDataType().getName() + "\n"
                                                + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + timeformat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                                                + ", fields: [";
                                        for (Field field : dp.getDataType().getFields()) {
                                            msg += field.getName() + "=" + dp.getValue(field) + " ";
                                            field_value = "" + dp.getValue(field);
                                        }
                                        stepsBean.setDate(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                                        stepsBean.setSteps(field_value);
                                        caloriesBeanArrayList.add(stepsBean);
                                        msg += "]";
                                        Log.e("msggggggggggg ,......", msg);
                                    }
                                }else{
                                    CaloriesBean stepsBean = new CaloriesBean();

                                    stepsBean.setSteps("0");
                                    caloriesBeanArrayList.add(stepsBean);
                                }
                            }
                        }
                    }

                }
            });
            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<CaloriesBean> aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Dashboard","Onpostexcute Calories");
           // caloriesBeanArrayList = aVoid;
            final long startTime, endTime;
            Calendar calendar = Calendar.getInstance();
            endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -6);
            calendar.add(Calendar.HOUR_OF_DAY, -(calendar.get(Calendar.HOUR_OF_DAY)));
            calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE)));
            calendar.add(Calendar.SECOND, -(calendar.get(Calendar.SECOND)));
            startTime = calendar.getTimeInMillis();

            Log.d("st " + startTime, "et " + endTime);
            if (CheckConnection.isConnection(context)) {
//                    Log.e("valeee",caloriesBeanArrayList.get(2).getDate());
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new PadometerAsync().execute(startTime, endTime);
                    }
                }, 5 * 1000);
                 //new PadometerAsync().execute(startTime, endTime);
            } else {

                swipeRefreshLayout.setRefreshing(false);

                new Helper().showNoInternetToast(context);
            }
            // showProgressBar(false);

        }
    }



}