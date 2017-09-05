package com.tupelo.wellness.helper;

/**
 * Created by Abhishek Singh Arya on 11-09-2015.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.OnBoard.OnboarderActivity;
import com.tupelo.wellness.R;
import com.tupelo.wellness.activity.CompanyCodeActivity;
import com.tupelo.wellness.activity.SplashScreen;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.activity.WebViewActivity;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.parcer.Jsonparser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class Helper {
    private static final String TAG = Helper.class.getName();
    final private static int MAGICNUMBER_PM = 0xA2E9324C;
    final private static int MAGICNUMBER_BL = 0x92E9324C;
    final private static int MAGICNUMBER_HR = 0xE2E9324C;
    DbAdapter dbAdapter;
    Cursor cursor;

    public static String Html_Pattern = ".*<[^>]+>.*";
    public static SimpleDateFormat simpleDateFormatUS = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static SimpleDateFormat simpleDateFormatDefault = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static SimpleDateFormat monthyearDefault = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());


    public static SimpleDateFormat sdfUS = new SimpleDateFormat("dd MMMM yyyy , hh:mm aa", Locale.US);
    public static SimpleDateFormat sdfDefault = new SimpleDateFormat("dd MMMM yyyy , hh:mm aa", Locale.getDefault());

    public static SimpleDateFormat sdfparserUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public static SimpleDateFormat sdfparserDefault = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static void makeLinkClickable(final Context context, SpannableStringBuilder strBuilder, final URLSpan span) {
        Log.e(TAG, "the span is " + span.toString());
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", span.getURL());
                context.startActivity(intent);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static void makeLinkClickableWithoutHTML(final Context context, SpannableStringBuilder strBuilder, final String span, String html) {
        Log.e(TAG, "the span is " + span.toString());
        int start = html.indexOf(span);
        int end = start + span.length();
        int flags = 0;
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", span.toString());
                context.startActivity(intent);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static void setTextViewHTML(Context context, TextView text, String html) {

        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        Log.e(TAG, "html is" + html);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(context, strBuilder, span);
        }
        text.setText(strBuilder);
        text.setLinksClickable(true);
        text.setLinkTextColor(Color.BLUE);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setTextViewWithoutHTML(Context context, TextView text, String html) {
        CharSequence sequence = html;
        int i = 0;
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        Log.e(TAG, "html is" + html);
        Matcher m = Patterns.WEB_URL.matcher(html);
        while (m.find()) {
            String url = m.group();
            Log.d(TAG, "URL extracted: " + url);
            makeLinkClickableWithoutHTML(context, strBuilder, url, html);
            i++;
        }
        if (i > 0) {
            text.setText(strBuilder);
            text.setLinksClickable(true);
            text.setLinkTextColor(Color.BLUE);
            text.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            text.setText(html);
            text.setLinksClickable(false);
            text.setLinkTextColor(Color.BLUE);
        }
    }

    public static void logoutVolley(final Context context, final ProgressDialog pDialog, final View view) {

        pDialog.show();
        final DbAdapter dbAdapter = DbAdapter.getInstance(context);

        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

        if (cursor.getCount() > 0) {
            final String sessionId = cursor.getString(0);
            final String userId = cursor.getString(1);

            String url = Constants.BASE_URL;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pDialog.hide();
                            Log.e(TAG, "the response of logout" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String error = jsonObject.getString("#error");
                                if (error.equalsIgnoreCase("false")) {
                                    String data = jsonObject.getString("#data");
                                    boolean s = false;

                                    if (data.equalsIgnoreCase("true")) {

                                        //   unregister();

                                        SharedPreferences preferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.clear();
                                        editor.commit();


                                        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_TUPELO);
                                        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_FITBIT);
                                        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_MYMO);
                                        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_GARMIN);
                                        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_JAWBONE);
                                        dbAdapter.deleteAll(DbAdapter.TABLE_NAME_PEDOMETER);
                                        context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE).edit().remove(Constants.BITMAP).commit();
                                        context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE).edit().remove("devices").commit();
                                        context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE).edit().remove("devType").commit();
                                        SharedPreference sharedPreference = SharedPreference.getInstance(context);
                                        sharedPreference.remove(Constants.IS_RECORDING_STARTED);

                                        editor = context.getSharedPreferences("MyCorpProfile", Context.MODE_PRIVATE).edit();

                                        editor.clear();
                                        editor.commit();

                                        editor = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).edit();

                                        editor.clear();
                                        editor.commit();

                                        if (view != null && context instanceof TabActivity) {
                                            ((TabActivity) context).blackenTheView(view);
                                        }


                                        Intent intent = new Intent(context, CompanyCodeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        ((Activity) context).finish();
                                    } else {
                                        Toast.makeText(context, "Error in logout", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.hide();
                            Toast.makeText(context, R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                            Log.e("ERROR", "error => " + error.getMessage());
                        }
                    }
            ) {
                // this is the relevant method
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();


                    String method = "user.logout_user";
                    ;
                    params = new Helper().addRequiredParams(params, method);
                    params.put("method", method);
                    params.put("userid", userId);
                    params.put("sesid", sessionId);
                    params.put("sessid", sessionId);

                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().

                    addToRequestQueue(postRequest);
        }

    }


    public static void mymoSerialCheck(int flag, Context context) {

        if (flag == 1) {
            SharedPreference pref = SharedPreference.getInstance(context);
            String editText = pref.getString("mymo_serial_change", "").trim();
            if (editText.length() == 10) {
                if (CheckConnection.isConnection(context)) {

                    String sessionId = "", userId = "";

                    DbAdapter dbAdapter = DbAdapter.getInstance(context);
                    Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

                    if (cursor.getCount() > 0) {
                        sessionId = cursor.getString(0);
                        userId = cursor.getString(1);
                    }

                    addDeviceVolley1(context, editText, sessionId, userId);

                    addDeviceVolley2(context, editText, sessionId, userId);

                    //   new AddDeviceAsync(context).execute(editText);
                } else
                    Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

            } else if (editText.length() == 0) {
                Toast.makeText(context, context.getString(R.string.enter_serial), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.enter_valid_serial), Toast.LENGTH_SHORT).show();

            }
        }


    }

    private static void addDeviceVolley1(final Context context, final String key, final String sessionId, final String userId) {

        final String TAG = "Add Device";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error 3=> " + error.getMessage());
                        pDialog.dismiss();
                        Toast.makeText(context, R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();

                String method = "userV2.addDevice";
                params.clear();

                params.put("method", method);
                params.put("sessid", sessionId);
                params.put("serial", new Helper().getImei(context));
                params.put("user", userId);
                params.put("devType", "999");
                //params.put("uid",userId+"");
                params = new Helper().addRequiredParams(params, method);

                Log.e(TAG, "params are " + params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }


    private static void addDeviceVolley2(final Context context, final String key, final String sessionId, final String userId) {

        final String TAG = "Add Device";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        String errCode = new Jsonparser(response).getErrorCodeAddDevice();
                        if (errCode.equals("0")) {
                            Toast.makeText(context, context.getString(R.string.connected), Toast.LENGTH_LONG).show();
                            ((OnboarderActivity) context).showNext(1);
                        } else if (errCode.equals("1")) {
                            Toast.makeText(context, context.getString(R.string.device_is), Toast.LENGTH_LONG).show();
                        } else if (errCode.equals("2")) {
                            Toast.makeText(context, context.getString(R.string.user_is), Toast.LENGTH_LONG).show();
                        } else if (errCode.equals("3")) {
                            Toast.makeText(context, context.getString(R.string.user_is_not), Toast.LENGTH_LONG).show();
                        } else if (errCode.equals("4")) {
                            Toast.makeText(context, context.getString(R.string.unknown_device), Toast.LENGTH_LONG).show();
                        } else if (errCode.equals("-3")) {
                            Toast.makeText(context, context.getString(R.string.try_again), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error 3=> " + error.getMessage());
                        pDialog.dismiss();
                        Toast.makeText(context, R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();

                String method = "userV2.addDevice";
                params.clear();
                params.put("method", method);
                params.put("serial", key);
                params.put("user", "0");
                params.put("sessid", sessionId);
                params.put("devType", Constants.MYMO_TYPE);

                params = new Helper().addRequiredParams(params, method);

                Log.e(TAG, "params are " + params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }

    public String getYouTubeIdFromUrl(String url) {
        String id = "";
        Log.e(TAG, "youtube url is " + url);

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            id = matcher.group();
        }

        Log.e(TAG, "id is " + id);
        return id;

    }

    public String getVimeoIdFromUrl(String url) {
        String id = "";
        Log.e(TAG, "vimeo url is " + url);

        String pattern = "https?:\\/\\/(?:www\\.|player\\.)?vimeo.com\\/(?:channels\\/(?:\\w+\\/)?|groups\\/([^\\/]*)\\/videos\\/|album\\/(\\d+)\\/video\\/|video\\/|)(\\d+)(?:$|\\/|\\?)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            id = matcher.group(3);
        }

        Log.e(TAG, "id is " + id);
        return id;

    }

    public String getTime(String time) {

        long date2 = Long.parseLong(time);
        long currentSeconds = new java.util.Date().getTime();
        currentSeconds /= 1000;
        long dateDifference = Math.abs(currentSeconds - date2);

        Log.e(TAG, "date difference is " + dateDifference + ", api time is  " + time + ", current time is " + currentSeconds);
        long seconds = (int) dateDifference;
        Log.e(TAG, "the seconds are " + seconds);
        if (seconds < 60) {
            if (seconds > 1)
                return seconds + " seconds ago";
            else
                return seconds + " second ago";
        } else {
            long minutes = seconds / 60;
            if (minutes < 60) {

                if (minutes > 1)
                    return minutes + " minutes ago";
                else
                    return minutes + " minute ago";
            } else {
                long hours = minutes / 60;
                if (hours < 24) {
                    if (hours > 1)
                        return hours + " hours ago";
                    else
                        return hours + " hour ago";
                } else {
                    long day = hours / 24;

                    if (day < 30) {
                        if (day > 1)
                            return day + " days ago";
                        return day + " day ago";
                    } else {
                        long month = day / 30;
                        {
                            if (month < 12) {
                                if (month > 1)
                                    return month + " months ago";
                                else
                                    return month + " month ago";

                            } else {

                                long year = month / 12;
                                if (year > 1)
                                    return year + " years ago";
                                else return year + " year ago";
                            }
                        }
                    }
                }
            }
        }
    }

    public String getUnixTimestamp() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public Map addRequiredParams(Map params, String method) {
        Helper helper = new Helper();
        String time_stamp = helper.getUnixTimestamp();
        params.put("hash", helper.getHashValue(Constants.API_KEY, Constants.DOMAIN, time_stamp, method));
        params.put("domain_name", Constants.DOMAIN);
        params.put("domain_time_stamp", time_stamp);
        params.put("nonce", time_stamp);
        return params;
    }

    public boolean validEmail(String email) {
        if (email.length() < 3 || email.length() > 265)
            return false;
        else {
            if (email.matches(Constants.EMAIL_PATTERN)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void hidekeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showKeyBoard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void setActivityBackAnimation(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.back_slide_in, R.anim.back_slide_out);
    }

    /***
     * Get hash value
     *
     * @param apiKey
     * @param domain
     * @param timeStamp
     * @param method
     * @return hash value as String object
     */
    public String getHashValue(final String apiKey, final String domain, final String timeStamp, final String method) {
        final StringBuilder sb = new StringBuilder();
        sb.append(timeStamp + ";");
        sb.append(domain + ";");
        sb.append(timeStamp + ";");
        sb.append(method);

        final Charset csets = Charset.forName("US-ASCII");
        SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(csets.encode(apiKey).array(), "HmacSHA256");

        byte[] hash = null;

        try {
            Mac mac;
            mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            hash = mac.doFinal(csets.encode(sb.toString()).array());
            mac = null;
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final InvalidKeyException e) {
            e.printStackTrace();
        }

        // String result = "";
        sb.setLength(0);

        for (final byte b : hash)
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));

        hash = null;
        keySpec = null;
        //Logger.info("hash key" + sb.toString());


        return sb.toString();
    }


    public void showNoInternetToast(Context context) {
        try {
            Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    public String bitmaptoString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return string;
    }

    public String getImei(Context context) {
        String imei = null;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = manager.getDeviceId();
        return imei;
    }

    public boolean isAppInstalled(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.google.android.apps.fitness", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    public long getDeviceSerailNumber(String device_name) {
        long serial = -1;
        try {
            int len = device_name.length();
            if (len > 3) {
                long value = Long.parseLong(device_name.substring(3, len), 16);
//				Log.i(TAG,value+"");
                serial = HMM_serialDecodePM(value);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return serial;
    }

    public static long HMM_serialDecodePM(long value) {
        int s;
        int r;
        long ret = 0;
        s = ~(int) value & 0xFFFFFFFF;
        r = (s >> 8) & 0xFF;
        r |= (s << 8) & 0x000000000000FF00;
        r |= (s >> 8) & 0x0000000000FF0000;
        r |= (s << 8) & 0x00000000FF000000;
        r = r ^ MAGICNUMBER_PM;
        ret = r & 0xFFFFFFFFL;

        return ret;
    }

    public static long HMM_serialDecodeBL(long value) {
        int s;
        int r;
        long ret = 0;
        s = ~(int) value & 0xFFFFFFFF;
        r = (s >> 8) & 0xFF;
        r |= (s << 8) & 0xFF00;
        r |= (s >> 8) & 0xFF0000;
        r |= (s << 8) & 0xFF000000;
        r = r ^ MAGICNUMBER_BL;
        ret = r & 0xFFFFFFFFL;
        return ret;
    }

    public static long HMM_serialDecodeHR(long value) {
        int s;
        int r;
        long ret = 0;
        s = ~(int) value & 0xFFFFFFFF;
        r = (s >> 8) & 0xFF;
        r |= (s << 8) & 0x000000000000FF00;
        r |= (s >> 8) & 0x0000000000FF0000;
        r |= (s << 8) & 0x00000000FF000000;
        r = r ^ MAGICNUMBER_HR;
        ret = r & 0xFFFFFFFFL;
        return ret;
    }

    public static long HMM_serialEncodePM(long value) {
        int s;
        int r;
        long ret = 0;
        s = (int) (value & 0xFFFFFFFFL);
        s = s ^ MAGICNUMBER_PM;
        r = (s >> 8) & 0xFF;
        r |= (s << 8) & 0x000000000000FF00;
        r |= (s >> 8) & 0x0000000000FF0000;
        r |= (s << 8) & 0x00000000FF000000;
        ret = (long) ~r;
        ret = ret & 0xFFFFFFFFL;
        return ret;
    }

    public static long HMM_serialEncodeHR(long value) {
        int s;
        int r;
        long ret = 0;
        s = (int) (value);
        s = (~s) & 0xFFFFFFFF;
        //s = s ^ MAGICNUMBER_PM;
        r = (s >> 8) & 0xFF;
        r |= (s << 8) & 0xFF00;
        r |= (s >> 8) & 0xFF0000;
        r |= (s << 8) & 0xFF000000;
        r = r ^ MAGICNUMBER_PM;
        ret = (long) ~r;
        ret = ret & 0xFFFFFFFFL;
        return ret;
    }

//    function hmm_devices_decode_serial($serial){
//
//        $serial = intval($serial);
//        $serial = (~$serial) & 0xFFFFFFFF;
//        $r = ($serial >> 8) & 0xFF;
//        $r |= ($serial << 8) & 0xFF00;
//        $r |= ($serial >> 8) & 0xFF0000;
//        $r |= ($serial << 8) & 0xFF000000;
//        $r = $r ^ 0xA2E9324C;
//        return $r;
//    }

    public String getSessionId(Context context) {
        dbAdapter = DbAdapter.getInstance(context);
        cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

        if (cursor.getCount() > 0) {
            return cursor.getString(0);
        }
        return null;
    }

    public String getUserId(Context context) {
        dbAdapter = DbAdapter.getInstance(context);
        cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

        if (cursor.getCount() > 0) {
            return cursor.getString(1);
        }
        return null;
    }

    public String getCorpId(Context context) {
        dbAdapter = DbAdapter.getInstance(context);
        cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

        if (cursor.getCount() > 0) {
            return cursor.getString(3);
        }
        return null;
    }

    public static String bitmapToString(Bitmap bitmap) {
        String string = "";
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public static Bitmap stringToBitmap(String bitString) {
        byte[] decodedString = Base64.decode(bitString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    public static int GetPlaceHolder(SharedPreferences prefs) {

        String prefsGender = prefs.getString("userGender", "0");
        if (prefsGender.equalsIgnoreCase("male") || prefsGender.equalsIgnoreCase("0"))
            return R.mipmap.male;
        else
            return R.mipmap.female;

    }

    public static void doLoadAgain(Context activity, int i, boolean value) {
        String booleanToget[] = {"reloadLeaderBoard", "reloadChallenge", "reloadNews"};
        SharedPreferences.Editor editor = activity.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).edit();
        editor.putBoolean(booleanToget[i - 1], value);
        editor.commit();

    }

    public static void reloadAllTheTabs(Context context) {

        doLoadAgain(context, 1, true);
        doLoadAgain(context, 2, true);
        doLoadAgain(context, 3, true);

    }

    public static void reloadTabs(Context context, int i) {

        doLoadAgain(context, i, true);

    }

    public static void logoutVolley(SplashScreen splashScreen, ProgressDialog pDialog) {
        logoutVolley(splashScreen, pDialog, null);
    }

    public static String getCMFromFootsAndInch(double feet, double inch) {
        return Math.round((feet + (inch / 12)) / 0.0328084 * 100.0) / 100.0 + " cms";

    }

    public static String getFeetFromCMS(String userWeight) {

        Double cm = Double.parseDouble(userWeight);

        Double feet = cm / 30.48;
        Double myInch = (cm / 2.54) - feet.intValue() * 12 + 0.5;
        int inch = myInch.intValue();
        if (inch == 12) {
            feet += 1;
            inch = 0;
        }
        Log.e(TAG, " the inch value is  " + inch);


        return feet.intValue() + " feet" + " " + inch + " inch";
    }
}
