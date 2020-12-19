package com.poupgo.driver;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BankDetailActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;
    MButton submitBtn;
    ImageView backImgView;
    MTextView titleTxt;

    MaterialEditText vPaymentEmail, vBankAccountHolderName, vAccountNumber, vBankLocation;
    String required_str = "";
    String error_email_str = "";

    private Spinner vBIC_SWIFT_Code,vBankName;
    private String tip = "";
    private String banco = "";

    List<String> list = new ArrayList<String>();

    List<String> listBanco = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);

        generalFunc = new GeneralFunctions(getActContext());
        submitBtn = ((MaterialRippleLayout) findViewById(R.id.submitBtn)).getChildView();

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);


        vPaymentEmail = (MaterialEditText) findViewById(R.id.vPaymentEmailBox);
        vBankAccountHolderName = (MaterialEditText) findViewById(R.id.vBankAccountHolderNameBox);
        vAccountNumber = (MaterialEditText) findViewById(R.id.vAccountNumberBox);
        vBankLocation = (MaterialEditText) findViewById(R.id.vBankLocation);
        vBankName =  findViewById(R.id.vBankName);

        vBIC_SWIFT_Code =  findViewById(R.id.vBIC_SWIFT_Code);


        String banks[] = generalFunc.getJsonValue("FINANCIAL_BANKS_LIST",generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON)).split(",");
        listBanco.add("Selecione");
        for (String s : banks ) {
            listBanco.add(s);
        }



        list.add("Selecione");
        list.add("Conta Corrente");
        list.add("Poupança");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActContext(),
                android.R.layout.simple_spinner_item, list){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    // Disabilita a primeira posição (hint)
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position == 0){

                    // Deixa o hint com a cor cinza ( efeito de desabilitado)
                    tv.setTextColor(Color.GRAY);

                }else {
                    tv.setTextColor(Color.BLACK);
                }

                return view;            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        vBIC_SWIFT_Code.setAdapter(dataAdapter);
        vBIC_SWIFT_Code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tip = list.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> dataBanco = new ArrayAdapter<String>(getActContext(),
                android.R.layout.simple_spinner_item, listBanco){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    // Disabilita a primeira posição (hint)
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position == 0){

                    // Deixa o hint com a cor cinza ( efeito de desabilitado)
                    tv.setTextColor(Color.GRAY);

                }else {
                    tv.setTextColor(Color.BLACK);
                }

                return view;            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        vBankName.setAdapter(dataBanco);
        vBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                banco = listBanco.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setData();
        submitBtn.setId(Utils.generateViewId());

        submitBtn.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        isBankDetailDisplay("", "", "", "", "", "", "Yes", false);
    }

    private void setData() {

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_BANK_DETAILS_TXT"));
        submitBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));

        vPaymentEmail.setBothText(generalFunc.retrieveLangLBl("", "LBL_PAYMENT_EMAIL_TXT"));
        vBankAccountHolderName.setBothText(generalFunc.retrieveLangLBl("", "LBL_PROFILE_BANK_HOLDER_TXT"));
        vAccountNumber.setBothText(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_NUMBER"));
        vBankLocation.setBothText(generalFunc.retrieveLangLBl("", "LBL_BANK_LOCATION"));

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
        String lista = generalFunc.retrieveLangLBl("", "FINANCIAL_BANK_LIST");
        Log.v("Felipe",lista);

        if (generalFunc.retrieveLangLBl("", "LBL_BIC_SWIFT_CODE").equals("Poupança")){
            vBIC_SWIFT_Code.setSelection(2);
        }else{
            vBIC_SWIFT_Code.setSelection(1);

        }

        vBankName.setSelection(lista.indexOf(generalFunc.retrieveLangLBl("", "LBL_BANK_NAME")));

        vAccountNumber.setInputType(InputType.TYPE_CLASS_TEXT);
        vBankLocation.setInputType(InputType.TYPE_CLASS_NUMBER);
        vPaymentEmail.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void isBankDetailDisplay(String vPaymentEmail, String vBankAccountHolderName, String vAccountNumber, String vBankLocation, String vBankName,
                                     String vBIC_SWIFT_Code, String eDisplay, final boolean isAlert) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "DriverBankDetails");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("userType", CommonUtilities.APP_TYPE);
        parameters.put("vPaymentEmail", vPaymentEmail);
        parameters.put("vBankAccountHolderName", vBankAccountHolderName);
        parameters.put("vAccountNumber", vAccountNumber);
        parameters.put("vBankLocation", vBankLocation);
        parameters.put("vBankName", vBankName);
        parameters.put("vBIC_SWIFT_Code", vBIC_SWIFT_Code);
        parameters.put("eDisplay", eDisplay);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);
                    if (isDataAvail == true) {
                        JSONObject msg_obj = generalFunc.getJsonObject("message", responseString);

                        String vPaymentEmail = generalFunc.getJsonValue("vPaymentEmail", msg_obj.toString());
                        String vBankAccountHolderName = generalFunc.getJsonValue("vBankAccountHolderName", msg_obj.toString());
                        String vAccountNumber = generalFunc.getJsonValue("vAccountNumber", msg_obj.toString());
                        String vBankLocation = generalFunc.getJsonValue("vBankLocation", msg_obj.toString());
                        String vBankName = generalFunc.getJsonValue("vBankName", msg_obj.toString());
                        String vBIC_SWIFT_Code = generalFunc.getJsonValue("vBIC_SWIFT_Code", msg_obj.toString());

                        if (!vPaymentEmail.equals("")) {
                            ((MaterialEditText) findViewById(R.id.vPaymentEmailBox)).setText(vPaymentEmail);
                        }
                        if (!vBankAccountHolderName.equals("")) {
                            ((MaterialEditText) findViewById(R.id.vBankAccountHolderNameBox)).setText(vBankAccountHolderName);
                        }
                        if (!vAccountNumber.equals("")) {
                            ((MaterialEditText) findViewById(R.id.vAccountNumberBox)).setText(vAccountNumber);
                        }
                        if (!vBankLocation.equals("")) {
                            ((MaterialEditText) findViewById(R.id.vBankLocation)).setText(vBankLocation);
                        }
                        if (!vBankName.equals("")) {
                            Log.v("Felipe",listBanco.indexOf(vBankName) + "");
                            ((Spinner) findViewById(R.id.vBankName)).setSelection(listBanco.indexOf(vBankName));

                        }
                        if (!vBIC_SWIFT_Code.equals("")) {
                            ((Spinner) findViewById(R.id.vBIC_SWIFT_Code)).setSelection(list.indexOf(vBIC_SWIFT_Code));
                        }


                        if (isAlert == true) {
                            GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                            alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_BANK_DETAILS_UPDATED"));
                            alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_GENERAL"));
                            alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                @Override
                                public void handleBtnClick(int btn_id) {
                                    if (btn_id == 1) {
                                        BankDetailActivity.super.onBackPressed();
                                    }
                                }
                            });
                            alertBox.showAlertBox();
                        }
                    } else {

                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return BankDetailActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            if (i == submitBtn.getId()) {
                checkData();
            } else if (i == R.id.backImgView) {
                BankDetailActivity.this.onBackPressed();
            }
        }
    }

    private void checkData() {

        boolean isPaymentEmail = Utils.checkText(vPaymentEmail) ?  true : Utils.setErrorFields(vPaymentEmail, required_str);

        boolean isSwiftCode = tip.equals("") ? false : true;
        boolean isAccountNumber = Utils.checkText(vAccountNumber) ? true : Utils.setErrorFields(vAccountNumber, required_str);
        boolean isBankAccountHolderName = Utils.checkText(vBankAccountHolderName) ? true : Utils.setErrorFields(vBankAccountHolderName, required_str);
        boolean isBankName = banco.isEmpty() ? false : true;
        boolean isBankLocation = Utils.checkText(vBankLocation) ? true : Utils.setErrorFields(vBankLocation, required_str);

        if (isPaymentEmail == false || isBankAccountHolderName == false || isAccountNumber == false || isBankLocation == false || isBankName == false || isSwiftCode == false) {
            return;
        }

        isBankDetailDisplay(Utils.getText(vPaymentEmail), Utils.getText(vBankAccountHolderName), Utils.getText(vAccountNumber),
                Utils.getText(vBankLocation), banco, tip, "No", true);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
