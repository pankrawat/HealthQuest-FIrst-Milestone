/**
 * Copyright (C) 2014 Samsung Electronics Co., Ltd. All rights reserved.
 * <p>
 * Mobile Communication Division,
 * Digital Media & Communications Business, Samsung Electronics Co., Ltd.
 * <p>
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 * <p>
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */

package com.tupelo.wellness.SHealthIntegration;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataObserver;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataResolver.Filter;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadResult;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.bean.CaloriesBean;
import com.tupelo.wellness.bean.DistanceBean;
import com.tupelo.wellness.bean.FloorBean;
import com.tupelo.wellness.bean.StepsBean;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.network.NetworkCall;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;


public class StepCountReporter {
    private final HealthDataStore mStore;
    private static StepCountReporter stepCountReporter;
    public Context context;
    String sessionId = "", userId = "";
    ArrayList<StepsBean> stepCounts, stepCountFinal;
    ArrayList<CaloriesBean> caloriesBeanArrayList;
    ArrayList<DistanceBean> distanceBeanArrayList;
    ArrayList<FloorBean> floorBeanArrayList;
    Boolean isfloorcount=true;
    ArrayList<StepsBean> finalarr= new ArrayList<>();

    public StepCountReporter(HealthDataStore store) {
        mStore = store;
    }

    public static void newInstance(HealthDataStore store) {
        stepCountReporter = new StepCountReporter(store);
    }

    public static StepCountReporter getInstance() {
        return stepCountReporter;
    }

    public void start(Context context) {
        this.context = context;
        HealthDataObserver.addObserver(mStore, Constants.SHEALTH_STEP_DAILY_TREND, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE, mObserver);

        //readTodayStepCount();
        //getFloorCount();
        getWeeklyFloors();
    }

    private void readTodayStepCount() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        Filter filter = Filter.eq("source_type", -2);
        HealthDataResolver.ReadRequest request = new ReadRequest.Builder()
                .setDataType(Constants.SHEALTH_STEP_DAILY_TREND)
                .setFilter(filter)
                .setSort("day_time", HealthDataResolver.SortOrder.DESC)
                .build();

        try {
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(Constants.SHEALTH_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(Constants.SHEALTH_TAG, "Getting step count fails.");
        }
    }

