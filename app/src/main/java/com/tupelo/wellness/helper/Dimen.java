package com.tupelo.wellness.helper;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Abhishek Singh Arya on 05/12/2015.
 */
public class Dimen {
    private Activity activity;
    private static Dimen dimen;
    int BASE_WIDTH = 320;//320; //Base device width on which you are designing the layout.
    int BASE_HEIGHT = 480;//503; //Base device height on which you are designing the layout.

    int deviceWidth, deviceHeight, width, height;
    float size, density;

    public Dimen(Activity activity) {
        this.activity = activity;
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

        deviceWidth = metrics.widthPixels;
        deviceHeight = metrics.heightPixels;
        density = metrics.density;
    }

    public static Dimen getInstance(Activity activity) {
        if (dimen == null) {
            dimen = new Dimen(activity);
        }
        return dimen;
    }

    public int getWidth2(float widthInPX) {
        float w = (widthInPX / BASE_WIDTH) * 100;
        width = (int) ((w * deviceWidth) / 100);
        return width;
    }

    public int getWidth(float widthInPX) {
        float w = ((widthInPX/density) / BASE_WIDTH) * 100;
        width = (int) ((w * deviceWidth) / 100);
        return width;
    }

    public int getHeight(float heightInPX) {
        float h = ((heightInPX/density) / BASE_HEIGHT) * 100;
        height = (int) ((h * deviceHeight) / 100);
        return height;
    }

    public void setDynamicParams(View view){
        ViewGroup.LayoutParams layoutParams;
        int h, w;
        if (view != null) {
            layoutParams = view.getLayoutParams();
            if (layoutParams.height != -1 && layoutParams.height != -2) {
                h = dimen.getHeight(layoutParams.height);
            } else {
                h = layoutParams.height;
            }
            if (layoutParams.width != -1 && layoutParams.width != -2) {
                w = dimen.getWidth(layoutParams.width);
            } else {
                w = layoutParams.width;
            }
            if(view instanceof ImageView){
                layoutParams.height = w;
            } else {
                layoutParams.height = h;
            }
            layoutParams.width = w;
            view.setLayoutParams(layoutParams);
            view.requestLayout();
        }
    }

    public float getSize(float sizeInPX) {
        size = (sizeInPX * activity.getResources().getDisplayMetrics().density);
        //size = ((h * deviceHeight) / 100);
        //size = (sizeInPX * activity.getResources().getDisplayMetrics().density);
        Log.d("den", activity.getResources().getDisplayMetrics().density + "");
        return size;
    }

    public int getScreenWidth() {
        return deviceWidth;
    }

    public int getScreenHeight() {
        return deviceHeight;
    }
}
