package com.tupelo.wellness.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tupelo.wellness.CircleTransform;
import com.tupelo.wellness.R;
import com.tupelo.wellness.bean.LeaderBoardBean;

import java.util.ArrayList;
import java.util.List;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.ViewHolder> {

    String TAG = LeaderAdapter.class.getSimpleName();
    List<LeaderBoardBean> llbb = new ArrayList<>();
    Context context;


    public LeaderAdapter(Context context, List<LeaderBoardBean> llbb) {

        this.llbb = llbb;
        this.context = context;
        Log.e(TAG, "size is " + llbb.size());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "creating " + " view type is " + viewType);

        int layout;
        View itemView = null;
        if (viewType == 1)
            layout = R.layout.leaderboard_first_rank;
        else if (viewType == 2)
            layout = R.layout.leaderboard_second_third_rank;
        else
            layout = R.layout.list_item_3;

        itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e(TAG, "binding " + position);


        int myPostion = position;
        LeaderBoardBean lbb = llbb.get(myPostion);


        int viewType = getItemViewType(myPostion);
        if (viewType == 1 || viewType == 2)
            holder.eNumber = AppTheme.changeBackgroundColor(holder.eNumber);


        holder.eName.setText(lbb.getName());
        holder.eStep.setText(lbb.getSteps());
        holder.eNumber.setText(lbb.getRank());
        holder.eStep.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        if(!lbb.getSteps().isEmpty())
              holder.stepsText.setVisibility(View.VISIBLE);


        String url = lbb.getImageUrl();
        if (url != null && !url.isEmpty()) {
            Log.e("foooooo", "the url of this image is" + url);
            Picasso.with(context).load(url.replaceAll(" ", "%20")).placeholder(R.mipmap.default_user_dark).transform(new CircleTransform()).into(holder.eImage);

        } else {
            Picasso.with(context).load(R.mipmap.default_user_dark).transform(new CircleTransform()).into(holder.eImage);
        }

    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "returning size " + llbb.size());
        return llbb.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0 || position == 2)
            return 2;
        if (position == 1)
            return 1;
        else
            return 3;

    }


    class ViewHolder extends RecyclerView.ViewHolder {


        TextView eName, eStep, eNumber,stepsText;
        ImageView eImage;

        public ViewHolder(View itemView) {
            super(itemView);

            eName = (TextView) itemView.findViewById(R.id.leaderName);
            eNumber = (TextView) itemView.findViewById(R.id.numberTxt);
            eStep = (TextView) itemView.findViewById(R.id.leaderSteps);
            eImage = (ImageView) itemView.findViewById(R.id.leaderImg);
            stepsText = (TextView) itemView.findViewById(R.id.first_steps_text);
        }
    }


}