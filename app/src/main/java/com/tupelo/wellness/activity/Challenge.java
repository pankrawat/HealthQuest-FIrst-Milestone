package com.tupelo.wellness.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.R;
import com.tupelo.wellness.bean.ChalangeBean;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.showcaseview.ShowcaseView;
import com.tupelo.wellness.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import android.content.SharedPreferences;

//import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 09-09-2015.
 */
public class Challenge extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    Context context;
    private ProgressDialog pDialog;
    private ListView chalengeList;
    private ArrayList<ChalangeBean> dataList = new ArrayList<>();
    FrameLayout fragContainer;
    String TAG = Challenge.class.getSimpleName();
    Fonts fonts;
    CAdapter cAdapter;
    ShowcaseView showcaseView;
    View view;
    boolean isShowing = false;
    SharedPreference sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_challenge, container, false);
        view = v;
        fragContainer = (FrameLayout) v.findViewById(R.id.lb);
        chalengeList = (ListView) v.findViewById(R.id.chalengeList);
        context = getActivity();

        fonts = new Fonts(context);
        sharedPreferences = SharedPreference.getInstance(context);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayoutChallenge);
        swipeRefreshLayout.setOnRefreshListener(this);
        chalengeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isShowing) {
                    showcaseView.hide();
                    sharedPreferences.putBoolean(Constants.CARD_SHOWCASE, true);
                }

                String challengeId = dataList.get(position).getId();
                String imageUrl = dataList.get(position).getImageUrl();
                String desc = dataList.get(position).getDescription();
