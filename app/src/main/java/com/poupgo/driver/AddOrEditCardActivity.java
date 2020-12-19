package com.poupgo.driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.MyApp;
import com.general.functions.GeneralFunctions;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MTextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class AddOrEditCardActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;
    private WebView webView;
    private ProgressDialog mProgress;
    private ImageView backImgView;
    private MTextView titleTxt;
    LinearLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_card);
        generalFunc = new GeneralFunctions(getActContext());
        backImgView = findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        webView = findViewById(R.id.webView1);

        if (getIntent().getExtras().getString("PAGE_MODE").equals("ADD_CARD")) {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
        } else {
            titleTxt.setText(generalFunc.retrieveLangLBl("Change Card", "LBL_CHANGE_CARD"));
        }

        backImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CreateCustomer();
    }

    public void CreateCustomer() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GenerateCustomer");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", CommonUtilities.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response--", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        String url = generalFunc.getJsonValue("message", responseString);
                        mProgress = new ProgressDialog(AddOrEditCardActivity.this);
                        mProgress.setMessage("Loading... ");
                        mProgress.setCancelable(false);

                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.loadUrl(url);
                        webView.setWebViewClient(new myWebClient());
                        webView.setWebChromeClient(new WebChromeClient());
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


    public class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mProgress != null && !isFinishing()) {
                mProgress.show();
            }

            if (url.contains("success=1")) {
                if (mProgress != null && mProgress.isShowing()) {
                    mProgress.dismiss();
                }
                autoLogin();
            } else if (url.contains("success=0")) {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    finish();
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.dismiss();
                    }
                });

                String message = generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS");
                if(url.contains("&msg=")) {
                    String[] urlParams = url.split("&msg=");
                    try {
                        message = URLDecoder.decode(urlParams[1], "UTF-8");
                    }
                    catch (UnsupportedEncodingException e) { }
                }

                generateAlert.setContentMessage("", message);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TRIP_TXT"));
                generateAlert.showAlertBox();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
//            Toast.makeText(AddOrEditCardActivity.this, "onPageFinished", Toast.LENGTH_SHORT).show();
        }
    }

    public void autoLogin() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDetail");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("AppVersion", Utils.getAppVersion());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                if (isFinishing()) {
                    return;
                }

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);


                    final String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);


                    if (message.equals("SESSION_OUT")) {
                        generalFunc.notifySessionTimeOut();
                        Utils.runGC();
                        return;
                    }

                    if (isDataAvail == true) {

                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, message);

                        generalFunc.storedata(Utils.SESSION_ID_KEY, generalFunc.getJsonValue("tSessionId", message));
                        generalFunc.storedata(Utils.DEVICE_SESSION_ID_KEY, generalFunc.getJsonValue("tDeviceSessionId", message));


                        Intent intent = new Intent(AddOrEditCardActivity.this, CardPaymentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        if (!generalFunc.getJsonValue("isAppUpdate", responseString).trim().equals("")
                                && generalFunc.getJsonValue("isAppUpdate", responseString).equals("true")) {

                        } else {

                            if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_COMPANY") ||
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_ACC_DELETE_TXT") ||
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_DRIVER")) {

                                GenerateAlertBox alertBox = generalFunc.notifyRestartApp("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                                alertBox.setCancelable(false);
                                alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                    @Override
                                    public void handleBtnClick(int btn_id) {

                                        if (btn_id == 1) {
//                                            generalFunc.logoutFromDevice(getActContext(),generalFunc,"Launcher");
                                            MyApp.getInstance().removePubSub();
                                            generalFunc.logOutUser();
                                            generalFunc.restartApp(LauncherActivity.class);
                                        }
                                    }
                                });
                                return;
                            }

                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        }
                    }
                } else {
                }
            }
        });
        exeWebServer.execute();
    }


    public Context getActContext() {
        return AddOrEditCardActivity.this;
    }

}
