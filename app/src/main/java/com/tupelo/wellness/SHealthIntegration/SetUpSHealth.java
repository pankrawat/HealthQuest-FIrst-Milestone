package com.tupelo.wellness.SHealthIntegration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;
import com.tupelo.wellness.activity.ChooseTracker;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.Constants;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 4/6/2017.
 */

public class SetUpSHealth {
    private static SetUpSHealth setUpSHealth;

    public Set<HealthPermissionManager.PermissionKey> mKeySet;
    public HealthDataStore mStore;
    public HealthConnectionErrorResult mConnError;
    Activity activity;
    int selector = 0;
    String sessionId = "", userId = "";


    public static SetUpSHealth getInstance() {
        if (setUpSHealth == null) {
            setUpSHealth = new SetUpSHealth();
        }
        return setUpSHealth;
    }

    public void init(Activity activity, int Selector) {
        this.activity = activity;
        this.selector = Selector;
        initialise();
    }


    public void initialise() {
        mKeySet = new HashSet<HealthPermissionManager.PermissionKey>();
        //mKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.Exercise.COUNT, HealthPermissionManager.PermissionType.READ));

        mKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.Exercise.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.FloorsClimbed.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));

        mKeySet.add(new HealthPermissionManager.PermissionKey(Constants.SHEALTH_STEP_DAILY_TREND, HealthPermissionManager.PermissionType.READ));
        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(activity, mConnectionListener);
        // Request the connection to the health data store
        mStore.connectService();
    }

    public void destroy() {
        if(mStore!=null) {
            mStore.disconnectService();
        }
    }

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            Log.d(Constants.SHEALTH_TAG, "Health data service is connected.");
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
            StepCountReporter.newInstance(mStore);

            try {
                // Check whether the permissions that this application needs are acquired
                Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);

                if (resultMap.containsValue(Boolean.FALSE)) {
                    // Request the permission for reading step counts if it is not acquired
                    if (selector == 0)
                        pmsManager.requestPermissions(mKeySet, activity).setResultListener(mPermissionListener);
                    else if (selector == 1)
                        GoToChooseTrackerActivity();
                } else {
                    // Get the current step count and display it
//                    mReporter.start();
                    if (selector == 0)
                        GoToTabActivity();
                    else if (selector == 1)
                        StepCountReporter.getInstance().start(activity);

                }
            } catch (Exception e) {
                if (selector == 1)
                    GoToChooseTrackerActivity();
                Log.e(Constants.SHEALTH_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(Constants.SHEALTH_TAG, "Permission setting fails.");
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(Constants.SHEALTH_TAG, "Health data service is not available.");
            if (selector == 0)
                showConnectionFailureDialog(error);
            else if (selector == 1)
                GoToChooseTrackerActivity();

        }

        @Override
        public void onDisconnected() {
            if (selector == 1)
                GoToChooseTrackerActivity();

            Log.d(Constants.SHEALTH_TAG, "Health data service is disconnected.");
        }
    };
    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                @Override
                public void onResult(HealthPermissionManager.PermissionResult result) {
                    Log.d(Constants.SHEALTH_TAG, "Permission callback is received.");
                    Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
//                        drawStepCount(null);
                        showPermissionAlarmDialog();
                    } else {
                        // Get the current step count and display it
                        GoToTabActivity();
                    }
                }
            };

    private void showPermissionAlarmDialog() {
        if (activity.isFinishing()) {
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Notice");
        alert.setMessage("All permissions should be acquired");
        alert.setPositiveButton("OK", null);
        alert.show();
    }


    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        mConnError = error;
        String message = "Connection with S Health is not available";

        if (mConnError.hasResolution()) {
            switch (error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install S Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade S Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable S Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with S Health policy";
                    break;
                default:
                    message = "Please make S Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnError.hasResolution()) {
                    mConnError.resolve(activity);
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }

    public void GoToChooseTrackerActivity() {
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        dbAdapter.deleteAllDevices();
        Intent intent = new Intent(activity, ChooseTracker.class);
        activity.startActivity(intent);
        ((TabActivity) activity).finish();
    }

    public void GoToTabActivity() {
        Intent intent;
        DbAdapter dbAdapter = DbAdapter.getInstance(activity);
        ContentValues contentValues = new ContentValues();
        contentValues.put("login", "true");
        dbAdapter.deleteAllDevices();
        dbAdapter.insertQuery(DbAdapter.TABLE_NAME_SHEALTH, contentValues);

        intent = new Intent(activity, TabActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.SERVICE, Constants.SHEALTH);

        activity.startActivity(intent);
    }
}
