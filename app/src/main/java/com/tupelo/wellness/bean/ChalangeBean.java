package com.tupelo.wellness.bean;

/**
 * Created by Abhishek Singh Arya on 21-09-2015.
 */
public class ChalangeBean {

    private String id="";
    private String title="";
    private String imageUrl="";
    private String chalangeWeight="";
    private String category="";
    private String status="";
    private String description="";
    private String startDate ="";
    private String endDate = "";

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getChalangeWeight() {
        return chalangeWeight;
    }

    public void setChalangeWeight(String chalangeWeight) {
        this.chalangeWeight = chalangeWeight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDesccription(String description) {
        this.description = description;
    }


}
