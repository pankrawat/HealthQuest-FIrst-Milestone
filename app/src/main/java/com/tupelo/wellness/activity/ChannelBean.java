package com.tupelo.wellness.activity;

import java.io.Serializable;

/**
 * Created by owner on 5/10/16.
 */
public class ChannelBean implements Serializable{
    public String  id;
    public String  channelname;
    public String  channel_created;
    public String  photourl;
    public String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public String getChannel_created() {
        return channel_created;
    }

    public void setChannel_created(String channel_created) {
        this.channel_created = channel_created;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}
