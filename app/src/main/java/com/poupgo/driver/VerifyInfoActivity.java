package com.poupgo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.autoReadOtp.OTPListener;
import com.autoReadOtp.SmsReceiver;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.files.SetUserData;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.utilities.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class VerifyInfoActivity extends AppCompatActivity implements OTPListener {
    CardView emailView, smsView;
    ProgressBar loading;
    MaterialEditText codeBox;
    MaterialEditText emailBox;

    ImageView backImgView;
    GeneralFunctions generalFunc;
    String required_str = "";
    String error_verification_code = "";

    String userProfileJson = "";
    MTextView titleTxt;

    MButton okBtn, emailOkBtn;
    MButton resendBtn, emailResendBtn;
    MButton editBtn;
    MaterialEditText emailEditBtn;
    Bundle bundle;
    String reqType = "";
    String vEmail = "", vPhone = "";

    String phoneVerificationCode = "";
    String emailVerificationCode = "";

    MTextView phonetxt;
    MTextView emailTxt;


    boolean isEditInfoTapped = false;
    CountDownTimer countDnTimer;
    CountDownTimer countDnEmailTimer;

    int maxAttemptCount = 0;
    int resendTime = 0;
    //    int resendSecAfter = 30 * 1000;
    int resendSecAfter;
    int maxAllowdCount;
    int resendSecInMilliseconds;
    boolean isProcessRunning = false;
    boolean isEmailSendProcessRunning = false;

    // Edit Email Or Number

    boolean isDialogOpen = false;
    private String error_email_str = "";
    BottomSheetDialog editInfoDialog;
    boolean isCountrySelected = false;
    private String vPhoneCode;
    private String vCountryCode;
    public int MY_PERMISSIONS_REQUEST_SMS = 53;
    String msg = "";

    private SmsReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_info);

        generalFunc = new GeneralFunctions(getActContext());
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        msg = bundle.getString("msg");

        resendSecAfter = generalFunc.parseIntegerValue(30, generalFunc.getJsonValue(Utils.VERIFICATION_CODE_RESEND_TIME_IN_SECONDS_KEY, generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON)));
        maxAllowdCount = generalFunc.parseIntegerValue(5, generalFunc.getJsonValue(Utils.VERIFICATION_CODE_RESEND_COUNT_KEY, generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON)));
        resendTime = generalFunc.parseIntegerValue(30, generalFunc.getJsonValue(Utils.VERIFICATION_CODE_RESEND_COUNT_RESTRICTION_KEY, generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON)));
        resendSecInMilliseconds = resendSecAfter * 1 * 1000;


        phonetxt = ((MTextView) findViewById(R.id.phoneTxt));
        emailTxt = ((MTextView) findViewById(R.id.emailTxt));

        if (!getIntent().hasExtra("MOBILE")) {
            userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

            vEmail = generalFunc.getJsonValue("vEmail", userProfileJson);
            vPhone = generalFunc.getJsonValue("vPhoneCode", userProfileJson) + generalFunc.getJsonValue("vPhone", userProfileJson);
        } else {
            vPhone = getIntent().getStringExtra("MOBILE");
        }
        emailView = (CardView) findViewById(R.id.emailView);
        smsView = (CardView) findViewById(R.id.smsView);

        if (msg.equalsIgnoreCase("DO_EMAIL_PHONE_VERIFY")) {
            // OtpReader.bind(this, generalFunc.retrieveValue(Utils.SMS_BODY_KEY));
            emailView.setVisibility(View.VISIBLE);
            smsView.setVisibility(View.VISIBLE);
            reqType = "DO_EMAIL_PHONE_VERIFY";
        } else if (msg.equalsIgnoreCase("DO_EMAIL_VERIFY")) {
            emailView.setVisibility(View.VISIBLE);
            smsView.setVisibility(View.GONE);
            reqType = "DO_EMAIL_VERIFY";
        } else if (msg.equalsIgnoreCase("DO_PHONE_VERIFY")) {
            //  OtpReader.bind(this, generalFunc.retrieveValue(Utils.SMS_BODY_KEY));
            smsView.setVisibility(View.VISIBLE);
            emailView.setVisibility(View.GONE);
            reqType = "DO_PHONE_VERIFY";
        }

        okBtn = ((MaterialRippleLayout) findViewById(R.id.okBtn)).getChildView();
        resendBtn = ((MaterialRippleLayout) findViewById(R.id.resendBtn)).getChildView();
        editBtn = ((MaterialRippleLayout) findViewById(R.id.editBtn)).getChildView();
        codeBox = (MaterialEditText) findViewById(R.id.codeBox);
        emailBox = (MaterialEditText) findViewById(R.id.emailCodeBox);
        emailOkBtn = ((MaterialRippleLayout) findViewById(R.id.emailOkBtn)).getChildView();
        emailResendBtn = ((MaterialRippleLayout) findViewById(R.id.emailResendBtn)).getChildView();
        emailEditBtn = (MaterialEditText) findViewById(R.id.emailEditBtn);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        loading = (ProgressBar) findViewById(R.id.loading);

        okBtn.setId(Utils.generateViewId());
        okBtn.setOnClickListener(new setOnClickList());

        resendBtn.setId(Utils.generateViewId());
        resendBtn.setOnClickListener(new setOnClickList());

        editBtn.setId(Utils.generateViewId());
        editBtn.setOnClickListener(new setOnClickList());

        emailOkBtn.setId(Utils.generateViewId());
        emailOkBtn.setOnClickListener(new setOnClickList());

        emailResendBtn.setId(Utils.generateViewId());
        emailResendBtn.setOnClickListener(new setOnClickList());

        emailEditBtn.setId(Utils.generateViewId());
        emailEditBtn.setOnClickListener(new setOnClickList());
        setLabels();

        smsReceiver = new SmsReceiver();
        smsReceiver.bind(this);

        startSMSListening();

        if (generalFunc.retrieveValue(CommonUtilities.SITE_TYPE_KEY).equalsIgnoreCase("Demo")) {
            findViewById(R.id.helpOTPTxtView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.helpOTPTxtView).setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void startSMSListening() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                //Log.e("startSMSListening", "listening sms");

                sendVerificationSMS("Mobile");

                //showHideLoadingView(false);
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                Log.e("startSMSListening", "failure listening sms");
                //showHideLoadingView(false);
            }
        });
    }

    private void setLabels() {

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_TXT"));
        ((MTextView) findViewById(R.id.smsTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_VERIFy_TXT"));
        ((MTextView) findViewById(R.id.smsSubTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SMS_SENT_TO") + " ");
        ((MTextView) findViewById(R.id.emailTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_VERIFy_TXT"));
        ((MTextView) findViewById(R.id.emailSubTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_SENT_TO") + " ");
        ((MTextView) findViewById(R.id.smsHelpTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SMS_SENT_NOTE"));
        ((MTextView) findViewById(R.id.emailHelpTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_SENT_NOTE"));

        ((MTextView) findViewById(R.id.phoneTxt)).setText("+" + vPhone);
        ((MTextView) findViewById(R.id.emailTxt)).setText(vEmail);

        okBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
        editBtn.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT_MOBILE"));

        emailOkBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
        emailEditBtn.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT_EMAIL"));

        error_verification_code = generalFunc.retrieveLangLBl("", "LBL_VERIFICATION_CODE_INVALID");
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
    }

    public void sendVerificationSMS(String showTimerFor) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "sendVerificationSMS");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("MobileNo", vPhone);
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("REQ_TYPE", reqType);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            loading.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    switch (reqType) {
                        case "DO_EMAIL_PHONE_VERIFY":
                            if (!generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                                        generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                            } else {
                                if (!generalFunc.getJsonValue(CommonUtilities.message_str + "_sms", responseString).equalsIgnoreCase("LBL_MOBILE_VERIFICATION_FAILED_TXT")) {
                                    phoneVerificationCode = generalFunc.getJsonValue(CommonUtilities.message_str + "_sms", responseString);
                                } else {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                                            generalFunc.getJsonValue(CommonUtilities.message_str + "_sms", responseString)));
                                }
                                if (!generalFunc.getJsonValue(CommonUtilities.message_str + "_email", responseString).equalsIgnoreCase("LBL_EMAIL_VERIFICATION_FAILED_TXT")) {
                                    emailVerificationCode = generalFunc.getJsonValue(CommonUtilities.message_str + "_email", responseString);
                                } else {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                                            generalFunc.getJsonValue(CommonUtilities.message_str + "_email", responseString)));
                                }
                            }
                            break;
                        case "DO_EMAIL_VERIFY":
                            emailVerificationCode = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                            break;
                        case "DO_PHONE_VERIFY":
                            phoneVerificationCode = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                            break;
                        case "PHONE_VERIFIED":
                            enableOrDisable(true, showTimerFor);
                            removecountDownTimer("Mobile");
                            isProcessRunning = false;

                            verifySuccessMessage(generalFunc.retrieveLangLBl("",
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString)), true, false);

                            break;
                        case "EMAIL_VERIFIED":

                            enableOrDisable(true, showTimerFor);
                            removecountDownTimer("Email");
                            isEmailSendProcessRunning = false;


                            verifySuccessMessage(generalFunc.retrieveLangLBl("",
                                    generalFunc.getJsonValue(CommonUtilities.message_str, responseString)), false, true);
                            break;


                    }
                    String userdetails = generalFunc.getJsonValue("userDetails", responseString);
                    if (!userdetails.equals("") && userdetails != null) {
                        String messageData = generalFunc.getJsonValue(CommonUtilities.message_str, userdetails);
                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, messageData);
                    }
                    checkVerification(responseString, isDataAvail, showTimerFor);
                } else {
                    checkVerification(responseString, isDataAvail, showTimerFor);
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));

                }

            } else {
                generalFunc.showError();
            }
        });

        exeWebServer.execute();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void checkVerification(String responseString, boolean isDataAvail, String showTimerFor) {
        switch (reqType) {
            case "DO_EMAIL_PHONE_VERIFY":

                if (generalFunc.getJsonValue("eEmailFailed", responseString).equals("Yes") && generalFunc.getJsonValue("eSMSFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Both");
                } else if (generalFunc.getJsonValue("eEmailFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Email");
                    resendProcess("Mobile");
                } else if (generalFunc.getJsonValue("eSMSFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Mobile");
                    resendProcess("Email");
                } else if (isDataAvail) {
                    resendProcess(showTimerFor);
                } else if (!isDataAvail) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Both");
                }
                break;
            case "DO_EMAIL_VERIFY":
                if (generalFunc.getJsonValue("eEmailFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Email");
                } else if (isDataAvail) {
                    resendProcess(showTimerFor);
                } else if (!isDataAvail) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Email");
                }
                break;
            case "DO_PHONE_VERIFY":
                if (generalFunc.getJsonValue("eEmailFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Mobile");
                    break;
                } else if (isDataAvail) {
                    resendProcess(showTimerFor);
                } else if (!isDataAvail) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Mobile");
                }
        }
    }

    public void verifySuccessMessage(String message, final boolean sms, final boolean email) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            if (TextUtils.isEmpty(generalFunc.getMemberId())) {
                if (TextUtils.isEmpty(generalFunc.getMemberId())) {
                    isProcessRunning = false;
                    new StartActProcess(getActContext()).setOkResult();
                    VerifyInfoActivity.super.onBackPressed();
                }
            } else {
                if (sms == true) {
                    smsView.setVisibility(View.GONE);
                    isProcessRunning = false;
                    if (emailView.getVisibility() == View.GONE) {
                        VerifyInfoActivity.super.onBackPressed();
                    }
                } else if (email == true) {
                    emailView.setVisibility(View.GONE);
                    isProcessRunning = false;
                    if (smsView.getVisibility() == View.GONE) {
                        VerifyInfoActivity.super.onBackPressed();
                    }
                }
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }

    public void resendProcess(final String showTimerFor) {

        if (!Utils.checkText(showTimerFor)) {
            enableOrDisable(true, showTimerFor);
            removecountDownTimer(showTimerFor);
            return;
        }


        enableOrDisable(false, showTimerFor);

        if (Utils.checkText(showTimerFor)) {
            setTime(generalFunc.parseLongValue(0L, String.valueOf(resendSecInMilliseconds)), showTimerFor);
            removecountDownTimer(showTimerFor);

            if (showTimerFor.equalsIgnoreCase("Email")) {
                showEmailTimer(showTimerFor);
            } else if (showTimerFor.equalsIgnoreCase("Mobile")) {
                showTimer(showTimerFor);
            } else if (showTimerFor.equalsIgnoreCase("Both")) {
                showTimer("Mobile");
                showEmailTimer("Email");
            }


        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enableOrDisable(true, showTimerFor);
                }
            }, resendSecInMilliseconds);
        }
    }

    private void setTime(long milliseconds, String showTimerFor) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;

        if (showTimerFor.equalsIgnoreCase("Both")) {
            resendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            emailResendBtn.setTextColor(Color.parseColor("#FFFFFF"));

            resendBtn.setText(String.format("%02d:%02d", minutes, seconds));
            emailResendBtn.setText(String.format("%02d:%02d", minutes, seconds));

        } else if (showTimerFor.equalsIgnoreCase("Email")) {
            emailResendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            emailResendBtn.setText(String.format("%02d:%02d", minutes, seconds));
        } else if (showTimerFor.equalsIgnoreCase("Mobile")) {
            resendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            resendBtn.setText(String.format("%02d:%02d", minutes, seconds));
        }

    }

    public void showTimer(String showTimerFor) {
        countDnTimer = new CountDownTimer(resendSecInMilliseconds, 1000) {
            @Override
            public void onTick(long milliseconds) {
                isProcessRunning = true;
                setTime(milliseconds, showTimerFor);


            }

            @Override
            public void onFinish() {
                isProcessRunning = false;
                // this function will be called when the timecount is finished


//                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
                /*resendBtn.setTextColor(getResources().getColor(R.color.appThemeColor_TXT_1));
                resendBtn.setClickable(true);*/
                enableOrDisable(true, showTimerFor);
                removecountDownTimer("Mobile");
            }
        }.start();

    }


    public void showEmailTimer(String showTimerFor) {


        countDnEmailTimer = new CountDownTimer(resendSecInMilliseconds, 1000) {
            @Override
            public void onTick(long milliseconds) {
                isEmailSendProcessRunning = true;
                setTime(milliseconds, showTimerFor);


            }

            @Override
            public void onFinish() {
                isEmailSendProcessRunning = false;
                // this function will be called when the timecount is finished

//                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
                /*resendBtn.setTextColor(getResources().getColor(R.color.appThemeColor_TXT_1));
                resendBtn.setClickable(true);*/
                enableOrDisable(true, showTimerFor);
                removecountDownTimer("Email");
            }
        }.start();

    }

    private void removecountDownTimer(String type) {

        if (type.equalsIgnoreCase("Mobile")) {
            if (countDnTimer != null) {
                countDnTimer.cancel();
                countDnTimer = null;
                isProcessRunning = false;
            }
        } else if (type.equalsIgnoreCase("Email")) {
            if (countDnEmailTimer != null) {
                countDnEmailTimer.cancel();
                countDnEmailTimer = null;
                isEmailSendProcessRunning = false;
            }
        } else if (type.equalsIgnoreCase("Both")) {
            if (countDnTimer != null) {
                countDnTimer.cancel();
                countDnTimer = null;
                isProcessRunning = false;
            }

            if (countDnEmailTimer != null) {
                countDnEmailTimer.cancel();
                countDnEmailTimer = null;
                isEmailSendProcessRunning = false;
            }
        }

    }

    public Context getActContext() {
        return VerifyInfoActivity.this;
    }

    @Override
    public void otpReceived(String messageText) {

        String codeRetrived = getDigitsFromText(messageText);
        if (codeRetrived != null && Utils.checkText(codeRetrived) && TextUtils.isDigitsOnly(codeRetrived.trim())) {
            codeBox.setText(codeRetrived.toString().trim());
            okBtn.performClick();
        }

    }

    private String getDigitsFromText(String messageText) {
        String value = messageText.replaceAll("<#> Your verification code for Brazil Go Application is","").replaceAll("b2N0U2TRDq7","");

        return value.trim();

    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(VerifyInfoActivity.this);
            if (i == R.id.backImgView) {
                onBackPressed();
                // VerifyInfoActivity.super.onBackPressed();
            } else if (i == okBtn.getId()) {
                boolean isCodeEntered = Utils.checkText(codeBox) ?
                        ((phoneVerificationCode.equalsIgnoreCase(Utils.getText(codeBox)) ||
                                (generalFunc.retrieveValue(CommonUtilities.SITE_TYPE_KEY).equalsIgnoreCase("Demo") && Utils.getText(codeBox).equalsIgnoreCase("12345"))) ? true
                                : Utils.setErrorFields(codeBox, error_verification_code)) : Utils.setErrorFields(codeBox, required_str);
                if (isCodeEntered) {
                    reqType = "PHONE_VERIFIED";
                    sendVerificationSMS("");
                }
            } else if (i == resendBtn.getId()) {
                reqType = "DO_PHONE_VERIFY";

               /* if (maxAttemptCount>=maxAllowdCount)
                {
                    // show blockage msg
                    generalFunc.showGeneralMessage("","You reached maximum attempt limit.Please try after "+resendTime +"min");
                }
                else
                {*/
                // maxAttemptCount++;
                sendVerificationSMS("Mobile");

                //resendProcess(resendBtn);
                // }

            } else if (i == editBtn.getId()) {
                Bundle bn = new Bundle();
                bn.putBoolean("isEdit", true);
                bn.putBoolean("isMobile", true);

                isEditInfoTapped = true;

                openEditDilaog("Mobile");

//                new StartActProcess(getActContext()).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);
            } else if (i == emailOkBtn.getId()) {
                boolean isEmailCodeEntered = Utils.checkText(emailBox) ?
                        ((emailVerificationCode.equalsIgnoreCase(Utils.getText(emailBox)) ||
                                (generalFunc.retrieveValue(CommonUtilities.SITE_TYPE_KEY).equalsIgnoreCase("Demo") && Utils.getText(emailBox).equalsIgnoreCase("12345"))) ? true
                                : Utils.setErrorFields(emailBox, error_verification_code)) : Utils.setErrorFields(emailBox, required_str);
                if (isEmailCodeEntered) {
                    reqType = "EMAIL_VERIFIED";
                    sendVerificationSMS("");
                }
            } else if (i == emailResendBtn.getId()) {
                reqType = "DO_EMAIL_VERIFY";
//                resendProcess(emailResendBtn);
                sendVerificationSMS("Email");
            } else if (i == emailEditBtn.getId()) {
                isEditInfoTapped = true;
                openEditDilaog("Email");
                /*Bundle bn = new Bundle();
                bn.putBoolean("isEdit", true);
                bn.putBoolean("isEmail", true);
                new StartActProcess(getActContext()).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);*/
            }
        }
    }

    public void openEditDilaog(String type) {
        // Reset Country Selection

        isCountrySelected = false;
        vPhoneCode = "";
        vCountryCode = "";

        editInfoDialog = new BottomSheetDialog(getActContext());
        View contentView = View.inflate(getActContext(), R.layout.design_edit_phn_email_dialog, null);
        editInfoDialog.setContentView(contentView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(1500);
        View bottomSheetView = editInfoDialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        setCancelable(editInfoDialog, false);

        MTextView titleTxt, hintTxt, errorTxt, updateEmailTxt, updateMobileTxt, cancelTxt;
        LinearLayout updateEmailArea, updateMobileArea;
        ImageView iv_img_icon;
        EditText mobileBox, countryBox, emailBox;

        titleTxt = (MTextView) editInfoDialog.findViewById(R.id.titleTxt);
        hintTxt = (MTextView) editInfoDialog.findViewById(R.id.hintTxt);
        errorTxt = (MTextView) editInfoDialog.findViewById(R.id.errorTxt);
        iv_img_icon = (ImageView) editInfoDialog.findViewById(R.id.iv_img_icon);
        updateEmailTxt = (MTextView) editInfoDialog.findViewById(R.id.updateEmailTxt);
        cancelTxt = (MTextView) editInfoDialog.findViewById(R.id.cancelTxt);
        updateMobileTxt = (MTextView) editInfoDialog.findViewById(R.id.updateMobileTxt);
        emailBox = (EditText) editInfoDialog.findViewById(R.id.emailBox);
        mobileBox = (EditText) editInfoDialog.findViewById(R.id.mobileBox);
        countryBox = (EditText) editInfoDialog.findViewById(R.id.countryBox);
        updateEmailArea = (LinearLayout) editInfoDialog.findViewById(R.id.updateEmailArea);
        updateMobileArea = (LinearLayout) editInfoDialog.findViewById(R.id.updateMobileArea);


        String text = type.equalsIgnoreCase("Email") ? generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT") : generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT");
        titleTxt.setText(text);

        String hintText = type.equalsIgnoreCase("Email") ? generalFunc.retrieveLangLBl("To update your existing email id, please enter new email id below.", "LBL_EMAIL_EDIT_NOTE") : generalFunc.retrieveLangLBl("To update your existing mobile number, please enter new mobile number below.", "LBL_MOBILE_EDIT_NOTE");
        hintTxt.setText(hintText);

        int icon = type.equalsIgnoreCase("Email") ? R.mipmap.ic_verify_email : R.mipmap.ic_mobile;
        iv_img_icon.setImageResource(icon);


        cancelTxt.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));

        updateEmailArea.setVisibility(View.GONE);
        updateMobileArea.setVisibility(View.GONE);
        errorTxt.setVisibility(View.GONE);

        if (type.equalsIgnoreCase("Email")) {
            updateEmailArea.setVisibility(View.VISIBLE);
            updateMobileArea.setVisibility(View.GONE);
        } else if (type.equalsIgnoreCase("Mobile")) {
            updateEmailArea.setVisibility(View.GONE);
            updateMobileArea.setVisibility(View.VISIBLE);
        }

        //set KeyPad

        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Set Existing Details

