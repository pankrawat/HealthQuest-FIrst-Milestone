package com.tupelo.wellness.helper;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Abhishek Singh Arya on 29/10/2015.
 */
public class SharedPref {
    public static final String MY_PREFERENCES = Constants.MY_PREFERENCES;

    public String getString(Context context, String key, String defValue) {
        return context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getString(key, defValue);
    }

    public String getString(Activity activity, String key, String defValue) {
        return activity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getString(key, defValue);
    }

    public int getInt(Activity activity, String key, int defValue) {
        return activity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getInt(key, defValue);
    }

    public int getInt(Context context, String key, int defValue) {
        return context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getInt(key, defValue);
    }

    public long getLong(Context context, String key, long defValue) {
        return context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getLong(key, defValue);
    }

    public long getLong(Activity activity, String key, long defValue) {
        return activity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getLong(key, defValue);
    }

    public float getFloat(Context context, String key, float defValue) {
        return context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getFloat(key, defValue);
    }

    public float getFloat(Activity activity, String key, float defValue) {
        return activity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getFloat(key, defValue);
    }

    public boolean getBoolean(Context context, String key, boolean defValue) {
        return context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }

    public boolean getBoolean(Activity activity, String key, boolean defValue) {
        return activity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }

    public boolean contains(Context context, String key) {
        return context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).contains(key);
    }

    public boolean contains(Activity activity, String key) {
        return activity.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).contains(key);
    }
}
