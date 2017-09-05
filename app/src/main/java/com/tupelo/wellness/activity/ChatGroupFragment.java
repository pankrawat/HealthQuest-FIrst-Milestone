package com.tupelo.wellness.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tupelo.wellness.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by owner on 5/10/16.
 */
public class ChatGroupFragment extends Fragment {
    public ListView channellistl;
    ChannelBean channeldata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chatgroupfrag, container, false);
        JSONObject getobj;
       // Toast.makeText(getActivity(), "cvvfgdfg", Toast.LENGTH_SHORT).show();
        ArrayList<ChannelBean> channellist = new ArrayList<ChannelBean>();
        SharedPreferences pref = getActivity().getSharedPreferences("MyCorpProfile", Context.MODE_PRIVATE);
        String groupChatList = pref.getString("channels", "");

        try {
            JSONObject channelListObj = new JSONObject(groupChatList);
            JSONArray channelList = channelListObj.getJSONArray("channelobj");

            for (int i = 0; i < channelList.length(); i++) {
                channeldata = new ChannelBean();
                getobj = channelList.getJSONObject(i);
                channeldata.setId(getobj.getString("id"));
                channeldata.setChannelname(getobj.getString("channelname"));
                channeldata.setDescription(getobj.getString("description"));

                if (!getobj.getString("photourl").isEmpty() || getobj.getString("photourl") != "" || !getobj.getString("photourl").equals("")) {
                    channeldata.setPhotourl(getobj.getString("photourl"));
                } else {
                    channeldata.setPhotourl("https://blogs.uoregon.edu/speddoc/files/2015/07/Blank-person-photo-2j572ea.png");
                }

                channellist.add(channeldata);
            }
            channellistl = (ListView) v.findViewById(R.id.channellistl);
            ChannelAdapter adp = new ChannelAdapter(getActivity(), channellist);
            channellistl.setAdapter(adp);

          //  Toast.makeText(getActivity(), "cvvfgdfg", Toast.LENGTH_SHORT).show();
          /*  if (Integer.parseInt(channelListObj.getString("channelcount")) > 0) {
                for (int i = 0; i < Integer.parseInt(channelListObj.getString("channelcount")); i++) {

                }
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return v;
    }
}
