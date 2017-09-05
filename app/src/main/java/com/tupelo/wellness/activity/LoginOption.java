package com.tupelo.wellness.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tupelo.wellness.Fonts;
import com.tupelo.wellness.R;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.SharedPreference;

public class LoginOption extends AppCompatActivity implements View.OnClickListener {

    Fonts fonts;
    Button signup, login;
    Context context;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(AppTheme.getInstance().colorPrimaryDark));
        }



        fonts = new Fonts(this);
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        context = this;
        sharedPreference = SharedPreference.getInstance(LoginOption.this);

        //showEULA();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.signup:
                intent = new Intent(LoginOption.this, SignUpActivity.class);
                break;
            case R.id.login:
                intent = new Intent(LoginOption.this, SignInActivity.class);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showEULA(){
        boolean isAccepted = sharedPreference.getBoolean(Constants.EULA_KEY, false);
        if (isAccepted == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setMessage(Html.fromHtml(Constants.EULA))
                    .setCancelable(false)
                    .setPositiveButton(R.string.accept,
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialogInterface, int i) {
                                    // Mark this version as read.

                                    sharedPreference.putBoolean(Constants.EULA_KEY, true);

                                    // Close dialog
                                    dialogInterface.dismiss();

                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Close the activity as they have declined
                                    // the EULA
                                    finish();
                                }

                            });
            AlertDialog welcomeAlert = builder.create();
            welcomeAlert.show();
            // Make the textview clickable. Must be called after show()
            ((TextView)welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
