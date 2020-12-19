package com.poupgo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adapter.files.PackageAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.utilities.general.files.StartActProcess;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RentalDetailsActivity extends AppCompatActivity implements PackageAdapter.setPackageClickList {

    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;
    MTextView addressHtxt, addressVtxt;
    MTextView cabTypeHtxt, minTxt, carTypeVtxt, carTypeDetailsTxt, packageHtxt, packageVtxt, rideLtaerDatetxt;
    ImageView carTypeImage;
    String imgName;
    String vehicleIconPath = CommonUtilities.SERVER_URL + "webimages/icons/VehicleType/";
    RecyclerView packageRecyclerView;

    public ArrayList<HashMap<String, String>> packageList = new ArrayList<>();
    PackageAdapter adapter;

    public MButton acceptBtn;

    MTextView fareTitletxt, fareMsgtxt;

    LinearLayout fareInfoArea;
    int selpos = 0;
    LinearLayout pkgArrow;
    ImageView imageArrow;
    String page_desc;
    String vehicle_list_title = "";

    View pkgDivideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_details);

        generalFunc = new GeneralFunctions(getActContext());


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        addressHtxt = (MTextView) findViewById(R.id.addressHtxt);
        addressVtxt = (MTextView) findViewById(R.id.addressVtxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        cabTypeHtxt = (MTextView) findViewById(R.id.cabTypeHtxt);
        packageHtxt = (MTextView) findViewById(R.id.packageHtxt);
        packageVtxt = (MTextView) findViewById(R.id.packageVtxt);
        imageArrow = (ImageView) findViewById(R.id.imageArrow);
        minTxt = (MTextView) findViewById(R.id.minTxt);
        carTypeVtxt = (MTextView) findViewById(R.id.carTypeVtxt);
        fareTitletxt = (MTextView) findViewById(R.id.fareTitletxt);
        fareMsgtxt = (MTextView) findViewById(R.id.fareMsgtxt);
        carTypeDetailsTxt = (MTextView) findViewById(R.id.carTypeDetailsTxt);
        carTypeImage = (ImageView) findViewById(R.id.carTypeImage);
        packageRecyclerView = (RecyclerView) findViewById(R.id.packageRecyclerView);
        fareInfoArea = (LinearLayout) findViewById(R.id.fareInfoArea);
        pkgArrow = (LinearLayout) findViewById(R.id.pkgArrow);
        fareInfoArea.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());

        rideLtaerDatetxt = (MTextView) findViewById(R.id.rideLtaerDatetxt);

        acceptBtn = ((MaterialRippleLayout) findViewById(R.id.acceptBtn)).getChildView();
        acceptBtn.setId(Utils.generateViewId());
        acceptBtn.setOnClickListener(new setOnClickList());
        pkgArrow.setOnClickListener(new setOnClickList());
        setLabel();
        getPackageDetails();

        pkgDivideView = (View) findViewById(R.id.pkgDivideView);

        packageVtxt.setVisibility(View.GONE);
        pkgDivideView.setVisibility(View.GONE);
        imageArrow.setVisibility(View.GONE);
    }

    public void setLabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_A_CAR"));
        addressHtxt.setText(generalFunc.convertNumberWithRTL("1") + ". " + generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_TXT"));
        cabTypeHtxt.setText(generalFunc.convertNumberWithRTL("2") + ". " + generalFunc.retrieveLangLBl("", "LBL_CAB_TYPE_HEADER_TXT"));
        packageHtxt.setText(generalFunc.convertNumberWithRTL("3") + ". " + generalFunc.retrieveLangLBl("", "LBL_SELECT_PACKAGE_TXT"));
        acceptBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_CONFIRM"));
        fareTitletxt.setText(generalFunc.retrieveLangLBl("", "LBL_FARE_DETAILS_AND_RULES_TXT"));
        fareMsgtxt.setText(generalFunc.retrieveLangLBl("", "LBL_FARE_DETAILS_DESCRIPTION_TXT"));


        carTypeVtxt.setText(getIntent().getStringExtra("vVehicleType"));

        if (getIntent().getStringExtra("selectedTime") != null && !getIntent().getStringExtra("selectedTime").equalsIgnoreCase("")) {
            rideLtaerDatetxt.setText(getIntent().getStringExtra("selectedTime"));
            minTxt.setVisibility(View.INVISIBLE);
            rideLtaerDatetxt.setVisibility(View.VISIBLE);
        }
        addressVtxt.setText(getIntent().getStringExtra("address"));

        imgName = getImageName(getIntent().getStringExtra("vLogo"));
        String imgUrl = vehicleIconPath + getIntent().getStringExtra("iVehicleTypeId") + "/android/" + imgName;


        Picasso.with(getActContext())
                .load(imgUrl)
                .error(R.mipmap.ic_car_lux)
                .placeholder(R.mipmap.ic_car_lux)
                .into(carTypeImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

    }


    public void getPackageDetails() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getRentalPackages");
        parameters.put("GeneralMemberId", generalFunc.getMemberId());
        parameters.put("iVehicleTypeId", getIntent().getStringExtra("iVehicleTypeId"));
        parameters.put("UserType", Utils.userType);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

                        page_desc = generalFunc.getJsonValue("page_desc", responseString);
                        vehicle_list_title = generalFunc.getJsonValue("vehicle_list_title", responseString);

                        JSONArray vehicleTypesArr = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);
                        for (int i = 0; i < vehicleTypesArr.length(); i++) {

                            JSONObject obj_temp = generalFunc.getJsonObject(vehicleTypesArr, i);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("iRentalPackageId", generalFunc.getJsonValue("iRentalPackageId", obj_temp.toString()));
                            map.put("vPackageName", generalFunc.getJsonValue("vPackageName", obj_temp.toString()));
                            map.put("fPrice", generalFunc.getJsonValue("fPrice", obj_temp.toString()));
                            map.put("fKiloMeter", generalFunc.getJsonValue("fKiloMeter", obj_temp.toString()));
                            map.put("fHour", generalFunc.getJsonValue("fHour", obj_temp.toString()));
                            map.put("fPricePerKM", generalFunc.getJsonValue("fPricePerKM", obj_temp.toString()));
                            map.put("fPricePerHour", generalFunc.getJsonValue("fPricePerHour", obj_temp.toString()));
                            packageList.add(map);
                        }


                        if (packageList.size() > 0) {
                            adapter = new PackageAdapter(getActContext(), packageList);
                            adapter.itemPackageClick(RentalDetailsActivity.this);
                            packageRecyclerView.setAdapter(adapter);
                            packageVtxt.setText(packageList.get(0).get("vPackageName") + " - " + packageList.get(0).get("fPrice"));
                        }

                        carTypeDetailsTxt.setText(vehicle_list_title);


                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


    private String getImageName(String vLogo) {
        String imageName = "";

        if (vLogo.equals("")) {
            return vLogo;
        }

        DisplayMetrics metrics = (getActContext().getResources().getDisplayMetrics());
        int densityDpi = (int) (metrics.density * 160f);
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                imageName = "mdpi_" + vLogo;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                imageName = "mdpi_" + vLogo;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                imageName = "hdpi_" + vLogo;
                break;

            case DisplayMetrics.DENSITY_TV:
                imageName = "hdpi_" + vLogo;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                imageName = "xhdpi_" + vLogo;
                break;

            case DisplayMetrics.DENSITY_280:
                imageName = "xhdpi_" + vLogo;
                break;

            case DisplayMetrics.DENSITY_400:
                imageName = "xxhdpi_" + vLogo;
                break;

            case DisplayMetrics.DENSITY_360:
                imageName = "xxhdpi_" + vLogo;
                break;
            case DisplayMetrics.DENSITY_420:
                imageName = "xxhdpi_" + vLogo;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                imageName = "xxhdpi_" + vLogo;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                imageName = "xxxhdpi_" + vLogo;
                break;

            case DisplayMetrics.DENSITY_560:
                imageName = "xxxhdpi_" + vLogo;
                break;

            default:
                imageName = "xxhdpi_" + vLogo;
                break;
        }

        return imageName;
    }

    public Context getActContext() {
        return RentalDetailsActivity.this;
    }

    @Override
    public void itemPackageClick(int position) {
        selpos = position;
        adapter.selPos(selpos);
        adapter.notifyDataSetChanged();
        packageVtxt.setText(packageList.get(selpos).get("vPackageName") + " - " + packageList.get(selpos).get("fPrice"));
        packageRecyclerView.setVisibility(View.GONE);
        imageArrow.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_down));

        packageVtxt.setVisibility(View.VISIBLE);
        pkgDivideView.setVisibility(View.VISIBLE);
        imageArrow.setVisibility(View.VISIBLE);

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == backImgView.getId()) {
                onBackPressed();
            } else if (i == acceptBtn.getId()) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("iRentalPackageId", packageList.get(selpos).get("iRentalPackageId"));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            } else if (i == fareInfoArea.getId()) {

                Bundle bn = new Bundle();
                HashMap<String, String> map = packageList.get(selpos);
                map.put("vVehicleType", getIntent().getStringExtra("vVehicleType"));
                map.put("page_desc", page_desc);
                bn.putSerializable("data", map);


                new StartActProcess(getActContext()).startActWithData(RentalInfoActivity.class, bn);

            } else if (i == pkgArrow.getId()) {
                if (packageRecyclerView.getVisibility() == View.VISIBLE) {
                    packageRecyclerView.setVisibility(View.GONE);
                    imageArrow.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_down));
                } else {
                    packageRecyclerView.setVisibility(View.VISIBLE);
                    imageArrow.setImageDrawable(getResources().getDrawable(R.mipmap.ic_arrow_up));
                }
            }
        }
    }
}
