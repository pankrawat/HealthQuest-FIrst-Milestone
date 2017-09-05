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
import java.util.Locale;


public class StepCountReporter {
    private final HealthDataStore mStore;
    private static StepCountReporter stepCountReporter;
    public Context context;
    String sessionId = "", userId = "";
    ArrayList<StepsBean> stepCounts, stepCountFinal;
    ArrayList<CaloriesBean> caloriesBeanArrayList;
    ArrayList<DistanceBean> distanceBeanArrayList;

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
        readTodayStepCount();
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

            new NetworkCall().setStepsToMymo(sessionId, userId, imei, serial, apitoken, apitype, stepCounts,caloriesBeanArrayList,distanceBeanArrayList);
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
            readTodayStepCount();
        }
    };

}
