package com.tupelo.wellness.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tupelo.wellness.OnBoard.OnboarderActivity;
import com.tupelo.wellness.OnBoard.OnboarderPage;
import com.tupelo.wellness.helper.Constants;

import java.util.ArrayList;
import java.util.List;

public class GoogleFitOnBoard extends OnboarderActivity {

    List<OnboarderPage> onboarderPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboarderPages = new ArrayList<OnboarderPage>();

        // Create your first page
     /*   OnboarderPage onboarderPage1 = new OnboarderPage("Title 1", "Description 1",R.mipmap.bg_1,0);
        OnboarderPage onboarderPage2 = new OnboarderPage(R.string.accept, R.string.accept, R.mipmap.bg_2,0);

        // You can define title and description colors (by default white)
        onboarderPage1.setTitleColor(R.color.white);
        onboarderPage1.setDescriptionColor(R.color.white);

        // Don't forget to set background color for your page
        int resource = AppTheme.getAppThemeResourceId(Integer.parseInt(WalkingPrefrence.getInstance().colorInitCode));
        onboarderPage1.setBackgroundColor(resource);
        onboarderPage2.setBackgroundColor(resource);
        // so this shit wants integer m giving it 0x7f0b0021 m givbig it FF4081
        // so there is some conversion mechanism going on
        // have to understand the conversion
        //basically it needs resource, I have to give him a resource id

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);

        // And pass your pages to 'setOnboardPagesReady' method
        setOnboardPagesReady(onboarderPages);
*/
    }

    @Override
    public void onSkipButtonPressed() {
        // Optional: by default it skips onboarder to the end
        super.onSkipButtonPressed();
        finish();
        // Define your actions when the user press 'Skip' button
    }

    @Override
    public void onFinishButtonPressed() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.GOOGLE_FIT_MARKET_LINK));
        startActivity(intent);
        finish();

        // Define your actions when the user press 'Finish' button
    }


}
