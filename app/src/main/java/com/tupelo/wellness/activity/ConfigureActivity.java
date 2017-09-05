package com.tupelo.wellness.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.Dimen;
import com.tupelo.wellness.helper.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ConfigureActivity extends AppCompatActivity {
    Activity activity;
    private String TAG = "ConfigureActivity";
    int i = 0;
    ImageView iv_phone;
    TextView myDownloadPercent;
    ImageView iv_green_bg;
    Dimen dimen;
  //  WaveLoadingView waveLoadingView;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
          //  waveLoadingView.setProgressValue(i++);
       //     waveLoadingView.setCenterTitle(i + "%");
            handler.postDelayed(runnable, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        activity = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }

//        myDownloadPercent = (TextView) this.findViewById(R.id.myDownloadPercent);
        iv_green_bg = (ImageView) findViewById(R.id.iv_green_bg);
      //  waveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        dimen = Dimen.getInstance(this);
        connectForSessId();
    }

    private void connectForSessId() {

            String json = getIntent().getStringExtra("json");
            Log.e(TAG,"json is " + json);
            /*
            JSONObject myJson = new JSONObject(json);
            JSONObject myJson2 = myJson.getJSONObject("#data");
            String web_logo_url = myJson2.getString("web_logo_url");
            String app_logo_url = myJson2.getString("app_logo_url");
            String app_splash_url = myJson2.getString("app_splash_url");
            String app_loginimage_url = myJson2.getString("app_loginimage_url");*/





            String mySplashImageURl = "";
            try {
                JSONObject pathTillKey = new JSONObject(getIntent().getStringExtra("json")).getJSONObject("#data").getJSONObject("splash_screen_urls").getJSONObject("Android");

                switch(getResources().getDisplayMetrics().densityDpi) {

                    case DisplayMetrics.DENSITY_MEDIUM:
                        Log.e(TAG,"mdpi");
                        mySplashImageURl = pathTillKey.getString("mdpi");
                        break;
                    case DisplayMetrics.DENSITY_HIGH:
                        Log.e(TAG,"hdpi");
                        mySplashImageURl = pathTillKey.getString("hdpi");
                        break;
                    case DisplayMetrics.DENSITY_XHIGH:
                        Log.e(TAG,"xhdpi");
                        mySplashImageURl = pathTillKey.getString("xhdpi");
                        break;
                    case DisplayMetrics.DENSITY_XXHIGH:
                        Log.e(TAG,"xxhdpi");
                        mySplashImageURl = pathTillKey.getString("xxhdpi");
                        break;
                    default:
                        mySplashImageURl = pathTillKey.getString("xxhdpi");


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        String[] images = {mySplashImageURl};




          //  Log.e(TAG,web_logo_url + " " + app_loginimage_url + " " + app_logo_url + " " + app_splash_url);


            for(int i = 0;i<images.length;i++)
            {
                images[i] = images[i].replaceAll(" ","%20");
                String[] data = {images[i],(i+1) + "",images.length+""};

                new DownloadFileFromURL().execute(data);
            }



    }

    public String getpercentString(long total,int position,int lenghtOfFile,int totalImages) {

        position = position -1;


        int perodicity = 100/totalImages;

        return (int) (perodicity*position +  ((total*perodicity)/lenghtOfFile)) + "";


    }


    class DownloadFileFromURL extends AsyncTask<String[], String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String[]... f_url) {



            int count;
            try {
                URL url = new URL(f_url[0][0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file lengthpubl
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
               // bmp = BitmapFactory.decodeStream(bufferedInputStream);
                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/mainscreens"  + f_url[0][1] + ".jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called

                    publishProgress(getpercentString(total,Integer.parseInt(f_url[0][1]),lenghtOfFile,Integer.parseInt(f_url[0][2])));

                    // writing data to file
                    output.write(data, 0, count);
                }


                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            Log.e(TAG,"I am here going to update progress");
//            myDownloadPercent.setText(progress[0]);
            Log.e(TAG,"progress is " + progress[0]);
          //  waveLoadingView.setProgressValue(Integer.parseInt(progress[0]));
         //   waveLoadingView.setCenterTitle(progress[0] + "%");


            updateWeightOfViews(progress[0]);
            //this condition is true only when the num length isdivided by 100
            /*if(progress[0].equalsIgnoreCase("100"))
            {
                //now start again with customized screen
            }*/
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded


            // Displaying downloaded image into image view
            // Reading image path from sdcard
            Log.e(TAG,"coming to on post execute");
            File f = new File("/mnt/sdcard/mainscreens1.jpg");
            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
            String myImage1 = Helper.bitmapToString(bmp);


            SharedPreferences.Editor editor = getSharedPreferences("MyCorpProfile", MODE_PRIVATE).edit();
            editor.putBoolean("DownloadedImages", true);
            try {

                JSONObject dataObject = new JSONObject(getIntent().getStringExtra("json")).getJSONObject("#data");
                String colorChoice =  dataObject.getString("primary_color");
                editor.putString("splashImage",myImage1);
                editor.putString("colorcode",colorChoice);
                editor.putString("corpname",dataObject.getString("corpname"));
                editor.putString("deviceSupport",dataObject.getJSONArray("devicesupport").toString());
                editor.putString("groupings",dataObject.getJSONObject("groupings").toString());
                editor.putString("programeName",dataObject.getString("program_name"));
                editor.putString("corpid",dataObject.getString("corpid"));
                editor.putString("corpcode",dataObject.getString("corpcode"));
                editor.putString("grp_alias",dataObject.getString("grp_alias"));
                editor.putString("clustr_alias",dataObject.getString("clustr_alias"));
                editor.putString("clustrmode",dataObject.getString("clustrmode"));
                editor.putString("grpmode",dataObject.getString("grpmode"));
                editor.putString("roleId",dataObject.getString("roleid"));
                editor.putString("expiry",dataObject.getString("expiry"));
                editor.putString("prgrm_sdate",dataObject.getString("prgrm_sdate"));
                editor.putString("prgrm_edate",dataObject.getString("prgrm_edate"));
                editor.putString("features",dataObject.getJSONArray("features").toString());
                editor.putString("channels",dataObject.getJSONObject("channels").toString());
                editor.putString("isUsername",dataObject.getString("isUsername"));
                editor.putString("isEmailFilter",dataObject.getString("isEmailFilter"));
                Log.e(TAG,"the corp id is" + dataObject.getString("corpid"));

                editor.commit();

                Intent intent = new Intent(ConfigureActivity.this,SplashScreen.class);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }




        }






    }

    private void updateWeightOfViews(String progres) {

       View unfilled = this.findViewById(R.id.hidden);
       View filled = this.findViewById(R.id.filled);
       TextView updatePercent = (TextView) this.findViewById(R.id.update_percent);

        int filledValue = Integer.parseInt(progres);
        int unfilledValue =100-filledValue;

        Log.e(TAG,"filled and unfilled values are" + filled + " " + unfilled);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) filled.getLayoutParams();
        params.weight =filledValue;
        filled.setLayoutParams(params);


        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) unfilled.getLayoutParams();
        params2.weight =unfilledValue;
        unfilled.setLayoutParams(params2);


        updatePercent.setText(progres + "%" );


    }


}
