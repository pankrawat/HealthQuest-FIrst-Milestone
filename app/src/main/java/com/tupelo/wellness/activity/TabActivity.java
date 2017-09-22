package com.tupelo.wellness.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.pubnub.api.Pubnub;
import com.squareup.picasso.Picasso;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.CircleTransform;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.GetAuthToken;
import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.R;
import com.tupelo.wellness.SHealthIntegration.SetUpSHealth;
import com.tupelo.wellness.bean.InfoStreamBean;
import com.tupelo.wellness.callbacks.NotificationListener;
import com.tupelo.wellness.callbacks.WinnerDialogListener;
import com.tupelo.wellness.circularimageview.CircularImageView;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.NotificationDialog;
import com.tupelo.wellness.helper.SharedPref;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.helper.WalkingPrefrence;
import com.tupelo.wellness.helper.WinnerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.util.Log;

public class TabActivity extends AppCompatActivity implements LBFragment.OnFragmentInteractionListener, NotificationListener, WinnerDialogListener {
    private static final String TAG = "TabActivity";
    public static boolean isTabShownIsChallenge = false;
    public static boolean showShowcase = false;
    private final Pubnub pubnub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
    AlertDialog alertDialog;
    LinearLayout left_drawer;
    Dashboard dashboard;
    Leaderboard leaderboard;
    Toolbar toolbar;
    public ImageView info;
    Context context;
    String regId;
    Challenge challenge;
    TextView tv_appname;
    LinearLayout fourTabLayout;
    FrameLayout drawerFragmentLayout;

    private CallbackManager facebookCallbackManager;
    News news;
    Fonts fonts;
    ViewPagerAdapter adapter;
    CharSequence Titles[] = {"Dashboard", "Leaderboard", "Challenge", "News Feed"};
    int TabIcons[] = {R.mipmap.dashboard, R.mipmap.leaderboard, R.mipmap.challenges, R.mipmap.newsfeed};
    GetAuthToken getAuthToken;
    SharedPreference sharedPreference;
    private TabLayout tabLayout;
    private ProgressDialog pDialog;
    private View previousLayout = null;
    private TextView previousPosition = null;
    private float lastTranslate = 0.0f;
    private ViewPager viewPager;
    private DrawerLayout mDrawerLayout;
    private String service;
    public Menu menu;
    SharedPreference spMain;
    private NotificationDialog notificationDialog;
    private WinnerDialog winnerDialog;
    ActionBarDrawerToggle mDrawerToggle;
    public View mTransparentImage;
    private String fitbit_token,fitbit_userid;

    protected BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            ... Do whatever you want here
            Log.d("Broadcastaaya", "dekho aaya");
            /*Toast.makeText(context, "Invoked", Toast.LENGTH_LONG).show();*/


