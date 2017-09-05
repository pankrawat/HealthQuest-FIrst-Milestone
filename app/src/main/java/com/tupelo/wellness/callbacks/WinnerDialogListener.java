package com.tupelo.wellness.callbacks;

/**
 * Created by owner on 13/10/16.
 */

public interface WinnerDialogListener {
    public void onclickoncancel();
    public void onfacebookbuttonclick(String payload_title, String payload_text);
    public void ontwitterbuttonclick(String payload_title, String payload_text);
}
