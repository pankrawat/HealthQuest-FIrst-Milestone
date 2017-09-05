package com.tupelo.wellness.parcer;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.bean.CaloriesBean;
import com.tupelo.wellness.bean.DistanceBean;
import com.tupelo.wellness.bean.FloorBean;
import com.tupelo.wellness.bean.InfoStreamBean;
import com.tupelo.wellness.bean.LeaderBoardBean;
import com.tupelo.wellness.bean.LeaderListBean;
import com.tupelo.wellness.bean.StepsBean;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import android.util.Log;


public class Jsonparser {
    private JSONObject jsonObject;
    private final String TAG = Jsonparser.class.getName();

    public Jsonparser() {
    }

    public Jsonparser(String response) {
        try {
            jsonObject = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String setTupeloSignIn(Context mContext, String type) {
        DbAdapter dbAdapter = DbAdapter.getInstance(mContext);
        ContentValues contentValues = new ContentValues();
        try {
            if (!jsonObject.isNull("#data")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
                if (!jsonObject1.isNull("sessid")) {
                    contentValues.put("sessid", jsonObject1.getString("sessid"));
                }
                if (!jsonObject1.isNull("user")) {
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("user");
                    if (!jsonObject2.isNull("userId")) {
                        contentValues.put("userId", jsonObject2.getInt("userId"));
                    }
                    if (!jsonObject2.isNull("userName")) {
                        contentValues.put("userName", jsonObject2.getString("userName"));
                    }
                    if (!jsonObject2.isNull("corpId")) {
                        contentValues.put("corpId", jsonObject2.getString("corpId"));
                    }
                    /*if (!jsonObject2.isNull("userDevices")) {
                        JSONArray userDevices = jsonObject2.getJSONArray("userDevices");
                        JSONObject jobjDevices = userDevices.getJSONObject(0);
                        mContext.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE).edit()
                                .putString("devices", jobjDevices.getString("devices"))
                                .putString("devType", jobjDevices.getString("devType"))
                                .commit();
                    }*/


                    if (!jsonObject2.isNull("userDevices")) {
                        JSONArray userDevices = jsonObject2.getJSONArray("userDevices");
                        JSONObject jobjDevices = userDevices.getJSONObject(0);
                        SharedPreference sharedPreference = SharedPreference.getInstance(mContext);
                        sharedPreference.putString("devices", jobjDevices.getString("devices"));
                        sharedPreference.putString("devType", jobjDevices.getString("devType"));
                    }

                    if (type.equalsIgnoreCase("login")) {
                        contentValues.put("userAvatarURL", jsonObject2.getString("userAvatarURL"));
                    }
                }
                dbAdapter.deleteAll(DbAdapter.TABLE_NAME_TUPELO);
                dbAdapter.insertQuery(DbAdapter.TABLE_NAME_TUPELO, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public GetterSetter getDashboardInfo(Context context) {
        GetterSetter getterSetter = null;
        try {
            getterSetter = GetterSetter.getInstance();
            JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
            if (!jsonObject1.isNull("today")) {
                getterSetter.setToday_steps(jsonObject1.getString("today"));
            }
            if (!jsonObject1.isNull("yesterday")) {
                getterSetter.setYesterday_steps(jsonObject1.getString("yesterday"));
            }
            if (!jsonObject1.isNull("bestsingle")) {
                getterSetter.setBest_single(jsonObject1.getString("bestsingle"));
            }
            if (!jsonObject1.isNull("totalsteps")) {
                getterSetter.setTotal_steps(jsonObject1.getString("totalsteps"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getterSetter;
    }

    public GetterSetter getLeaderboardInfo(Context context) {
        GetterSetter getterSetter = null;
        try {
            getterSetter = GetterSetter.getInstance();
            JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
            if (!jsonObject1.isNull("rank")) {
                getterSetter.setRank(jsonObject1.getString("rank"));
            }
            if (!jsonObject1.isNull("steps")) {
                getterSetter.setSteps(jsonObject1.getString("steps"));
            }
            if (!jsonObject1.isNull("challenge_start")) {
                getterSetter.setChallenge_start(jsonObject1.getString("challenge_start"));
            }
            if (!jsonObject1.isNull("challenge_end")) {
                getterSetter.setChallenge_end(jsonObject1.getString("challenge_end"));
            }
            if (!jsonObject1.isNull("message")) {
                getterSetter.setMessage(jsonObject1.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getterSetter;
    }

    public List<LeaderBoardBean> getLeaderboardList(List<LeaderBoardBean> arr) {


        return arr;
    }

    public GetterSetter getLeaderCard(Context context) {
        GetterSetter getterSetter = null;
        try {
            getterSetter = GetterSetter.getInstance();
            JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
            if (!jsonObject1.isNull("name")) {
                getterSetter.setL_name(jsonObject1.getString("name"));
            }
            if (!jsonObject1.isNull("challenge_label")) {
                getterSetter.setL_stepLabel(jsonObject1.getString("challenge_label"));
            }
            if (!jsonObject1.isNull("rank")) {
                getterSetter.setL_rank(jsonObject1.getString("rank"));
            }
            if (!jsonObject1.isNull("steps")) {
                getterSetter.setL_steps(jsonObject1.getString("steps"));
            }
            if (!jsonObject1.isNull("challenge_start")) {
                getterSetter.setL_challenge_start(jsonObject1.getString("challenge_start"));
            }
            if (!jsonObject1.isNull("challenge_end")) {
                getterSetter.setL_challenge_end(jsonObject1.getString("challenge_end"));
            }
            if (!jsonObject1.isNull("challenge_unit")) {
                getterSetter.setL_challenge_unit(jsonObject1.getString("challenge_unit"));
            }
            if (!jsonObject1.isNull("message")) {
                getterSetter.setL_message(jsonObject1.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getterSetter;
    }

    public ArrayList<LeaderListBean> getLeaderList() {
        ArrayList<LeaderListBean> arr = new ArrayList<LeaderListBean>();
        try {
            JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
            JSONArray jsonArray = jsonObject1.getJSONArray("content");
            for (int i = 0; i < jsonArray.length(); i++) {
                LeaderListBean lead_list = new LeaderListBean();
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                if (!jsonObject2.isNull("name")) {
                    lead_list.setName(jsonObject2.getString("name"));
                }
                if (!jsonObject2.isNull("steps")) {
                    lead_list.setSteps(jsonObject2.getString("steps"));
                }
                if (!jsonObject2.isNull("photourl")) {
                    lead_list.setPhotoUrl(jsonObject2.getString("photourl"));
                }
                if (!jsonObject2.isNull("uid")) {
                    lead_list.setUid(jsonObject2.getString("uid"));
                }
                if (!jsonObject2.isNull("total")) {
                    lead_list.setTotal(jsonObject2.getString("total"));
                }
                if (!jsonObject2.isNull("participants")) {
                    lead_list.setParticipants(jsonObject2.getString("participants"));
                }
                arr.add(lead_list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    public GetterSetter getSignUpInfo(Context mContext) {
        GetterSetter getterSetter = null;
        JSONObject jsonObject2 = null;
        try {
            getterSetter = GetterSetter.getInstance();
            JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
            if (!jsonObject1.isNull("sessid")) {
                getterSetter.setSessionid(jsonObject1.getString("sessid"));
            }
            if (!jsonObject1.isNull("user")) {
                jsonObject2 = jsonObject1.getJSONObject("user");
            }
            if (!jsonObject2.isNull("userId")) {
                getterSetter.setUserid(jsonObject2.getString("userId"));
            }
            if (!jsonObject2.isNull("userName")) {
                getterSetter.setUsername(jsonObject2.getString("userName"));
            }
            if (!jsonObject2.isNull("corpId")) {
                getterSetter.setCorpid(jsonObject2.getString("corpId"));
            }
            setTupeloSignIn(mContext, "signup");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getterSetter;
    }

    public ArrayList<InfoStreamBean> getInfoStream() {
        ArrayList<InfoStreamBean> arrayList = new ArrayList<InfoStreamBean>();
        try {
            JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
            int msgcount = jsonObject1.getInt("messagecount");
            if (msgcount > 0) {
                JSONArray jsonArray = jsonObject1.getJSONArray("challengemessages");
                for (int i = 0; i < msgcount; i++) {
                    InfoStreamBean infoStreamBean = new InfoStreamBean();
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    if (!jsonObject2.isNull("title")) {
                        infoStreamBean.setTitle(jsonObject2.getString("title"));
                    }
                    if (!jsonObject2.isNull("message")) {
                        infoStreamBean.setMessage(jsonObject2.getString("message"));
                    }
                    if (!jsonObject2.isNull("photourl")) {
                        infoStreamBean.setPhotourl(jsonObject2.getString("photourl"));
                    }
                    arrayList.add(infoStreamBean);
                }
            }
            int infomsgcount = jsonObject1.getInt("infomsgcount");
            if (infomsgcount > 0) {
                JSONArray jsonArray = jsonObject1.getJSONArray("infomessages");
                for (int i = 0; i < infomsgcount; i++) {
                    InfoStreamBean infoStreamBean = new InfoStreamBean();
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    if (!jsonObject2.isNull("title")) {
                        infoStreamBean.setTitle(jsonObject2.getString("title"));
                    }
                    if (!jsonObject2.isNull("message")) {
                        infoStreamBean.setMessage(jsonObject2.getString("message"));
                    }
                    if (!jsonObject2.isNull("photourl")) {
                        infoStreamBean.setPhotourl(jsonObject2.getString("photourl"));
                    }
                    arrayList.add(infoStreamBean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public ArrayList<StepsBean> getFitbitArray() {
        ArrayList<StepsBean> stepsArray = new ArrayList<StepsBean>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("activities-tracker-steps");
            for (int i = 0; i < jsonArray.length(); i++) {
                StepsBean stepsBean = new StepsBean();
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                if (!jsonObject2.isNull("dateTime")) {
                    stepsBean.setDate(jsonObject2.getString("dateTime"));
                }
                if (!jsonObject2.isNull("value")) {
                    stepsBean.setSteps(jsonObject2.getString("value"));
                }
                stepsArray.add(stepsBean);
            }
            return stepsArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<CaloriesBean> getFitbitCaloriesArray() {
        ArrayList<CaloriesBean> stepsArray = new ArrayList<CaloriesBean>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("activities-tracker-calories");
            for (int i = 0; i < jsonArray.length(); i++) {
                CaloriesBean stepsBean = new CaloriesBean();
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                if (!jsonObject2.isNull("dateTime")) {
                    stepsBean.setDate(jsonObject2.getString("dateTime"));
                }
                if (!jsonObject2.isNull("value")) {
                    stepsBean.setSteps(jsonObject2.getString("value"));
                }
                stepsArray.add(stepsBean);
            }
            return stepsArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<DistanceBean> getFitbitDistanceArray() {
        ArrayList<DistanceBean> stepsArray = new ArrayList<DistanceBean>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("activities-tracker-distance");
            for (int i = 0; i < jsonArray.length(); i++) {
                DistanceBean stepsBean = new DistanceBean();
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                if (!jsonObject2.isNull("dateTime")) {
                    stepsBean.setDate(jsonObject2.getString("dateTime"));
                }
                if (!jsonObject2.isNull("value")) {
                    stepsBean.setDistance(jsonObject2.getString("value"));
                }
                stepsArray.add(stepsBean);
            }
            return stepsArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<FloorBean> getFitbitFloorArray() {
        ArrayList<FloorBean> stepsArray = new ArrayList<FloorBean>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("activities-tracker-floors");
            for (int i = 0; i < jsonArray.length(); i++) {
                FloorBean stepsBean = new FloorBean();
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                if (!jsonObject2.isNull("dateTime")) {
                    stepsBean.setDate(jsonObject2.getString("dateTime"));
                }
                if (!jsonObject2.isNull("value")) {
                    stepsBean.setFloor(jsonObject2.getString("value"));
                }
                stepsArray.add(stepsBean);
            }
            return stepsArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getJawboneError() {
        try {
            if (!jsonObject.isNull("meta")) {
                JSONObject meta = jsonObject.getJSONObject("meta");
                String message = meta.getString("message");
                if (message.equalsIgnoreCase("ok")) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getJawboneRefreshToken() {
        try {
            if (!jsonObject.isNull("meta")) {
                JSONObject meta = jsonObject.getJSONObject("meta");
                String message = meta.getString("message");
                if (message.equalsIgnoreCase("ok")) {
                    if (!jsonObject.isNull("meta")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String refresh_token = data.getString("refresh_token");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<StepsBean> getJawboneArray() {
        ArrayList<StepsBean> stepsArray = new ArrayList<StepsBean>();
        try {
            if (!jsonObject.isNull("data")) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (!data.isNull("items")) {
                    JSONArray items = data.getJSONArray("items");
                    int size = 7 < items.length() ? 7 : items.length();
                    for (int i = 0; i < size; i++) {
                        StepsBean stepsBean = new StepsBean();
                        if (!items.isNull(i)) {
                            JSONObject jsonObject1 = items.getJSONObject(i);
                            if (!jsonObject1.isNull("date")) {
//                                Log.d("jawbone date", jsonObject1.toString());
                                //Log.d("jawbone date", jsonObject1.getLong("date"));
                                SimpleDateFormat sdfParse = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                Date date = sdfParse.parse(String.valueOf(jsonObject1.getLong("date")));
                                stepsBean.setDate(sdf.format(date.getTime()));
                            }
                            if (!jsonObject1.isNull("details")) {
                                JSONObject details = jsonObject1.getJSONObject("details");
                                if (!details.isNull("steps")) {
                                    stepsBean.setSteps(String.valueOf(details.getLong("steps")));
                                }
                            }
                        }
                        stepsArray.add(stepsBean);
                    }
                }
            }
            return stepsArray;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String parseErrorMsg() {
        String s = null;
        try {
            s = jsonObject.getString("#data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    public boolean getErrorMsg() {
        try {
            if (!jsonObject.getBoolean("#error")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getError() {
        try {
            JSONObject object = jsonObject.getJSONObject("#data");
            if (object.isNull("#error"))
                return "0";
            else {
                String msg = object.getString("#message");
                return msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-3";
    }

    public String getErrorCodePersonalDetail() {
        try {
            if (!jsonObject.getBoolean("#error"))
                return "0";
            else
                return "1";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-3";
    }


    public boolean forgetPassword() {
        try {
            return jsonObject.getBoolean("#data");
        } catch (Exception exception) {
            Log.e(TAG, "could not parse data ");
        }
        return false;
    }

    public String[] getProfileImagefid() {
        String fid = null;
        String path = null;
        try {
            JSONObject object2 = jsonObject.getJSONObject("#data");
            fid = object2.getString("fid");
            path = object2.getString("path");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String[] returndedValue = {fid, path};
        return returndedValue;
    }

    public String getErrorCodeAddDevice() {
        try {
            JSONObject obj = jsonObject.getJSONObject("#data");
            if (!obj.isNull("added")) {
                if (obj.getBoolean("added"))
                    return "0";
                else
                    return "1";
            } else if (!obj.isNull("#message")) {
                String msg = obj.getString("#message");
                if (msg.equals("793"))
                    return "2";
                else if (msg.equals("593"))
                    return "3";
                else if (msg.equals("693"))
                    return "4";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-3";
    }

    public boolean getChallengeCategory() {
        boolean challengeCategory = false;
        try {
            JSONObject obj = jsonObject.getJSONObject("#data");

            Log.e(TAG," category is " + obj.getString("challenge_cat"));
            if(obj.getString("challenge_cat").equalsIgnoreCase("2"))
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return challengeCategory;
    }
}
