package com.tupelo.wellness.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by owner on 23/6/16.
 */
public class WalkingPrefrence {


    public static WalkingPrefrence walkPrefrence;
    public String deviceSupport;
    public String corpId;
    //device support key tells us which device will be used by the ownder or app example:
    //fit bit,mymo,google fit, jaw bone et. al.
    public String companyName;
    public String groupings;
    public String programeName;
    public String grp_alias;
    public String groupMode;
    public String clusterMode;
    public String clustr_alias;
    public String roleId;
    public String colorInitCode;
    public String companyCode;
    public String prgrm_edate;
    public String prgrm_sdate;

    public static WalkingPrefrence getInstance() {
        if (walkPrefrence == null) {
            walkPrefrence = new WalkingPrefrence();
        }
        return walkPrefrence;
    }

    public static String bitmapToString(Bitmap bitmap) {
        String string = "";
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public static Bitmap stringToBitmap(String bitString) {
        byte[] decodedString = Base64.decode(bitString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void setWalkingPrefrence(String prgrm_edate,String prgrm_sdate,String companyName, String deviceSupport, String grouings, String programeName, String corpid, String grp_alias, String clustr_alias, String colorInitCode, String companyCode, String clusterMode, String groupMode, String roleId) {
        this.prgrm_sdate=prgrm_sdate;
        this.prgrm_edate=prgrm_edate;
        this.companyName = companyName;
        this.deviceSupport = deviceSupport;
        this.groupings = grouings;
        this.programeName = programeName;
        this.companyCode = companyCode;
        this.corpId = corpid;
        Log.e("Walking Prefs", "the corp id is" + this.corpId);
        this.clustr_alias = clustr_alias;
        this.grp_alias = grp_alias;
        this.colorInitCode = colorInitCode;
        this.clusterMode = clusterMode;
        this.groupMode = groupMode;
        this.roleId = roleId;
    }


}