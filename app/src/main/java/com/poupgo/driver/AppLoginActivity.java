package com.poupgo.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.functions.InternetConnection;
import com.utilities.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MTextView;
import com.view.indicator.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

public class AppLoginActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;

    MTextView introductondetailstext, languageText, currancyText, loginbtn, registerbtn;

    LinearLayout languagearea, currencyarea;

    LinearLayout languageCurrancyArea;

    GenerateAlertBox languageListAlertBox;

    String selected_language_code = "";

    ArrayList<HashMap<String, String>> languageDataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> currencyDataList = new ArrayList<>();

    String selected_currency = "";
    String selected_currency_symbol = "";

    GenerateAlertBox currencyListAlertBox;

    String type = "";

    AVLoadingIndicatorView loaderView;
    InternetConnection intCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_app_login);

        generalFunc = new GeneralFunctions(getActContext());
        intCheck = new InternetConnection(getActContext());
        generalFunc.getHasKey(getActContext());
        initview();
        setLabel();


    }

    private void initview() {

        introductondetailstext = (MTextView) findViewById(R.id.introductondetailstext);
        languageText = (MTextView) findViewById(R.id.languageText);
        currancyText = (MTextView) findViewById(R.id.currancyText);

        languagearea = (LinearLayout) findViewById(R.id.languagearea);
        currencyarea = (LinearLayout) findViewById(R.id.currencyarea);
        loginbtn = (MTextView) findViewById(R.id.loginbtn);
        registerbtn = (MTextView) findViewById(R.id.registerbtn);

        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        loaderView.setVisibility(View.GONE);

        languageCurrancyArea = (LinearLayout) findViewById(R.id.languageCurrancyArea);

        loginbtn.setOnClickListener(new setOnClickAct());
        registerbtn.setOnClickListener(new setOnClickAct());
        languagearea.setOnClickListener(new setOnClickAct());
        currencyarea.setOnClickListener(new setOnClickAct());


    }


    private void setLabel() {
        introductondetailstext.setText(generalFunc.retrieveLangLBl("", "LBL_HOME_DRIVER_INTRO_DETAILS"));
        loginbtn.setText(generalFunc.retrieveLangLBl("", "LBL_LOGIN"));
        registerbtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_REGISTER_TXT"));

        languageText.setText(generalFunc.retrieveValue(CommonUtilities.DEFAULT_LANGUAGE_VALUE));
        currancyText.setText(generalFunc.retrieveValue(CommonUtilities.DEFAULT_CURRENCY_VALUE));


    }




    public Context getActContext() {
        return AppLoginActivity.this;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void showLanguageList() {
        if (currencyListAlertBox.alertDialog == null || (currencyListAlertBox.alertDialog != null && !currencyListAlertBox.alertDialog.isShowing())) {
            languageListAlertBox.showAlertBox();
        }
    }

    public void showCurrencyList() {
        if (languageListAlertBox.alertDialog == null || (languageListAlertBox.alertDialog != null && !languageListAlertBox.alertDialog.isShowing())) {
            currencyListAlertBox.showAlertBox();
        }
    }

    public class setOnClickAct implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(AppLoginActivity.this);
            if (i == R.id.languagearea) {

                if (loaderView.getVisibility() == View.GONE) {
                    showLanguageList();
                }

            } else if (i == R.id.currencyarea) {
                if (loaderView.getVisibility() == View.GONE) {
                    showCurrencyList();
                }
            } else if (i == R.id.loginbtn) {
                if (loaderView.getVisibility() == View.GONE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "login");

                    new StartActProcess(getActContext()).startActWithData(AppLoignRegisterActivity.class, bundle);
                }


            } else if (i == R.id.registerbtn) {
                if (loaderView.getVisibility() == View.GONE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "register");
                    new StartActProcess(getActContext()).startActWithData(AppLoignRegisterActivity.class, bundle);
                }

            }
        }


    }

    public void changeLanguagedata(String langcode) {
        loaderView.setVisibility(View.VISIBLE);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "changelanguagelabel");
        parameters.put("vLang", langcode);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {
            Utils.printLog("responseString", "::" + responseString);
            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    generalFunc.storedata(CommonUtilities.languageLabelsKey, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                    generalFunc.storedata(CommonUtilities.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("eType", responseString));
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            loaderView.setVisibility(View.GONE);
                            generalFunc.restartApp(LauncherActivity.class);
                        }
                    }, 2000);


                } else {
                    loaderView.setVisibility(View.GONE);

                }
            } else {
                loaderView.setVisibility(View.GONE);

            }

        });
        exeWebServer.execute();
    }

}