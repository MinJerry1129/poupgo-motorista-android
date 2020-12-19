package com.poupgo.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.fragments.AddCardFragment;
import com.fragments.ViewCardFragment;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.utilities.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MTextView;

import java.util.HashMap;

import co.omise.android.ui.CreditCardActivity;

public class CardPaymentActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    public String userProfileJson = "";
    MTextView titleTxt;
    ImageView backImgView;
    ViewCardFragment viewCardFrag;
    AddCardFragment addCardFrag;
    public static final int REQ_ADD_CARD_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_payment);

        generalFunc = new GeneralFunctions(getActContext());

        //  userProfileJson = getIntent().getStringExtra("UserProfileJson");

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        setLabels();

        backImgView.setOnClickListener(new setOnClickList());

        openViewCardFrag();


    }


    public void setLabels() {
        changePageTitle(generalFunc.retrieveLangLBl("", "LBL_CARD_PAYMENT_DETAILS"));
    }

    public void changePageTitle(String title) {
        titleTxt.setText(title);
    }

    public void changeUserProfileJson(String userProfileJson) {
        this.userProfileJson = userProfileJson;

        Bundle bn = new Bundle();
        // bn.putString("UserProfileJson", userProfileJson);
        new StartActProcess(getActContext()).setOkResult(bn);

        openViewCardFrag();

        generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_INFO_UPDATED_TXT"));
    }

    public View getCurrView() {
        return generalFunc.getCurrentView(CardPaymentActivity.this);
    }

    public void openViewCardFrag() {

        if (viewCardFrag != null) {
            viewCardFrag = null;
            addCardFrag = null;
            Utils.runGC();
        }
        viewCardFrag = new ViewCardFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, viewCardFrag).commit();
    }

    public void openAddCardFrag(String mode) {

        if (addCardFrag != null) {
            addCardFrag = null;
            viewCardFrag = null;
            Utils.runGC();
        }
        if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Stripe")) {
            Bundle bundle = new Bundle();
            bundle.putString("PAGE_MODE", mode);
            bundle.putString("carno", generalFunc.getJsonValue("vCreditCard", userProfileJson));
//        bundle.putString("month",generalFunc.getJsonValue("vExpMonth", userProfileJson));
//        bundle.putString("year",generalFunc.getJsonValue("vExpYear", userProfileJson));
            addCardFrag = new AddCardFragment();
            addCardFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, addCardFrag).commit();
        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Braintree")) {
            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setCancelable(false);
            generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                @Override
                public void handleBtnClick(int btn_id) {

                    if (btn_id == 1) {
                        String Token = generalFunc.getJsonValue("BRAINTREE_TOKEN_KEY", userProfileJson);
                        DropInRequest dropInRequest = new DropInRequest()
                                .amount(generalFunc.getJsonValue("BRAINTREE_CHARGE_AMOUNT", userProfileJson))
                                .clientToken(Token);
                        startActivityForResult(dropInRequest.getIntent(getActContext()), REQ_ADD_CARD_CODE);
                    } else {
                        generateAlert.closeAlertBox();
                    }

                }
            });
            generateAlert.setContentMessage("", generalFunc.getJsonValue("BRAINTREE_CHARGE_MESSAGE", userProfileJson));
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
            generateAlert.showAlertBox();
        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Paymaya")) {
            Bundle bundle = new Bundle();
            bundle.putString("PAGE_MODE", mode);
            addCardFrag = new AddCardFragment();
            addCardFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, addCardFrag).commit();

        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Omise"))

        {
            final String OMISE_PKEY = generalFunc.getJsonValue("OMISE_PUBLIC_KEY", userProfileJson);
            Bundle bn = new Bundle();
            bn.putString(CreditCardActivity.EXTRA_PKEY, OMISE_PKEY);
            new StartActProcess(getActContext()).startActForResult(CreditCardActivity.class, bn, Utils.REQ_OMISE_CODE);

        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Adyen")) {
            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setCancelable(false);
            generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                @Override
                public void handleBtnClick(int btn_id) {

                    if (btn_id == 1) {

                        CreateCustomer("", "");

                    } else {
                        generateAlert.closeAlertBox();
                    }

                }
            });
            generateAlert.setContentMessage("", generalFunc.getJsonValue("ADEYN_CHARGE_MESSAGE", userProfileJson));
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
            generateAlert.showAlertBox();

        }
        else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Xendit")) {

            Bundle bundle = new Bundle();
            bundle.putString("PAGE_MODE", mode);
            addCardFrag = new AddCardFragment();
            addCardFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, addCardFrag).commit();
        }
    }

    public Context getActContext() {
        return CardPaymentActivity.this;
    }

    @Override
    public void onBackPressed() {
        backImgView.performClick();
        return;
    }

    PaymentMethodNonce mNonce = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_ADD_CARD_CODE) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                mNonce = result.getPaymentMethodNonce();
                CreateCustomer(mNonce.getNonce() + "", "");

            } else if (requestCode == Utils.REQ_VERIFY_CARD_PIN_CODE) {

                if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Adyen")) {
                    UpdateCustomerToken();
                } else {

                    if (addCardFrag != null) {
                        addCardFrag.setdata(requestCode, resultCode, data);
                    }
                }
            }
        } else if (resultCode == CreditCardActivity.RESULT_OK)

        {
            String token = data.getStringExtra(CreditCardActivity.EXTRA_TOKEN);
            CreateCustomer("", token);
        }

    }

    public void CreateCustomer(String paymentMethodNonce, String OmisepaymentMethodNonce) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GenerateCustomer");
        parameters.put("iUserId", generalFunc.getMemberId());

        if (!paymentMethodNonce.equalsIgnoreCase("")) {
            parameters.put("paymentMethodNonce", paymentMethodNonce);
        }

        if (!OmisepaymentMethodNonce.equalsIgnoreCase("")) {
            parameters.put("vOmiseToken", OmisepaymentMethodNonce);
            parameters.put("CardNo", "");
        }


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


//                        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
//                        generateAlert.setCancelable(false);
//                        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
//                            @Override
//                            public void handleBtnClick(int btn_id) {
//                                finish();
//                            }
//                        });
//                        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str_one, responseString)));
//                        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
//                        generateAlert.showAlertBox();

                        if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Adyen")) {
                            Bundle bn = new Bundle();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("URL", generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                            String vCreditCard = generalFunc.getJsonValue("vCreditCard", userProfileJson);
                            if (vCreditCard.equalsIgnoreCase("")) {
                                map.put("vPageTitle", generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
                            } else {
                                map.put("vPageTitle", generalFunc.retrieveLangLBl("", "LBL_CHANGE_CARD"));
                            }
                            bn.putSerializable("data", map);

                            new StartActProcess(getActContext()).startActForResult(VerifyCardTokenActivity.class, bn, Utils.REQ_VERIFY_CARD_PIN_CODE);
                        } else {
                            generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                            changeUserProfileJson(generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        }
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


    private void UpdateCustomerToken() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateCustomerToken");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vPaymayaToken", "");
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
                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        changeUserProfileJson(generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
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

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(CardPaymentActivity.this);
            if (i == R.id.backImgView) {

                if (addCardFrag != null && addCardFrag.isInProcessMode) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("You cannot go back while your transaction is being processed. Please wait for transaction being completed.", "LBL_TRANSACTION_IN_PROCESS_TXT"));
                } else if (addCardFrag == null) {
                    CardPaymentActivity.super.onBackPressed();
                } else {
                    openViewCardFrag();
                }
            }
        }
    }

}
