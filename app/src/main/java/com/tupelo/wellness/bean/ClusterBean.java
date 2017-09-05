package com.tupelo.wellness.bean;

import java.util.ArrayList;

/**
 * Created by Abhishek Singh Arya on 21-09-2015.
 */
public class ClusterBean {

    private String clusterid = "";
    private String clustername = "";
    private ArrayList<GroupBean> group = null;

    public String getClusterid() {
        return clusterid;
    }

    public void setClusterid(String clusterid) {
        this.clusterid = clusterid;
    }

    public String getClustername() {
        return clustername;
    }

    public void setClustername(String clustername) {
        this.clustername = clustername;
    }

    public ArrayList<GroupBean> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<GroupBean> group) {
        this.group = group;
    }

}
