package com.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.poupgo.driver.AppLoignRegisterActivity;
import com.poupgo.driver.ContactUsActivity;
import com.poupgo.driver.ForgotPasswordActivity;
import com.poupgo.driver.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.files.OpenMainProfile;
import com.general.files.SetUserData;
import com.utilities.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MButton btn_type2;

    AppLoignRegisterActivity appLoginAct;
    GeneralFunctions generalFunc;

    int submitBtnId;
    MTextView forgetPassTxt;

    View view;

    String required_str = "";
    String error_email_str = "";

    MTextView registerTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        appLoginAct = (AppLoignRegisterActivity) getActivity();
        generalFunc = appLoginAct.generalFunc;

        emailBox = view.findViewById(R.id.emailBox);
        passwordBox = view.findViewById(R.id.passwordBox);
        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();
        forgetPassTxt = view.findViewById(R.id.forgetPassTxt);


        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));


        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);


        registerTxt = view.findViewById(R.id.registerTxt);
        registerTxt.setOnClickListener(new setOnClickList());

        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);


        btn_type2.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());

        setLabels();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        emailBox.requestFocus();
    }


    public void setLabels() {
        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PHONE_EMAIL"));
        emailBox.setHelperText(generalFunc.retrieveLangLBl("","LBL_SIGN_IN_MOBILE_EMAIL_HELPER"));
        passwordBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PASSWORD_LBL_TXT"));
        registerTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DONT_HAVE_AN_ACCOUNT"));

        forgetPassTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FORGET_PASS_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_TXT"));

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == submitBtnId) {
                Utils.hideKeyboard(appLoginAct);

                checkValues();
            } else if (i == forgetPassTxt.getId()) {
                new StartActProcess(getActContext()).startAct(ForgotPasswordActivity.class);
            } else if (i == registerTxt.getId()) {
                Utils.hideKeyboard(appLoginAct);
                appLoginAct.titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP"));
                appLoginAct.hadnleFragment(new SignUpFragment());
                appLoginAct.signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP_WITH_SOC_ACC"));
            }

        }
    }

    public void checkValues() {
        Utils.hideKeyboard(getActContext());
        String noWhiteSpace = generalFunc.retrieveLangLBl("Password should not contain whitespace.", "LBL_ERROR_NO_SPACE_IN_PASS");
        String pass_length = generalFunc.retrieveLangLBl("Password must be", "LBL_ERROR_PASS_LENGTH_PREFIX")
                + " " + Utils.minPasswordLength + " " + generalFunc.retrieveLangLBl("or more character long.", "LBL_ERROR_PASS_LENGTH_SUFFIX");


        boolean emailEntered = Utils.checkText(emailBox.getText().toString().replace("+", "")) ? true
                : Utils.setErrorFields(emailBox, required_str);

        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, noWhiteSpace)
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, pass_length)))
                : Utils.setErrorFields(passwordBox, required_str);


        String regexStr = "^[0-9]*$";

        if (emailBox.getText().toString().trim().replace("+", "").matches(regexStr)) {
            if (emailEntered) {
                emailEntered = emailBox.length() >= 3 ? true : Utils.setErrorFields(emailBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
            }

        } else {
            emailEntered = Utils.checkText(emailBox) ?
                    (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                    : Utils.setErrorFields(emailBox, required_str);

            if (emailEntered == false) {
                return;
            }
        }

        if (emailEntered == false || passwordEntered == false) {
            return;
        }

        btn_type2.setEnabled(false);
        signInUser();
    }

    public void signInUser() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signIn");
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("vPassword", Utils.getText(passwordBox));
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("vCurrency", generalFunc.retrieveValue(CommonUtilities.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                btn_type2.setEnabled(true);
                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        new SetUserData(responseString, generalFunc, getActContext(), true);

                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));

                        generalFunc.storedata(CommonUtilities.WORKLOCATION, generalFunc.getJsonValue("vWorkLocation", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));

                        new OpenMainProfile(getActContext(),
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString), false, generalFunc).startProcess();

                    } else {
                        passwordBox.setText("");


                        if (generalFunc.getJsonValue("eStatus", responseString).equalsIgnoreCase("Deleted")) {
                            GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                            alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                            alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                            alertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
                            alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                @Override
                                public void handleBtnClick(int btn_id) {

                                    alertBox.closeAlertBox();
                                    if (btn_id == 0) {
                                        new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                                    }
                                }
                            });
                            alertBox.showAlertBox();
                        } else {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        }

                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(appLoginAct);
    }

    public Context getActContext() {
        return appLoginAct.getActContext();
    }
}
