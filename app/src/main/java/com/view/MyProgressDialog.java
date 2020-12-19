package com.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.poupgo.driver.R;
import com.general.functions.GeneralFunctions;

public class MyProgressDialog {
    boolean cancelable;
    Context mContext;
    Dialog my_progress_dialog;

    public MyProgressDialog(Context mContext, boolean cancelable, String message_str) {
        this.mContext = mContext;
        this.cancelable = cancelable;
        build();
        setMessage(message_str);
    }

    public void build() {
        this.my_progress_dialog = new Dialog(this.mContext, R.style.theme_my_progress_dialog);
        this.my_progress_dialog.setContentView(R.layout.my_progress_dilalog_design);
        Window window = this.my_progress_dialog.getWindow();
        window.setGravity(17);
        window.setLayout(-2, -2);
        this.my_progress_dialog.getWindow().setLayout(-2, -2);
        this.my_progress_dialog.setCanceledOnTouchOutside(false);
        this.my_progress_dialog.setCancelable(this.cancelable);
    }

    public void setMessage(String msg_str) {
        ((MTextView) this.my_progress_dialog.findViewById(R.id.msgTxt)).setText(msg_str);
    }

    public void show() {
        GeneralFunctions generalFunc = new GeneralFunctions(this.mContext);
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(this.my_progress_dialog);
        } else {
            generalFunc.forceLTRIfSupported(this.my_progress_dialog);
        }
        this.my_progress_dialog.show();
    }

    public void close() {
        try {
            if (this.my_progress_dialog != null) {
                this.my_progress_dialog.dismiss();
            }
        } catch (Exception e) {
        }
    }
}
