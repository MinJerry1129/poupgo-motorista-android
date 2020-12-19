package com.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;

import com.poupgo.driver.MyProfileActivity;
import com.poupgo.driver.R;
import com.poupgo.driver.SelectCountryActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.files.SetUserData;
import com.utilities.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.view.MButton;
import com.view.MaterialRippleLayout;
import com.view.indicator.AVLoadingIndicatorView;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    MyProfileActivity myProfileAct;
    View view;

    GeneralFunctions generalFunc;

    String userProfileJson = "";

    MaterialEditText fNameBox;
    MaterialEditText lNameBox;
    MaterialEditText emailBox;
    MaterialEditText profileDescriptionEditBox;
    MaterialEditText countryBox;
    MaterialEditText mobileBox;


    AVLoadingIndicatorView loaderView;


    String selected_language_code = "";


    String selected_currency = "";

    MButton btn_type2;
    int submitBtnId;

    String required_str = "";
    String error_email_str = "";

    String vCountryCode = "";
    String vPhoneCode = "";
    boolean isCountrySelected = false;
    CheckBox haveMachine;
    CheckBox acceptCash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        myProfileAct = (MyProfileActivity) getActivity();

        generalFunc = myProfileAct.generalFunc;
        userProfileJson = myProfileAct.userProfileJson;

        fNameBox = (MaterialEditText) view.findViewById(R.id.fNameBox);
        lNameBox = (MaterialEditText) view.findViewById(R.id.lNameBox);
        emailBox = (MaterialEditText) view.findViewById(R.id.emailBox);
        countryBox = (MaterialEditText) view.findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) view.findViewById(R.id.mobileBox);

        loaderView = (AVLoadingIndicatorView) view.findViewById(R.id.loaderView);
        profileDescriptionEditBox = (MaterialEditText) view.findViewById(R.id.profileDescriptionEditBox);
        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();
        haveMachine = view.findViewById(R.id.haveMachine);
        acceptCash = view.findViewById(R.id.acceptCash);
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        setLabels();
        removeInput();

        setData();
        myProfileAct.changePageTitle(generalFunc.retrieveLangLBl("", "LBL_EDIT_PROFILE_TXT"));

        Utils.printLog("isEmail", myProfileAct.isEmail + "");
        Utils.printLog("isMobile", myProfileAct.isMobile + "");
        if (myProfileAct.isEmail) {
            emailBox.requestFocus();
        }

        if (myProfileAct.isMobile) {
            mobileBox.requestFocus();
        }

