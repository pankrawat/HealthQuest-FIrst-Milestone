package com.tupelo.wellness.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tupelo.wellness.MainActivity;
import com.tupelo.wellness.R;

import java.util.List;

/**
 * Created by owner on 5/10/16.
 */
public class ChannelAdapter extends BaseAdapter implements View.OnClickListener {
    private static LayoutInflater inflater = null;
    public List<ChannelBean> data;
    RelativeLayout root;
    ImageView photourl;
    TextView channelname,date,createdn;
    private Activity activity;

    public ChannelAdapter(Activity activity, List<ChannelBean> data) {
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }

    @Override
    public int getViewTypeCount() {
        if (data != null)
            return data.size();
        else
            return super.getViewTypeCount();
    }


    public int getCount() {
        /*return data.size();*/
        if (data != null)
            return data.size();
        else
            return 0;
    }

    public Object getItem(int position) {
      /*  return data.get(position);*/
        if (data != null)
            return data.get(position);
        else
            return position;
    }


    public long getItemId(int position) {

        return data.indexOf(getItem(position));
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        // name,location,calldetail,premises,time,type,messagebut,callbut,statusbut
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.channelitem, null);
        root = (RelativeLayout) vi.findViewById(R.id.root);
        channelname = (TextView) vi.findViewById(R.id.channelname);
        createdn = (TextView) vi.findViewById(R.id.des);
        photourl = (ImageView) vi.findViewById(R.id.photourl);



        if ((data.get(i).getPhotourl() != null)) {
            if (URLUtil.isValidUrl(data.get(i).getPhotourl())) {
                Picasso.with(activity)
                        .load(data.get(i).getPhotourl().toString())
                        .into(photourl);

            }
        } else {
            Picasso.with(activity)
                    .load(R.mipmap.default_user_dark)
                    .into(photourl);
        }
        createdn.setText(data.get(i).getDescription());
        channelname.setText(data.get(i).getChannelname());

        root.setTag(i);
        root.setOnClickListener(this);
        return vi;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.root) {
            SharedPreferences pref = activity.getSharedPreferences("MyCorpProfile", Context.MODE_PRIVATE);
            String corpcode= pref.getString("corpcode", "");
            int pos = (int) v.getTag();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("channel", corpcode + data.get(pos).getId());
            intent.putExtra("channelname", data.get(pos).getChannelname());
            activity.startActivity(intent);
           // activity.finish();
        }

    }
}