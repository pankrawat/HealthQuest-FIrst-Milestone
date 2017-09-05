package com.tupelo.wellness.activity;

/**
 * Created by Abhishek Singh Arya on 11-09-2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.tupelo.wellness.helper.Constants;

public class BaseActivity extends FragmentActivity{

    /**
     *
     BaseActivity class
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        registerReceiver(receiver,new IntentFilter(Constants.KILL_ACTIVITY));

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

    }

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(Constants.KILL_ACTIVITY)){
                BaseActivity.this.finish();
            }
        }
    };


}
