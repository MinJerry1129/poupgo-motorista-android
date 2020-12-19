package com.poupgo.driver;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.general.files.DecimalDigitsInputFilter;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.utilities.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.ErrorView;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
/**
 * Created by Admin on 04-11-2016.
 */
public class MyWalletActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    MTextView titleTxt, tv_datini, tv_datfin, totalLoginTime, totalFinishedTrips;
    ImageView backImgView;
    ProgressBar loading_wallet_history;
    MTextView viewTransactionsTxt;

    ErrorView errorView;
    String required_str = "";
    String error_money_str = "";
    String userProfileJson = "";
    boolean mIsLoading = false;
    String next_page_str = "0";
    private MTextView yourBalTxt;
    private MButton btn_type1;
    @SuppressLint("SimpleDateFormat")
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");

    Locale ptBr = new Locale("pt", "BR");
    private String driverBalance = "";

    Calendar dateIni = Calendar.getInstance();
    Calendar dateFinal = Calendar.getInstance();


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        totalLoginTime = (MTextView) findViewById(R.id.totalLoginTime);
        totalFinishedTrips = (MTextView) findViewById(R.id.totalFinishedTrips);

        loading_wallet_history = (ProgressBar) findViewById(R.id.loading_wallet_history);
        viewTransactionsTxt = (MTextView) findViewById(R.id.viewTransactionsTxt);
        errorView = (ErrorView) findViewById(R.id.errorView);


        errorView = (ErrorView) findViewById(R.id.errorView);
        yourBalTxt = (MTextView) findViewById(R.id.yourBalTxt);
        btn_type1 = ((MaterialRippleLayout) findViewById(R.id.btn_type1)).getChildView();

        generalFunc = new GeneralFunctions(getActContext());
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);


        backImgView.setOnClickListener(new setOnClickList());
        viewTransactionsTxt.setOnClickListener(new setOnClickList());
        tv_datini = findViewById(R.id.tv_datini);
        tv_datfin = findViewById(R.id.tv_datfin);

        btn_type1.setId(Utils.generateViewId());
        btn_type1.setOnClickListener(new setOnClickList());
        tv_datini.setOnClickListener(new setOnClickList());
        tv_datfin.setOnClickListener(new setOnClickList());


        setLabels();


        getTransactionHistory(false);


    }

    public void setLabels() {

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LEFT_MENU_WALLET"));
        yourBalTxt.setText(generalFunc.retrieveLangLBl("", "LBL_USER_BALANCE"));
        viewTransactionsTxt.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_TRANS_HISTORY"));
        btn_type1.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_TRANS_HISTORY"));


        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_money_str = generalFunc.retrieveLangLBl("", "LBL_ADD_CORRECT_DETAIL_TXT");

    }


    public void closeLoader() {
        if (loading_wallet_history.getVisibility() == View.VISIBLE) {
            loading_wallet_history.setVisibility(View.GONE);
        }
    }

    public void generateErrorView() {

        closeLoader();
        generalFunc.generateErrorView(errorView, "", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                getTransactionHistory(false);
            }
        });
    }

    public void getTransactionHistory(final boolean isLoadMore) {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading_wallet_history.getVisibility() != View.VISIBLE && isLoadMore == false) {
            loading_wallet_history.setVisibility(View.VISIBLE);
        }

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDriverBalance");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", CommonUtilities.app_type);
//        parameters.put("TimeZone", generalFunc.getTimezone());
        if (!tv_datini.getText().toString().equals("__/__/___")){
            parameters.put("tStartDate", df2.format(dateIni.getTime()));
        }
        if (!tv_datfin.getText().toString().equals("__/__/___")){
            parameters.put("tEndDate", df2.format(dateFinal.getTime()));
        }
        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {
                    driverBalance = responseString;
                    closeLoader();

                    String LBL_BALANCE = generalFunc.getJsonValue("total", responseString);

                    ((MTextView) findViewById(R.id.yourBalTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_USER_BALANCE"));

                    ((MTextView) findViewById(R.id.walletamountTxt)).setText(NumberFormat.getCurrencyInstance(ptBr).format(Float.valueOf(LBL_BALANCE.replaceAll(",", ""))));

                    String sTotalLoginTime = generalFunc.getJsonValue("totalLoginTime", responseString);
                    String sTotalFinishedTrips = generalFunc.getJsonValue("totalFinishedTrips", responseString);

                    totalLoginTime.setText(sTotalLoginTime);
                    totalFinishedTrips.setText(sTotalFinishedTrips);
                } else {
                    if (isLoadMore == false) {
                        generateErrorView();
                    }
                }

                mIsLoading = false;
            }
        });
        exeWebServer.execute();
    }

    public void getHistory() {

        loading_wallet_history.setVisibility(View.VISIBLE);

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDriverTransactionsHistory");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", CommonUtilities.app_type);
        String str = "__/__/___";

        if (!this.tv_datini.getText().toString().equals(str)){
            parameters.put("tStartDate", df2.format(dateIni.getTime()));
        }
        if (!this.tv_datfin.getText().toString().equals(str)){
            parameters.put("tEndDate", df2.format(dateFinal.getTime()));
        }

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {
                    Bundle bn = new Bundle();
                    bn.putString("getDriverTransactionsHistory", responseString);
                    bn.putSerializable("driverBalance", driverBalance);
                    closeLoader();
                    JSONArray data = generalFunc.getJsonArray("data", responseString);
                    if (data == null || data.toString().equals("[]")){
                        //TODO informar o usuário que não teminformação
                    }else{
                        new StartActProcess(getActContext()).startActWithData(MyWalletHistoryActivity.class, bn);
                    }
                }
                mIsLoading = false;
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return MyWalletActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == btn_type1.getId()) {
                getHistory();
                //new StartActProcess(getActContext()).startAct(MyWalletHistoryActivity.class);
            }
            switch (view.getId()) {
                case R.id.backImgView:
                    onBackPressed();
                    break;
                case R.id.viewTransactionsTxt:
                    new StartActProcess(getActContext()).startAct(MyWalletHistoryActivity.class);
                    break;
                case R.id.tv_datini:
                    datePicker(0);
                    break;
                case R.id.tv_datfin:
                    datePicker(1);
                    break;

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void datePicker(Integer type) {
        final Calendar c = Calendar.getInstance();
        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePickerDialog.OnDateSetListener) (view1, year, monthOfYear, dayOfMonth) -> {
                    if (type == 0) {
                        dateIni.set(Calendar.YEAR, year);
                        dateIni.set(Calendar.MONTH, monthOfYear);
                        dateIni.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (dateIni.before(dateFinal)) {
                            tv_datini.setText(df.format(dateIni.getTime()));
                        } else {
                            Toast.makeText(this, "Data inicial menor que a data final!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        dateFinal.set(Calendar.YEAR, year);
                        dateFinal.set(Calendar.MONTH, monthOfYear);
                        dateFinal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (dateIni.before(dateFinal)) {
                            tv_datfin.setText(df.format(dateFinal.getTime()));
                            getTransactionHistory(false);

                        } else {
                            Toast.makeText(this, "Data inicial menor que a data final!", Toast.LENGTH_LONG).show();
                        }

                    }



                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}