            if (intent.hasExtra("userid")) {

                String userid = intent.getStringExtra("userid");
                String payload_text = intent.getStringExtra("payload_text");
                String payload_title = intent.getStringExtra("payload_title");
                String payload_type = intent.getStringExtra("payload_type");

                showDialog2(payload_title, payload_text);
            } else {
                String payload_text = intent.getStringExtra("payload_text");
                String payload_title = intent.getStringExtra("payload_title");
                String payload_type = intent.getStringExtra("payload_type");
                showDialog1(payload_title, payload_text);

            }

        }
    };

    public static void callShareIntent(Activity activity, String shareContent, String subject) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        activity.startActivity(Intent.createChooser(sharingIntent, "Refer Friend via"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        registerReceiver(mNotificationReceiver, new IntentFilter("broadcast"));
        context = TabActivity.this;
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        notificationDialog = new NotificationDialog(this);
        winnerDialog = new WinnerDialog(this);
        spMain = spMain.getInstance(this);
       SharedPreferences editor=getSharedPreferences("companyprefs",0);
        if(!editor.contains("distance")){
            Helper.logoutVolley(TabActivity.this, pDialog);
        }
        info = (ImageView) findViewById(R.id.info);
       //  info.setVisibility(View.GONE);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog= new Dialog(TabActivity.this,R.style.NewDialog);
                    dialog.setContentView(R.layout.dialog_info);
                    dialog.setCancelable(false);
                    RelativeLayout back=(RelativeLayout)dialog.findViewById(R.id.linearLayout1);
                    back.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimaryDark));
                    TextView msg=(TextView)dialog.findViewById(R.id.message);
                    Button ok=(Button)dialog.findViewById(R.id.cancel);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    if(Dashboard.slide==0) {
                        msg.setText("Step count is the number of steps you take throughout the day.HealthQuest app fetch this number from the activity tracker you use.");
                    }else if(Dashboard.slide==1)
                    {
                        msg.setText("This is the estimated number of calories burned by the user when engage in non-sedenatary activity.This calorie count does not include the passive calorie (BMR) count. HealthQuest app fetches this number from the activity tracker you use.");

                    }else if(Dashboard.slide==2){
                        msg.setText("The estimated total distance covered that the user achieved throughout the day whether through walking or running or any other physical activity. HealthQuest app fetches this number from the activity tracker you use.");
                    }else if(Dashboard.slide==3){
                        msg.setText("A flight of stairs that the user climbed up or down throughout the day. HealthQuest app fetches this number from the activity tracker you use.");

                    }
                                                dialog.show();
                }
            });

        /*  view.putExtra("userid","1");
                    view.putExtra("payload_text",payload_text);
                    view.putExtra("payload_title",payload_title);
                    view.putExtra("payload_type",payload_type);*/
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra("userid")) {
                if (intent.getStringExtra("payload_text1") != null) {
                    String payload_text = intent.getStringExtra("payload_text1");
                    String payload_title = intent.getStringExtra("payload_title1");
                    String payload_type = intent.getStringExtra("payload_type1");
//win
                    showDialog2(payload_title, payload_text);
                }
            } else {
                if (intent.getStringExtra("payload_text") != null) {
                    String payload_text = intent.getStringExtra("payload_text");
                    String payload_title = intent.getStringExtra("payload_title");
                    String payload_type = intent.getStringExtra("payload_type");
                    showDialog1(payload_title, payload_text);


                }
            }
        }


        Helper.reloadAllTheTabs(this);

        SharedPreferences prefs = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String selectedDevice = prefs.getString("selectedDevice", "");

        SharedPreferences.Editor sp = getSharedPreferences("MyProfile", MODE_PRIVATE).edit();
        sp.putString("selectedDeviceGlobal", selectedDevice);
        sp.commit();


        SharedPreferences pref = getSharedPreferences("MyCorpProfile", Context.MODE_PRIVATE);
        String features = pref.getString("features", "");

        try {
            JSONArray channelListObj = new JSONArray(features);
            JSONObject data = channelListObj.getJSONObject(0);
            String value = data.getString("value");
            if (value.equals("1")) {
                findViewById(R.id.layout_tab_chat).setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ((pref.getString("feature", "")).equals("GroupChat") && (pref.getString("shouldEnableChat", "")).equals("1")) {
            findViewById(R.id.layout_tab_chat).setVisibility(View.VISIBLE);
        }
       /* SharedPreferences pref = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
        if((pref.getString("feature","")).equals("GroupChat")&&(pref.getString("shouldEnableChat","")).equals("1")){
            findViewById(R.id.layout_tab_chat).setVisibility(View.VISIBLE);
        }*/


        left_drawer = (LinearLayout) this.findViewById(R.id.left_drawer);
        for (int i = 3; i <= 5; i++) {
            View toBeBlackedLayout = left_drawer.getChildAt(i);
            blackenTheView(toBeBlackedLayout);

        }
        //the need for this code is when the user chooses the myproifile tab and closes the app


        View defaultPreviousLayout = this.findViewById(R.id.defaultPreviousLayout);
        changeViwes(defaultPreviousLayout);
        previousLayout = defaultPreviousLayout;


        toolbar = (Toolbar) this.findViewById(R.id.tabanim_toolbar);
        TextView tv_appname = (TextView) this.findViewById(R.id.tv_appname);
        toolbar.setTitle("");
        tv_appname.setTextColor(Color.WHITE);
        tv_appname.setText(WalkingPrefrence.getInstance().programeName);
        int textSize = tv_appname.getLineCount() == 1 ? R.dimen.sp18 : R.dimen.sp16;
        float myTextSize = getResources().getDimension(textSize) / getResources().getDisplayMetrics().density;
        Log.e(TAG, "the text size is " + textSize + " my text size is " + myTextSize);
        tv_appname.setTextSize(TypedValue.COMPLEX_UNIT_SP, myTextSize);
        toolbar.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        setSupportActionBar(toolbar);
        sharedPreference = SharedPreference.getInstance(context);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.side_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        DbAdapter dbAdapter = DbAdapter.getInstance(context);
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);
        String userName = "", avatarUrl = "";
        if (cursor.getCount() > 0) {
            userName = cursor.getString(2);
            avatarUrl = cursor.getString(4);
        }

        ImageView imageView = (CircularImageView) this.findViewById(R.id.profileImg);
        prefs = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String userAvatarURL = prefs.getString("userAvatarURL", "");

        if (!(userAvatarURL == null || userAvatarURL.isEmpty())) {
            Picasso.with(context).load(userAvatarURL.replaceAll(" ", "%20")).placeholder(Helper.GetPlaceHolder(prefs)).transform(new CircleTransform()).into(imageView);
        } else {
            imageView.setImageResource(Helper.GetPlaceHolder(prefs));
        }

  /*
        String encoded = sharedPreference.getString(Constants.BITMAP, null);
        if (encoded != null) {
            byte[] imageAsBytes = Base64.decode(encoded, Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        } else if (!(avatarUrl == null || avatarUrl.isEmpty())) {
            Picasso.with(context).load(avatarUrl).placeholder(R.mipmap.default_user).transform(new CircleTransform()).into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.default_user);
        }
*/

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        // Set the adapter for the list view

        sharedPreference = SharedPreference.getInstance(context);

        String statusBarColor = AppTheme.getInstance().colorPrimaryDark;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }



        /*try {
            Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (Exception e){}*/


        fonts = new Fonts(getApplicationContext());
        Uri uriData = getIntent().getData();
        if (uriData == null) {

            Log.e(TAG, "uri data is null");
            getAuthToken = new GetAuthToken(context);
            fitbit_token=new GetAuthToken(context).getACCESS_TOKEN();
            fitbit_userid=new GetAuthToken(context).getUSER_ID();
        } else {
            Log.e(TAG, "uri data is not null and its value is " + uriData);
            getAuthToken = new GetAuthToken(context, uriData);
            fitbit_token=new GetAuthToken(context,uriData).getACCESS_TOKEN();
            fitbit_userid=new GetAuthToken(context,uriData).getUSER_ID();
        }
        if (getIntent().hasExtra(Constants.SERVICE)) {
            service = getIntent().getExtras().getString(Constants.SERVICE);
        }

        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        viewPager.setOffscreenPageLimit(Titles.length);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        //   tabLayout.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        tabLayout.setupWithViewPager(viewPager);
        setupTabs();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                callRespectiveMeathod(tab.getPosition());


                if (previousPosition != null) {
                    Drawable myDrawable = previousPosition.getCompoundDrawables()[1];
                    myDrawable.setColorFilter(AppTheme.blackFade, PorterDuff.Mode.SRC_ATOP);
                    previousPosition.setCompoundDrawables(null, myDrawable, null, null);
                    previousPosition.setTextColor(AppTheme.blackFade);
                }

                TextView view = (TextView) tab.getCustomView();
                Drawable myDrawable = view.getCompoundDrawables()[1];
                myDrawable.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);
                view.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
                view.setCompoundDrawables(null, myDrawable, null, null);
                previousPosition = (TextView) tab.getCustomView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        codeToSlideMainContent();

    }

    private void codeToSlideMainContent() {


        final LinearLayout mainContent = (LinearLayout) this.findViewById(R.id.main_content);

        ActionBarDrawerToggle mDrawerToggle;
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.appbar_scrolling_view_behavior, R.string.app_name) {
            @SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (left_drawer.getWidth() * slideOffset);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mainContent.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    mainContent.startAnimation(anim);

                    lastTranslate = moveFactor;
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);


    }

    private void callRespectiveMeathod(int position) {


        //this code is to call the shocase view if challenge tab is there otherwise hide it

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 2);
        if (fragment instanceof Challenge) {
            if (position != 2)
                ((Challenge) fragment).hideCardShowcaseView();
            else
                ((Challenge) fragment).doShowShowCaseView();
        }

        if (position == 0)
            return;


        // the code written below
        //ensures that the respective function of the
        //3 tabs which calls the api are only called on the first
        //time click

        SharedPreferences prefs = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + position);
        String booleanToget[] = {"reloadLeaderBoard", "reloadChallenge", "reloadNews"};

        boolean myBool = prefs.getBoolean(booleanToget[position - 1], false);


        switch (position) {
            case 1:
                if (fragment instanceof Leaderboard && myBool) {
                    ((Leaderboard) fragment).getMainLeaderBoard();

                }
                break;
            case 2:
                if (fragment instanceof Challenge && myBool) {
                    ((Challenge) fragment).getChallengeData();
                }
                break;
            case 3:
                if (fragment instanceof News && myBool) {
                    ((News) fragment).getNewsFeedFromApi();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
        unregisterReceiver(mNotificationReceiver);
        SetUpSHealth.getInstance().destroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setupTabs() {
        Log.e(TAG, "the count of adapter is " + adapter.getCount());
        for (int i = 0; i < adapter.getCount(); i++) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            textView.setText(Titles[i]);
            int myTextAndImageColor;
            if (i == 0) {
                myTextAndImageColor = Color.parseColor(AppTheme.getInstance().colorPrimary);
                previousPosition = textView;
            } else
                myTextAndImageColor = AppTheme.blackFade;

            Drawable myDrawable = getResources().getDrawable(TabIcons[i]);
            myDrawable.setColorFilter(myTextAndImageColor, PorterDuff.Mode.SRC_ATOP);
            textView.setCompoundDrawablesWithIntrinsicBounds(null, myDrawable, null, null);
            textView.setTextColor(myTextAndImageColor);
            tabLayout.getTabAt(i).setCustomView(textView);

        }
    }

    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), viewPager);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.GET_AUTH_TOKEN, getAuthToken);

        if (service != null) {
            bundle.putString(Constants.SERVICE, service);
        } else {
            bundle.putString(Constants.SERVICE, Constants.CALLBACK);
        }

        dashboard = new Dashboard();
        dashboard.setArguments(bundle);
        leaderboard = new Leaderboard();
        challenge = new Challenge();
        news = new News();


        adapter.addFrag(dashboard, "DASHBOARD");
        adapter.addFrag(leaderboard, "LEADERBOARD");
        adapter.addFrag(challenge, "CHALLENGE");
        adapter.addFrag(news, "NEWS");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void changeNavDrawerImage(String path) {

        Log.e(TAG, "here changing the image view to path " + path);
        SharedPreferences prefs = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        ImageView imageView = (CircularImageView) left_drawer.findViewById(R.id.profileImg);

        Picasso.with(context).invalidate(path);
        if (!(path == null || path.isEmpty())) {
            Picasso.with(context).load(path.replaceAll(" ", "%20")).placeholder(Helper.GetPlaceHolder(prefs)).transform(new CircleTransform()).into(imageView);
        } else {
            imageView.setImageResource(Helper.GetPlaceHolder(prefs));
        }

        Helper.reloadTabs(context, 1);


    }

    public void apiClientConnected() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).startRecording();
        }
    }

    public void getPadometerSteps() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).getPadometerSteps();
        }
    }

    public void apiClientConnectionFailed(int Selector) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            if (Selector == 0)
                ((Dashboard) fragment).syncDashboardFromPado();
            else if (Selector == 1)
                ((Dashboard) fragment).syncDashboardFromSHealth();
        }
    }

    public void setDataToShow(GetterSetter toShow) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).setDataToList(toShow);
        }
    }

    public void showError() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).showError();
        }
    }

    public void setDashboardCanvas(ArrayList<InfoStreamBean> arrayList) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).setCanvas(arrayList);
        }
    }

    public void showProgress(boolean show) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).showProgressBar(show);
        }
    }

    String tag;

    public void changeColor(final View view) {


        mDrawerLayout.closeDrawer(GravityCompat.START);
        LinearLayout fourTabLayout = (LinearLayout) this.findViewById(R.id.fourFragmentLayout);
        FrameLayout drawerFragmentLayout = (FrameLayout) this.findViewById(R.id.changeFragment);
        if (previousLayout != null) {
            blackenTheView(previousLayout);
        }

        if (drawerFragmentLayout.getChildCount() > 0)
            drawerFragmentLayout.removeAllViews();

        changeViwes(view);
        previousLayout = view;


        tag = view.getTag().toString();
        switch (tag) {
            case "logout": {
                checkmenu();
                info.setVisibility(View.GONE);
                new AlertDialog.Builder(context)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //   new LogoutAsync(context).execute();
                                Helper.logoutVolley(context, pDialog,view);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            }
            case "chat": {
                checkmenu();
                info.setVisibility(View.GONE);
                drawerFragmentLayout.removeAllViews();
                fourTabLayout.setVisibility(View.GONE);
                drawerFragmentLayout.setVisibility(View.VISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.changeFragment, new ChatGroupFragment());
                ft.commit();

                break;

            }

            case "mydevice": {
                checkmenu();
                info.setVisibility(View.GONE);
                Log.e(TAG, "Inside My device");
                drawerFragmentLayout.removeAllViews();
                fourTabLayout.setVisibility(View.GONE);
                drawerFragmentLayout.setVisibility(View.VISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.changeFragment, new MyDevice());
                ft.commit();

                break;
            }
            case "profile": {
                checkmenu();
                info.setVisibility(View.GONE);
                Log.e(TAG, "Inside Profile");
                drawerFragmentLayout.removeAllViews();
                fourTabLayout.setVisibility(View.GONE);
                drawerFragmentLayout.setVisibility(View.VISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.changeFragment, new MyProfile());
                ft.commit();
                break;
            }

            case "home": {
                checkmenu();
                info.setVisibility(View.GONE);
                drawerFragmentLayout.setVisibility(View.GONE);
                fourTabLayout.setVisibility(View.VISIBLE);
                Log.e(TAG, "Inside Home");
                break;
            }
            case "mygoals": {
                checkmenu();
                info.setVisibility(View.GONE);
                Log.e(TAG, "Inside Profile");
                drawerFragmentLayout.removeAllViews();
                fourTabLayout.setVisibility(View.GONE);
                drawerFragmentLayout.setVisibility(View.VISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.changeFragment, new MyGoals());
                ft.commit();
                break;
            }

        }


    }

    public void blackenTheView(View view) {

        view.setBackgroundColor(Color.WHITE);
        TextView heading = (TextView) ((LinearLayout) view).getChildAt(1);
        heading.setTextColor(ContextCompat.getColor(context, R.color.wc_default));
        ImageView image = (ImageView) ((LinearLayout) view).getChildAt(0);
        Drawable myDrawable = image.getDrawable();
        myDrawable.setColorFilter(ContextCompat.getColor(context, R.color.wc_default), PorterDuff.Mode.SRC_ATOP);
        image.setImageDrawable(myDrawable);

    }

    public void changeViwes(View view) {
        try {
            view.setBackgroundColor(Color.parseColor(AppTheme.getInstance(context).colorPrimary));
            TextView heading = (TextView) ((LinearLayout) view).getChildAt(1);
            heading.setTextColor(Color.WHITE);
            ImageView image = (ImageView) ((LinearLayout) view).getChildAt(0);
            Drawable myDrawable = image.getDrawable();
            myDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            image.setImageDrawable(myDrawable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + viewPager.getCurrentItem());
            if (fragment instanceof Challenge) {
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
            if (fragment instanceof Dashboard) {
                super.onBackPressed();
                finish();
            }
            if (fragment instanceof Leaderboard) {
                super.onBackPressed();
                finish();
            }
        } catch (Exception e) {
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).onActivityResult(requestCode, resultCode, data);
        }


        fragment = getSupportFragmentManager().findFragmentById(R.id.changeFragment);
        if (fragment instanceof MyProfile) {
            ((MyProfile) fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            if (intent.hasExtra("userid")) {
                if (intent.getStringExtra("payload_text1") != null) {
                    String payload_text = intent.getStringExtra("payload_text1");
                    String payload_title = intent.getStringExtra("payload_title1");
                    String payload_type = intent.getStringExtra("payload_type1");
//win
                    showDialog2(payload_title, payload_text);
                }
            } else {
                if (intent.getStringExtra("payload_text") != null) {
                    String payload_text = intent.getStringExtra("payload_text");
                    String payload_title = intent.getStringExtra("payload_title");
                    String payload_type = intent.getStringExtra("payload_type");
                    showDialog1(payload_title, payload_text);


                }
            }
        }
                    /*    intent1.putExtra("userid", userid);
                        intent1.putExtra("payload_text", payload_text);
                        intent1.putExtra("payload_title", payload_title);
                        intent1.putExtra("payload_type", payload_type);
        if (intent.hasExtra("userid")) {
            String userid = intent.getStringExtra("userid");
            String payload_text = intent.getStringExtra("payload_text");
            String payload_title = intent.getStringExtra("payload_title");
            String payload_type = intent.getStringExtra("payload_type");
            showDialog2(payload_title, payload_text);
        } else {
            String payload_text = intent.getStringExtra("payload_text");
            String payload_title = intent.getStringExtra("payload_title");
            String payload_type = intent.getStringExtra("payload_type");
            showDialog1(payload_title, payload_text);
        }*/


    }


    public void showDialog2(final String payload_title, final String payload_text) {

        winnerDialog.setTextonContent(payload_text);
        winnerDialog.setTextonHeader(payload_title);
        winnerDialog.setWinnerDialogListener(this);
        winnerDialog.setCanceledOnTouchOutside(false);
        winnerDialog.setCancelable(true);
        if (!winnerDialog.isShowing()) {
            winnerDialog.show();
        }
    }

    /*public void showDialog2(final String payload_title, final String payload_text) {

//        openPopup = true;
//        setContentView(R.layout.popup);
        Toast.makeText(context, "ddddd", Toast.LENGTH_SHORT).show();
        RelativeLayout promptView = (RelativeLayout) TabActivity.this.getLayoutInflater().inflate(R.layout.winner, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this)
                .setView(promptView);
        final AlertDialog alertDialog = builder.create();

        TextView title = (TextView) promptView.findViewById(R.id.congo);
        title.setText(payload_title.toString());
        TextView des = (TextView) promptView.findViewById(R.id.dis);
        des.setText(payload_text.toString());

        ImageView ok = (ImageView) promptView.findViewById(R.id.close);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        ImageView fb = (ImageView) promptView.findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callShareIntent(TabActivity.this, payload_text, payload_title);
            }
        });


        ImageView tw = (ImageView) promptView.findViewById(R.id.tw);
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callShareIntent(TabActivity.this, payload_text, payload_title);
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }*/

    @Override
    protected void onResume() {
        AppController.isActive = true;
        super.onResume();
    }

    public void checkmenu() {
        if (tag != null) {
            if (!tag.equalsIgnoreCase("mydevice")) {
                if (menu != null) {
                    if (menu.findItem(R.id.action_menu_shealth) != null) {
                        menu.findItem(R.id.action_menu_shealth).setVisible(false);
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        AppController.isActive = false;
        super.onPause();
        onStart();
    }


    public void showDialog1(String payload_title, String payload_text) {
        notificationDialog.setTextonContent(payload_text);
        notificationDialog.setTextonHeader(payload_title);
        notificationDialog.setNotificationListener(this);
        notificationDialog.setCanceledOnTouchOutside(false);
        notificationDialog.setCancelable(true);
        if (!notificationDialog.isShowing()) {
            notificationDialog.show();
        }
    }

    @Override
    public void onPositiveButtonClick() {
        if (notificationDialog != null && notificationDialog.isShowing()) {
            notificationDialog.dismiss();
        }
    }

    @Override
    public void onclickoncancel() {
        if (winnerDialog != null && winnerDialog.isShowing()) {
            winnerDialog.dismiss();
        }
    }

    @Override
    public void onfacebookbuttonclick(String payload_title, String payload_text) {
        if (winnerDialog != null && winnerDialog.isShowing()) {
            //  winnerDialog.dismiss();
        }
        shareOnFacebook(payload_text, payload_title);
        // callShareIntent(TabActivity.this, payload_text, payload_title);
    }

    private void shareOnFacebook(String payload_text, String payload_title) {

        FacebookSdk.sdkInitialize(context);


        Log.e(TAG, "the payload data is " + payload_text + " " + payload_title);
 /*       SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(getBitmapFromResource(R.mipmap.prizee))
                .setCaption(payload_title)
                .setUserGenerated(false)
                .build();*/

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(payload_title)
                .setContentDescription(payload_text)
                .setContentUrl(Uri.parse("http://www.tupelolife.com/"))
                .setImageUrl(Uri.parse("http://worldartsme.com/images/winner-trophy-clipart-1.jpg"))
                .build();


/*

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle("Your Title")
                .setContentDescription("Your Description")
                .setImageUrl(Uri.parse(""))
                .build();
*/
/*
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();*/


        ShareDialog shareDialog = new ShareDialog(TabActivity.this);

        shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (tag != null) {
            if (!tag.equalsIgnoreCase("mydevice")) {
                if (menu.findItem(R.id.action_menu_shealth) != null) {
                }
            }
        }
        return false;
    }

    @Override
    public void ontwitterbuttonclick(String payload_title, String payload_text) {
        if (winnerDialog != null && winnerDialog.isShowing()) {
            // winnerDialog.dismiss();
        }

        shareOnTwitter(payload_title, payload_text);
        // callShareIntent(TabActivity.this, payload_text, payload_title);
    }

    private void shareOnTwitter(String payload_title, String payload_text) {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        int flag = 0;
        for (final ResolveInfo app : activityList) {
            Log.e("PACKAGE NAME", app.activityInfo.name);
            if ((app.activityInfo.name).contains("com.twitter.android")) {
                flag = 1;
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                shareIntent.putExtra(Intent.EXTRA_STREAM, getUriFromBitMap(getBitmapFromResource(R.mipmap.prizee)));
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, payload_title + "\n" + payload_text);
                startActivity(shareIntent);
                break;
            }
        }
        if (flag == 0)
            Toast.makeText(context, "Please Install Twitter App To Share On Twitter", Toast.LENGTH_LONG);


    }

    private Uri getUriFromBitMap(Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Winner", null);
        return Uri.parse(path);
    }

    private Bitmap getBitmapFromResource(int prizee) {

        Bitmap bm =/* Bitmap.createScaledBitmap(*/BitmapFactory.decodeResource(getResources(), prizee)/*, 120, 120, false)*/;
        return bm;
    }

    public void swipeRefreshDashBoard(boolean b) {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 0);
        if (fragment instanceof Dashboard) {
            ((Dashboard) fragment).swipeRefreshLayout.setRefreshing(b);
        }


    }


    /*public void showDialog1(String payload_title, String payload_text) {

//        openPopup = true;
//        setContentView(R.layout.popup);

        RelativeLayout promptView = (RelativeLayout) TabActivity.this.getLayoutInflater().inflate(R.layout.announcement, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this)
                .setView(promptView);


          alertDialog = builder.create();


        TextView title = (TextView) promptView.findViewById(R.id.congo);
        title.setText(payload_title.toString());


        TextView des = (TextView) promptView.findViewById(R.id.dis);
        des.setText(payload_text.toString());


        LinearLayout ok = (LinearLayout) promptView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        if (alertDialog.isShowing() && !TabActivity.this.isFinishing()) {
            alertDialog.dismiss();
        }
         alertDialog.show();
    }*/

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        static ViewPager pager;
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager, ViewPager pager) {
            super(manager);
            this.pager = pager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



}
