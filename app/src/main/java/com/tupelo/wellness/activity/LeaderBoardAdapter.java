package com.tupelo.wellness.activity;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
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

/**
 * Created by owner on 29/6/16.
 */
public class LeaderBoardAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FragmentActivity context;
    ArrayList<LeaderBoardBean> list;

    public LeaderBoardAdapter(FragmentActivity context, ArrayList<LeaderBoardBean> list) {
        this.context = context;
        this.list = list;

        Log.e("LeaderBoardAdapter","I am here intilizing values");
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e("LeaderBoardAdapter","I am here Creating Holders " + viewType);

        switch (viewType) {
            case 0: {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_header_item, parent, false);
                return new ViewHolder0(itemView);

            }
            case 1: {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ViewHolder0(itemView);
            }
        }


        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myholder, int position) {

        Log.e("LeaderBoardAdapter", "Binding Views");
        ViewHolder0 holder = (ViewHolder0)myholder;

        holder.rank.setTextColor(Color.WHITE);
        holder.name.setText(list.get(position).getName());
        holder.rank.setText((position + 1) + "");
        holder.steps.setText(list.get(position).getSteps());

        if (list.get(position).getImageUrl() != null && !list.get(position).getImageUrl().equals("")) {
            Picasso.with(context).load(list.get(position).getImageUrl().replaceAll(" ","%20")).transform(new CircleTransform()).into(holder.profilepic);
        }
        else{
            Picasso.with(context).load(R.mipmap.default_user).transform(new CircleTransform()).into(holder.profilepic);
        }
    }
    @Override
    public int getItemViewType(int position) {

        if(position  ==0 || position == 1 || position == 2)
           return 0;
        else
            return 1;

    }


    @Override
    public int getItemCount() {
        return 0;
    }


    class ViewHolder0 extends RecyclerView.ViewHolder {

        TextView name,steps,rank;
        ImageView profilepic;


        public ViewHolder0(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.leaderName);
            rank = (TextView) view.findViewById(R.id.numberTxt);
            steps = (TextView) view.findViewById(R.id.leaderSteps);
            profilepic = (ImageView) view.findViewById(R.id.leaderImg);


        }


    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        public ViewHolder2(View itemView) {
            super(itemView);
        }
    }



}
