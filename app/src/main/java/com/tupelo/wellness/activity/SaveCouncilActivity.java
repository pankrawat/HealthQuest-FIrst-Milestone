package com.tupelo.wellness.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.R;
import com.tupelo.wellness.bean.ClusterBean;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.helper.StringWithTag;
import com.tupelo.wellness.helper.TakeImageClass;
import com.tupelo.wellness.helper.WalkingPrefrence;
import com.tupelo.wellness.parcer.Jsonparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

public class SaveCouncilActivity extends AppCompatActivity {

    private String TAG = "SaveCouncilActivity";
    private Spinner down_division, sector_down;
    String ctype = null, ttype = "", ptype = null;
    EditText et_zip;
    //et_title_other;
    String headingLabel = "Select Division";
    String valuesLabel = "Select Group";
    Button btn_register_council;
    private ProgressDialog pDialog;
    String sessionid, userid, username, password, corpid;
    boolean booCity;
    private ImageView iv_profile_img;
    private TakeImageClass takeImageClass;
    private Bitmap bitmap = null;
    private LinearLayout tv_click;
    Fonts fonts;
    Helper helper;
    SharedPreference sharedPref;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    List<String> listCouncil;
    List<String> listTitle;
    ArrayAdapter<String> councilAdapter;
    ArrayAdapter<String> titleAdapter;
    ArrayList<ClusterBean> clusterBeans = null;
    HashMap<String, String> groupMap;
    HashMap<String, String> clusterMap;


    private static final String LOG_TAG = "CheckNetworkStatus";


    @Override
    protected void onPause() {
        super.onPause();
        if (pleaseWaitDialog != null)
            pleaseWaitDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //If there is a background task set it to the new activity

    }

