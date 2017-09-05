package com.tupelo.wellness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.tupelo.wellness.database.DbAdapter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Abhishek Singh Arya on 10-09-2015.
 */
public class GetAuthToken implements Serializable{
    //private Uri uriData;
    private String USER_ID, TOKEN_TYPE, EXPIRES_IN, ACCESS_TOKEN;

    public GetAuthToken(Context context, Uri uriData) {

        Log.e("fooooooooooo","inside get auth token uri data" + uriData);

        try {

            Map<String, String> queryParams = splitQuery(uriData.toString());

            Log.e("Get Auth Token"," params are  " + queryParams.toString());


            USER_ID = queryParams.get("user_id");

            TOKEN_TYPE = queryParams.get("token_type");

            EXPIRES_IN = queryParams.get("expires_in");

            ACCESS_TOKEN = queryParams.get("access_token");

            setFitbitSignIn(context);


            Log.e("foooooooooooo","the user id is " + USER_ID + " " + "token type is " + TOKEN_TYPE + " Expired in is " + EXPIRES_IN + "Access token is " + ACCESS_TOKEN);


        }

        catch (UnsupportedEncodingException u)
        {
            u.printStackTrace();
        }

/*
     String[] data = String.valueOf(uriData).split("&");
        String Param = data[1];
        String[] singleParam = Param.split("=");


        USER_ID = singleParam[1];


        Param = data[2];
        singleParam = Param.split("=");
        TOKEN_TYPE = singleParam[1];


        Param = data[3];
        singleParam = Param.split("=");
        EXPIRES_IN = singleParam[1];



        Param = data[4];
        singleParam = Param.split("=");
        ACCESS_TOKEN = singleParam[1];
*/


  //


    }

    public GetAuthToken(Context context){
        Log.e("Get Auth Token","hellow 1");
        getFitbitSignIn(context);
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getTOKEN_TYPE() {
        return TOKEN_TYPE;
    }

    public String getEXPIRES_IN() {
        return EXPIRES_IN;
    }

    public String getACCESS_TOKEN() {
        return ACCESS_TOKEN;
    }

    public void setFitbitSignIn(Context mContext){

        Log.e("Get Auth Token","inside uri data fitbit sign in");
        DbAdapter dbAdapter = DbAdapter.getInstance(mContext);
        ContentValues contentValues = new ContentValues();
        try{
            contentValues.put(DbAdapter.COLUMN_FITBIT_USER_ID, USER_ID);
            contentValues.put(DbAdapter.COLUMN_FITBIT_TOKEN_TYPE, TOKEN_TYPE);
            contentValues.put(DbAdapter.COLUMN_FITBIT_EXPIRES_IN, EXPIRES_IN);
            contentValues.put(DbAdapter.COLUMN_FITBIT_ACCESS_TOKEN, ACCESS_TOKEN);

            Calendar calendar = Calendar.getInstance();
            long exp = Long.parseLong(EXPIRES_IN);
            calendar.add(Calendar.SECOND, (int) exp);
//            } else if(exp>432000){
//                calendar.add(Calendar.SECOND, (int) (exp-172800));
//            } else if(exp>79200){
//                calendar.add(Calendar.SECOND, (int) (exp-7200));
//            } else {
//
//            }

//                switch (EXPIRES_IN){
//                case "2592000":
//                    calendar.add(Calendar.SECOND, 2160000);
//                    break;
//                case "604800":
//                    calendar.add(Calendar.SECOND, 432000);
//                    break;
//                case "86400":
//                    calendar.add(Calendar.SECOND, 86000);
//                    break;
//            }
            contentValues.put(DbAdapter.COLUMN_FITBIT_EXPIRY_DATE, String.valueOf(calendar.getTimeInMillis()));

            Log.e("Get Auth Token","inserting table firbit");
            dbAdapter.deleteAll(DbAdapter.TABLE_NAME_FITBIT);
            dbAdapter.insertQuery(DbAdapter.TABLE_NAME_FITBIT, contentValues);

           Log.e("Get Auth Token"," table fit bit is present" +  dbAdapter.isLogin(DbAdapter.TABLE_NAME_FITBIT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFitbitSignIn(Context mContext) {

        Log.e("Get Auth Token","hellow 2");
        DbAdapter dbAdapter = DbAdapter.getInstance(mContext);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_FITBIT);
        if(cursor.getCount()>0){

            Log.e("Get Auth Token","hellow 3");
            USER_ID = cursor.getString(0);
            TOKEN_TYPE = cursor.getString(1);
            EXPIRES_IN = cursor.getString(2);
            ACCESS_TOKEN = cursor.getString(3);
        }
    }



    public static Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {

        int index  = url.indexOf("#");
        if(index!=-1)
            url = url.substring(index+1);

        Map<String, String> query_pairs = new LinkedHashMap<String, String>();

        String[] pairs = url.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
