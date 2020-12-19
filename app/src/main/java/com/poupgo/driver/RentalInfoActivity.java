package com.poupgo.driver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.general.functions.GeneralFunctions;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.view.MTextView;

import java.util.HashMap;

public class RentalInfoActivity extends AppCompatActivity {

    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;
    MTextView baseFareHTxt, baseFareVTxt, baseFareInfotxt, addKMFareHTxt,
            addKMFareVTxt, addKmFareInfoTxt, addTimeFareHTxt, addTimeFareVTxt, addTimeFareInfoTxt;

    String userProfileJson;
    HashMap<String, String> data;
    MTextView noteTitleTxt;
    WebView noteMsgTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_info);

        generalFunc = new GeneralFunctions(getActContext());
        data = (HashMap<String, String>) getIntent().getSerializableExtra("data");
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());

        baseFareHTxt = (MTextView) findViewById(R.id.baseFareHTxt);
        baseFareVTxt = (MTextView) findViewById(R.id.baseFareVTxt);
        baseFareInfotxt = (MTextView) findViewById(R.id.baseFareInfotxt);
        addKMFareHTxt = (MTextView) findViewById(R.id.addKMFareHTxt);
        addKMFareVTxt = (MTextView) findViewById(R.id.addKMFareVTxt);
        addKmFareInfoTxt = (MTextView) findViewById(R.id.addKmFareInfoTxt);
        addTimeFareHTxt = (MTextView) findViewById(R.id.addTimeFareHTxt);
        addTimeFareVTxt = (MTextView) findViewById(R.id.addTimeFareVTxt);
        addTimeFareInfoTxt = (MTextView) findViewById(R.id.addTimeFareInfoTxt);
        noteTitleTxt = (MTextView) findViewById(R.id.noteTitleTxt);
        noteMsgTxt = (WebView) findViewById(R.id.noteMsgTxt);


        setLabel();
    }


    public void setLabel() {
        baseFareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENTAL_FARE_TXT"));
        baseFareVTxt.setText(data.get("fPrice"));

        if (generalFunc.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
            baseFareInfotxt.setText(generalFunc.retrieveLangLBl("", "LBL_INCLUDES") + " " + data.get("fHour") + " "
                    + generalFunc.retrieveLangLBl("", "LBL_HOURS_TXT") + " " + data.get("fKiloMeter") + " "
                    + generalFunc.retrieveLangLBl("", "LBL_DISPLAY_KMS"));
        } else {
            baseFareInfotxt.setText(generalFunc.retrieveLangLBl("", "LBL_INCLUDES") + " " + data.get("fHour") + " "
                    + generalFunc.retrieveLangLBl("", "LBL_HOURS_TXT") + " " + data.get("fKiloMeter") + " "
                    + generalFunc.retrieveLangLBl("", "LBL_MILE_DISTANCE_TXT"));

        }

        addKMFareVTxt.setText(data.get("fPricePerKM"));


        if (generalFunc.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
            addKMFareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_FARE"));
            addKmFareInfoTxt.setText(generalFunc.retrieveLangLBl("", "LBL_AFTER_FIRST") + " " + data.get("fKiloMeter") + " "
                    + generalFunc.retrieveLangLBl("", "LBL_DISPLAY_KMS"));
        } else {
            addKMFareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_MILES_FARE"));
            addKmFareInfoTxt.setText(generalFunc.retrieveLangLBl("", "LBL_AFTER_FIRST") + " " + data.get("fKiloMeter") +
                    " " + generalFunc.retrieveLangLBl("", "LBL_MILE_DISTANCE_TXT"));

        }

        addTimeFareVTxt.setText(data.get("fPricePerHour"));


        addTimeFareInfoTxt.setText(generalFunc.retrieveLangLBl("", "LBL_AFTER_FIRST") + " " + data.get("fHour") + " "
                + generalFunc.retrieveLangLBl("", "LBL_HOURS_TXT"));
        addTimeFareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_RIDE_TIME_FARE"));

        noteTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NOTE") + ":");

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_A_TXT") + " " + data.get("vVehicleType"));

        // noteMsgTxt.setText(Html.fromHtml(generalFunc.wrapHtml(getActContext(), data.get("page_desc"))));

        noteMsgTxt.loadDataWithBaseURL(null, generalFunc.wrapHtml(noteMsgTxt.getContext(), "<font color='gray'>" + data.get("page_desc")), "text/html", "UTF-8", null);

    }

    public Context getActContext() {
        return RentalInfoActivity.this;
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == backImgView.getId()) {
                onBackPressed();
            }
        }
    }
}
