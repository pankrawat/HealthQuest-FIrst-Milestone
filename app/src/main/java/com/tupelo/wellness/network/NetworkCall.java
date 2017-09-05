package com.tupelo.wellness.network;

/**
 * Created by Abhishek Singh Arya on 11-09-2015.
 */


import com.tupelo.wellness.bean.CaloriesBean;
import com.tupelo.wellness.bean.DistanceBean;
import com.tupelo.wellness.bean.FloorBean;
import com.tupelo.wellness.bean.StepsBean;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

//import android.util.Log;


public class NetworkCall {


    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    private final String TAG = NetworkCall.class.getName();


    public String fitbit(String url, String key) {

        params.clear();
        params.add(new BasicNameValuePair("Authorization", key));

        String response = new NetworkConnection().networkHit(params, url);
        return response;

    }





    public String getPersonalDashboard(String sessid, String userid, String corpid, String date) {
        String method = "corpchallenge.getpersonaldashboard";
        params.clear();

        addRequiredParams(method);

        params.add(new BasicNameValuePair("method", method));
        params.add(new BasicNameValuePair("sessid", sessid));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("corpid", corpid));
        params.add(new BasicNameValuePair("date", date));

        String response = new NetworkConnection().networkHit(params, Constants.BASE_URL);
        return response;

    }

    /******
     * logout
     ***/


    private void addRequiredParams(String method) {
        Helper helper = new Helper();
        String time_stamp = helper.getUnixTimestamp();
        params.add(new BasicNameValuePair("hash", helper.getHashValue(Constants.API_KEY, Constants.DOMAIN, time_stamp, method)));
        params.add(new BasicNameValuePair("domain_name", Constants.DOMAIN));
        params.add(new BasicNameValuePair("domain_time_stamp", time_stamp));
        params.add(new BasicNameValuePair("nonce", time_stamp));
    }

    /*
     * This handles the initial POST to JSON Server to retrieve the sessid.
*/





    public String setTimeZoneForUser(String userid, String sessid, String timezone) {

        String method = "userV2.timezone";
        params.clear();
        addRequiredParams(method);
        params.add(new BasicNameValuePair("uid", userid));
        params.add(new BasicNameValuePair("timezone", timezone));
        params.add(new BasicNameValuePair("method", method));
        params.add(new BasicNameValuePair("sessid", sessid));
        String response = new NetworkConnection().networkHit(params, Constants.BASE_URL);
        return response;
    }

    public String setStepsToMymo(String sessid, String userid, String imei, String serial, String apitoken, String apitype, ArrayList<StepsBean> stepsArray) {

        String method = "hmm_device_data.add_data_thirdparty";

        String dataType = "101";

        StringBuilder stringBuilder = new StringBuilder("{\"userid\":\"" + userid + "\",\"dataType\":\"" + dataType + "\",\"imei\":\"" + imei + "\",\"serial\":\"" + serial + "\",\"apitoken\":\"" + apitoken + "\",\"apitype\":\"" + apitype + "\",\"data\":[");
        for (int i = 0; i < stepsArray.size(); i++) {
            String datetime = stepsArray.get(i).getDate() + " 00:00:00";
            String steps = stepsArray.get(i).getSteps();
            String data;
            if (i == stepsArray.size() - 1) {
                  data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\"null\",\"distance\":\"null\",\"floors\":\"null\"}";
               // data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+String.valueOf(calories)+"\"}";

            } else {
                   data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\"null\"},";
            //    data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+String.valueOf(calories)+"\"},";

            }
            stringBuilder.append(data);
        }
        stringBuilder.append("]}");

//        Log.d("stringbuilder", stringBuilder.toString());
        params.clear();
        addRequiredParams(method);
        params.add(new BasicNameValuePair("jsonObj", stringBuilder.toString()));
        params.add(new BasicNameValuePair("method", method));
        params.add(new BasicNameValuePair("sessid", sessid));
        String response = new NetworkConnection().networkHit(params, Constants.BASE_URL);
        return response;
    }

    public String setStepsToMymo(String sessid, String userid, String imei, String serial, String apitoken, String apitype, ArrayList<StepsBean> stepsArray, ArrayList<CaloriesBean> caloriesArray, ArrayList<DistanceBean> distanceArray, ArrayList<FloorBean> floorArray) {

        String method = "hmm_device_data.add_data_thirdparty";

        String dataType = "101";

        StringBuilder stringBuilder = new StringBuilder("{\"userid\":\"" + userid + "\",\"dataType\":\"" + dataType + "\",\"imei\":\"" + imei + "\",\"serial\":\"" + serial + "\",\"apitoken\":\"" + apitoken + "\",\"apitype\":\"" + apitype + "\",\"data\":[");
        for (int i = 0; i < stepsArray.size(); i++) {
            String datetime = stepsArray.get(i).getDate() + " 00:00:00";
            String steps = stepsArray.get(i).getSteps();
            String distance= distanceArray.get(i).getDistance();
            String calories=caloriesArray.get(i).getSteps();
            String floor=floorArray.get(i).getFloor();
            String data;
            if (i == stepsArray.size() - 1) {
                data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+calories+"\",\"distance\":\""+distance+"\",\"floors\":\""+floor+"\"}";
                // data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+String.valueOf(calories)+"\"}";

            } else {
                data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+calories+"\",\"distance\":\""+distance+"\",\"floors\":\""+floor+"\"}";
                //    data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+String.valueOf(calories)+"\"}";

            }
            stringBuilder.append(data);
        }
        stringBuilder.append("]}");

//        Log.d("stringbuilder", stringBuilder.toString());
        params.clear();
        addRequiredParams(method);
        params.add(new BasicNameValuePair("jsonObj", stringBuilder.toString()));
        params.add(new BasicNameValuePair("method", method));
        params.add(new BasicNameValuePair("sessid", sessid));
        String response = new NetworkConnection().networkHit(params, Constants.BASE_URL);
        return response;
    }


    public String setStepsToMymo(String sessid, String userid, String imei, String serial, String apitoken, String apitype, ArrayList<StepsBean> stepsArray, ArrayList<CaloriesBean> caloriesArray, ArrayList<DistanceBean> distanceArray) {
        String response="";
        String method = "hmm_device_data.add_data_thirdparty";

        String dataType = "101";

        StringBuilder stringBuilder = new StringBuilder("{\"userid\":\"" + userid + "\",\"dataType\":\"" + dataType + "\",\"imei\":\"" + imei + "\",\"serial\":\"" + serial + "\",\"apitoken\":\"" + apitoken + "\",\"apitype\":\"" + apitype + "\",\"data\":[");
        for (int i = 0; i < distanceArray.size(); i++) {
        //    if (stepsArray.size() < i && distanceArray.size() < i && caloriesArray.size() < i) {
                String datetime = stepsArray.get(i).getDate() + " 00:00:00";
                String steps = stepsArray.get(i).getSteps();
                String distance = distanceArray.get(i).getDistance();
                String calories = caloriesArray.get(i).getSteps();
                String data;
                if (i == distanceArray.size() - 1) {
                    data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\"" + calories + "\",\"distance\":\"" + distance + "\",\"floors\":\"null\"}";
                    // data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+String.valueOf(calories)+"\"}";

                } else {
                    data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\"" + calories + "\",\"distance\":\"" + distance + "\",\"floors\":\"null\"},";
                    //    data = "{\"dateTime\":\"" + datetime + "\",\"steps\":\"" + steps + "\",\"met\":\"null\",\"calories\":\""+String.valueOf(calories)+"\"}";

                }
                stringBuilder.append(data);
            }
            stringBuilder.append("]}");

//        Log.d("stringbuilder", stringBuilder.toString());
            params.clear();
            addRequiredParams(method);
            params.add(new BasicNameValuePair("jsonObj", stringBuilder.toString()));
            params.add(new BasicNameValuePair("method", method));
            params.add(new BasicNameValuePair("sessid", sessid));
             response = new NetworkConnection().networkHit(params, Constants.BASE_URL);
       //  }
            return response;
        }

}
