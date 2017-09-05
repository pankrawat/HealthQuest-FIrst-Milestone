package com.tupelo.wellness.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 29/10/2015.
 */
public class SharedPreference {
    public static final String MY_PREFERENCES = "MY_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;
    private static SharedPreference pref;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    private SharedPreference(Context context) {
        sharedPreference = context.getSharedPreferences(MY_PREFERENCES, MODE);
        editor = sharedPreference.edit();
    }

    public static SharedPreference getInstance(Context context) {
        if (pref == null) {
            pref = new SharedPreference(context);
        }
        return pref;
    }

    public String getString(String key, String defValue) {
        return sharedPreference.getString(key, defValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }


    public int getInt(String key, int defValue) {
        return sharedPreference.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }


    public long getLong(String key, long defValue) {
        return sharedPreference.getLong(key, defValue);
    }


    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }


    public float getFloat(String key, float defValue) {
        return sharedPreference.getFloat(key, defValue);
    }


    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        Log.e("SharedPrefrence", "Returned the key is " + key + " the value is " + sharedPreference.getBoolean(key, defValue));
    //    editor.putBoolean(key, value).commit();
        return sharedPreference.getBoolean(key, defValue);
    }


    public void putBoolean(String key, boolean value) {
        Log.e("SharedPrefrence","the key is " + key + " the value is "+value);
        editor.putBoolean(key, value).commit();
    }


    public boolean contains(String key) {
        return sharedPreference.contains(key);
    }

    public void remove(String key) {
        editor.remove(key).commit();
    }
}