    private static ProgressDialog pleaseWaitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_council);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait..");
        pDialog.setCancelable(false);
        RelativeLayout myLayout = (RelativeLayout) this.findViewById(R.id.backgroundColor);


        AppTheme app = AppTheme.getInstance();
        myLayout.setBackgroundColor(Color.parseColor(app.colorPrimary));

        String statusBarColor = app.colorPrimaryDark;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }
        TextView tv_app_name = (TextView) this.findViewById(R.id.tv_app_name);
        tv_app_name.setText(WalkingPrefrence.getInstance().programeName);


        helper = new Helper();
        sessionid = helper.getSessionId(SaveCouncilActivity.this);
        userid = helper.getUserId(SaveCouncilActivity.this);
        corpid = WalkingPrefrence.getInstance().corpId;

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);


        sharedPref = SharedPreference.getInstance(SaveCouncilActivity.this);

        sharedPref.putBoolean(Constants.IS_SIGNUP_COMPLETE, false);
        booCity = sharedPref.getBoolean("booCity", true);
        username = sharedPref.getString("username", "");
        password = sharedPref.getString("password", "");

        fonts = new Fonts(SaveCouncilActivity.this);

        takeImageClass = new TakeImageClass(this);
        //new CompanyGroupingsAsync(SaveCouncilActivity.this).execute();
        initViews();

        List<StringWithTag> clusterName = new ArrayList<>();
        headingLabel = "Select " +  WalkingPrefrence.getInstance().clustr_alias;
        valuesLabel = "Select " + WalkingPrefrence.getInstance().grp_alias;


        final List<StringWithTag> groupName = new ArrayList<>();
        ArrayAdapter<StringWithTag> arrayAdapter = new ArrayAdapter<StringWithTag>(
                this,
                R.layout.spinner_selected_textview, clusterName
        );
        down_division.setAdapter(arrayAdapter);

        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);


        final ArrayAdapter<StringWithTag> arrayAdapter1 = new ArrayAdapter<StringWithTag>(
                this, R.layout.spinner_selected_textview, groupName);

        arrayAdapter1.setDropDownViewResource(R.layout.spinner_item);

        sector_down.setAdapter(arrayAdapter1);

        StringWithTag stw = null;
        if (WalkingPrefrence.getInstance().clusterMode.equalsIgnoreCase("1"))
            stw = new StringWithTag(headingLabel, null);
        else if (WalkingPrefrence.getInstance().clusterMode.equalsIgnoreCase("0")) {
            stw = new StringWithTag(headingLabel, "0");
            ctype = "0";
            down_division.setVisibility(View.GONE);
        }
        clusterName.add(stw);

        stw = new StringWithTag(valuesLabel, null);
        groupName.add(stw);

        String groupings = WalkingPrefrence.getInstance().groupings;
        Log.d("groups are", groupings);


        JSONObject groups = null;
        try {
            groups = new JSONObject(groupings);
            groupName.clear();
            stw = new StringWithTag(valuesLabel, null);

            groupName.add(stw);

            JSONArray groupJsonArray = groups.getJSONArray("companygroup");
            for (int i = 0; i < groupJsonArray.length(); i++) {
                JSONObject individualGroup = groupJsonArray.getJSONObject(i);
                groupName.add(new StringWithTag(individualGroup.getString("groupname"), individualGroup.getString("groupid")));
            }


            arrayAdapter1.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // sector_down.setAdapter(arrayAdapter1);

        try {

            if (WalkingPrefrence.getInstance().clusterMode.equalsIgnoreCase("1")) {
                groups = new JSONObject(groupings);
                for (int i = 0; i < groups.getJSONArray("companygroup").length(); i++) {
                    JSONObject cluster = groups.getJSONArray("companygroup").getJSONObject(i);
                    clusterName.add(new StringWithTag(cluster.getString("clustername"), cluster.getString("clusterid")));

                }

            }

            arrayAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // JSONArray clusters = new JsonObject(groupings).getAsJsonObject("").


        final JSONObject finalGroups = groups;
        down_division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (WalkingPrefrence.getInstance().clusterMode.equalsIgnoreCase("1")) {
                    try {


                        StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                        ctype = s.tag;
                        groupName.clear();
                        StringWithTag stw = new StringWithTag(valuesLabel, null);
                        groupName.add(stw);
                        sector_down.setSelection(0);
                        Log.e(TAG, s.toString() + " " + s.tag);
                        if (finalGroups != null) {
                            if (position - 1 >= 0) {
                                JSONArray jsonArray = finalGroups.optJSONArray("companygroup").getJSONObject(position - 1).getJSONArray("groups");

                                for (int j = 0; j < jsonArray.length(); j++) {
                                    groupName.add(new StringWithTag(jsonArray.getJSONObject(j).getString("name"), jsonArray.getJSONObject(j).getString("groupid")));
                                    Log.e("groups are ", jsonArray.getJSONObject(j).getString("name"));
                                }
                            }
                        }
                        arrayAdapter1.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        sector_down.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                ttype = s.tag;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }


    private void initViews() {
        iv_profile_img = (ImageView) findViewById(R.id.iv_profile);
        iv_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Helper().hidekeyboard(SaveCouncilActivity.this, et_zip);
                verifyStoragePermissions();
                takeImageClass.getImagePickerDialog(SaveCouncilActivity.this, getString(R.string.app_name), getString(R.string.choose_image));
            }
        });

        tv_click = (LinearLayout) findViewById(R.id.tv_click);
        for (int i = 0; i < tv_click.getChildCount(); i++) {
            TextView myText = (TextView) tv_click.getChildAt(i);
            myText.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        }
       /* final SpannableStringBuilder sb = new SpannableStringBuilder(getString(R.string.click));

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(bss, 14, 23, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
*/
        tv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Helper().hidekeyboard(SaveCouncilActivity.this, et_zip);
                takeImageClass.getImagePickerDialog(SaveCouncilActivity.this, getString(R.string.app_name), getString(R.string.choose_image));
            }
        });
     /*   tv_click.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        tv_click.setText(sb);*/
        et_zip = (EditText) findViewById(R.id.et_zip);
        //   et_zip.setTypeface(fonts.getTypefaceNormal());
        down_division = (Spinner) findViewById(R.id.down_division);
        sector_down = (Spinner) findViewById(R.id.sector_down);


        //et_title_other = (EditText) findViewById(R.id.et_title_other);
        btn_register_council = (Button) findViewById(R.id.btn_register_council);

        btn_register_council.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        //et_title_other.setVisibility(View.GONE);

        // sector_down.setVisibility(View.GONE);
        listCouncil = new ArrayList<String>();
        listTitle = new ArrayList<String>();

        btn_register_council.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {

                    if (CheckConnection.isConnection(SaveCouncilActivity.this)) {
                        String zip_code = et_zip.getText().toString();
                        //String title_other = et_title_other.getText().toString();
                        String title_other = "";
                        Log.e(TAG, " cluster id is " + ctype + "Group id is " + ttype);
                        saveImageAndUploadData(bitmap, sessionid, userid, zip_code, "0", "2014", title_other, username, password, corpid, ctype, ttype);

                        //   new SaveCouncilAsync(SaveCouncilActivity.this, bitmap).execute(sessionid, userid, zip_code, "0", "2014", title_other, username, password, corpid, ctype,ttype);
                    } else {
                        Toast.makeText(SaveCouncilActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void saveImageAndUploadData(final Bitmap bitmap, final String... eclipseParams) {

        if (bitmap != null) {

            //   String resimage = new NetworkCall().saveProfileImage(bitmap, eclipseParams[1], eclipseParams[0]);

            pDialog.show();
            StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "Response is " + response);
                            String fid, path;
                            Jsonparser jsonparser;
                            Log.e("My profile Image", response);
                            jsonparser = new Jsonparser(response);
                            fid = jsonparser.getProfileImagefid()[0];
                            path = jsonparser.getProfileImagefid()[1];
                            Log.e("fid", fid + " " + path);
                            SharedPreferences.Editor editor = getSharedPreferences("MyProfile", MODE_PRIVATE).edit();
                            editor.putString("userAvatarURL",path);
                            editor.commit();
                            uploadRemainingDataWithFid(path, fid, eclipseParams);


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.hide();
                            if (error.getMessage() != null) {
                                Log.e("ERROR", "error 3=> " + error.getMessage());

                                Toast.makeText(SaveCouncilActivity.this, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SaveCouncilActivity.this, "Unable to save council details.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            ) {
                // this is the relevant method
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    Helper helper = new Helper();
                    String method = "file.save", jsonString = "";
                    JSONObject param = new JSONObject();

                    try {
                        String bitString = helper.bitmaptoString(bitmap);
                        String time_stamp = helper.getUnixTimestamp();

                        String sessid = eclipseParams[0];
                        String uid = eclipseParams[1];
                        param.put("file", bitString);
                        param.put("filename", "myPic" + uid + ".png");
                        param.put("filepath", "sites/default/files/myPic" + uid + ".png");
                        param.put("uid", uid);
                        param.put("timestamp", time_stamp);
                        jsonString = param.toString();

                        params = new Helper().addRequiredParams(params, method);
                        params.put("method", method);
                        params.put("file", jsonString);
                        params.put("sessid", sessid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }


                    return params;


                }
            };
            // Adding request to request queue

            AppController.getInstance().addToRequestQueue(postRequest);

        } else
            uploadRemainingDataWithFid("", "", eclipseParams);
    }

    private void uploadRemainingDataWithFid(final String path, final String fid, final String... eclipseParams) {
        if (!pDialog.isShowing())
            pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        Log.e(TAG, "Response is " + response);

                        String result = "";

                        if (!response.equals("Connection Timeout")) {
                            Jsonparser jsonparser = new Jsonparser(response);

                            String message = jsonparser.parseErrorMsg();

                            result = message;
                        } else
                            result = "-3";


                        if (result.equals("1")) {
                            afterSavingCouncilSuccessFully();
                        } else if (result.equals("-3")) {
                            Toast.makeText(SaveCouncilActivity.this, SaveCouncilActivity.this.getString(R.string.universal_err), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SaveCouncilActivity.this, "Unable to save council details.", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        if (error.getMessage() != null) {
                            Log.e("ERROR", "error 3=> " + error.getMessage());
                            Toast.makeText(SaveCouncilActivity.this, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SaveCouncilActivity.this, "Unable to save council details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                String method = "userV2.savecouncil";


                params = new Helper().addRequiredParams(params, method);
                String sessid = eclipseParams[0];
                String uid = eclipseParams[1];
                String zip = eclipseParams[2];
                String council_type = eclipseParams[3];
                String title_type = eclipseParams[4];
                String title_other = eclipseParams[5];
                String orgid = eclipseParams[8];
                String clusterid = eclipseParams[9];
                String groupid = eclipseParams[10];

                params.put("method", method);
                params.put("sessid", sessid);
                params.put("uid", uid);
                params.put("zip", zip);
                params.put("council_type", council_type);
                params.put("title_type", title_type);
                params.put("title_other", title_other);
                params.put("imageId", fid);
                params.put("orgid", orgid);
                params.put("groupid", groupid);
                params.put("clusterid", clusterid);


                Log.e(TAG,"org id is " + orgid);
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }


    private boolean validation() {
        if ((!booCity) && et_zip.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please input zip code of your city.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (ctype == null) {
            Toast.makeText(this, "Please " + headingLabel, Toast.LENGTH_SHORT).show();
            return false;
        } else if (ttype == null) {
            Toast.makeText(this, "Please " + valuesLabel , Toast.LENGTH_SHORT).show();
            return false;

        } else if (iv_profile_img.getDrawable() == null) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SaveCouncilActivity.this);

            alertDialogBuilder.setTitle("No Profile Picture added");
            alertDialogBuilder.setMessage("No profile picture added. Do you want to proceed");

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (CheckConnection.isConnection(SaveCouncilActivity.this)) {
                        String zip_code = et_zip.getText().toString();
                        //String title_other = et_title_other.getText().toString();
                        String title_other = "";
                        Log.e(TAG, " cluster id is " + ctype + "Group id is " + ttype);

                        // saveCouncilVolley(bitmap,sessionid, userid, zip_code, "0", "2014", title_other, username, password, corpid,ctype,ttype);
                        //new SaveCouncilAsync(SaveCouncilActivity.this, bitmap).execute(sessionid, userid, zip_code, "0", "2014", title_other, username, password, corpid,ctype,ttype);
                        saveImageAndUploadData(bitmap, sessionid, userid, zip_code, "0", "2014", title_other, username, password, corpid, ctype, ttype);

                    }
                }
            });

            alertDialogBuilder.setCancelable(false).setNegativeButton("ADD NOW", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    new Helper().hidekeyboard(SaveCouncilActivity.this, et_zip);
                    verifyStoragePermissions();
                    takeImageClass.getImagePickerDialog(SaveCouncilActivity.this, getString(R.string.app_name), getString(R.string.choose_image));

                }
            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakeImageClass.REQUEST_CODE_TAKE_PICTURE:
                    takeImageClass.onActivityResult(requestCode, resultCode, data);
                    break;
                case TakeImageClass.REQUEST_CODE_GALLERY:
                    takeImageClass.onActivityResult(requestCode, resultCode, data);
                    break;
                case TakeImageClass.REQUEST_CODE_CROP_IMAGE:
                    takeImageClass.onActivityResult(requestCode, resultCode, data);
                    if (TakeImageClass.sImagePath != null) {
                        bitmap = BitmapFactory.decodeFile(TakeImageClass.sImagePath);
                        if (bitmap != null) {
                            iv_profile_img.setImageBitmap(bitmap);
                            tv_click.setVisibility(View.GONE);
                        }
                    }
            }
        }

    }

    public void afterSavingCouncilSuccessFully() {
        Helper helper = new Helper();
        if (bitmap != null) {
            String bitString = helper.bitmaptoString(bitmap);

//            SharedPreferences pref = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
//            pref.edit().putString(Constants.BITMAP, bitString).commit();
            sharedPref.putString(Constants.BITMAP, bitString);
            sharedPref.putBoolean(Constants.IS_SIGNUP_COMPLETE, true);
        }
        Log.d(TAG, "going to open ChooseTracker");
        Intent intent = new Intent(SaveCouncilActivity.this, ChooseTracker.class);
        startActivity(intent);
        finish();
    }

    public boolean verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    //Logic business after the web service complete here
    //Do the thing that modify the UI in a function like this


}