package com.tupelo.wellness.bean;

import java.util.ArrayList;

/**
 * Created by Abhishek Singh Arya on 21-09-2015.
 */
public class CompanyListBean {

    public ArrayList<ClusterBean> getClustergroup() {
        return clustergroup;
    }

    public void setClustergroup(ArrayList<ClusterBean> clustergroup) {
        this.clustergroup = clustergroup;
    }

    private ArrayList<ClusterBean> clustergroup = null;


}
