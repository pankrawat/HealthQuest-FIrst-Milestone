package com.tupelo.wellness;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.bean.CaloriesBean;
import com.tupelo.wellness.bean.DistanceBean;
import com.tupelo.wellness.bean.StepsBean;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.jawbone.datamodel.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FitnessHelper {
    private static FitnessHelper fitnessHelper;
    private Activity context;
    public static GoogleApiClient fitnessClient;
    private boolean authInProgress = false;
    String TAG = FitnessHelper.class.getName();
    //private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public FitnessHelper(Activity context) {
        this.context = context;

    }

    public static FitnessHelper getInstance(Activity context) {
//        if (fitnessHelper == null) {
            fitnessHelper = new FitnessHelper(context);
//        }
        return fitnessHelper;
    }

    public void fitnessClientBuilder() {
        fitnessClient = null;
        if (fitnessClient == null) {
            buildFitnessClient();
            fitnessClient.connect();
        } else if (fitnessClient != null && !fitnessClient.isConnected()) {
            fitnessClient.reconnect();
            //((TabActivity) context).apiClientConnected();
        } else if (fitnessClient.isConnected()) {
            //((TabActivity) context).apiClientConnected();
        }
    }

    public void buildFitnessClient() {
        fitnessClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                ((TabActivity) context).apiClientConnected();
                                Log.d("google", "1");
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                Log.d("google", i + "onConnectionSuspended");
//                                ((TabActivity) context).showProgress(false);
                                ((TabActivity) context).apiClientConnectionFailed(0);

                            }
                        })
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.e("onConnectionFailed",result.getErrorCode()+"");
//                                ((TabActivity) context).showProgress(false);
                                ((TabActivity) context).apiClientConnectionFailed(0);
                                if (!result.hasResolution()) {
                                    GooglePlayServicesUtil.getErrorDialog(
                                            result.getErrorCode(), context, 0)
                                            .show();
                                    Log.d("google res", result.getErrorCode() + "");
                                    return;
                                }
                                if (!authInProgress) {
                                    try {
                                        Log.d("google", "4");
                                        authInProgress = true;

                                        result.startResolutionForResult(context, Constants.REQUEST_OAUTH);
                                    } catch (Throwable e) {
                                        e.printStackTrace();

                                    }
                                }
                            }
                        }).build();
    }




    public void subscribeRecording() {
        Fitness.RecordingApi.subscribe(fitnessClient, DataType.TYPE_STEP_COUNT_DELTA).
                setResultCallback(new ResultCallback<Status>() {

                    @Override
                    public void onResult(Status arg0) {
                        if (arg0.isSuccess()) {
                            Log.e("Steps Recording", "Subcribe");
                            ((TabActivity)context).getPadometerSteps();
                        }
                    }
                });
    }


    public ArrayList<StepsBean> getStepsCountByBucket(long startTime, long endTime) {
        ArrayList<StepsBean> list = new ArrayList<StepsBean>();
        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();
        DataSource USER_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("user_input")
                .setAppPackageName("com.google.android.apps.fitness")
                .build();
        PendingResult<DataReadResult> pendingResult = Fitness.HistoryApi
                .readData(
                        fitnessClient,
                        new DataReadRequest.Builder()
                                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA) //DataType.TYPE_STEP_COUNT_DELTA
                                .bucketByTime(1, TimeUnit.DAYS)
                                .setTimeRange(startTime, endTime,
                                        TimeUnit.MILLISECONDS).build());
        PendingResult<DataReadResult> pendingUserResult = Fitness.HistoryApi
                .readData(
                        fitnessClient,
                        new DataReadRequest.Builder()
                                .aggregate(USER_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA) //DataType.TYPE_STEP_COUNT_DELTA
                                .bucketByTime(1, TimeUnit.DAYS)
                                .setTimeRange(startTime, endTime,
                                        TimeUnit.MILLISECONDS).build());
        int steps = 0,stepsUser = 0;
        DataReadResult dataReadResult = pendingResult.await();
        DataReadResult dataUserReadResult = pendingUserResult.await();
        int[] userStepsAr = new int[dataReadResult.getBuckets().size()];
        if (dataReadResult.getBuckets().size() > 0) {
            int cnt=0;
            for(Bucket bucketUser : dataUserReadResult.getBuckets()){
                List<DataSet> dataSets = bucketUser.getDataSets();
                for (DataSet dataSet : dataSets) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        long s = dp.getStartTime(TimeUnit.DAYS);
                        for (Field field : dp.getDataType().getFields()) {
                            stepsUser += dp.getValue(field).asInt();
                        }
                    }
                }
                userStepsAr[cnt] = stepsUser;
                //Log.e("Buckate stepsUser",stepsUser+"");
                stepsUser=0;
                cnt++;
            }
            int cnt2=0;
            for (Bucket bucket : dataReadResult.getBuckets()) {
                StepsBean stepsBean = new StepsBean();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                stepsBean.setDate(sdf.format(new Date(bucket.getStartTime(TimeUnit.MILLISECONDS))));
//                Log.d("bucket", bucket.getStartTime(TimeUnit.MILLISECONDS) + "");
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        long s = dp.getStartTime(TimeUnit.DAYS);
                        for (Field field : dp.getDataType().getFields()) {
                            steps += dp.getValue(field).asInt();
//                            Log.e("Buckate steps",steps+"");
                        }
                    }
                }
                Log.e("Buckate Usersteps",userStepsAr[cnt2]+"");
                steps = steps-userStepsAr[cnt2];
                stepsBean.setSteps(String.valueOf(steps));
                Log.e("Buckate steps",steps+"");
                steps = 0;
                list.add(stepsBean);
                cnt2++;
            }
        }
        return list;
    }
    public void getGoogleDistance() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTim = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTim = cal.getTimeInMillis();

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTim, endTim, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_DISTANCE_DELTA)

                .setSessionName("distance_session")
                .build();

        PendingResult<SessionReadResult> sessionReadResult =
                Fitness.SessionsApi.readSession(fitnessClient, readRequest);

        sessionReadResult.setResultCallback(new ResultCallback<SessionReadResult>() {
            @Override
            public void onResult(SessionReadResult sessionReadResult) {
                if (sessionReadResult.getStatus().isSuccess()) {
                    Log.i("Tuts+", "Successfully read session data");
                    for (Session session : sessionReadResult.getSessions()) {
                        Log.i("Tuts+", "Session name: " + session.getName());
                        for (DataSet dataSet : sessionReadResult.getDataSet(session)) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                Log.i("Tuts+", "Speed: " + dataPoint.getValue(Field.FIELD_DISTANCE));
                            }
                        }
                    }
                } else {
                    Log.i("Tuts+", "Failed to read session data");
                }
            }
        });
    }

    public ArrayList<CaloriesBean> getdata(){
        final ArrayList<CaloriesBean> caloriesBeanArrayList= new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

       final java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();



        Fitness.HistoryApi.readData(fitnessClient, readRequest).setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                if (dataReadResult.getBuckets().size() > 0) {
                    Log.e("DataSet.size(): ",
                             ""+dataReadResult.getBuckets().size());
                    for (Bucket bucket : dataReadResult.getBuckets()) {
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            Log.e("dataSet.dataType: ","" + dataSet.getDataType().getName());

                            for (DataPoint dp : dataSet.getDataPoints()) {
                                caloriesBeanArrayList.add(describeDataPoint(dp, dateFormat));
                            }
                        }
                    }
                } else if (dataReadResult.getDataSets().size() > 0) {
                    Log.e("dataSet.size(): " ,""+ dataReadResult.getDataSets().size());
                    for (DataSet dataSet : dataReadResult.getDataSets()) {
                        Log.e("dataType: ","" + dataSet.getDataType().getName());

                        for (DataPoint dp : dataSet.getDataPoints()) {
                            caloriesBeanArrayList.add(describeDataPoint(dp, dateFormat));
                        }
                    }
                }

            }
        });
        return caloriesBeanArrayList;

    }

    public ArrayList<DistanceBean> getdistancedata(){

        final ArrayList<DistanceBean> distanceBeanArrayList= new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        final java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();



        Fitness.HistoryApi.readData(fitnessClient, readRequest).setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                if (dataReadResult.getBuckets().size() > 0) {
                    Log.e("DataSet.size(): ",
                            ""+dataReadResult.getBuckets().size());
                    for (Bucket bucket : dataReadResult.getBuckets()) {
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            Log.e("dataSet.dataType: ","" + dataSet.getDataType().getName());

                            for (DataPoint dp : dataSet.getDataPoints()) {
                                distanceBeanArrayList.add(describeDistanceDataPoint(dp, dateFormat));
                            }
                        }
                    }
                } else if (dataReadResult.getDataSets().size() > 0) {
                    Log.e("dataSet.size(): " ,""+ dataReadResult.getDataSets().size());
                    for (DataSet dataSet : dataReadResult.getDataSets()) {
                        Log.e("dataType: ","" + dataSet.getDataType().getName());

                        for (DataPoint dp : dataSet.getDataPoints()) {
                            distanceBeanArrayList.add(describeDistanceDataPoint(dp, dateFormat));
                        }
                    }
                }

            }
        });

        return distanceBeanArrayList;
    }
    public CaloriesBean describeDataPoint(DataPoint dp, DateFormat dateFormat) {
        CaloriesBean stepsBean= new CaloriesBean();
        String field_value="";
        String msg = "dataPoint: "
                + "type: " + dp.getDataType().getName() +"\n"
                + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                + ", fields: [";

        for(Field field : dp.getDataType().getFields()) {
            msg += field.getName() + "=" + dp.getValue(field) + " ";
            field_value=""+dp.getValue(field);
        }
            stepsBean.setDate(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            stepsBean.setSteps(field_value);
        msg += "]";
        Log.e("msggggggggggg ,......",msg);
        return stepsBean;
    }
    public DistanceBean describeDistanceDataPoint(DataPoint dp, DateFormat dateFormat) {
        DistanceBean distanceBean= new DistanceBean();
        String field_value="";

        String msg = "dataPoint: "
                + "type: " + dp.getDataType().getName() +"\n"
                + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                + ", fields: [";

        for(Field field : dp.getDataType().getFields()) {
            msg += field.getName() + "=" + dp.getValue(field) + " ";
        }
        distanceBean.setDate(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
        distanceBean.setDistance(field_value);
        msg += "]";
        Log.e("msggggggggggg ,......",msg);
        return distanceBean;
    }

    public CaloriesBean describeCaloriesDataPoint(DataPoint dp, DateFormat dateFormat) {
        CaloriesBean distanceBean= new CaloriesBean();
        String field_value="";

        String msg = "dataPoint: "
                + "type: " + dp.getDataType().getName() +"\n"
                + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                + ", fields: [";

        for(Field field : dp.getDataType().getFields()) {
            msg += field.getName() + "=" + dp.getValue(field) + " ";
        }
        distanceBean.setDate(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
        distanceBean.setSteps(field_value);
        msg += "]";
        Log.e("msggggggggggg ,......",msg);
        return distanceBean;
    }
//    public int getSteps(long s,long e){
//        int steps=0;
//        DataReadRequest ds=new DataReadRequest.Builder()
//                .setTimeRange(s, e, TimeUnit.MILLISECONDS)
//                .read(DataType.TYPE_STEP_COUNT_DELTA)
//                .build();
//        DataReadResult res=Fitness.HistoryApi.readData(fitnessClient, ds).await(1, TimeUnit.MINUTES);
//        List<DataSet> dataList2=res.getDataSets();
//        for (DataSet data : dataList2) {
//            for (DataPoint dataP : data.getDataPoints()) {
//                steps+= dataP.getValue(Field.FIELD_STEPS).asInt();
//            }
//        }
//        return steps;
//    }
//	public void cancleSubscription() {
//		 Fitness.RecordingApi.unsubscribe(fitnessClient, DataType.TYPE_ACTIVITY_SAMPLE)
//         .setResultCallback(new ResultCallback<Status>() {
//             @Override
//             public void onResult(Status status) {
//                 if (status.isSuccess()) {
//                     Log.i(TAG, "Successfully unsubscribed for data type: ");
//                 } else {
//                     // Subscription not removed
//                     Log.i(TAG, "Failed to unsubscribe for data type: " );
//                     }
//             }
//         });
//
//		 Fitness.RecordingApi.unsubscribe(fitnessClient, DataType.TYPE_STEP_COUNT_DELTA)
//         .setResultCallback(new ResultCallback<Status>() {
//             @Override
//             public void onResult(Status status) {
//                 if (status.isSuccess()) {
//                     Log.i(TAG, "Successfully unsubscribed for data type: ");
//                 } else {
//                     // Subscription not removed
//                     Log.i(TAG, "Failed to unsubscribe for data type: " );
//                     }
//             }
//         });
//		 Fitness.RecordingApi.unsubscribe(fitnessClient, DataType.TYPE_DISTANCE_DELTA)
//         .setResultCallback(new ResultCallback<Status>() {
//             @Override
//             public void onResult(Status status) {
//                 if (status.isSuccess()) {
//                     Log.i(TAG, "Successfully unsubscribed for data type: ");
//                 } else {
//                     // Subscription not removed
//                     Log.i(TAG, "Failed to unsubscribe for data type: " );
//                     }
//             }
//         });
//
//		 Fitness.RecordingApi.unsubscribe(fitnessClient, DataType.TYPE_CALORIES_EXPENDED)
//         .setResultCallback(new ResultCallback<Status>() {
//             @Override
//             public void onResult(Status status) {
//                 if (status.isSuccess()) {
//                     Log.i(TAG, "Successfully unsubscribed for data type: ");
//                 } else {
//                     // Subscription not removed
//                     Log.i(TAG, "Failed to unsubscribe for data type: " );
//                     }
//             }
//         });
//	}


    /*
     * To save User Height
     */
    public boolean saveUserHeight(int heightCentimeters) {
        // to post data
        float height = ((float) heightCentimeters) / 100.0f;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        DataSet heightDataSet = createDataForRequest(DataType.TYPE_HEIGHT, // for
                DataSource.TYPE_RAW, height, // weight in kgs
                startTime, // start time
                endTime, // end time
                TimeUnit.MILLISECONDS // Time Unit, for example,
                // TimeUnit.MILLISECONDS
        );
        Status heightInsertStatus = Fitness.HistoryApi
                .insertData(fitnessClient, heightDataSet).await(1,
                        TimeUnit.MINUTES);
        if (heightInsertStatus.isSuccess()) {
            //Log.e("Height", heightCentimiters+"Inserted");
        } else {
            //Log.e("Height", "inserted failed");
        }
        return heightInsertStatus.isSuccess();
    }

    /*
     * To save Users Weight
     */
    public boolean saveUserWeight(float weight) {
        // to post data
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        DataSet weightDataSet = createDataForRequest(DataType.TYPE_WEIGHT, // for
                DataSource.TYPE_RAW, weight, // weight in kgs
                startTime, // start time
                endTime, // end time
                TimeUnit.MILLISECONDS // Time Unit, for example,
                // TimeUnit.MILLISECONDS
        );

        Status weightInsertStatus = Fitness.HistoryApi
                .insertData(fitnessClient, weightDataSet).await(1,
                        TimeUnit.MINUTES);
        if (weightInsertStatus.isSuccess()) {
            // Log.e("Weight",weight+"Inserted");
        } else {
            // Log.e("Weight","inserted failed");
        }
        return weightInsertStatus.isSuccess();
    }

    private DataSet createDataForRequest(DataType dataType, int dataSourceType,
                                         Object values, long startTime, long endTime, TimeUnit timeUnit) {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context).setDataType(dataType)
                .setType(dataSourceType).build();

        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(
                startTime, endTime, timeUnit);

        if (values instanceof Integer) {
            dataPoint = dataPoint.setIntValues((Integer) values);
        } else {
            dataPoint = dataPoint.setFloatValues((Float) values);
        }

        dataSet.add(dataPoint);
        return dataSet;
    }


    public void setAuthInProgress(boolean authInProgress) {
        this.authInProgress = authInProgress;
    }

    /*
     * getting the steps count according to time
     */
    public int getStepsCount(long startTime, long endTime) {
        /*DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
				.setType(DataSource.TYPE_DERIVED)
				.setStreamName("estimated_steps")
				.setAppPackageName("com.google.android.gms").build();*/
        PendingResult<DataReadResult> pendingResult = Fitness.HistoryApi
                .readData(
                        fitnessClient,
                        new DataReadRequest.Builder()
                                .aggregate(DataType.TYPE_STEP_COUNT_DELTA,
                                        DataType.AGGREGATE_STEP_COUNT_DELTA)
                                .bucketByTime(1, TimeUnit.DAYS)
                                .setTimeRange(startTime, endTime,
                                        TimeUnit.MILLISECONDS)
                                .build());
        int steps = 0;
        DataReadResult dataReadResult = pendingResult.await();
        if (dataReadResult.getBuckets().size() > 0) {
            //Log.e("TAG", "Number of returned buckets of DataSets is: "
            //+ dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        for (Field field : dp.getDataType().getFields()) {
                            steps += dp.getValue(field).asInt();
                        }
                    }
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    for (Field field : dp.getDataType().getFields()) {
                        steps += dp.getValue(field).asInt();
                    }
                }
            }
        }
        return steps;
    }


    public boolean isConnecting() {
        return fitnessClient.isConnecting();
    }

    public boolean isConnected() {
        return fitnessClient.isConnected();
    }

    public void connect() {
        fitnessClient.connect();
    }

    public void disconnect() {
        fitnessClient.disconnect();
        fitnessClient = null;
    }
}
