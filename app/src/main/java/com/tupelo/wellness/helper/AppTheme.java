package com.tupelo.wellness.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tupelo.wellness.R;

/**
 * Created by appsquadz on 22/6/16.
 */
public class AppTheme {
    public String colorPrimary;
    public static int colorPrimaryResource;
    public String colorPrimaryDark;
    public String colorAccent = "#444444";
    public static int blackFade = Color.parseColor("#2b2b2c");

    public static AppTheme appTheme;
    private String TAG = AppTheme.class.getSimpleName();

    public AppTheme(Context context) {

        if(colorPrimary == null) {
            SharedPreferences prefs = context.getSharedPreferences("MyCorpProfile", Context.MODE_PRIVATE);
            String color = prefs.getString("colorcode", "1");
            AppTheme app = AppTheme.getInstance();
            app.setAppTheme(Integer.parseInt(color));
        }
    }

    public AppTheme()
    {}

    public static AppTheme getInstance(){
        if(appTheme == null){
            appTheme = new AppTheme();
        }
        return appTheme;
    }

    public static AppTheme getInstance(Context context){
        if(appTheme == null){
            appTheme = new AppTheme(context);
        }
        return appTheme;
    }

    public void setAppTheme(int themeCode){
        Log.e(TAG,"theme code is " + themeCode);
        switch (themeCode){
            case 1:
                colorPrimary = "#f44336";
                colorPrimaryDark = "#e03327";
                break;
            case 2:
                colorPrimary = "#e91363";
                colorPrimaryDark = "#bf1555";
                break;
            case 3:
                colorPrimary = "#9c27b0";
                colorPrimaryDark = "#7e0f91";
                break;
            case 4:
                colorPrimary = "#673ab7";
                colorPrimaryDark = "#52279e";
                break;
            case 5:
                colorPrimary = "#3f51b5";
                colorPrimaryDark = "#2336a0";
                break;
            case 6:
                colorPrimary = "#2196f3";
                colorPrimaryDark = "#197ac7";
                break;
            case 7:
                colorPrimary = "#03a9f4";
                colorPrimaryDark = "#008fd0";
                break;
            case 8:
                colorPrimary = "#00bcd4";
                colorPrimaryDark = "#0099ad";
                break;
            case 9:
                colorPrimary = "#009688";
                colorPrimaryDark = "#006a60";
                break;
            case 10:
                colorPrimary = "#4caf50";
                colorPrimaryDark = "#338d37";
                break;
            case 11:
                colorPrimary = "#ff9800";
                colorPrimaryDark = "#d37e00";
                break;
            case 12:
                colorPrimary = "#ff5722";
                colorPrimaryDark = "#e4420f";
                break;
            case 13:
                colorPrimary = "#795548";
                colorPrimaryDark = "#624236";
                break;
            case 14:
                colorPrimary = "#607d8b";
                colorPrimaryDark = "#4a6471";
                break;
            case 15:
                colorPrimary = "#252525";
                colorPrimaryDark = "#000000";
                break;
            case 16:
                colorPrimary = "#ffc107";
                colorPrimaryDark = "#d4a005";
                break;

        }
    }

    public static TextView changeBackgroundColor(TextView v) {

        v.setTextColor(Color.WHITE);
        v.setBackgroundResource(R.drawable.rounded_corder_fully_transparent);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        return v;
    }
    public static Button changeBackgroundColor(Button v) {

        v.setTextColor(Color.WHITE);
        v.setBackgroundResource(R.drawable.rounded_corder_fully_transparent);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        return v;
    }
    public static FrameLayout changeBackgroundColor(FrameLayout v) {

        v.setBackgroundResource(R.drawable.rounded_corner_help2);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        return v;
    }

    public static ImageView changeBackgroundColor(ImageView v) {

        v.setBackgroundResource(R.drawable.rounded_button);

        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        return v;
    }
   /* public static int getAppThemeResourceId(int id)
    {
        switch (id){
            case 1:
                colorPrimaryResource = R.color.comp1;
                break;
            case 2:
                colorPrimaryResource = R.color.comp2;
                break;
            case 3:
                colorPrimaryResource = R.color.comp3;
                break;
            case 4:
                colorPrimaryResource = R.color.comp4;
                break;
            case 5:
                colorPrimaryResource = R.color.comp5;
                break;
            case 6:
                colorPrimaryResource = R.color.comp6;
                break;
            case 7:
                colorPrimaryResource = R.color.comp7;
                break;
            case 8:
                colorPrimaryResource = R.color.comp8;
                break;
            case 9:
                colorPrimaryResource = R.color.comp9;
                break;
            case 10:
                colorPrimaryResource = R.color.comp10;
                break;
            case 11:
                colorPrimaryResource = R.color.comp11;
                break;
            case 12:
                colorPrimaryResource = R.color.comp12;
                break;
            case 13:
                colorPrimaryResource = R.color.comp13;
                break;
            case 14:
                colorPrimaryResource = R.color.comp14;
                break;
            case 15:
                colorPrimaryResource = R.color.comp15;
                break;
            case 16:
                colorPrimaryResource = R.color.comp16;
                break;

        }
        return colorPrimaryResource;

    }*/

}
