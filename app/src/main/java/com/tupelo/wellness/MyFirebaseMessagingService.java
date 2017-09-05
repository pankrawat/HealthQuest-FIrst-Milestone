package com.tupelo.wellness;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.WalkingPrefrence;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Toast.makeText(MyFirebaseMessagingService.this, "ffffffffffff", Toast.LENGTH_SHORT).show();
        Log.e("dddddddddd", remoteMessage.getData().toString());
        Map<String, String> map = remoteMessage.getData();

        Log.e("dddddddddsssssss", map.get("msg_payload"));
        try {
            String push_notification_msg = map.get("GCM");
            JSONObject pushmsg = new JSONObject(map.get("msg_payload"));
            String payload_text = pushmsg.optString("payload_text");
            String payload_title = pushmsg.optString("payload_title");
            String payload_type = pushmsg.optString("payload_type");
            String userid = pushmsg.optString("userid");
            sendNotification2(payload_text, payload_title, payload_type, userid, push_notification_msg);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SharedPreferences prefs = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
        String color = prefs.getString("colorcode", "1");
        AppTheme app = AppTheme.getInstance();
        app.setAppTheme(Integer.parseInt(color));



        Log.e(TAG, "the corp id is" + prefs.getString("corpid", ""));
        String companyName = prefs.getString("corpname", "");
        String grp_alias = prefs.getString("grp_alias", "Select Division");
        String clustr_alias = prefs.getString("clustr_alias", "Select Group");
        String deviceSupport = prefs.getString("deviceSupport", "");
        String groupings = prefs.getString("groupings", "");
        String programName = prefs.getString("programeName", "");
        String corpid = prefs.getString("corpid", "");
        String corpcode = prefs.getString("corpcode", "");
        String clustrmode = prefs.getString("clustrmode", "");
        String grpmode = prefs.getString("grpmode", "");
        String prgrm_edate = prefs.getString("prgrm_edate", "");
        String prgrm_sdate = prefs.getString("prgrm_sdate", "");


        //  System.out.println("Current time => " + c.getTime());


        String roleId = prefs.getString("roleId", "");
        WalkingPrefrence walkingPrefrence = WalkingPrefrence.getInstance();
        walkingPrefrence.setWalkingPrefrence(prgrm_edate,prgrm_sdate,companyName, deviceSupport, groupings, programName, corpid, grp_alias, clustr_alias, color, corpcode, clustrmode, grpmode, roleId);



    }


    public void sendNotification2(String payload_text, String payload_title, String payload_type, String userid, String push_notification_msg) {

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        // new Intent(this, SplashScreen.class)

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        SharedPreferences prefsp = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
        boolean alreadyLoggedIn = prefsp.getBoolean("DownloadedImages", false);

        if (AppController.isActive) {
            if (alreadyLoggedIn) {


                if (userid != null && !userid.isEmpty()) {

                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    String userId = pref1.getString("userId", "0");
                    if (userId.equals(userid)) {


                       /* editor.putString("userid", userid);
                        editor.putString("check", "3"); // Storing string

                        editor.putString("payload_text", payload_text); // Storing string
                        editor.putString("payload_title", payload_title); // Storing string
                        editor.putString("payload_type", payload_type); // Storing string
                        editor.commit();*/

//                        Intent intent1 = new Intent(this, TabActivity.class);
//                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );

                        Intent intent1 = new Intent("broadcast");


                        intent1.putExtra("userid", userid);
                        intent1.putExtra("payload_text", payload_text);
                        intent1.putExtra("payload_title", payload_title);
                        intent1.putExtra("payload_type", payload_type);

                        sendBroadcast(intent1);

                    }

                } else {
                   /* editor.putString("check", "3"); // Storing string

                    editor.putString("payload_text", payload_text); // Storing string
                    editor.putString("payload_title", payload_title); // Storing string
                    editor.putString("payload_type", payload_type); // Storing string
                    editor.commit();

*/
                    Intent intent1 = new Intent("broadcast"/*this, TabActivity.class*/);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent1.putExtra("payload_text", payload_text);
                    intent1.putExtra("payload_title", payload_title);
                    intent1.putExtra("payload_type", payload_type);


                    sendBroadcast(intent1);


                }


            }

        } else {

            if (alreadyLoggedIn) {
                if (userid != null && !userid.isEmpty()) {

                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    String userId = pref1.getString("userId", "0");
                    if (userId.equals(userid)) {


                    /*    editor.putString("userid", userid);
                        editor.putString("check", "1"); // Storing string

                        editor.putString("payload_text", payload_text); // Storing string
                        editor.putString("payload_title", payload_title); // Storing string
                        editor.putString("payload_type", payload_type); // Storing string
                        editor.commit();

*/
                        Intent view = new Intent(this, TabActivity.class);
                        view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        view.putExtra("userid", "1");
                        view.putExtra("payload_text1", payload_text);
                        view.putExtra("payload_title1", payload_title);
                        view.putExtra("payload_type1", payload_type);


                        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, view, PendingIntent.FLAG_UPDATE_CURRENT);
                      /*  SharedPreferences prefs = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
                        String programName = prefs.getString("programeName", "");*/
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(getNotificationIcon())
                                        .setContentTitle(payload_title)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText(push_notification_msg))
                                        .setSound(defaultSoundUri)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                        .setContentText(push_notification_msg);

                        mBuilder.setAutoCancel(true);
                        mBuilder.setContentIntent(contentIntent);
                        mNotificationManager.notify(1, mBuilder.build());

                    }

                } else {
                   /* editor.putString("check", "0"); // Storing string

                    editor.putString("payload_text", payload_text); // Storing string
                    editor.putString("payload_title", payload_title); // Storing string
                    editor.putString("payload_type", payload_type); // Storing string
                    editor.commit();*/


                    Intent view = new Intent(this, TabActivity.class);
                    view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    view.putExtra("payload_text", payload_text);
                    view.putExtra("payload_title", payload_title);
                    view.putExtra("payload_type", payload_type);


                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, view, PendingIntent.FLAG_UPDATE_CURRENT);
                    /*SharedPreferences prefs = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
                    String programName = prefs.getString("programeName", "");*/
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(getNotificationIcon())
                                    .setContentTitle(payload_title)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(push_notification_msg))
                                    .setSound(defaultSoundUri)
                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                    .setContentText(push_notification_msg);

                    mBuilder.setAutoCancel(true);
                    mBuilder.setContentIntent(contentIntent);
                    mNotificationManager.notify(1, mBuilder.build());

                }
            }
        }
    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.white_launcher : R.mipmap.ic_launcher;
    }

}
