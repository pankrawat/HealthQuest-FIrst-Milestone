package com.tupelo.wellness.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.squareup.picasso.Picasso;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.CircleTransform;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.R;
import com.tupelo.wellness.bean.LeaderBoardBean;
import com.tupelo.wellness.circularimageview.CircularImageView;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.LeaderAdapter;
import com.tupelo.wellness.helper.NpaGridLayoutManager;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.parcer.Jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 09-09-2015.
 */
public class Leaderboard extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView leaderRecyclerView;
    Fonts fonts;
    Context context;
    List<LeaderBoardBean> llbb = new ArrayList<>();
    LeaderAdapter leaderAdapter;
    View view;
    String TAG = Leaderboard.class.getSimpleName();
    ProgressDialog pDialog;
    private CircularImageView imageView;
    SharedPreference sharedPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_leaderboard, container, false);
        context = getActivity();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        fonts = new Fonts(getActivity());
        initView(v);
        sharedPreference = SharedPreference.getInstance(getActivity());
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayoutLeader);
        swipeRefreshLayout.setOnRefreshListener(this);


        return v;
    }

    private void initView(View v) {

        view = v.findViewById(R.id.card_view_user_rank);
        leaderAdapter = new LeaderAdapter(getActivity(), llbb);
        leaderRecyclerView = (RecyclerView) v.findViewById(R.id.leaderBoardRecyclerView);
        leaderRecyclerView.setAdapter(leaderAdapter);


        NpaGridLayoutManager manager = new NpaGridLayoutManager(context, 3);
        manager.setSpanSizeLookup(new NpaGridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == 1 || position == 2)
                    return 1;
                return 3;
            }
        });

        leaderRecyclerView.setLayoutManager(manager);
        //    leaderRecyclerView.setLayoutManager(new LinearLayoutManager(context));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void setListAdapter(GetterSetter toShow) {
        Log.e(TAG, llbb.size() + "");
        getHeaderView(toShow);
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private void getHeaderView(GetterSetter toShow) {

        view.setVisibility(View.VISIBLE);
        Log.e(TAG, "inside header view " + toShow.getRank());


        TextView change = (TextView) view.findViewById(R.id.changeTxt);
        TextView rank = (TextView) view.findViewById(R.id.headerRnkTxt);
        TextView steps = (TextView) view.findViewById(R.id.headerStepsTxt);
        imageView = (CircularImageView) view.findViewById(R.id.profileImg);


        if (sharedPreference.contains("rank")) {
            int prev_rank = Integer.parseInt(sharedPreference.getString("rank", "0"));
            int cur_rank = Integer.parseInt(toShow.getRank());
            if (prev_rank == cur_rank) {
                change.setText("0");
                ColorStateList colorDefaultBlack = rank.getTextColors();
                change.setTextColor(colorDefaultBlack);
            } else if (prev_rank > cur_rank) {
                int cur_change = prev_rank - cur_rank;
                change.setText("+" + cur_change);
                change.setTextColor(getResources().getColor(R.color.green));
            } else {
                int cur_change = cur_rank - prev_rank;
                change.setText("-" + cur_change);
                change.setTextColor(getResources().getColor(R.color.red));
            }
        } else {
            sharedPreference.putString("rank", toShow.getRank());
        }
        rank.setText(toShow.getRank());
        steps.setText(toShow.getSteps());
        DbAdapter dbAdapter = DbAdapter.getInstance(getContext());
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);
        String userName = "";
        if (cursor.getCount() > 0) {
            userName = cursor.getString(2);

        }

        SharedPreferences prefs = getActivity().getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String userAvatarURL = prefs.getString("userAvatarURL", "");

        if (!(userAvatarURL == null || userAvatarURL.isEmpty())) {
            Picasso.with(getContext()).load(userAvatarURL.replaceAll(" ", "%20")).placeholder(Helper.GetPlaceHolder(prefs)).transform(new CircleTransform()).into(imageView);
        } else {
            imageView.setImageResource(Helper.GetPlaceHolder(prefs));
        }


    }

    @Override
    public void onRefresh() {
        Log.e("swipe refresh", "on refresh leaderboard");

        if (CheckConnection.isConnection(getActivity())) {
            swipeRefreshLayout.setColorSchemeResources(R.color.ColorPrimary);
            swipeRefreshLayout.setRefreshing(true);
            getMainLeaderBoard();
        } else {
            new Helper().showNoInternetToast(getActivity());
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }


    public void getMainLeaderBoard() {
        llbb.clear();


        Log.e(TAG, "getting list of main leader board ");
        pDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        swipeRefreshLayout.setRefreshing(false);

                        Helper.doLoadAgain(getActivity(), 1, false);
                        Log.e(TAG, "Response is " + response);

                        Jsonparser jsonparser = new Jsonparser(response);
                        String result = jsonparser.getErrorCodePersonalDetail();
                        if (result.equals("0")) {
                            GetterSetter getterSetter;
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("#data");
                                JSONArray jsonArray = jsonObject1.getJSONArray("content");


                                int i;
                                for (i = 0; i < jsonArray.length(); i++) {
                                    LeaderBoardBean lead = new LeaderBoardBean();
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    if (!jsonObject2.isNull("name")) {
                                        lead.setName(jsonObject2.getString("name"));
                                    }
                                    if (!jsonObject2.isNull("steps")) {
                                        lead.setSteps(jsonObject2.getString("steps"));
                                    }
                                    if (!jsonObject2.isNull("photourl")) {
                                        lead.setImageUrl(jsonObject2.getString("photourl"));
                                    }
                                    lead.setRank(String.valueOf(i + 1));
                                    llbb.add(lead);
                                }

                                Log.e(TAG, " i is " + i);

                                if (jsonArray.length() == 1) {
                                    Log.e(TAG, "length is only one making it 2");
                                    //as we need atleast two items to show item one at position 2
                                    llbb.add(new LeaderBoardBean());
                                }

                                llbb = swapList(llbb, 0, 1);
                                leaderAdapter.notifyDataSetChanged();
                                pDialog.hide();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            llbb = jsonparser.getLeaderboardList(llbb);
                            getterSetter = jsonparser.getLeaderboardInfo(context);
                            setListAdapter(getterSetter);
                        } else if (result.equals("-3")) {
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

                String sessionId = "", userId = "", corpId = "";
                if (cursor.getCount() > 0) {
                    sessionId = cursor.getString(0);
                    userId = cursor.getString(1);
                    corpId = cursor.getString(3);
                }


                Map<String, String> params = new HashMap<String, String>();
                String method = "corpchallenge.getmainleaderboard";

                params.clear();
                params.put("method", method);
                params.put("sessid", sessionId);
                params.put("userid", userId);
                params.put("corpid", corpId);

                params = new Helper().addRequiredParams(params, method);

                Log.e(TAG, "params are " + params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }


    private List<LeaderBoardBean> swapList(List<LeaderBoardBean> llbb, int index1, int index2) {

        LeaderBoardBean lb1 = null;
        LeaderBoardBean lb2 = null;
        lb1 = llbb.get(index1);
        lb2 = llbb.get(index2);
        llbb.set(index2, lb1);
        llbb.set(index1, lb2);

        return llbb;
    }


}
