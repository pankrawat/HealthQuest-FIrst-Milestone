package com.tupelo.wellness.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tupelo.wellness.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Ameen on 5/10/2016.
 */
public class ComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean> postList;
    String TAG = "ComplexRecyclerViewAdapter";
    Context mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG,"items is " + viewType);
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_1, parent, false);
            return new ViewHolder1(itemView);
        }
        else if(viewType ==2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_2, parent, false);
            return new ViewHolder2(itemView);
        }
        else if(viewType ==3) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_3, parent, false);
            return new ViewHolder3(itemView);
        }
        else if(viewType == 4)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_4, parent, false);
            return new ViewHolder4(itemView);
        }
        else if(viewType == 5)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_5, parent, false);
            return new ViewHolder5(itemView);
        }
        else if(viewType == 6)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_6, parent, false);
            return new ViewHolder6(itemView);

        }
        else if(viewType == 7)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_7, parent, false);
            return new ViewHolder7(itemView);
        }


      return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sHolder, int position) {


        int item = getItemViewType(position);
        NewsBean currentFeed = postList.get(position);
        Log.e(TAG,currentFeed.feed_title);
        if (item == 1)
        {
            ViewHolder1 holder = (ViewHolder1)sHolder;
            if (currentFeed.feed_caption.matches(Helper.Html_Pattern))
                Helper.setTextViewHTML(mContext, holder.feed_caption, currentFeed.feed_caption);
            else
                Helper.setTextViewWithoutHTML(mContext, holder.feed_caption, currentFeed.feed_caption);

            holder.feed_title.setText(currentFeed.feed_title);
            Log.e(TAG, "feed title is " + currentFeed.feed_title + "  time is " + currentFeed.feed_timestamp);
            holder.feed_timestamp.setText(new Helper().getTime(currentFeed.feed_timestamp));

        }
        else if(item ==2)
        {
            if (currentFeed.feed_caption.matches(Helper.Html_Pattern))
                Log.e(TAG, "Html pattern detected " + currentFeed.feed_caption);
            ViewHolder2 holder = (ViewHolder2) sHolder;
            holder.feed_timestamp.setText(new Helper().getTime(currentFeed.feed_timestamp));
            holder.feed_title.setText(currentFeed.feed_title);
            if (currentFeed.feed_caption.matches(Helper.Html_Pattern))
                Helper.setTextViewHTML(mContext, holder.feed_caption, currentFeed.feed_caption);
            else
                Helper.setTextViewWithoutHTML(mContext, holder.feed_caption, currentFeed.feed_caption);

            Picasso.with(mContext).load(currentFeed.feed_media_url.replaceAll(" ", "%20")).placeholder(R.drawable.placeholder).into(holder.feed_media_url);
        }
        else if(item == 3)
        {
            ViewHolder3 holder = (ViewHolder3)sHolder;
                holder.feed_timestamp.setText(new Helper().getTime(currentFeed.feed_timestamp));
            holder.feed_title.setText(currentFeed.feed_title);
            Picasso.with(mContext).load(currentFeed.feed_media_url.replaceAll(" ","%20")).placeholder(R.drawable.placeholder).into(holder.feed_media_url);

        }
        else if(item == 4)
        {
            ViewHolder4 holder = (ViewHolder4)sHolder;

            String preURl = "https://www.youtube.com/embed/";
            String id = new Helper().getYouTubeIdFromUrl(currentFeed.feed_media_url);

            boolean isVimeoUrl = false;
            try {
                URL aURL = new URL(currentFeed.feed_media_url);
                String auth = aURL.getAuthority();

                if(auth.contains("vimeo"))
                    isVimeoUrl = true;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            if(isVimeoUrl) {
                preURl = "https://player.vimeo.com/video/";
                id =  new Helper().getVimeoIdFromUrl(currentFeed.feed_media_url);
            }
            Log.e(TAG,"the full address is " + preURl +" "+id);
            String url = preURl+id;
            WebSettings settings;
            settings = holder.feed_media_url.getSettings();
            settings.setJavaScriptEnabled(true);
            holder.feed_media_url.loadUrl(url);

            Log.e(TAG, "media url is " + currentFeed.feed_media_url);
            holder.feed_timestamp.setText(new Helper().getTime(currentFeed.feed_timestamp));
            holder.feed_title.setText(currentFeed.feed_title);
        }
        else if(item == 5)
        {
            ViewHolder5 holder = (ViewHolder5)sHolder;

            String preURl = "https://www.youtube.com/embed/";
            String id = new Helper().getYouTubeIdFromUrl(currentFeed.feed_media_url);

            boolean isVimeoUrl = false;
            try {
                URL aURL = new URL(currentFeed.feed_media_url);
                String auth = aURL.getAuthority();

                if(auth.contains("vimeo"))
                    isVimeoUrl = true;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            if(isVimeoUrl) {
                preURl = "https://player.vimeo.com/video/";
                id =  new Helper().getVimeoIdFromUrl(currentFeed.feed_media_url);
            }
            Log.e(TAG,"the full address is " + preURl +" "+id);
            String url = preURl+id;
            WebSettings settings;
            settings = holder.feed_media_url.getSettings();
            if (currentFeed.feed_caption.matches(Helper.Html_Pattern))
                Helper.setTextViewHTML(mContext, holder.feed_caption, currentFeed.feed_caption);
            else
                Helper.setTextViewWithoutHTML(mContext, holder.feed_caption, currentFeed.feed_caption);
            if (currentFeed.feed_caption.matches(Helper.Html_Pattern))
                Log.e(TAG, "Html pattern detected " + currentFeed.feed_caption);
            settings.setJavaScriptEnabled(true);
            holder.feed_media_url.loadUrl(url);

            Log.e(TAG, "media url is " + currentFeed.feed_media_url);
            holder.feed_timestamp.setText(new Helper().getTime(currentFeed.feed_timestamp));
            holder.feed_title.setText(currentFeed.feed_title);

            holder.feed_caption.setText(currentFeed.feed_caption);
        }
        else if(item == 6)
        {
            ViewHolder6 holder = (ViewHolder6) sHolder;
            if (currentFeed.feed_caption.matches(Helper.Html_Pattern))
                Helper.setTextViewHTML(mContext, holder.feed_caption, currentFeed.feed_caption);
            else
                Helper.setTextViewWithoutHTML(mContext, holder.feed_caption, currentFeed.feed_caption);

            holder.feed_timestamp.setText(new Helper().getTime(currentFeed.feed_timestamp));


            holder.feed_user.setText(currentFeed.feed_user);
            Picasso.with(mContext).load(currentFeed.feed_media_url.replaceAll(" ", "%20")).placeholder(R.drawable.placeholder).into(holder.feed_media_url);

        }
        else if(item == 7)
        {
            ViewHolder7 holder = (ViewHolder7) sHolder;
            Log.e(TAG, "binding view holder 7" + " the feed title is " + currentFeed.feed_title + " timestamp is " + currentFeed.feed_timestamp);

            holder.feed_user.setText(currentFeed.feed_user);
            if (currentFeed.feed_caption.matches(Helper.Html_Pattern))
                Helper.setTextViewHTML(mContext, holder.feed_caption, currentFeed.feed_caption);
            else
                Helper.setTextViewWithoutHTML(mContext, holder.feed_caption, currentFeed.feed_caption);

            holder.feed_timestamp.setText(new Helper().getTime(currentFeed.feed_timestamp));
        }

    }

    


    @Override
    public int getItemViewType(int position) {


        return Integer.parseInt(postList.get(position).feed_type);
    }

    public ComplexRecyclerViewAdapter(List<NewsBean> postList, Context context) {
        this.postList = postList;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView feed_user,feed_timestamp,feed_caption,feed_title,feedid;
        public ViewHolder1(View itemView) {
            super(itemView);

         //   feed_user = (TextView)itemView.findViewById(R.id.feed_user);
            feed_timestamp = (TextView) itemView.findViewById(R.id.feed_timestamp);
            feed_title = (TextView) itemView.findViewById(R.id.feed_title);
            feed_caption = (TextView) itemView.findViewById(R.id.feed_caption);
        }
    }
    class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView feed_user,feed_timestamp,feed_caption,feed_title;
        ImageView feed_media_url;
        public ViewHolder2(View itemView) {
            super(itemView);
            feed_media_url = (ImageView) itemView.findViewById(R.id.feed_media_url);
         //   feed_user = (TextView)itemView.findViewById(R.id.feed_user);
            feed_timestamp = (TextView) itemView.findViewById(R.id.feed_timestamp);
            feed_title = (TextView) itemView.findViewById(R.id.feed_title);
            feed_caption = (TextView) itemView.findViewById(R.id.feed_caption);
        }
    }
    class ViewHolder3 extends RecyclerView.ViewHolder{
        TextView feed_user,feed_timestamp,feed_caption,feed_title,feedid;
        ImageView feed_media_url;
        public ViewHolder3(View itemView) {
            super(itemView);

            feed_media_url = (ImageView) itemView.findViewById(R.id.feed_media_url);
          //  feed_user = (TextView)itemView.findViewById(R.id.feed_user);
            feed_timestamp = (TextView) itemView.findViewById(R.id.feed_timestamp);
            feed_title = (TextView) itemView.findViewById(R.id.feed_title);
        }
    }
    class ViewHolder4 extends RecyclerView.ViewHolder{
        TextView feed_user,feed_timestamp,feed_caption,feed_title,feedid;
        WebView feed_media_url;
        public ViewHolder4(View itemView) {
            super(itemView);
            feed_media_url = (WebView) itemView.findViewById(R.id.feed_media_url);
            feed_timestamp = (TextView) itemView.findViewById(R.id.feed_timestamp);
            feed_title = (TextView) itemView.findViewById(R.id.feed_title);
        }
    }
    class ViewHolder5 extends RecyclerView.ViewHolder{
        TextView feed_user,feed_timestamp,feed_caption,feed_title,feedid;
        WebView feed_media_url;
        public ViewHolder5(View itemView) {
            super(itemView);

           // feed_user = (TextView)itemView.findViewById(R.id.feed_user);
            feed_media_url = (WebView) itemView.findViewById(R.id.feed_media_url);
            feed_timestamp = (TextView) itemView.findViewById(R.id.feed_timestamp);
            feed_title = (TextView) itemView.findViewById(R.id.feed_title);
            feed_caption = (TextView) itemView.findViewById(R.id.feed_caption);
        }
    }
    class ViewHolder6 extends RecyclerView.ViewHolder{
        TextView feed_user,feed_timestamp,feed_caption,feed_title,feedid;
        ImageView feed_media_url;
        public ViewHolder6(View itemView) {
            super(itemView);

            feed_media_url = (ImageView) itemView.findViewById(R.id.feed_media_url);
            feed_user = (TextView)itemView.findViewById(R.id.feed_user);
            feed_timestamp = (TextView) itemView.findViewById(R.id.feed_timestamp);
            feed_caption = (TextView) itemView.findViewById(R.id.feed_caption);
        }
    }
    class ViewHolder7 extends RecyclerView.ViewHolder{
        TextView feed_user,feed_timestamp,feed_caption,feedid;
        public ViewHolder7(View itemView) {
            super(itemView);

            feed_user = (TextView)itemView.findViewById(R.id.feed_user);
            feed_timestamp = (TextView) itemView.findViewById(R.id.feed_timestamp);
            //feed_title = (TextView) itemView.findViewById(R.id.feed_title);
            feed_caption = (TextView) itemView.findViewById(R.id.feed_caption);
        }
    }



}