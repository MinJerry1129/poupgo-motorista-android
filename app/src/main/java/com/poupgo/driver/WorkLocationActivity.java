package com.poupgo.driver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.ExecuteWebServerUrl;
import com.general.functions.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.utilities.general.files.StartActProcess;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkLocationActivity extends AppCompatActivity implements GetAddressFromLocation.AddressFound, GetLocationUpdates.LocationUpdatesListener {


    GeneralFunctions generalFunc;

    MTextView titleTxt;
    ImageView backImgView;

    FrameLayout locWorkSelectArea;
    String userProfileJson;
    ArrayList<String> items_work_location = new ArrayList<String>();
    ArrayList<String> real_items_work_location = new ArrayList<String>();
    ArrayList<HashMap<String, String>> items_work_radius = new ArrayList<HashMap<String, String>>();
    ArrayList<String> real_items_work_radius = new ArrayList<String>();
    MaterialEditText locationWorkBox, radiusWorkBox, otherBox;
    String selected_work_location = "";
    String selected_work_radius = "";
    android.support.v7.app.AlertDialog list_work_location;
    android.support.v7.app.AlertDialog list_work_radius;
    MTextView addressTxt, workradiusTitleTxt, workLocTitleTxt;
    GetAddressFromLocation getAddressFromLocation;

    Location location;
    ImageView editLocation;
    String eSelectWorkLocation = "";
    String vCountryUnitDriver = "";
    LinearLayout otherArea;
    MButton btn_type2;
    int submitBtnId;
    String required_str;

    MTextView demonoteText, demoText;
    MTextView noteText, noteDetailsText;
    LinearLayout workLocationArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_location);

        generalFunc = new GeneralFunctions(getActContext());
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
        getAddressFromLocation.setAddressList(this);
        initViews();
        getDetails();
    }


    public void handleWorkAddress()

    {
        if (generalFunc.getJsonValue("PROVIDER_AVAIL_LOC_CUSTOMIZE", userProfileJson).equalsIgnoreCase("Yes")) {
            if (eSelectWorkLocation.equalsIgnoreCase("")) {
                return;
            }

            if (eSelectWorkLocation.equalsIgnoreCase("Fixed")) {
                editLocation.setVisibility(View.VISIBLE);

                if (!generalFunc.retrieveValue(CommonUtilities.WORKLOCATION).equals("")) {
                    addressTxt.setText(generalFunc.retrieveValue(CommonUtilities.WORKLOCATION));
                } else {
                    if (location != null) {
                        getAddressFromLocation.setLocation(location.getLatitude(), location.getLongitude());
                        getAddressFromLocation.execute();
                    }
                }
            } else {
                editLocation.setVisibility(View.GONE);
                if (location != null) {
                    getAddressFromLocation.setLocation(location.getLatitude(), location.getLongitude());
                    getAddressFromLocation.execute();
                }
            }
        } else {
            editLocation.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleWorkAddress();

    }

    public void setLabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANAGE_WORK_LOCATION"));

        addressTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));
        workradiusTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RADIUS"));
        workLocTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_YOUR_JOB_LOCATION_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        demonoteText.setText(generalFunc.retrieveLangLBl("", "LBL_NOTE") + ":");
        demoText.setText(generalFunc.retrieveLangLBl("", "LBL_WORK_LOCATION_NOTE"));
        noteText.setText(generalFunc.retrieveLangLBl("", "LBL_NOTE"));

    }

    public void initViews() {

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView.setOnClickListener(new setOnClickList());
        locWorkSelectArea = (FrameLayout) findViewById(R.id.locWorkSelectArea);
        locationWorkBox = (MaterialEditText) findViewById(R.id.locationWorkBox);
        otherBox = (MaterialEditText) findViewById(R.id.otherBox);
        workradiusTitleTxt = (MTextView) findViewById(R.id.workradiusTitleTxt);
        workLocTitleTxt = (MTextView) findViewById(R.id.workLocTitleTxt);
        demonoteText = (MTextView) findViewById(R.id.demonoteText);
        noteDetailsText = (MTextView) findViewById(R.id.noteDetailsText);
        workLocationArea = (LinearLayout) findViewById(R.id.workLocationArea);
        noteText = (MTextView) findViewById(R.id.noteText);
        demoText = (MTextView) findViewById(R.id.demoText);
        otherBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        otherBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        otherBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_ENTER_RADIUS_HINT"));
        if (userProfileJson != null && generalFunc.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
            otherBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_ENTER_RADIUS_PER_KMS"));
        } else {
            otherBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_ENTER_RADIUS_PER_MILE"));
        }


        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_SUBMIT_TXT"));

        radiusWorkBox = (MaterialEditText) findViewById(R.id.radiusWorkBox);
        addressTxt = (MTextView) findViewById(R.id.addressTxt);
        editLocation = (ImageView) findViewById(R.id.editLocation);
        otherArea = (LinearLayout) findViewById(R.id.otherArea);


        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());


        setLabel();

        locationWorkBox.getLabelFocusAnimator().start();
        radiusWorkBox.getLabelFocusAnimator().start();

        Utils.removeInput(locationWorkBox);
        Utils.removeInput(radiusWorkBox);
        locationWorkBox.setOnTouchListener(new setOnTouchList());
        radiusWorkBox.setOnTouchListener(new setOnTouchList());
        locationWorkBox.setOnClickListener(new setOnClickList());
        radiusWorkBox.setOnClickListener(new setOnClickList());
        editLocation.setOnClickListener(new setOnClickList());


        if (generalFunc.getJsonValue("PROVIDER_AVAIL_LOC_CUSTOMIZE", userProfileJson).equalsIgnoreCase("yes")) {
            items_work_location.add(generalFunc.retrieveLangLBl("Specified Location", "LBL_SPECIFIED_LOCATION"));
            items_work_location.add(generalFunc.retrieveLangLBl("Any Location", "LBL_ANY_LOCATION"));
            real_items_work_location.add("Fixed");
            real_items_work_location.add("Dynamic");
            workLocationArea.setVisibility(View.VISIBLE);

            noteDetailsText.setText(generalFunc.retrieveLangLBl("", "LBL_INFO_WORK_LOCATION") + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_INFO_WORK_RADIUS"));

        } else {
            workLocationArea.setVisibility(View.GONE);
            noteDetailsText.setText(generalFunc.retrieveLangLBl("", "LBL_INFO_WORK_RADIUS"));
        }


        if (generalFunc.getJsonValue("eSelectWorkLocation", userProfileJson) != null && !generalFunc.getJsonValue("eSelectWorkLocation", userProfileJson).equalsIgnoreCase("")) {
            selected_work_location = generalFunc.getJsonValue("eSelectWorkLocation", userProfileJson);

            if (selected_work_location.equalsIgnoreCase("Fixed")) {
                locationWorkBox.setText(generalFunc.retrieveLangLBl("Specified Location", "LBL_SPECIFIED_LOCATION"));
            } else {
                locationWorkBox.setText(generalFunc.retrieveLangLBl("Any Location", "LBL_ANY_LOCATION"));
            }
        }
        GetLocationUpdates.getInstance().startLocationUpdates(this, this);


    }


    public void updateuserRadius(final String val) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateRadius");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("vWorkLocationRadius", val);


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


                        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                        generateAlert.setCancelable(false);
                        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                            @Override
                            public void handleBtnClick(int btn_id) {
                                generateAlert.closeAlertBox();

                                //setRadiusVal();
                                otherArea.setVisibility(View.GONE);
                                otherBox.setText("");
                                getDetails();


                            }
                        });
