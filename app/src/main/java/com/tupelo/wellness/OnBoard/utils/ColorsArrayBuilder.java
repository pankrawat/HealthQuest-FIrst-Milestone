package com.tupelo.wellness.OnBoard.utils;

import android.content.Context;

import com.tupelo.wellness.OnBoard.OnboarderPage;

import java.util.ArrayList;
import java.util.List;

public class ColorsArrayBuilder {

    public static Integer[] getPageBackgroundColors(Context context, List<OnboarderPage> pages) {
        List<Integer> colorsList = new ArrayList<>();
        for (OnboarderPage page : pages) {
            colorsList.add( page.getBackgroundColor());
        }
        return colorsList.toArray(new Integer[pages.size()]);
    }

}
