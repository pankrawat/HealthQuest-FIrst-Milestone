package com.tupelo.wellness.helper;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.tupelo.wellness.R;
import com.tupelo.wellness.callbacks.NotificationListener;
import com.tupelo.wellness.view.CircularImageView;
import com.tupelo.wellness.view.NewTextView;

/**
 * Created by owner on 13/10/16.
 */

public class NotificationDialog extends Dialog implements View.OnClickListener {

    private NotificationListener listener;
    private com.tupelo.wellness.view.CircularImageView logo;
    private NewTextView congo, dis;
    private LinearLayout ok;


    public NotificationDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.announcement);
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(params);
        init();
    }

    public NotificationDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected NotificationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init() {
        logo = (CircularImageView) findViewById(R.id.logo);
        congo = (NewTextView) findViewById(R.id.congo);
        dis = (NewTextView) findViewById(R.id.dis);
        ok = (LinearLayout) findViewById(R.id.ok);

        ok.setOnClickListener(this);
    }

    public void setTextonHeader(String text) {
        congo.setText(text);
    }

    public void setTextonContent(String text) {
        dis.setText(Html.fromHtml(text));
        dis.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setNotificationListener(NotificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok) {
            if (listener != null) {
                listener.onPositiveButtonClick();
            }
        }
    }
}