//                        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("Radius Added Successfully.", "LBL_RADIUS_ADDED_SUCESS_MSG")));
                        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message1", responseString)));
                        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));

                        generateAlert.showAlertBox();


                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onAddressFound(String address, double latitude, double longitude) {

        if (generalFunc.getJsonValue("PROVIDER_AVAIL_LOC_CUSTOMIZE", userProfileJson).equalsIgnoreCase("Yes") && generalFunc.getJsonValue("eSelectWorkLocation", userProfileJson).equalsIgnoreCase("Fixed")) {
            if (!generalFunc.retrieveValue(CommonUtilities.WORKLOCATION).equals("")) {
                addressTxt.setText(generalFunc.retrieveValue(CommonUtilities.WORKLOCATION));
            } else {
                addressTxt.setText(address);
            }
        } else {
            addressTxt.setText(address);
        }

    }

    @Override
    public void onLocationUpdate(Location location) {
        this.location = location;

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

            if (view.getId() == submitBtnId) {
                if (otherBox.getText().toString().length() > 0) {

                    if (GeneralFunctions.parseIntegerValue(0, otherBox.getText().toString()) > 0) {
                        updateuserRadius(otherBox.getText().toString());
                    } else {
                        Utils.setErrorFields(otherBox, generalFunc.retrieveLangLBl("", "LBL_FILL_PROPER_DETAILS"));
                    }
                } else {
                    Utils.setErrorFields(otherBox, required_str);
                }

            }

            switch (view.getId()) {
                case R.id.backImgView:
                    WorkLocationActivity.super.onBackPressed();
                    break;

                case R.id.locationWorkBox:
                    buildLocationWorkList();
                    break;

                case R.id.radiusWorkBox:
                    buildWorkRadiusList();
                    break;

                case R.id.editLocation:
                    Bundle bn = new Bundle();
                    bn.putString("locationArea", "dest");
                    if (location != null) {
                        bn.putDouble("lat", location.getLatitude());
                        bn.putDouble("long", location.getLongitude());
                    }
                    new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class, bn, Utils.SEARCH_PICKUP_LOC_REQ_CODE);
                    break;


            }
        }
    }

    public void buildLocationWorkList()

    {


        CharSequence[] wk_location_txt = items_work_location.toArray(new CharSequence[items_work_location.size()]);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_WORKLOCATION"));

        builder.setItems(wk_location_txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                if (list_work_location != null) {
                    list_work_location.dismiss();
                }
                selected_work_location = real_items_work_location.get(item);
                //locationWorkBox.setText(items_work_location.get(item));

                if (selected_work_location.equalsIgnoreCase("Fixed")) {
                    if (generalFunc.retrieveValue(CommonUtilities.WORKLOCATION).equals("")) {
                        Bundle bn = new Bundle();
                        bn.putString("locationArea", "dest");
                        if (location != null) {
                            bn.putDouble("lat", location.getLatitude());
                            bn.putDouble("long", location.getLongitude());
                        }
                        new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class, bn, Utils.SEARCH_PICKUP_LOC_REQ_CODE);

                        return;

                    }
                    locationWorkBox.setText(generalFunc.retrieveLangLBl("Specified Location", "LBL_SPECIFIED_LOCATION"));
                } else {
                    locationWorkBox.setText(generalFunc.retrieveLangLBl("Any Location", "LBL_ANY_LOCATION"));
                }

                updateWorkLocationSelection();

            }
        });

        list_work_location = builder.create();

        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(list_work_location);
        }

        list_work_location.show();


    }

    android.support.v7.app.AlertDialog alertDialog;


    public void buildWorkRadiusList() {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < items_work_radius.size(); i++) {
            items.add(generalFunc.convertNumberWithRTL(items_work_radius.get(i).get("value")) + " " + items_work_radius.get(i).get("eUnit"));
        }

        CharSequence[] wk_location_txt = items.toArray(new CharSequence[items.size()]);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_RADIUS"));

        builder.setItems(wk_location_txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                if (list_work_radius != null) {
                    list_work_radius.dismiss();
                }

                if (items_work_radius.get(item).get("name").equalsIgnoreCase("other")) {
                    radiusWorkBox.setText(items_work_radius.get(item).get("name"));
                    otherArea.setVisibility(View.VISIBLE);
                    return;
                }
                otherArea.setVisibility(View.GONE);
                selected_work_radius = items_work_radius.get(item).get("name");
                radiusWorkBox.setText(items.get(item));

                updateuserRadius(selected_work_radius);

            }
        });

        list_work_radius = builder.create();

        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(list_work_radius);
        }

        list_work_radius.show();


    }

    public void getDetails() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDriverWorkLocationUFX");
        parameters.put("iDriverId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        items_work_radius.clear();

                        String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                        eSelectWorkLocation = generalFunc.getJsonValue("eSelectWorkLocation", message);
                        selected_work_location = eSelectWorkLocation;
                        vCountryUnitDriver = generalFunc.getJsonValue("vCountryUnitDriver", message);

                        generalFunc.storedata(CommonUtilities.WORKLOCATION, generalFunc.getJsonValue("vWorkLocation", message));

                        JSONArray radiusArray = generalFunc.getJsonArray("RadiusList", message);

                        for (int i = 0; i < radiusArray.length(); i++) {
                            JSONObject jsonObject = generalFunc.getJsonObject(radiusArray, i);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("value", generalFunc.getJsonValue("value", jsonObject.toString()));
                            map.put("name", generalFunc.getJsonValue("value", jsonObject.toString()));
                            map.put("eUnit", generalFunc.getJsonValue("eUnit", jsonObject.toString()));
                            map.put("eSelected", generalFunc.getJsonValue("eSelected", jsonObject.toString()));

                            items_work_radius.add(map);
                        }

                        HashMap<String, String> map = new HashMap<>();
                        map.put("value", "Other");
                        map.put("name", generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
                        map.put("eUnit", "");
                        map.put("eSelected", "");

                        items_work_radius.add(map);


                        handleWorkRadius();
                        handleWorkAddress();

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


    public void handleWorkRadius() {
        if (items_work_radius != null) {

            for (int i = 0; i < items_work_radius.size(); i++) {
                if (items_work_radius.get(i).get("eSelected").equalsIgnoreCase("Yes")) {
                    selected_work_radius = items_work_radius.get(i).get("value");
                    radiusWorkBox.setText(selected_work_radius + " " + items_work_radius.get(i).get("eUnit"));

                }
            }

        }
    }

    public void updateWorkLocationSelection() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateDriverWorkLocationSelectionUFX");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("eSelectWorkLocation", selected_work_location);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str_one, responseString)));
                        String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, message);
                        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
                        eSelectWorkLocation = selected_work_location;
                        handleWorkAddress();

                    } else {

                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();

    }

    public void updateWorkLocation(String worklat, String worklong, String workaddress) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateDriverWorkLocationUFX");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("vWorkLocationLatitude", worklat);
        parameters.put("vWorkLocationLongitude", worklong);
        parameters.put("vWorkLocation", workaddress);
        if (generalFunc.retrieveValue(CommonUtilities.WORKLOCATION).equals("")) {
            parameters.put("eSelectWorkLocation", eSelectWorkLocation);
        }
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        if (generalFunc.retrieveValue(CommonUtilities.WORKLOCATION).equals("")) {
                            eSelectWorkLocation = "Fixed";
                            parameters.put("eSelectWorkLocation", eSelectWorkLocation);
                            locationWorkBox.setText(generalFunc.retrieveLangLBl("Specified Location", "LBL_SPECIFIED_LOCATION"));
                        }
                        addressTxt.setText(workaddress);
                        generalFunc.storedata(CommonUtilities.WORKLOCATION, workaddress);
                        handleWorkAddress();


                    } else {

                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                String worklat = data.getStringExtra("Latitude");
                String worklong = data.getStringExtra("Longitude");
                String workadddress = data.getStringExtra("Address");

                updateWorkLocation(worklat, worklong, workadddress);


            }
        }
    }

    public Context getActContext() {
        return WorkLocationActivity.this;
    }

}
