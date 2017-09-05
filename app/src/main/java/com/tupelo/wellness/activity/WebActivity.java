package com.tupelo.wellness.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.WalkingPrefrence;

public class WebActivity extends AppCompatActivity {
    String TAG = WebActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = (WebView) findViewById(R.id.webView);

        TextView tv_appname = (TextView) findViewById(R.id.textAppName);
        tv_appname.setTextColor(Color.WHITE);

        tv_appname.setText(WalkingPrefrence.getInstance().programeName);
        int textSize = tv_appname.getLineCount() == 1?R.dimen.sp18:R.dimen.sp16;
        float myTextSize = getResources().getDimension(textSize)/getResources().getDisplayMetrics().density;
        Log.e(TAG,"the text size is " + textSize + " my text size is " + myTextSize);
        tv_appname.setTextSize(TypedValue.COMPLEX_UNIT_SP,myTextSize);
        String statusBarColor = AppTheme.getInstance().colorPrimaryDark;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(Constants.HELP_LINK);

    }
}