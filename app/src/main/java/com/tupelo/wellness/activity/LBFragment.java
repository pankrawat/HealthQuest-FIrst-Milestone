package com.tupelo.wellness.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
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
import com.squareup.picasso.Transformation;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.R;
import com.tupelo.wellness.RoundCornerTransformation;
import com.tupelo.wellness.bean.LeaderListBean;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.WalkingPrefrence;
import com.tupelo.wellness.parcer.Jsonparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.tupelo.wellness.R.id.dateTvParent;

//import android.util.Log;

public class LBFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = LBFragment.class.getSimpleName();
    public Date date;
    Context context;
    String challengeid, imageUrl, desc;
    Fonts fonts;
    TextView dateTextView;
    Helper helper;
    FrameLayout background_date;
    DatePickerDialog datePickerDialog;
    TextView dateTV;
    String year1, month1, day1;
    private String stepsLabel = "STEPS";
    private ProgressDialog pDialog;
    private ListView challenge_list;
    private ArrayList<LeaderListBean> leader_list;
    private OnFragmentInteractionListener mListener;

    public LBFragment() {
        // Required empty public constructor

    }

    public static LBFragment newInstance(String param1, String param2) {
        LBFragment fragment = new LBFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lb, container, false);


        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

       /* dateTV = (TextView) v.findViewById(R.id.select_date);
        dateTV = AppTheme.changeBackgroundColor(dateTV);
*/

        showDatePicker();
    /*    dateTV.setOnClickListener(this);
*/
        challenge_list = (ListView) v.findViewById(R.id.challenge_list);
        context = getActivity();
        fonts = new Fonts(context);
        helper = new Helper();
        Bundle bundle = this.getArguments();
        challengeid = bundle.getString("challengeId");
        imageUrl = bundle.getString("imageUrl");
        desc = bundle.getString("desc");
        if (CheckConnection.isConnection(getActivity())) {
            getLeaderBoardVolley("");
        } else {
            new Helper().showNoInternetToast(getActivity());
        }
        return v;
    }

    private void getLeaderBoardVolley(final String date) {
        pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        Log.e("responce", "Response is " + response.toString());


                        Jsonparser jsonparser = new Jsonparser(response);
                        boolean showDatePicker = jsonparser.getChallengeCategory();

                        int visibility = showDatePicker ? View.VISIBLE : View.GONE;

                        Log.e(TAG, showDatePicker + " " + visibility);


                        //   dateTV.setVisibility(visibility);


                        String message = jsonparser.getErrorCodePersonalDetail();
                        leader_list = jsonparser.getLeaderList();

                        GetterSetter result = jsonparser.getLeaderCard(context);
                        setListAdapter(result, leader_list,visibility);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error 3=> " + error.getMessage());
                        pDialog.hide();
                        Toast.makeText(getActivity(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {

                String sessionId = "", userId = "", corpId = "";

                sessionId = helper.getSessionId(context);
                userId = helper.getUserId(context);
                corpId = helper.getCorpId(context);

                String method = "corpchallenge.getleaderboard";

                // addRequiredParams(method);

                Map<String, String> params = new HashMap<String, String>();

                params = new Helper().addRequiredParams(params, method);

                params.put("method", method);
                if (!date.isEmpty()) {
                    params.put("dateStr", date);
                }
                params.put("sessid", sessionId);
                params.put("userid", userId);
                params.put("corpid", corpId);
                params.put("challengeid", challengeid);
                Log.e(TAG, "params are " + params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
           /* case R.id.select_date:
               datePickerDialog.show();
                break;*/
            case dateTvParent:
                datePickerDialog.show();
                break;
        }
    }

    public void setListAdapter(GetterSetter toShow, ArrayList<LeaderListBean> arr, int visibility) {
//        Log.e("Size", arr.size() + "");
        Log.e(TAG, "header count is " + challenge_list.getHeaderViewsCount());
        if (challenge_list.getHeaderViewsCount() == 0)
            challenge_list.addHeaderView(getHeaderView(toShow,visibility));
        LLAdapter leaderAdapter = new LLAdapter(leader_list);
        challenge_list.setAdapter(leaderAdapter);
    }

    private View getHeaderView(GetterSetter toShow, int visibility) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.challenge_header_view, null);
        TextView headernameTxt = (TextView) view.findViewById(R.id.cheaderTextView);
        TextView descTxt = (TextView) view.findViewById(R.id.cDescTextView);
        ImageView imageView = (ImageView) view.findViewById(R.id.chalengeimageView);
        TextView startTime = (TextView) view.findViewById(R.id.tv_starttime);
        TextView endTime = (TextView) view.findViewById(R.id.tv_endtime);
        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        tick.getDrawable().setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);
        TextView startTimeHeading = (TextView) view.findViewById(R.id.start_time_heading);
        startTimeHeading = AppTheme.changeBackgroundColor(startTimeHeading);

        TextView endTimeHeading = (TextView) view.findViewById(R.id.end_time_heading);
        endTimeHeading = AppTheme.changeBackgroundColor(endTimeHeading);


        RelativeLayout dateTvParent = (RelativeLayout) view.findViewById(R.id.dateTvParent);
        dateTvParent.setVisibility(visibility);
        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        dateTextView.setTextColor(Color.WHITE);
        dateTvParent.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        dateTvParent.setOnClickListener(this);

        headernameTxt.setText(toShow.getL_name());
        stepsLabel = toShow.getL_stepLabel();
        descTxt.setText(desc);
        //yyyy-month-date hh:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date;
        try {
            date = sdfParse.parse(toShow.getL_challenge_start());
            startTime.setText(sdf.format(date.getTime()));
            date = sdfParse.parse(toShow.getL_challenge_end());
            endTime.setText(sdf.format(date.getTime()));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("fooooooooooooo", "image url is " + imageUrl);
        Picasso.with(context).load(imageUrl.replaceAll(" ", "%20")).into(imageView);

        //    headernameTxt.setTypeface(fonts.getTypefaceBold());
        //      descTxt.setTypeface(fonts.getTypefaceNormal());
        //      startTime.setTypeface(fonts.getTypefaceNormal());
        //      endTime.setTypeface(fonts.getTypefaceNormal());

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }

    public void showDatePicker() {


        Calendar calendar = Calendar.getInstance();
/*
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
*/

        final SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String startdate = WalkingPrefrence.getInstance().prgrm_sdate.toString();
        Log.e("", startdate);
        SimpleDateFormat dateFormat;

        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = dateFormat.parse(startdate);

            dateFormat = new SimpleDateFormat("MM");
            month1 = dateFormat.format(date);


            dateFormat = new SimpleDateFormat("yyyy");
            year1 = dateFormat.format(date);


            dateFormat = new SimpleDateFormat("dd");
            day1 = dateFormat.format(date);


        } catch (ParseException e) {
            e.printStackTrace();
        }


        int day = Integer.parseInt(day1);
        int month = Integer.parseInt(month1);
        int year = Integer.parseInt(year1);
        Log.e("apiiiiiiii date", day + "-" + month + "-" + year);


        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), R.style.myTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateTextView.setText(simpleDateFormat.format(newDate.getTime()));
                Log.e(TAG, "date is " + simpleDateFormat.format(newDate.getTime()));
                getLeaderBoardVolley(simpleDateFormat.format(newDate.getTime()));
                // tv_birthdate.setText(simpleDateFormat.format(newDate.getTime()));
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.getDatePicker().setMinDate(date.getTime());
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class LLAdapter extends BaseAdapter {
        private ArrayList<LeaderListBean> l_list;
        private ViewHolder holder;

        public LLAdapter(ArrayList<LeaderListBean> l_list) {
            this.l_list = l_list;
        }

        @Override
        public int getCount() {
            return l_list.size();
        }

        @Override
        public Object getItem(int position) {
            return l_list.get(position);
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
                view = inflater.inflate(R.layout.list_item_4, null);
                holder.nameTxt = (TextView) view.findViewById(R.id.leaderName);
                holder.numberTxt = (TextView) view.findViewById(R.id.numberTxt);
                holder.stepsTxt = (TextView) view.findViewById(R.id.leaderSteps);
                holder.imageView = (ImageView) view.findViewById(R.id.leaderImg);
                holder.stepsTxt.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
                holder.stepsLabel = (TextView) view.findViewById(R.id.stepsLabel);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.nameTxt.setText(leader_list.get(position).getName());
            holder.numberTxt.setText((position + 1) + "");
            holder.numberTxt = AppTheme.changeBackgroundColor(holder.numberTxt);
            holder.stepsLabel.setText(stepsLabel);
            //Double d = Double.valueOf(leader_list.get(position).getSteps());
            //holder.stepsTxt.setText(String.format("%.2f", d));
            holder.stepsTxt.setText(leader_list.get(position).getSteps());
            final Transformation transformation = new RoundCornerTransformation(8, 5);
//            final Transformation transformation = new RoundedCornersTransformation(radius, margin);
            //          Picasso.with(activity).load(url).transform(transformation).into(imageView);
            if (leader_list.get(position).getPhotoUrl() != null && !leader_list.get(position).getPhotoUrl().equals("")) {
                Picasso.with(context).load(leader_list.get(position).getPhotoUrl().replaceAll(" ", "%20")).transform(transformation).into(holder.imageView);
            } else {
                Picasso.with(context).load(R.mipmap.square).transform(transformation).into(holder.imageView);
            }
            return view;
        }

        class ViewHolder {
            TextView nameTxt, numberTxt, stepsTxt, stepsLabel, dateTVv;
            ImageView imageView;
        }
    }


}
