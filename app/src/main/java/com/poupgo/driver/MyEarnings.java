package com.poupgo.driver;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.functions.Utils;
import com.utils.CommonUtilities;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyEarnings extends AppCompatActivity {
    Locale ptBr = new Locale("pt", "BR");
    MTextView titleTxt;
    GeneralFunctions generalFunc;
    ImageView backImgView;
    String userProfileJson = "";
    MButton btn_det;
    MTextView tv_datini, tv_datfin, total_driver_payment, tv_payment_tot, tv_number_travels;
    LinearLayout container;
    int submitBtnId;
    @SuppressLint("SimpleDateFormat")
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");
    private JSONObject obj_all;
    Calendar dateIni = Calendar.getInstance();
    Calendar dateFinal = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_earnings);
        generalFunc = new GeneralFunctions(getActContext());
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        btn_det = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        tv_datini = findViewById(R.id.tv_datini);
        tv_datfin = findViewById(R.id.tv_datfin);
        container = findViewById(R.id.container);
        container.setVisibility(View.GONE);
        total_driver_payment = findViewById(R.id.total_driver_payment);
        tv_payment_tot = findViewById(R.id.tv_payment_tot);
        tv_number_travels = findViewById(R.id.tv_number_travels);
        submitBtnId = Utils.generateViewId();
        btn_det.setId(submitBtnId);


        backImgView.setOnClickListener(new MyEarnings.setOnClickList());
        btn_det.setOnClickListener(new MyEarnings.setOnClickList());
        tv_datini.setOnClickListener(new MyEarnings.setOnClickList());
        tv_datfin.setOnClickListener(new MyEarnings.setOnClickList());

        setData();
    }

    public void setLabels() {
        titleTxt.setText("Meus Ganhos");
        btn_det.setText("Detalhamento");
        tv_datini.setText(df.format(dateIni.getTime()));
        tv_datfin.setText(df.format(dateFinal.getTime()));
        total_driver_payment.setText(NumberFormat.getCurrencyInstance(ptBr).format(Float.valueOf(generalFunc.getJsonValueStr("total_driver_payment", obj_all).replaceAll("\\D+", "")) / 100));

        tv_number_travels.setText(generalFunc.getJsonValueStr("count", obj_all));

        String total_commission = generalFunc.getJsonValueStr("total_commission", obj_all).replaceAll("\\D+", "");
        String total_outstanding_amount = generalFunc.getJsonValueStr("total_outstanding_amount", obj_all).replaceAll("\\D+", "");
        String total_taxes = generalFunc.getJsonValueStr("total_taxes", obj_all).replaceAll("\\D+", "");

        Float total = Float.valueOf(total_commission) + Float.valueOf(total_outstanding_amount) + Float.valueOf(total_taxes);
        tv_payment_tot.setText(NumberFormat.getCurrencyInstance(ptBr).format(total / 100));
        container.setVisibility(View.VISIBLE);
    }

    public void setData() {
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
        }
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("GeneralMemberId", generalFunc.getMemberId());
        parameters.put("GeneralUserType", CommonUtilities.app_type);
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY));
        parameters.put("startDate", df2.format(dateIni.getTime()));
        parameters.put("endDate", df2.format(dateFinal.getTime()));
        parameters.put("type", "getMyEarnings");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

            String msg = generalFunc.getJsonValue("data", responseString);
            if (isDataAvail) {


                String all = generalFunc.getJsonValue("All", msg);

                obj_all = generalFunc.getJsonObject(all);
                setLabels();

            }
        });
        exeWebServer.execute();

    }

    public Context getActContext() {
        return MyEarnings.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(MyEarnings.this);

            if (i == R.id.backImgView) {
                MyEarnings.super.onBackPressed();
            } else if (i == R.id.tv_datini) {
                datePicker(0);

            } else if (i == R.id.tv_datfin) {
                datePicker(1);

            }
        }
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
                        }else{
                            Toast.makeText(this,"Data inicial menor que a data final!",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        dateFinal.set(Calendar.YEAR, year);
                        dateFinal.set(Calendar.MONTH, monthOfYear);
                        dateFinal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (dateIni.before(dateFinal)) {
                            tv_datfin.setText(df.format(dateFinal.getTime()));
                        }else{
                            Toast.makeText(this,"Data inicial menor que a data final!",Toast.LENGTH_LONG).show();
                        }
                    }


                    if (dateIni.before(dateFinal)) {
                        setData();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
