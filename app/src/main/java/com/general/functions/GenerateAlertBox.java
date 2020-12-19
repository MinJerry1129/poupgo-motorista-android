package com.general.functions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.poupgo.driver.R;
import com.view.MTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class GenerateAlertBox {
    public AlertDialog alertDialog;
    Builder alertDialogBuilder;
    GeneralFunctions generalFunc;
    boolean isCancelable = false;
    HandleAlertBtnClick listener;
    Context mContext;

    public interface HandleAlertBtnClick {
        void handleBtnClick(int i);
    }

    private class AlertListAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> dataList;
        String keyToShow;
        Context mContext;

        public AlertListAdapter(ArrayList<HashMap<String, String>> dataList, Context mContext, String keyToShow) {
            this.dataList = dataList;
            this.mContext = mContext;
            this.keyToShow = keyToShow;
        }

        public int getCount() {
            return this.dataList.size();
        }

        public Object getItem(int i) {
            return this.dataList.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                LinearLayout linearLay = new LinearLayout(this.mContext);
                MTextView mTxtView = new MTextView(this.mContext);
                mTxtView.setTextSize(2, 16.0f);
                mTxtView.setTextColor(Color.parseColor("#1c1c1c"));
                mTxtView.mTypeface = Typeface.createFromAsset(this.mContext.getAssets(), this.mContext.getResources().getString(R.string.robotomediumFont));
                mTxtView.setLayoutParams(new LayoutParams(-1, -2));
                mTxtView.setMinHeight(Utils.dipToPixels(this.mContext, 40.0f));
                mTxtView.setPadding(Utils.dipToPixels(this.mContext, 25.0f), Utils.dipToPixels(this.mContext, 5.0f), Utils.dipToPixels(this.mContext, 25.0f), Utils.dipToPixels(this.mContext, 5.0f));
                mTxtView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                mTxtView.setGravity(8388627);
                linearLay.addView(mTxtView);
                view = linearLay;
            }
            LinearLayout linearLay2 = (LinearLayout) view;
            if (linearLay2.getChildCount() > 0 && (linearLay2.getChildAt(0) instanceof MTextView)) {
                String item = (String) ((HashMap) this.dataList.get(position)).get(this.keyToShow);
                ((MTextView) linearLay2.getChildAt(0)).setText(item != null ? item : "");
            }
            return view;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public GenerateAlertBox(Context mContext) {
        this.mContext = mContext;
        this.alertDialogBuilder = new Builder(mContext);
        this.generalFunc = new GeneralFunctions(this.mContext);
    }

    public GenerateAlertBox(Context mContext, boolean isCancelable) {
        this.mContext = mContext;
        this.isCancelable = isCancelable;
        this.alertDialogBuilder = new Builder(mContext);
        this.generalFunc = new GeneralFunctions(this.mContext);
    }

    public Builder getBuilder() {
        return this.alertDialogBuilder;
    }

    public void setContentMessage(String title_str, String message_str) {
        this.alertDialogBuilder.setTitle((CharSequence) title_str);
        this.alertDialogBuilder.setMessage((CharSequence) message_str);
    }

    public void setCancelable(boolean value) {
        this.isCancelable = value;
        this.alertDialogBuilder.setCancelable(value);
        if (this.alertDialog != null) {
            this.alertDialog.setCanceledOnTouchOutside(value);
            this.alertDialog.setCancelable(value);
        }
    }

    public void setNegativeBtn(String negative_btn_str) {
        this.alertDialogBuilder.setNegativeButton((CharSequence) negative_btn_str, new GenerateAlertBox$$Lambda$0(this));
    }

    final /* synthetic */ void lambda$setNegativeBtn$0$GenerateAlertBox(DialogInterface dialog, int id) {
        if (this.listener != null) {
            this.listener.handleBtnClick(0);
        }
    }

    public void setPositiveBtn(String positive_btn_str) {
        this.alertDialogBuilder.setPositiveButton((CharSequence) positive_btn_str, new GenerateAlertBox$$Lambda$1(this));
    }

    final /* synthetic */ void lambda$setPositiveBtn$1$GenerateAlertBox(DialogInterface dialog, int id) {
        if (this.listener != null) {
            this.listener.handleBtnClick(1);
        }
    }

    public void resetBtn() {
        this.alertDialogBuilder.setNegativeButton(null, null);
        this.alertDialogBuilder.setPositiveButton(null, null);
    }

    public void showAlertBox() {
        if (this.mContext instanceof Activity) {
            ((Activity) this.mContext).runOnUiThread(new Runnable() {
                public void run() {
                    GenerateAlertBox.this.showAlert();
                }
            });
        } else {
            showAlert();
        }
    }

    private void showAlert() {
        try {
            if (this.alertDialog == null) {
                this.alertDialog = this.alertDialogBuilder.create();
                this.alertDialog.setCancelable(this.isCancelable);
                if (this.generalFunc.isRTLmode()) {
                    this.generalFunc.forceRTLIfSupported(this.alertDialog);
                } else {
                    this.generalFunc.forceLTRIfSupported(this.alertDialog);
                }
            }
            this.alertDialog.show();
        } catch (Exception e) {
            Utils.printLog(StringUtils.SPACE, e.toString());
        }
    }

    public void createList(ArrayList<HashMap<String, String>> dataList, String keyToShow, OnItemClickListener onItemClickListener) {
        if (this.alertDialogBuilder != null && this.mContext != null) {
            ListView lstView = new ListView(this.mContext);
            lstView.setDivider(null);
            lstView.setPadding(0, Utils.dipToPixels(this.mContext, 10.0f), 0, 0);
            lstView.setAdapter(new AlertListAdapter(dataList, this.mContext, keyToShow));
            lstView.setOnItemClickListener(new GenerateAlertBox$$Lambda$2(onItemClickListener));
            this.alertDialogBuilder.setView(lstView);
        }
    }

    static final /* synthetic */ void lambda$createList$2$GenerateAlertBox(OnItemClickListener onItemClickListener, AdapterView parent, View view, int position, long id) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }

    public void closeAlertBox() {
        try {
            if (this.alertDialog != null) {
                this.alertDialog.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public void setBtnClickList(HandleAlertBtnClick listener) {
        this.listener = listener;
    }

    public void showSessionOutAlertBox() {
        try {
            if (this.alertDialog == null || !this.alertDialog.isShowing()) {
                this.alertDialog = this.alertDialogBuilder.create();
                this.alertDialog.setCancelable(false);
                if (this.generalFunc.isRTLmode()) {
                    this.generalFunc.forceRTLIfSupported(this.alertDialog);
                } else {
                    this.generalFunc.forceLTRIfSupported(this.alertDialog);
                }
                this.alertDialog.show();
            }
        } catch (Exception e) {
        }
    }
}
