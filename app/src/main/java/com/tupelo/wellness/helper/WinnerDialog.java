package com.tupelo.wellness.helper;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tupelo.wellness.R;
import com.tupelo.wellness.callbacks.WinnerDialogListener;
import com.tupelo.wellness.view.CircularImageView;
import com.tupelo.wellness.view.NewTextView;

/**
 * Created by owner on 13/10/16.
 */

public class WinnerDialog extends Dialog implements View.OnClickListener {

    private WinnerDialogListener listener;
    private com.tupelo.wellness.view.CircularImageView close, fb, tw;
    private NewTextView congo, dis;
    private String payload_text = "", payload_title = "";


    public WinnerDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.winner);
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(params);
        init();
    }

    public WinnerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WinnerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init() {
        close = (CircularImageView) findViewById(R.id.close);
        fb = (CircularImageView) findViewById(R.id.fb);
        tw = (CircularImageView) findViewById(R.id.tw);

        congo = (NewTextView) findViewById(R.id.congo);
        dis = (NewTextView) findViewById(R.id.dis);

        close.setOnClickListener(this);

        fb.setOnClickListener(this);

        tw.setOnClickListener(this);

    }

    public void setTextonHeader(String text) {
        payload_title = text;
        congo.setText(text);
    }

    public void setTextonContent(String text) {
        payload_text = text;
        dis.setText(Html.fromHtml(text));
        dis.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setWinnerDialogListener(WinnerDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                if (listener != null) {
                    listener.onclickoncancel();
                }
                break;

            case R.id.fb:
                if (listener != null) {
                    listener.onfacebookbuttonclick(payload_title, payload_text);
                }
                break;

            case R.id.tw:
                if (listener != null) {
                    listener.ontwitterbuttonclick(payload_title, payload_text);
                }
                break;
        }
    }
}
