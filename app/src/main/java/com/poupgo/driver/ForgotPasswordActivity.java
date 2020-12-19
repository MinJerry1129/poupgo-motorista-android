package com.poupgo.driver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.functions.InternetConnection;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity implements GenerateAlertBox.HandleAlertBtnClick{


    MaterialEditText emailBox;
    GeneralFunctions generalFunc;
    public String userProfileJson = "";
    String required_str = "";
    String error_email_str = "";
    MTextView forgotpasswordHint, forgotpasswordNote, backbtn;
    MButton btn_type2;
    int submitBtnId;
    InternetConnection intCheck;
    public MTextView titleTxt;
    ImageView backImgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
        setLabel();

    }

    public Context getActContext() {
        return ForgotPasswordActivity.this;
    }

    private void initView() {
        generalFunc = new GeneralFunctions(getActContext());
        intCheck = new InternetConnection(this);
        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        forgotpasswordHint = (MTextView) findViewById(R.id.forgotpasswordHint);
        forgotpasswordNote = (MTextView) findViewById(R.id.forgotpasswordNote);
        backbtn = (MTextView) findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new setOnClickList());
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());

        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);


    }

    private void setLabel() {

        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
        titleTxt.setText(generalFunc.retrieveLangLBl("","LBL_FORGET_PASS_TXT"));
        forgotpasswordNote.setText(generalFunc.retrieveLangLBl("","LBL_FORGET_PASS_NOTE"));
        backbtn.setText("<<"+" "+generalFunc.retrieveLangLBl("","LBL_BACK"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ARRIVED_DIALOG_BTN_CONTINUE_TXT"));

        emailBox.getLabelFocusAnimator().start();


    }

    @Override
    public void handleBtnClick(int btn_id) {
        Utils.hideKeyboard(getActContext());
        if (btn_id == 1) {
            onBackPressed();
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i=view.getId();

             if (i == submitBtnId) {

                 if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

                     generalFunc.showMessage(emailBox, generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));
                 } else {
                     checkValues();

                 }


            }
            else if(i==backImgView.getId())
             {
                 onBackPressed();

             }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void checkValues() {


        boolean emailEntered = Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                : Utils.setErrorFields(emailBox, required_str);


        if (emailEntered == false) {
            return;
        }


        forgptPasswordCall();
    }

    public void forgptPasswordCall() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "requestResetPassword");
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("UserType", CommonUtilities.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

//                    if (isDataAvail == true) {
//
//
//
//                    }
                    if (isDataAvail == true) {
                        emailBox.setText("");
                    }
                    GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
                    generateAlert.setBtnClickList(ForgotPasswordActivity.this);
                    generateAlert.showAlertBox();


//                    generalFunc.showMessage(emailBox, generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));


                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


}
