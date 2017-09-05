package com.tupelo.wellness.OnBoard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tupelo.wellness.activity.MymoSerialFragment;

import java.util.ArrayList;
import java.util.List;

public class OnboarderAdapter extends FragmentStatePagerAdapter {

    List<OnboarderPage> pages = new ArrayList<OnboarderPage>();

    public OnboarderAdapter(List<OnboarderPage> pages, FragmentManager fm) {
        super(fm);
        this.pages = pages;
    }

    @Override
    public Fragment getItem(int position) {

        int flag = pages.get(position).flag;

        if (flag == 0)
            return OnboarderFragment.newInstance(pages.get(position));

        else if (flag == 1)
            return new MymoSerialFragment();

        return null;

    }


    @Override
    public int getCount() {
        return pages.size();
    }


}
