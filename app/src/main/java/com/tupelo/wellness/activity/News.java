package com.tupelo.wellness.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.ComplexRecyclerViewAdapter;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.NewsBean;
import com.tupelo.wellness.helper.WalkingPrefrence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 09-09-2015.
 */
public class News extends Fragment {

    private RecyclerView fragmentRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog pDialog;
    String TAG = "News";
   // TwoButtons buttons;
    ComplexRecyclerViewAdapter adapter;
    List<NewsBean> newsFeeds = new ArrayList<NewsBean>();
    TextView hahtag,all;

    GradientDrawable backgroundGradient,backgroundGradient2;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_main, container, false);
        Log.e(TAG, "Inside News Page 1");
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        fragmentRecyclerView = (RecyclerView) v.findViewById(R.id.fragmentRecyclerView);
        fragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsFeeds = new ArrayList<NewsBean>();

        adapter = new ComplexRecyclerViewAdapter(newsFeeds, getActivity());
        fragmentRecyclerView.setAdapter(adapter);
      // buttons = (TwoButtons) v.findViewById(R.id.twoButtons1);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);



         hahtag=(TextView)v.findViewById(R.id.hahtag);
         all=(TextView)v.findViewById(R.id.add);


         backgroundGradient = (GradientDrawable)all.getBackground();
        backgroundGradient.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        backgroundGradient2 = (GradientDrawable)hahtag.getBackground();
        backgroundGradient2.setColor(Color.WHITE);


        all.setTextColor(Color.WHITE);
        /*all.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));*/
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setTextColor(Color.WHITE);
                /*all.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));*/
                backgroundGradient = (GradientDrawable)all.getBackground();
                backgroundGradient.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
                hahtag.setTextColor(Color.BLACK);

                backgroundGradient2 = (GradientDrawable)hahtag.getBackground();
                backgroundGradient2.setColor(Color.WHITE);
                filterFeeds("all");
                /*hahtag.setBackgroundColor(Color.WHITE);*/
            }
        });




        hahtag.setTextColor(Color.BLACK);
        /*hahtag.setBackgroundColor(Color.WHITE);*/
        hahtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundGradient2 = (GradientDrawable)hahtag.getBackground();
                backgroundGradient2.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
                hahtag.setTextColor(Color.WHITE);


                all.setTextColor(Color.BLACK);
                backgroundGradient = (GradientDrawable)all.getBackground();
                backgroundGradient.setColor(Color.WHITE);
                filterFeeds("hash");
            }
        });



/*
        buttons.setOnButtonClickEvent(new TwoButtons.ButtonClickEvents() {

            @Override
            public void redButtonClick() {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "red",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void blueButtonClick() {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "blue",
                        Toast.LENGTH_SHORT).show();

            }
        });
*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                Log.e(TAG, "log 1");
                if (CheckConnection.isConnection(getActivity())) {
                    Log.e(TAG, "log 2");
                    swipeRefreshLayout.setColorSchemeResources(R.color.ColorPrimary);
                    swipeRefreshLayout.setRefreshing(true);
                    getNewsFeedFromApi();
                } else {
                    Log.e(TAG, "log 3");
                    new Helper().showNoInternetToast(getActivity());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*            try {
                if (CheckConnection.isConnection(getActivity())) {
                    getNewsFeedFromApi();
                } else {
                    new Helper().showNoInternetToast(getActivity());
                }
            } catch (Exception e){}
            Log.e(TAG, "Inside News Page 2");*/
    }


    public void getNewsFeedFromApi() {
        swipeRefreshLayout.setRefreshing(true);
        pDialog.show();
        Log.e(TAG, "log 4");
        Log.e(TAG, "Inside volley calling methyod");
        String url = Constants.BASE_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonResponse) {
                        Log.e(TAG, "Response is " + jsonResponse);
                        pDialog.hide();
                        Log.e(TAG, "log 5");
                        Helper.doLoadAgain(getActivity(), 3, false);
                        try {
                            swipeRefreshLayout.setRefreshing(false);
                            newsFeeds.clear();
                            JSONArray newsfeeds = new JSONObject(jsonResponse).getJSONObject("#data").getJSONArray("newsfeeds");
                            for (int i = 0; i < newsfeeds.length(); i++) {
                                String type = "";
                                JSONObject myCurrentFeed = newsfeeds.getJSONObject(i);
                                String feed_media_url = myCurrentFeed.optString("feed_media_url");
                                String feed_user = myCurrentFeed.optString("feed_user");
                                String feed_timestamp = myCurrentFeed.optString("feed_timestamp");
                                String feed_caption = myCurrentFeed.optString("feed_caption");
                                String feed_type = myCurrentFeed.optString("feed_type");
                                String feed_title = myCurrentFeed.optString("feed_title");
                                type = myCurrentFeed.optString("type");

                                if (feed_type.equalsIgnoreCase("5") | feed_type.equalsIgnoreCase("4")) {
                                    Log.e(TAG, feed_media_url);
                                }

                                NewsBean newsBean = new NewsBean(feed_media_url, feed_user, feed_timestamp, feed_caption, feed_type, feed_title, type);
                                newsFeeds.add(newsBean);

                            }
                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "log 6");
                        pDialog.hide();
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("ERROR", "error => " + error.getMessage());
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                String corpId = WalkingPrefrence.getInstance().corpId;
                String sessionid = new Helper().getSessionId(getActivity());

                if (sessionid == null) {
                    sessionid = getActivity().getSharedPreferences("MyProfile", 0).getString("sessid", "");
                }

                String method = "corpchallenge.getnewsfeed";
                Map<String, String> params = new HashMap<String, String>();
                params = new Helper().addRequiredParams(params, method);
                //params.put("sessid", new Helper().getSessionId(getActivity()));
                params.put("sessid", sessionid);
                params.put("method", method);
                params.put("corpid", corpId);
                Log.e(TAG, params.toString());
                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }

    public void filterFeeds(String type) {

        Log.e(TAG, "coming to filter feeds value-> " + type);


        if (type.contains("hash")) {

            if(newsFeeds.size()>0)
            {
                List<NewsBean> resultList = new ArrayList<NewsBean>();
                for (NewsBean result : newsFeeds) {
                    if (result.feed_type.equalsIgnoreCase("6") || result.feed_type.equalsIgnoreCase("7"))
                        resultList.add(result);
                }

                adapter = new ComplexRecyclerViewAdapter(resultList,getActivity());
                fragmentRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

        } else if (type.equalsIgnoreCase("all")) {


            adapter = new ComplexRecyclerViewAdapter(newsFeeds,getActivity());
            fragmentRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }


    }
}