//        emailBox.setVisibility(View.GONE);
        return view;
    }

    public void setLabels() {
        fNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_FIRST_NAME_HEADER_TXT"));
        lNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_LAST_NAME_HEADER_TXT"));
        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        countryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));
        mobileBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));

        profileDescriptionEditBox.setBothText(generalFunc.retrieveLangLBl("Service Description", "LBL_SERVICE_DESCRIPTION"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_PROFILE_UPDATE_PAGE_TXT"));
        haveMachine.setText(generalFunc.retrieveLangLBl("", "LBL_HAVE_MACHINE"));
        acceptCash.setText(generalFunc.retrieveLangLBl("", "LBL_RECEIVE_CARD"));
        fNameBox.getLabelFocusAnimator().start();
        lNameBox.getLabelFocusAnimator().start();
        emailBox.getLabelFocusAnimator().start();
        countryBox.getLabelFocusAnimator().start();
        mobileBox.getLabelFocusAnimator().start();

        profileDescriptionEditBox.getLabelFocusAnimator().start();

        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
    }

    public void removeInput() {
        Utils.removeInput(countryBox);
        countryBox.setOnTouchListener(new setOnTouchList());
        countryBox.setOnClickListener(new setOnClickList());
    }

    public class setOnTouchList implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                view.performClick();
            }
            return true;
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActivity());
            if (i == submitBtnId) {

                checkValues();
            } else if (i == R.id.countryBox) {
                new StartActProcess(getActContext()).startActForResult(myProfileAct.getEditProfileFrag(),
                        SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            }
        }
    }

    public void setData() {
        fNameBox.setText(generalFunc.getJsonValue("vName", userProfileJson));
        lNameBox.setText(generalFunc.getJsonValue("vLastName", userProfileJson));
        emailBox.setText(generalFunc.getJsonValue("vEmail", userProfileJson));
        countryBox.setText(generalFunc.getJsonValue("vCode", userProfileJson));
        mobileBox.setText(generalFunc.getJsonValue("vPhone", userProfileJson));
        profileDescriptionEditBox.setText(generalFunc.getJsonValue("tProfileDescription", userProfileJson));
        if (generalFunc.getJsonValue("eAcceptPOS", userProfileJson).equals("Yes")) {
            haveMachine.setChecked(true);
        } else {
            haveMachine.setChecked(false);
        }

        if (generalFunc.getJsonValue("eNotAcceptCash", userProfileJson).equals("Yes")) {
            acceptCash.setChecked(true);
        } else {
            acceptCash.setChecked(false);
        }


        if (!generalFunc.getJsonValue("vCode", userProfileJson).equals("")) {
            isCountrySelected = true;
            vPhoneCode = generalFunc.getJsonValue("vCode", userProfileJson);
            vCountryCode = generalFunc.getJsonValue("vCountry", userProfileJson);
        }

        selected_currency = generalFunc.getJsonValue("vCurrencyDriver", userProfileJson);
    }


    public String getSelectLangText() {
        return ("" + generalFunc.retrieveLangLBl("Select", "LBL_SELECT_LANGUAGE_HINT_TXT"));
    }

    public void checkValues() {


        boolean fNameEntered = Utils.checkText(fNameBox) ? true : Utils.setErrorFields(fNameBox, required_str);
        boolean lNameEntered = Utils.checkText(lNameBox) ? true : Utils.setErrorFields(lNameBox, required_str);
        boolean emailEntered = Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                : Utils.setErrorFields(emailBox, required_str);
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, required_str);
        boolean countryEntered = isCountrySelected ? true : Utils.setErrorFields(countryBox, required_str);


        if (mobileEntered) {
            mobileEntered = mobileBox.length() >= 3 ? true : Utils.setErrorFields(mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }
        if (fNameEntered == false || lNameEntered == false || emailEntered == false || mobileEntered == false
                || countryEntered == false) {
            return;
        }

        String currentMobileNum = generalFunc.getJsonValue("vPhone", userProfileJson);
        String currentPhoneCode = generalFunc.getJsonValue("vCode", userProfileJson);

        if (!currentPhoneCode.equals(vPhoneCode) || !currentMobileNum.equals(Utils.getText(mobileBox))) {
            if (generalFunc.retrieveValue(CommonUtilities.MOBILE_VERIFICATION_ENABLE_KEY).equals("Yes")) {
                notifyVerifyMobile();

                return;
            }
        }

        updateProfile();
    }

    public void notifyVerifyMobile() {
        Bundle bn = new Bundle();
        bn.putString("MOBILE", vPhoneCode + Utils.getText(mobileBox));
        bn.putString("msg", "DO_PHONE_VERIFY");
        generalFunc.verifyMobile(bn, myProfileAct.getEditProfileFrag());
    }

    public void updateProfile() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateUserProfileDetail");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("vName", Utils.getText(fNameBox));
        parameters.put("vLastName", Utils.getText(lNameBox));
        parameters.put("vPhone", Utils.getText(mobileBox));
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("tProfileDescription", Utils.getText(profileDescriptionEditBox));
        parameters.put("vPhoneCode", vPhoneCode);
        parameters.put("vCountry", vCountryCode);
        parameters.put("CurrencyCode", selected_currency);
        parameters.put("LanguageCode", selected_language_code);
        parameters.put("UserType", CommonUtilities.app_type);
        parameters.put("eAcceptPOS", haveMachine.isChecked() ? "Yes" : "No");
        parameters.put("eNotAcceptCash", acceptCash.isChecked() ? "Yes" : "No");
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

                        String currentLangCode = generalFunc.retrieveValue(CommonUtilities.LANGUAGE_CODE_KEY);
                        String vCurrencyPassenger = generalFunc.getJsonValue("vCurrencyDriver", userProfileJson);

                        try {
                            String messgeJson = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                            generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, messgeJson);
                            responseString = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

                        } catch (Exception e) {

                        }

                        new SetUserData(responseString, generalFunc, getActContext(), false);


                        myProfileAct.changeUserProfileJson(generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON));


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



    public Context getActContext() {
        return myProfileAct.getActContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == myProfileAct.RESULT_OK && data != null) {
            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = data.getStringExtra("vPhoneCode");
            isCountrySelected = true;

            countryBox.setText("+" + vPhoneCode);
        } else if (requestCode == Utils.VERIFY_MOBILE_REQ_CODE && resultCode == myProfileAct.RESULT_OK) {
            updateProfile();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }
}