package com.tupelo.wellness.bean;

/**
 * Created by Abhishek Singh Arya on 21-09-2015.
 */
public class LeaderBoardBean {
    private String name = "";
    private String imageUrl = "";
    private String steps = "";
    private String rank = "";


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }


    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