    private final HealthResultHolder.ResultListener<ReadResult> mListener = new HealthResultHolder.ResultListener<ReadResult>() {
        @Override
        public void onResult(ReadResult result) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Cursor c = null;
            StepsBean stepCount;
            CaloriesBean caloriesBean;
            DistanceBean distanceBean;
            FloorBean floorBean;
            Calendar cal = Calendar.getInstance();
            Calendar FirstDayOfWeek = Calendar.getInstance();
            FirstDayOfWeek.add(Calendar.DAY_OF_YEAR, -6);

            Log.e(Constants.SHEALTH_TAG, "Today's date: " + dateFormat.format(cal.getTime()) + " FirstDayofweek: " + dateFormat.format(FirstDayOfWeek.getTime()));
            stepCounts = new ArrayList<>();
            caloriesBeanArrayList=new ArrayList<>();
            distanceBeanArrayList=new ArrayList<>();
            try {
                c = result.getResultCursor();
                if (c != null) {
                    Log.e(Constants.SHEALTH_TAG, "getCount " + c.getCount());
                    while (c.moveToNext()) {
                        stepCount = new StepsBean();
                        distanceBean= new DistanceBean();
                        caloriesBean=new CaloriesBean();
                        stepCount.setSteps(String.valueOf(c.getInt(c.getColumnIndex("count"))));
                        distanceBean.setDistance(String.valueOf(c.getInt(c.getColumnIndex("distance"))));
                        caloriesBean.setSteps(String.valueOf(c.getInt(c.getColumnIndex("calorie"))));
                        Log.e("distance.......",""+c.getInt(c.getColumnIndex("distance")));
                        stepCount.setDate(dateFormat.format(c.getLong(c.getColumnIndex("day_time"))));
                        stepCounts.add(stepCount);
                        distanceBeanArrayList.add(distanceBean);
                        caloriesBeanArrayList.add(caloriesBean);
                    }

                }
            } finally {
                if (c != null) {
                    c.close();
                    stepCountFinal = new ArrayList<>();
                    Log.e(Constants.SHEALTH_TAG, "total Count " + stepCounts.size());
                    while (cal.get(Calendar.DAY_OF_YEAR) >= FirstDayOfWeek.get(Calendar.DAY_OF_YEAR)) {
                        for (StepsBean bean : stepCounts) {
                            if (bean.getDate().equalsIgnoreCase(dateFormat.format(cal.getTime()))) {
                                Log.e(Constants.SHEALTH_TAG, "Added " + bean.getDate() + " Count: " + bean.getSteps());
                                stepCountFinal.add(bean);
                            }
                        }
                        cal.add(Calendar.DAY_OF_YEAR, -1);
                    }
                }


                Calendar cal1 = Calendar.getInstance();
                Hashtable<String,StepsBean> stepbeans= new Hashtable<>();
                ArrayList<String> datearr= new ArrayList<>();

                while (cal1.get(Calendar.DAY_OF_YEAR) >= FirstDayOfWeek.get(Calendar.DAY_OF_YEAR)) {

                    datearr.add(dateFormat.format(cal1.getTime()));
                    cal1.set(Calendar.HOUR_OF_DAY,-1);

                }
                for(int i=0;i<datearr.size();i++) {
                    for(int j=0;j<stepCountFinal.size();j++) {
                        if (stepCountFinal.get(j).getDate().equalsIgnoreCase(datearr.get(i).toString())) {
                            //if(stepbeans.get(stepCountFinal.get(j).getDate()).getSteps().equals("null")) {
                            stepbeans.put(stepCountFinal.get(j).getDate(), stepCountFinal.get(j));

                            //   }
                        } else {
                            StepsBean bean = new StepsBean();
                            bean.setDate(datearr.get(i).toString());
                            bean.setSteps("0");
                            if(!stepbeans.containsKey(bean.getDate())) {
                                stepbeans.put(bean.getDate(), bean);
                            }
                        }

                    }
                }

                finalarr.addAll(stepbeans.values());

                for (int i=0;i<finalarr.size();i++){
                    Log.e("Arrrayyyyy",finalarr.get(i).getDate()+"   "+finalarr.get(i).getSteps());
                }

            }
            new SHealthAsync().execute();
        }
    };

    public class SHealthAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DbAdapter dbAdapter = DbAdapter.getInstance(context);
            Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

            if (cursor.getCount() > 0) {
                sessionId = cursor.getString(0);
                userId = cursor.getString(1);
            }

            if (sessionId == null) {
                sessionId = context.getSharedPreferences("MyProfile", 0).getString("sessid", "");
            }

            String imei = new Helper().getImei(context);
            String serial = "534845414c5448", apitoken = "", apitype = "1006";
                if(isfloorcount) {
                   String result= new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepCountFinal, caloriesBeanArrayList, distanceBeanArrayList,floorBeanArrayList);
                    Log.e(StepCountReporter.class.getName(),"1"+result);
                }else {
                    String result= new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepCountFinal, caloriesBeanArrayList, distanceBeanArrayList);
                Log.e(StepCountReporter.class.getName(),"2"+result);
                }
                    return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((TabActivity) context).apiClientConnectionFailed(0);
        }

    }


    private final HealthDataObserver mObserver = new HealthDataObserver(null) {

        @Override
        public void onChange(String dataTypeName) {
            Log.d(Constants.SHEALTH_TAG, "Observer receives a data changed event");
           // readTodayStepCount();
            getFloorCount();
        }
    };


    private void getFloorCount(){
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 24*60*60*1000;
        HealthDataResolver.Filter filter = HealthDataResolver.Filter.and(HealthDataResolver.Filter.greaterThanEquals(HealthConstants.Exercise.START_TIME, startTime),
                HealthDataResolver.Filter.lessThanEquals(HealthConstants.Exercise.END_TIME, endTime));
        HealthDataResolver.ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE)
                .setProperties(new String[]{HealthConstants.FloorsClimbed.FLOOR})
                .setFilter(filter)
               // .setSort("day_time", HealthDataResolver.SortOrder.DESC)
                .build();

        try {
            resolver.read(request).setResultListener(mFloorListener);
        } catch (Exception e) {
            Log.e(Constants.SHEALTH_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(Constants.SHEALTH_TAG, "Getting Floors count fails.");
        }
    }
    private final HealthResultHolder.ResultListener<ReadResult> mFloorListener = new HealthResultHolder.ResultListener<ReadResult>() {
        @Override
        public void onResult(ReadResult result) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Cursor c = null;
            StepsBean stepCount;
            CaloriesBean caloriesBean;
            DistanceBean distanceBean;
            FloorBean floorBean;
            Calendar cal = Calendar.getInstance();
            Calendar FirstDayOfWeek = Calendar.getInstance();
            FirstDayOfWeek.add(Calendar.DAY_OF_YEAR, -6);

            Log.e(Constants.SHEALTH_TAG, "Today's date: " + dateFormat.format(cal.getTime()) + " FirstDayofweek: " + dateFormat.format(FirstDayOfWeek.getTime()));
            stepCounts = new ArrayList<>();
            caloriesBeanArrayList=new ArrayList<>();
            distanceBeanArrayList=new ArrayList<>();
            try {
                c = result.getResultCursor();
                if (c != null) {
                    Log.e(Constants.SHEALTH_TAG, "getCount " + c.getCount());
                    while (c.moveToNext()) {
                        String data = String.valueOf(c.getInt(c.getColumnIndex("floor")));
                        Log.e("Floor data",data);
                    }

                }
            } finally {
                if (c != null) {
                    c.close();

                }
            }
            readTodayStepCount();
           // new SHealthAsync().execute();
        }
    };



    private void getWeeklyFloors() {

        long startTime = getStartTimeOfWeek();
        long endTime = System.currentTimeMillis();
        floorBeanArrayList=new ArrayList<>();
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        Log.d("TEST", "start "+startTime);
        Log.d("TEST", "end "+endTime);
        HealthDataResolver.Filter filter =
                HealthDataResolver.Filter.greaterThanEquals(HealthConstants.FloorsClimbed.START_TIME, startTime)
                        .lessThan(HealthConstants.FloorsClimbed.END_TIME, endTime);

        HealthDataResolver.AggregateRequest request = new HealthDataResolver.AggregateRequest.Builder()
                .setDataType(HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE)
                .setFilter(filter)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.SUM,
                        HealthConstants.FloorsClimbed.FLOOR, "sum")
                .setTimeGroup(HealthDataResolver.AggregateRequest.TimeGroupUnit.DAILY, 1,
                        HealthConstants.FloorsClimbed.TIME_OFFSET,
                        HealthConstants.FloorsClimbed.END_TIME, "day")
                .build();

        try {
            resolver.aggregate(request).setResultListener(mStepAggrResult);
        } catch (Exception e) {
            Log.d("TEST", "Aggregating health data fails.");
        }
    }

    private final HealthResultHolder.ResultListener<HealthDataResolver.AggregateResult> mStepAggrResult=
            new HealthResultHolder.ResultListener<HealthDataResolver.AggregateResult>() {

                @Override
                public void onResult(HealthDataResolver.AggregateResult result) {
// Checks the result
                    Cursor c = result.getResultCursor();
                     FloorBean floorBean;

                    if (c != null) {
                        Log.e(Constants.SHEALTH_TAG, "getCount " + c.getCount());
                        if (c.getCount() > 0) {
                            while (c.moveToNext()) {
                                floorBean = new FloorBean();

                                String day = c.getString(c.getColumnIndex("day"));
                                String sum = c.getString(c.getColumnIndex("sum"));
                                floorBean.setFloor(sum);

                                Log.d("TEST", "sum " + sum);
                                Log.d("TEST", "day " + day.toString());
                                floorBeanArrayList.add(floorBean);
                            }
                            c.close();
                            isfloorcount = true;
                            readTodayStepCount();
                        }
                     else {
                        isfloorcount = false;
                        readTodayStepCount();
                    }
                }
                    else {
                        Log.d("TEST", "There is no result.");
                    }
                }
            };

    private long getStartTimeOfWeek() {
        Calendar week = Calendar.getInstance();
        week.add(Calendar.DATE, -7);
        return week.getTimeInMillis();
    }
}