//                Log.d("item clicked", "");
                Bundle bundle = new Bundle();
                bundle.putString("challengeId", challengeId);
                bundle.putString("imageUrl", imageUrl);
                bundle.putString("desc", desc);
                LBFragment lbFragment = new LBFragment();
                lbFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.lb, lbFragment).addToBackStack(this.toString()).commit();

            }
        });
        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }


    public void showCardShowcaseView() {


        try {
            if(showcaseView == null) {
                final Activity activity = getActivity();

                final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.imageLayout1);


                final Rect rect = new Rect();
                final int loc[] = new int[2];
                relativeLayout.getLocalVisibleRect(rect);
                relativeLayout.getLocationOnScreen(loc);

                Challenge myChallenge = (Challenge) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.tabanim_viewpager + ":" + 2);
                if (myChallenge != null && myChallenge.isVisible()) {

                    ViewTarget target1 = new ViewTarget(R.id.imageLayout1, getActivity());

                    showcaseView = new ShowcaseView.Builder(activity, true)
                            .setTarget(target1)
                            .setStyle(R.style.CustomShowcaseTheme)
                            .setContentTitle("Click On Highlited Area")
                            .setContentText("To see the latest leaderboard results for this challenge.")
                            //.hideOnTouchOutside()

                            .build();
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;
                    //Log.d("rect", "" + loc[0] + " " + loc[1] + " " + rect.right + " " + rect.bottom);
                    showcaseView.setViewRect(6, loc[1], width - 6, rect.height());

                    showcaseView.hideButton();
                    isShowing = true;
                }
            }
            else
                showcaseView.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideCardShowcaseView() {
        if (isShowing) {
            showcaseView.hide();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


//        Log.d("in", "challenge");
    }


    public void setListAdapter(ArrayList<ChalangeBean> arr) {
        dataList = arr;
//        Log.e("Size", arr.size() + "");
        cAdapter = new CAdapter(dataList);
        chalengeList.setAdapter(cAdapter);

        doShowShowCaseView();


        cAdapter.notifyDataSetChanged();


    }

    @Override
    public void onRefresh() {

        Log.e("swipe refresh", "on refresh challenge");
        if (CheckConnection.isConnection(getActivity())) {
            swipeRefreshLayout.setColorSchemeResources(R.color.ColorPrimary);

            getChallengeData();
            //  new ChallengeAsync(context, true).execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            new Helper().showNoInternetToast(getActivity());
        }
    }


    public void doShowShowCaseView()
    {

        if (dataList.size() >= 1) {
            boolean isShowedSwipeShowcaseView = sharedPreferences.getBoolean(Constants.CARD_SHOWCASE, false);
            Log.e(TAG, "isShowedSwipeShowcaseView " + isShowedSwipeShowcaseView);
            if (isShowedSwipeShowcaseView == false) {
                showCardShowcaseView();
            }
        }
    }

    public ArrayList<ChalangeBean> getChallengesList(String res) {
        ArrayList<ChalangeBean> arr = new ArrayList<ChalangeBean>();
        try {
            JSONObject jsonObject = new JSONObject(res);
            Log.e(TAG, "the challenge data is " + res.toString());
            JSONObject json = jsonObject.getJSONObject("#data");
            //Log.d("ChallengeAsync", json.toString());
            int count = json.getInt("challengecount");
            if (count > 0) {
                JSONArray jsonArray = json.getJSONArray("challenges");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ChalangeBean lead = new ChalangeBean();
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    if (!jsonObject2.isNull("id")) {
                        lead.setId(jsonObject2.getString("id"));
                    }
                    if (!jsonObject2.isNull("title")) {
                        lead.setTitle(jsonObject2.getString("title"));
                    }
                    if (!jsonObject2.isNull("desc")) {
                        lead.setDesccription(jsonObject2.getString("desc"));
                    }
                    if (!jsonObject2.isNull("status")) {
                        lead.setStatus(jsonObject2.getString("status"));
                    }
                    if (!jsonObject2.isNull("imageURL")) {
                        lead.setImageUrl(jsonObject2.getString("imageURL"));
                    }
                    if (!jsonObject2.isNull("category")) {
                        lead.setCategory(jsonObject2.getString("category"));
                    }
                    if (!jsonObject2.isNull("challengeweight")) {
                        lead.setChalangeWeight(jsonObject2.getString("challengeweight"));
                    }
                    if (!jsonObject2.isNull("startdate")) {
                        lead.setStartDate(jsonObject2.getString("startdate"));
                    }
                    if (!jsonObject2.isNull("enddate")) {
                        lead.setEndDate(jsonObject2.getString("enddate"));
                    }
                    arr.add(lead);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }


    public void getChallengeData() {
        swipeRefreshLayout.setRefreshing(true);
        pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "Response is " + response);
                        Helper.doLoadAgain(getActivity(), 2, false);
                        if (!response.equals("Connection Timeout")) {
//            Log.d("challenge response", response);
                            ArrayList<ChalangeBean> arr = getChallengesList(response);
                            setListAdapter(arr);
                        } else {
                            Toast.makeText(context, context.getString(R.string.universal_err), Toast.LENGTH_SHORT).show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error 3=> " + error.getMessage());
                        pDialog.hide();

                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {


                DbAdapter dbAdapter = DbAdapter.getInstance(context);
                Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);

                String sessionId = "", corpId = "";
                if (cursor.getCount() > 0) {
                    sessionId = cursor.getString(0);
                    corpId = cursor.getString(3);
                }

                Calendar calendar = Calendar.getInstance();
                String date = calendar.get(Calendar.YEAR) + "-" + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);


                String method = "corpchallenge.getdaychallenges";

                Map<String, String> params = new HashMap<String, String>();
                params.put("method", method);
                params.put("sessid", sessionId);
                params.put("corpid", corpId);
                params.put("date", date);


                params = new Helper().addRequiredParams(params, method);

                Log.e(TAG, "params are " + params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }

    class CAdapter extends BaseAdapter {
        private ArrayList<ChalangeBean> list;
        private ViewHolder holder;

        public CAdapter(ArrayList<ChalangeBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chalange_list_item, null);

                holder.title = (TextView) view.findViewById(R.id.challenge_title);
                holder.type = (TextView) view.findViewById(R.id.challenge_type);
                holder.imageView = (ImageView) view.findViewById(R.id.challenge_imageView);
                holder.startTime = (TextView) view.findViewById(R.id.tv_starttime);
                holder.endTime = (TextView) view.findViewById(R.id.tv_endtime);
                holder.startTimeHeading = (TextView) view.findViewById(R.id.start_time_heading);
                holder.endTimeHeading = (TextView) view.findViewById(R.id.end_time_heading);
                holder.startTimeHeading = AppTheme.changeBackgroundColor(holder.startTimeHeading);
                holder.endTimeHeading = AppTheme.changeBackgroundColor(holder.endTimeHeading);

                //  holder.title.setTypeface(fonts.getTypefaceBold());
                //  holder.type.setTypeface(fonts.getTypefaceNormal());
                //  holder.startTime.setTypeface(fonts.getTypefaceNormal());
                //  holder.endTime.setTypeface(fonts.getTypefaceNormal());
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.title.setText(list.get(position).getTitle());
            holder.type.setText(list.get(position).getCategory());

            SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
            Date date;
            try {
                date = sdfParse.parse(list.get(position).getStartDate());
                holder.startTime.setText(sdf.format(date.getTime()));
                date = sdfParse.parse(list.get(position).getEndDate());
                holder.endTime.setText(sdf.format(date.getTime()));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            //uncomment below code
            if (list.get(position).getImageUrl() != null && !list.get(position).getImageUrl().equals("")) {
                Picasso.with(context).load(list.get(position).getImageUrl().replaceAll(" ", "%20")).into(holder.imageView);
//                Log.e("ImageUrl", list.get(position).getImageUrl());
            }

            //comment below code
            //Picasso.with(context).load(R.mipmap.bg_half).into(holder.imageView);

            return view;
        }

        class ViewHolder {
            TextView title, type, startTime, endTime, startTimeHeading, endTimeHeading;
            ImageView imageView;
        }
    }
}
