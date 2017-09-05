/**
 * @author Omer Muhammed
 * Copyright 2014 (c) Jawbone. All rights reserved.
 *
 */
package com.tupelo.wellness.jawbone.oauth;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tupelo.wellness.R;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.jawbone.api.ApiManager;
import com.tupelo.wellness.jawbone.api.response.OauthAccessTokenResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Simple Web View for Oauth authorization, we display the web page so that
 * user can agree to, or cancel the permissions requested
 */
public class OauthWebViewActivity extends Activity {

    private static final String TAG = OauthWebViewActivity.class.getSimpleName();

    // AccessCode returned from server.
    private String accessCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.oauth_webview);

        Intent intent = this.getIntent();
        Uri uri = intent.getParcelableExtra(Constants.AUTH_URI);

        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(false);

        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String accessCodeFragment = "?code=";
                Log.e(TAG, "oauth response from server: " + url);

                int start = url.indexOf(accessCodeFragment);

                // We hijack the GET request to extract the OAuth parameters
                if(start > -1) {
                    // the GET request contains an authorization code
                    Log.d(TAG, "user accepted, url is :" + url);
                    accessCode = url.substring(start + accessCodeFragment.length(), url.length());
                    Log.d(TAG, "user accepted, code is :" + accessCode);

                    view.clearCache(true);
                    view.clearHistory();
                    view.destroy();
                    CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(OauthWebViewActivity.this);
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookie();

                    if (accessCode != null) {
                        //first clear older accessToken, if it exists..
                        ApiManager.getRequestInterceptor().clearAccessToken();

                        ApiManager.getRestApiInterface().getAccessToken(
                                Constants.CLIENT_ID,
                                Constants.CLIENT_SECRET_KEY,
                                accessCode,
                                accessTokenRequestListener);
                    }
                }
            }
        });
        webview.loadUrl(uri.toString());
    }
    private Callback accessTokenRequestListener = new Callback<OauthAccessTokenResponse>() {
        @Override
        public void success(OauthAccessTokenResponse result, Response response) {

            if (result.access_token != null) {
                setJawboneSignIn(OauthWebViewActivity.this, result.access_token, result.refresh_token);

                Intent intent = new Intent(OauthWebViewActivity.this, TabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.SERVICE, DbAdapter.TABLE_NAME_JAWBONE);
                startActivity(intent);
                finish();

                Log.d("jawbone", "accessToken:" + result.access_token);
            } else {
                Log.d("jawbone", "accessToken not returned by Oauth call, exiting...");
                finish();
            }
        }
        @Override
        public void failure(RetrofitError retrofitError) {
            Log.e("jawbone", "failed to get accessToken:" + retrofitError.getMessage());
            finish();
        }
    };
    public void setJawboneSignIn(Context mContext, String ACCESS_TOKEN, String REFRESH_TOKEN){
        DbAdapter dbAdapter = DbAdapter.getInstance(mContext);
        ContentValues contentValues = new ContentValues();
        try{
            contentValues.put(DbAdapter.COLUMN_JAWBONE_ACCESS_TOKEN, ACCESS_TOKEN);
            contentValues.put(DbAdapter.COLUMN_JAWBONE_REFRESH_TOKEN, REFRESH_TOKEN);

            dbAdapter.deleteAll(DbAdapter.TABLE_NAME_JAWBONE);
            dbAdapter.insertQuery(DbAdapter.TABLE_NAME_JAWBONE, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
