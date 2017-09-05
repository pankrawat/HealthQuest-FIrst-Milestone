package com.tupelo.wellness.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tupelo.wellness.DrawCanvasDot;
import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.GoogleFitVuFlipper;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.SharedPreference;

public class GoogleFitHelpActivity extends AppCompatActivity {
    public DrawCanvasDot dot;
    private Fonts fonts;
    LinearLayout dotLayout;
    int size = 4;
    private float initialX;
    Button btnNext, btnDone;
    ImageButton btnPlayStore;
    GoogleFitVuFlipper vuFlipper;
    int flipLayout[] = {R.layout.google_fit_tut_1, R.layout.google_fit_tut_2};
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_fit_help);
        fonts = new Fonts(this);
        vuFlipper = (GoogleFitVuFlipper) findViewById(R.id.vuFlipper);
        dotLayout = (LinearLayout) findViewById(R.id.dotLayout);

        sharedPreference = SharedPreference.getInstance(GoogleFitHelpActivity.this);

        setViewFlipper();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setViewFlipper() {
        vuFlipper.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = null;
        dot = new DrawCanvasDot(this, null);
        for (int i = 0; i < flipLayout.length; i++) {

            if (i == 0) {
                layout = (RelativeLayout) inflater.inflate(flipLayout[i], null);
                btnPlayStore = (ImageButton) layout.findViewById(R.id.btnPlayStore);
                btnPlayStore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Constants.GOOGLE_FIT_MARKET_LINK));
                        startActivity(intent);
                    }
                });

                btnNext = (Button) layout.findViewById(R.id.btnNext);
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vuFlipper.setInAnimation(AnimationUtils.loadAnimation(GoogleFitHelpActivity.this, R.anim.in_right));
                        vuFlipper.setOutAnimation(AnimationUtils.loadAnimation(GoogleFitHelpActivity.this, R.anim.out_left));
                        vuFlipper.showNext();
                    }
                });
            } else {
                layout = (RelativeLayout) inflater.inflate(flipLayout[i], null);

                btnDone = (Button) layout.findViewById(R.id.btnDone);
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Constants.GOOGLE_FIT_MARKET_LINK));
                        startActivity(intent);
                        finish();
                    }
                });
            }

            vuFlipper.addView(layout);
        }
        dotLayout.addView(dot);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (size > 1) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float finalX = motionEvent.getX();
                    if (initialX > finalX) {
                        if (vuFlipper.getDisplayedChild() == 1)
                            break;
                        vuFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_right));
                        vuFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_left));
                        vuFlipper.showNext();

                    } else {
                        if (vuFlipper.getDisplayedChild() == 0)
                            break;
                        vuFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_left));
                        vuFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_right));
                        vuFlipper.showPrevious();
                    }
                    break;
            }
        }
        return true;
    }
}
