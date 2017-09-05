package com.tupelo.wellness.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.WalkingPrefrence;

public class WebViewActivity extends AppCompatActivity {
    Fonts fonts;
    ProgressDialog progressBar;
    String TAG = WebViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = (WebView) findViewById(R.id.webView);

        progressBar = ProgressDialog.show(WebViewActivity.this, "", "loading...");

        String url = getIntent().getExtras().getString("url");

        Log.e(TAG, "url is " + url);



        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.tabanim_toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        setSupportActionBar(toolbar);

/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
*/





        TextView tv_appname = (TextView) findViewById(R.id.textAppName);
        tv_appname.setText(WalkingPrefrence.getInstance().programeName);
        int textSize = tv_appname.getLineCount() == 1 ? R.dimen.sp18 : R.dimen.sp16;
        float myTextSize = getResources().getDimension(textSize) / getResources().getDisplayMetrics().density;
        Log.e(TAG, "the text size is " + textSize + " my text size is " + myTextSize);
        tv_appname.setTextSize(TypedValue.COMPLEX_UNIT_SP, myTextSize);


        String statusBarColor = AppTheme.getInstance().colorPrimaryDark;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }

        this.setProgressBarVisibility(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {


            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
        webView.loadUrl(url);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        Log.e(TAG, "the item id is " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

}