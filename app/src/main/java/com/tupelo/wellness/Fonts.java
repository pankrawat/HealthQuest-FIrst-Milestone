package com.tupelo.wellness;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Abhishek Singh Arya on 18-09-2015.
 */
public class Fonts {
    Context context;
    public Fonts(Context context) {
        this.context = context;
    }

    public Typeface getTypefaceNormal() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/segoeui.ttf");
    }
    public Typeface getTypefaceLite() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/SEGOEUIL_0.ttf");
    }
    public Typeface getTypefaceBold() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/seguisb.ttf");
    }
}
