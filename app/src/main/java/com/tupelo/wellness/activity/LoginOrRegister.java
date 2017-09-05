package com.tupelo.wellness.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pubnub.api.Pubnub;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.WalkingPrefrence;


/**
 * Created by owner on 22/6/16.
 */
public class LoginOrRegister extends AppCompatActivity {

    static String TAG = LoginOrRegister.class.getSimpleName();
    private final Pubnub pubnub = new Pubnub(Constants.PUBLISH_KEY,Constants.SUBSCRIBE_KEY);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerorlogin);

        TextView terms  = (TextView) this.findViewById(R.id.terms);
        terms.setText( Html.fromHtml("By signing up you agree to TupeloLife's <u><font color=#ffffff> <a href=\"http://www.tupelolife.com/privacy-policy\">Privacy Policy.</a></font></u>"));
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        String color = AppTheme.getInstance().colorPrimary;
        String statusBarColor = AppTheme.getInstance().colorPrimaryDark;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }







        //change action bar color
        //  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(statusBarColor)));*/



        String companyName = WalkingPrefrence.getInstance().companyName;
        TextView mImgView1 = (TextView) this.findViewById(R.id.client_logo);
        mImgView1.setText(companyName);
        RelativeLayout myLayout = (RelativeLayout) this.findViewById(R.id.backgroundColor);
        myLayout.setBackgroundColor(Color.parseColor(color));
        TextView program_name = (TextView) this.findViewById(R.id.program_name);
        program_name.setText(WalkingPrefrence.getInstance().programeName);

    }

    public void loginorregister(View view) {

        Button button =  (Button)view;

        setMyBackground(button,R.drawable.rounded_corner);

        button.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));

        switch (view.getTag().toString()) {

               case "login":
                Intent intent = new Intent(LoginOrRegister.this, SignInActivity.class);
                startActivity(intent);
                finish();

                    // sendNotification();
                   //this function was made just to test push, if the above code is uncommented user will recieve push
                break;
            case "register":
                Intent intent1 = new Intent(LoginOrRegister.this, SignUpActivity.class);
                startActivity(intent1);
                finish();

                break;
        }

    }

    private void setMyBackground(Button button, int rounded_corner) {

        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            }
        }
    }

   /* public void sendNotification() {

        Log.e(TAG, "sending notification");

        PnGcmMessage gcmMessage = new PnGcmMessage();
        JSONObject jso = new JSONObject();
        try {
            jso.put("GCMSays", "hi");
        } catch (JSONException e) {
        }
        gcmMessage.setData(jso);

        PnMessage message = new PnMessage(
                pubnub,
                WalkingPrefrence.getInstance().companyCode,
                callback,
                gcmMessage);
        try {
            message.publish();
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }


    public static Callback callback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            Log.e(TAG, "Success on Channel " + channel + " : " + message);
        }
        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.e(TAG, "Error On Channel " + channel + " : " + error);
        }
    };*/

}