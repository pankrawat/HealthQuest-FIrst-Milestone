package com.tupelo.wellness.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.tupelo.wellness.OnBoard.OnboarderActivity;
import com.tupelo.wellness.OnBoard.OnboarderPage;
import com.tupelo.wellness.R;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;

import java.util.ArrayList;
import java.util.List;

public class MymoOnBoard extends OnboarderActivity {

    List<OnboarderPage> onboarderPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboarderPages = new ArrayList<OnboarderPage>();

        // Create your first page
        OnboarderPage onboarderPage1 = new OnboarderPage("", "",R.mipmap.tut_01,0);
        OnboarderPage onboarderPage2 = new OnboarderPage("", "", R.mipmap.tut_02,0);
        OnboarderPage onboarderPage3 = new OnboarderPage("", "", R.mipmap.tut_03,1);
        OnboarderPage onboarderPage4 = new OnboarderPage("", "", R.mipmap.tut_04,0);

        // You can define title and description colors (by default white)
        onboarderPage1.setTitleColor(R.color.white);
        onboarderPage1.setDescriptionColor(R.color.white);

        // Don't forget to set background color for your page
        int resource = Color.parseColor(AppTheme.getInstance().colorPrimary)                                                                   ;
        onboarderPage1.setBackgroundColor(resource);
        onboarderPage2.setBackgroundColor(resource);
        onboarderPage4.setBackgroundColor(resource);
        onboarderPage3.setBackgroundColor(Color.parseColor("#ffffff"));

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);
        onboarderPages.add(onboarderPage4);
        // And pass your pages to 'setOnboardPagesReady' method
        setOnboardPagesReady(onboarderPages);

    }

    @Override
    public void onSkipButtonPressed() {
        // Optional: by default it skips onboarder to the end
        finish();
        super.onSkipButtonPressed();
        // Define your actions when the user press 'Skip' button
    }

    @Override
    public void onFinishButtonPressed() {
        // Define your actions when the user press 'Finish' button
/*

        Intent intent = new Intent(MymoOnBoard.this,MymoSerialNumber.class);
        startActivity(intent);
        finish();
*/



        Intent intent = new Intent(MymoOnBoard.this, TabActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_MYMO);
        startActivity(intent);


    }


}