//        emailBox.setText(generalFunc.getJsonValue("vEmail", userProfileJson));
        countryBox.setText("+" + generalFunc.getJsonValue("vPhoneCode", userProfileJson));
//        mobileBox.setText(generalFunc.getJsonValue("vPhone", userProfileJson));

        if (!generalFunc.getJsonValue("vPhoneCode", userProfileJson).equals("")) {
            isCountrySelected = true;
            vPhoneCode = generalFunc.getJsonValue("vPhoneCode", userProfileJson);
            vCountryCode = generalFunc.getJsonValue("vCountry", userProfileJson);
        }
        Utils.removeInput(countryBox);

        countryBox.setOnClickListener(v -> new StartActProcess(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE));

        countryBox.setOnTouchListener((v, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !countryBox.hasFocus()) {
                countryBox.performClick();
            }
            return true;
        });


        updateEmailTxt.setOnClickListener(view -> {

            // Hide KeyBoard
            Utils.hideKeyPad(VerifyInfoActivity.this);

            if (type.equalsIgnoreCase("Email")) {

                boolean emailEntered = Utils.checkText(emailBox) ?
                        (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : false)
                        : false;


                if (!emailEntered && !Utils.checkText(emailBox)) {
                    errorTxt.setText(Utils.checkText(required_str) ? StringUtils.capitalize(required_str.toLowerCase().trim()) : required_str);
                } else if (!emailEntered && Utils.checkText(emailBox) && !generalFunc.isEmailValid(Utils.getText(emailBox))) {
                    errorTxt.setText(Utils.checkText(error_email_str) ? StringUtils.capitalize(error_email_str.toLowerCase().trim()) : error_email_str);
                }

                /*
                boolean emailEntered = Utils.checkText(emailBox) ?
                        (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                        : Utils.setErrorFields(emailBox, required_str);
*/

                if (emailEntered == false) {

                    errorTxt.setVisibility(View.VISIBLE);
                    return;
                }
                errorTxt.setVisibility(View.GONE);

                if (Utils.getText(emailBox).trim().equalsIgnoreCase(generalFunc.getJsonValue("vEmail", userProfileJson).trim())) {
                    editInfoDialog.dismiss();
                    return;
                }
                updateProfile(type, Utils.getText(emailBox), "", vCountryCode, vPhoneCode);

            }
        });


        updateMobileTxt.setOnClickListener(view -> {

            // Hide KeyBoard
            Utils.hideKeyPad(VerifyInfoActivity.this);

            boolean mobileEntered = Utils.checkText(mobileBox) ? true : false;
            boolean countryEntered = isCountrySelected ? true : false;

//                if (mobileEntered) {
//                    mobileEntered = mobileBox.length() >= 3 ? true : Utils.setErrorFields(mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
//                }
//

            if (!mobileEntered || countryEntered) {

                errorTxt.setText(Utils.checkText(required_str) ? StringUtils.capitalize(required_str.toLowerCase().trim()).toLowerCase() : required_str);
            } else if (mobileEntered && (mobileBox.length() < 3)) {
                errorTxt.setText(Utils.checkText(generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO")) ? StringUtils.capitalize(generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO").toLowerCase().trim()).toLowerCase() : generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
            }

            if (mobileEntered == false || countryEntered == false) {
                errorTxt.setVisibility(View.VISIBLE);
                return;
            }

            errorTxt.setVisibility(View.GONE);

            String currentMobileNum = generalFunc.getJsonValue("vPhone", userProfileJson);
            String currentPhoneCode = generalFunc.getJsonValue("vPhoneCode", userProfileJson);

            if (!currentPhoneCode.equals(vPhoneCode) || !currentMobileNum.equals(Utils.getText(mobileBox))) {
                updateProfile(type, "", Utils.getText(mobileBox), vCountryCode, vPhoneCode);
                return;
            }

            editInfoDialog.dismiss();


        });

        cancelTxt.setOnClickListener(view -> editInfoDialog.dismiss());

        editInfoDialog.setOnDismissListener(dialogInterface -> isDialogOpen = false);
        isDialogOpen = true;
        editInfoDialog.show();

    }


    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }


    public void updateProfile(String type, String email, String mobile, String countryCode, String vPhoneCode) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateUserProfileDetail");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("vName", generalFunc.getJsonValue("vName", userProfileJson));
        parameters.put("vLastName", generalFunc.getJsonValue("vLastName", userProfileJson));
        parameters.put("vPhone", type.equalsIgnoreCase("Mobile") ? mobile : generalFunc.getJsonValue("vPhone", userProfileJson));
        parameters.put("vPhoneCode", type.equalsIgnoreCase("Mobile") ? vPhoneCode : generalFunc.getJsonValue("vPhoneCode", userProfileJson));
        parameters.put("vCountry", type.equalsIgnoreCase("Mobile") ? countryCode : generalFunc.getJsonValue("vCountry", userProfileJson));
        parameters.put("vEmail", type.equalsIgnoreCase("Email") ? email : generalFunc.getJsonValue("vEmail", userProfileJson));
        parameters.put("CurrencyCode", generalFunc.getJsonValue("vCurrencyPassenger", userProfileJson));
        parameters.put("LanguageCode", generalFunc.getJsonValue("vLang", userProfileJson));
        parameters.put("UserType", CommonUtilities.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            Utils.printLog("Response", "::" + responseString);

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    String currentLangCode = generalFunc.retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY);
                    String vCurrencyPassenger = generalFunc.getJsonValue("vCurrencyPassenger", userProfileJson);

                    String messgeJson = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                    generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, messgeJson);
                    responseString = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);


                    new SetUserData(responseString, generalFunc, getActContext(), false);
                    userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);


                    vEmail = generalFunc.getJsonValue("vEmail", userProfileJson);
                    vPhone = generalFunc.getJsonValue("vPhoneCode", userProfileJson) + generalFunc.getJsonValue("vPhone", userProfileJson);


                    ((MTextView) findViewById(R.id.phoneTxt)).setText("+" + vPhone);
                    ((MTextView) findViewById(R.id.emailTxt)).setText(vEmail);


                    String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);
                    String eEmailVerified = generalFunc.getJsonValue("eEmailVerified", userProfileJson);


                    enableOrDisable(true, type);
                    removecountDownTimer(type);

                    if (type.equalsIgnoreCase("Mobile") && !ePhoneVerified.equalsIgnoreCase("Yes")) {
                        reqType = "DO_PHONE_VERIFY";
                        phoneVerificationCode = "";
                    } else if (type.equalsIgnoreCase("Email") && !eEmailVerified.equalsIgnoreCase("Yes")) {
                        reqType = "DO_EMAIL_VERIFY";
                        emailVerificationCode = "";
                    }

                    editInfoDialog.dismiss();
                    sendVerificationSMS(type);


                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });

        exeWebServer.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

            /*You will receive user selected phone number here if selected and send it to the server for request the otp*/
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);


        }
    }

    public void enableOrDisable(boolean activate, String showTimerFor) {

        if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Both")) {
            setButtonEnabled(resendBtn, activate);
            setButtonEnabled(emailResendBtn, activate);
        } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Email")) {
            setButtonEnabled(emailResendBtn, activate);
        } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Mobile")) {
            setButtonEnabled(resendBtn, activate);
        } else if (!Utils.checkText(showTimerFor)) {
            setButtonEnabled(resendBtn, activate);
            setButtonEnabled(emailResendBtn, activate);
        }

        if (activate && Utils.checkText(showTimerFor)) {
            if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Both")) {
                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
                emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
            } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Email")) {
                emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
            } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Mobile")) {
                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));

            } else if (!Utils.checkText(showTimerFor)) {
                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
                emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
            }

        }
    }

    private void setButtonEnabled(MButton btn, boolean setEnable) {
        btn.setFocusableInTouchMode(setEnable);
        btn.setFocusable(setEnable);
        btn.setEnabled(setEnable);
        btn.setOnClickListener(setEnable ? new setOnClickList() : null);
        btn.setTextColor(setEnable ? Color.parseColor("#FFFFFF") : Color.parseColor("#BABABA"));
        btn.setClickable(setEnable);
    }

    @Override
    public void onBackPressed() {

        /*if (isProcessRunning) {
            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setCancelable(false);
            generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                @Override
                public void handleBtnClick(int btn_id) {
                    generateAlert.closeAlertBox();

                    if (btn_id == 0) {
                        VerifyInfoActivity.super.onBackPressed();
                    }


                }
            });
            generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Are you sure you want to cancel current running request's process?", "LBL_CANCEL_VERIFY_SCREEN_PROCESS_TXT"));
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_TXT"));
            generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
            generateAlert.showAlertBox();
        } else {*/
        removecountDownTimer("Both");
        super.onBackPressed();
        //}


    }

    @Override
    protected void onDestroy() {
        removecountDownTimer("Both");
        super.onDestroy();
    }
}
