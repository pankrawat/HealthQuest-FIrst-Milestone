package com.tupelo.wellness.helper;

/**
 * Created by owner on 1/7/16.
 */
public class NewsBean {

    public String feed_media_url;
    public String feed_user;
    public String feed_timestamp;
    public String feed_caption;
    public String feed_type;
    public String feed_title;
    public String type;


    public NewsBean(String feed_media_url, String feed_user, String feed_timestamp, String feed_caption, String feed_type, String feed_title, String type) {
        this.feed_caption = feed_caption;
        this.feed_type = feed_type;
        this.feed_media_url = feed_media_url;
        this.feed_user = feed_user;
        this.feed_title = feed_title;
        this.feed_timestamp = feed_timestamp;
        this.type = type;
    }


}
