package com.poupgo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.adapter.files.DrawerAdapter;
import com.adapter.files.ManageVehicleListAdapter;
import com.fragments.InactiveFragment;
import com.general.files.BackgroundAppReceiver;
import com.general.files.ConfigPubNub;
import com.general.files.DividerItemDecoration;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.FireTripStatusMsg;
import com.general.files.LocalNotification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.general.functions.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.functions.InternetConnection;
import com.general.files.MyApp;
import com.utilities.general.files.StartActProcess;
import com.general.files.UpdateDriverStatus;
import com.utilities.general.files.UpdateFrequentTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.kyleduo.switchbutton.SwitchButton;
import com.pubnub.api.enums.PNStatusCategory;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.CreateRoundedView;
import com.general.functions.GenerateAlertBox;
import com.utils.CustomChildEventListener;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, OnMapReadyCallback,GetLocationUpdates.LocationUpdatesListener, GoogleMap.OnCameraChangeListener, UpdateFrequentTask.OnTaskRunCalled,
        ManageVehicleListAdapter.OnItemClickList, GetAddressFromLocation.AddressFound {

    public GeneralFunctions generalFunc;
    public DrawerLayout mDrawerLayout;
    public String userProfileJson = "";
    public Location userLocation;

    MTextView titleTxt;
    ImageView menuImgView;
    ListView menuListView;
    DrawerAdapter drawerAdapter;
    ArrayList<String[]> list_menu_items;
    SupportMapFragment map;
    GoogleMap gMap;
    boolean isFirstLocation = true;
    ImageView userLocBtnImgView;
    ImageView userHeatmapBtnImgView;


    MTextView onlineOfflineTxtView;
    MTextView ufxonlineOfflineTxtView, ufxTitleonlineOfflineTxtView;
    MTextView carNumPlateTxt;
    MTextView carNameTxt;
    MTextView changeCarTxt;
    MTextView addressTxtView, addressTxtViewufx;
    SwitchButton onlineOfflineSwitch, ufxonlineOfflineSwitch;
    ImageView refreshImgView;

    public boolean isDriverOnline = false;

    Intent startUpdatingStatus;

    String radiusval = "0";

    ArrayList<String> items_txt_car = new ArrayList<String>();
    ArrayList<String> items_txt_car_json = new ArrayList<String>();
    ArrayList<String> items_isHail_json = new ArrayList<String>();
    ArrayList<String> items_car_id = new ArrayList<String>();

    android.support.v7.app.AlertDialog list_car;
    android.support.v7.app.AlertDialog gender;

    MTextView joblocHTxtView, joblocHTxtViewufx;

    boolean isOnlineAvoid = false;

    String assignedTripId = "";
    String ENABLE_HAIL_RIDES = "";

    GetAddressFromLocation getAddressFromLocation;

    ExecuteWebServerUrl heatMapAsyncTask;
    HashMap<String, String> onlinePassengerLocList = new HashMap<String, String>();
    HashMap<String, String> historyLocList = new HashMap<String, String>();
    ArrayList<TileOverlay> mapOverlayList = new ArrayList<>();

    double radius_map = 0;

    Boolean isShowNearByPassengers = false;

    String app_type = "Ride";
    int currentRequestPositions = 0;
    UpdateFrequentTask updateRequest;
    BackgroundAppReceiver bgAppReceiver;

    boolean isCurrentReqHandled = false;

    LinearLayout left_linear;

    ImageView imgSetting;

    LinearLayout logoutarea;
    ImageView logoutimage;
    MTextView logoutTxt;
    public String selectedcar = "";

    LinearLayout mapbottomviewarea;
    RelativeLayout mapviewarea;
    boolean iswallet = false;

    SelectableRoundedImageView hileimagview;

    InternetConnection intCheck;
    boolean isrefresh = false;

    private String getState = "GPS";
    ImageView menuufxImgView;

    RelativeLayout rideviewarea, ufxarea;
    MTextView ufxDrivername;

    boolean isFirstAddressLoaded = false;
    RelativeLayout pendingarea, upcomginarea;

    MTextView pendingjobHTxtView, pendingjobValTxtView, upcomingjobHTxtView, upcomingjobValTxtView;
    LinearLayout pendingMainArea;
    LinearLayout botomarea;
    MTextView radiusTxtView, radiusTxtViewufx;
    ImageView imageradius, headerLogo, imageradiusufx;

    RelativeLayout activearea;
    private JSONObject obj_userProfile;

    String HailEnableOnDriverStatus = "";


    boolean isBtnClick = false;

    boolean isCarChangeTxt = false;
    LinearLayout joblocareaufx;
    LinearLayout workArea;
    View workAreaLine;
    MTextView workTxt;
    MTextView btn_edit;
    boolean isfirstZoom = false;
    Location lastPublishedLoc = null;
    double PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = 5;

    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 123;

    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generalFunc = new GeneralFunctions(getActContext());
        intCheck = new InternetConnection(this);
        isCarChangeTxt = true;
        askForSystemOverlayPermission();

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);
        app_type = generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile);

        PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = GeneralFunctions.parseDoubleValue(5, generalFunc.retrieveValue(Utils.PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT));

        getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
        getAddressFromLocation.setAddressList(this);


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        refreshImgView = (ImageView) findViewById(R.id.refreshImgView);
        menuImgView = (ImageView) findViewById(R.id.menuImgView);
        pendingarea = (RelativeLayout) findViewById(R.id.pendingarea);
        upcomginarea = (RelativeLayout) findViewById(R.id.upcomginarea);
        pendingarea.setOnClickListener(new setOnClickList());
        upcomginarea.setOnClickListener(new setOnClickList());
        rideviewarea = (RelativeLayout) findViewById(R.id.rideviewarea);
        pendingjobHTxtView = (MTextView) findViewById(R.id.pendingjobHTxtView);
        pendingjobValTxtView = (MTextView) findViewById(R.id.pendingjobValTxtView);
        upcomingjobHTxtView = (MTextView) findViewById(R.id.upcomingjobHTxtView);
        upcomingjobValTxtView = (MTextView) findViewById(R.id.upcomingjobValTxtView);
        radiusTxtView = (MTextView) findViewById(R.id.radiusTxtView);
        radiusTxtViewufx = (MTextView) findViewById(R.id.radiusTxtViewufx);
        imageradius = (ImageView) findViewById(R.id.imageradius);
        imageradiusufx = (ImageView) findViewById(R.id.imageradiusufx);
        headerLogo = (ImageView) findViewById(R.id.headerLogo1);
        activearea = (RelativeLayout) findViewById(R.id.activearea);
        joblocareaufx = (LinearLayout) findViewById(R.id.joblocareaufx);
        workArea = (LinearLayout) findViewById(R.id.workArea);
        workAreaLine = (View) findViewById(R.id.workAreaLine);
        workTxt = (MTextView) findViewById(R.id.workTxt);
        btn_edit = (MTextView) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new setOnClickList());


        radiusTxtView.setOnClickListener(new setOnClickList());
        radiusTxtViewufx.setOnClickListener(new setOnClickList());

        imageradius.setOnClickListener(new setOnClickList());
        imageradiusufx.setOnClickListener(new setOnClickList());
        refreshImgView.setOnClickListener(new setOnClickList());
        ufxarea = (RelativeLayout) findViewById(R.id.ufxarea);

        rideviewarea.setVisibility(View.VISIBLE);
        ufxarea.setVisibility(View.GONE);

        menuListView = (ListView) findViewById(R.id.menuListView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        userLocBtnImgView = (ImageView) findViewById(R.id.userLocBtnImgView);
        userHeatmapBtnImgView = (ImageView) findViewById(R.id.userHeatmapBtnImgView);
        menuufxImgView = (ImageView) findViewById(R.id.menuufxImgView);
        joblocHTxtView = (MTextView) findViewById(R.id.joblocHTxtView);
        joblocHTxtViewufx = (MTextView) findViewById(R.id.joblocHTxtViewufx);
        addressTxtView = (MTextView) findViewById(R.id.addressTxtView);
        addressTxtViewufx = (MTextView) findViewById(R.id.addressTxtViewufx);
        menuufxImgView.setOnClickListener(new setOnClickList());
        ufxDrivername = (MTextView) findViewById(R.id.ufxDrivername);
        pendingMainArea = (LinearLayout) findViewById(R.id.pendingMainArea);
        botomarea = (LinearLayout) findViewById(R.id.botomarea);

        pendingjobHTxtView.setText(generalFunc.retrieveLangLBl("Pending Jobs", "LBL_PENDING_JOBS"));
        upcomingjobHTxtView.setText(generalFunc.retrieveLangLBl("Upcoming Jobs", "LBL_UPCOMING_JOBS"));

        joblocHTxtView.setText(generalFunc.retrieveLangLBl("Your Job Location", "LBL_YOUR_JOB_LOCATION_TXT"));
        joblocHTxtViewufx.setText(generalFunc.retrieveLangLBl("Your Job Location", "LBL_YOUR_JOB_LOCATION_TXT"));
        addressTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));
        addressTxtViewufx.setText(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));
        btn_edit.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT"));
        workTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT_WORK_LOCATION"));
        handleWorkAddress();

        hileimagview = (SelectableRoundedImageView) findViewById(R.id.hileImageview);
        hileimagview.setOnClickListener(new setOnClickList());

        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 35), 2,
                getActContext().getResources().getColor(R.color.appThemeColor_1), hileimagview);

        hileimagview.setColorFilter(getActContext().getResources().getColor(R.color.white));


        mapviewarea = (RelativeLayout) findViewById(R.id.mapviewarea);
        mapbottomviewarea = (LinearLayout) findViewById(R.id.mapbottomviewarea);


        logoutarea = (LinearLayout) findViewById(R.id.logoutarea);
        logoutimage = (ImageView) findViewById(R.id.logoutimage);
        logoutTxt = (MTextView) findViewById(R.id.logoutTxt);
        logoutTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGNOUT_TXT"));
        logoutarea.setOnClickListener(new setOnClickList());

        left_linear = (LinearLayout) findViewById(R.id.left_linear);

        onlineOfflineTxtView = (MTextView) findViewById(R.id.onlineOfflineTxtView);
        onlineOfflineTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_GO_ONLINE_TXT"));
        ufxonlineOfflineTxtView = (MTextView) findViewById(R.id.ufxonlineOfflineTxtView);
        ufxTitleonlineOfflineTxtView = (MTextView) findViewById(R.id.ufxTitleonlineOfflineTxtView);
        carNumPlateTxt = (MTextView) findViewById(R.id.carNumPlateTxt);
        carNameTxt = (MTextView) findViewById(R.id.carNameTxt);
        changeCarTxt = (MTextView) findViewById(R.id.changeCarTxt);
        onlineOfflineSwitch = (SwitchButton) findViewById(R.id.onlineOfflineSwitch);
        ufxonlineOfflineSwitch = (SwitchButton) findViewById(R.id.ufxonlineOfflineSwitch);

        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);


        imgSetting = (ImageView) findViewById(R.id.imgSetting);
        imgSetting.setOnClickListener(new setOnClickList());

        startUpdatingStatus = new Intent(getApplicationContext(), UpdateDriverStatus.class);
        bgAppReceiver = new BackgroundAppReceiver(getActContext());


        android.view.Display display = ((android.view.WindowManager) getActContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        left_linear.getLayoutParams().width = display.getWidth() * 75 / 100;
        left_linear.requestLayout();


        ufxDrivername.setText(generalFunc.getJsonValueStr("vName", obj_userProfile) + " "
                + generalFunc.getJsonValueStr("vLastName", obj_userProfile));

        setGeneralData();

        buildMenu();

        setUserInfo();

        if (generalFunc.getJsonValue("RIDE_LATER_BOOKING_ENABLED", userProfileJson).equalsIgnoreCase("Yes")) {
            pendingMainArea.setVisibility(View.VISIBLE);
            botomarea.setVisibility(View.VISIBLE);
        } else {
            pendingMainArea.setVisibility(View.GONE);
            botomarea.setVisibility(View.GONE);
        }

        map.getMapAsync(MainActivity.this);

        menuImgView.setOnClickListener(new setOnClickList());
        changeCarTxt.setOnClickListener(new setOnClickList());

        userLocBtnImgView.setOnClickListener(new setOnClickList());
        userHeatmapBtnImgView.setOnClickListener(new setOnClickList());

            onlineOfflineSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

                if (!intCheck.isNetworkConnected()) {

                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));
                    return;
                }

                if (b == true) {
                    onlineOfflineSwitch.setThumbColorRes(R.color.Green);
                    onlineOfflineSwitch.setBackColorRes(android.R.color.white);
                } else {
                    onlineOfflineSwitch.setThumbColorRes(android.R.color.holo_red_dark);
                    onlineOfflineSwitch.setBackColorRes(android.R.color.white);
                }

                if (isOnlineAvoid == true) {
                    isOnlineAvoid = false;
                    return;
                }

                goOnlineOffline(b, true);
                MainActivity.super.onResume();
            });

        if (savedInstanceState != null) {
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                generalFunc.restartApp(LauncherActivity.class);
            }
        }

        generalFunc.storedata(CommonUtilities.DRIVER_CURRENT_REQ_OPEN_KEY, "false");

        JSONArray arr_CurrentRequests = generalFunc.getJsonArray("CurrentRequests", userProfileJson);

        if (arr_CurrentRequests.length() > 0) {
            updateRequest = new UpdateFrequentTask(5 * 1000);
//            this.updateRequest = updateRequest;
            updateRequest.setTaskRunListener(this);
            updateRequest.startRepeatingTask();
        } else {
            removeOldRequestsCode();
            isCurrentReqHandled = true;
        }

        registerBackgroundAppReceiver();

        String eStatus = generalFunc.getJsonValueStr("eStatus", obj_userProfile);

        if (eStatus.equalsIgnoreCase("inactive")) {
            mapbottomviewarea.setVisibility(View.GONE);
            mapviewarea.setVisibility(View.GONE);
            hileimagview.setVisibility(View.GONE);
            headerLogo.setVisibility(View.VISIBLE);
            InactiveFragment inactiveFragment = new InactiveFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, inactiveFragment);
            ft.commit();
        } else {
            headerLogo.setVisibility(View.GONE);

            if (isDriverOnline) {
                isHailRideOptionEnabled();
            }
            mapbottomviewarea.setVisibility(View.VISIBLE);
            mapviewarea.setVisibility(View.VISIBLE);


            handleNoLocationDial();

        }

        generalFunc.deleteTripStatusMessages();

        Utils.printELog("tsite_upload_docs_file_extensions", "::" + generalFunc.getJsonValueStr("tsite_upload_docs_file_extensions", obj_userProfile));
    }


    public void handleWorkAddress() {
        if (generalFunc.getJsonValue("PROVIDER_AVAIL_LOC_CUSTOMIZE", userProfileJson).equalsIgnoreCase("Yes")) {

            if (generalFunc.getJsonValue("eSelectWorkLocation", userProfileJson).equalsIgnoreCase("Fixed")) {
                if (!generalFunc.retrieveValue(CommonUtilities.WORKLOCATION).equals("")) {
                    addressTxtView.setText(generalFunc.retrieveValue(CommonUtilities.WORKLOCATION));
                    addressTxtViewufx.setText(generalFunc.retrieveValue(CommonUtilities.WORKLOCATION));
                } else {
                    if (userLocation != null) {
                        getAddressFromLocation.setLocation(userLocation.getLatitude(), userLocation.getLongitude());
                        getAddressFromLocation.execute();
                        addressTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));
                        addressTxtViewufx.setText(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));
                    }
                }
            } else {
                if (userLocation != null) {
                    getAddressFromLocation.setLocation(userLocation.getLatitude(), userLocation.getLongitude());
                    getAddressFromLocation.execute();
                    addressTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));
                    addressTxtViewufx.setText(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));

                }

            }
        }
    }


    private void isHailRideOptionEnabled() {

        if (!HailEnableOnDriverStatus.equalsIgnoreCase("") && HailEnableOnDriverStatus.equalsIgnoreCase("No")) {
            hileimagview.setVisibility(View.GONE);
            return;
        }
        String eStatus = generalFunc.getJsonValueStr("eStatus", obj_userProfile);

        if (!eStatus.equalsIgnoreCase("inactive")) {
            ENABLE_HAIL_RIDES = generalFunc.getJsonValue("ENABLE_HAIL_RIDES", userProfileJson);
            if (ENABLE_HAIL_RIDES.equalsIgnoreCase("Yes") && HailEnableOnDriverStatus.equalsIgnoreCase("Yes")) {
                hileimagview.setVisibility(View.VISIBLE);
            } else {
                hileimagview.setVisibility(View.GONE);
            }
        } else {
            hileimagview.setVisibility(View.GONE);
        }
    }

    public void removeOldRequestsCode() {

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActContext());
        Map<String, ?> keys = mPrefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {

            if (entry.getKey().contains(CommonUtilities.DRIVER_REQ_CODE_PREFIX_KEY)) {
                //generalFunc.removeValue(entry.getKey());
                Long CURRENTmILLI = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 1);
                long value = generalFunc.parseLongValue(0, entry.getValue().toString());
                if (CURRENTmILLI >= value) {
                    generalFunc.removeValue(entry.getKey());
                }
            }
        }
    }


    boolean isFirstRunTaskSkipped = false;

    @Override
    public void onTaskRun() {
        if (isFirstRunTaskSkipped == false) {
            isFirstRunTaskSkipped = true;
            return;
        }
        if (generalFunc.retrieveValue(CommonUtilities.DRIVER_CURRENT_REQ_OPEN_KEY).equals("true")) {
            return;
        }

        JSONArray arr_CurrentRequests = generalFunc.getJsonArray("CurrentRequests", userProfileJson);

        if (currentRequestPositions < arr_CurrentRequests.length()) {
            JSONObject obj_temp = generalFunc.getJsonObject(arr_CurrentRequests, currentRequestPositions);

            String message_str = generalFunc.getJsonValue("tMessage", obj_temp.toString()).replace("\\\"", "\"");

            String codeKey = CommonUtilities.DRIVER_REQ_CODE_PREFIX_KEY + generalFunc.getJsonValue("MsgCode", message_str);

            if (generalFunc.retrieveValue(codeKey).equals("") && !generalFunc.containsKey(CommonUtilities.DRIVER_REQ_COMPLETED_MSG_CODE_KEY + generalFunc.getJsonValue("MsgCode", message_str))) {
                generalFunc.storedata(codeKey, "true");

                generalFunc.storedata(CommonUtilities.DRIVER_CURRENT_REQ_OPEN_KEY, "true");

                (new FireTripStatusMsg()).fireTripMsg(message_str);
            }

            currentRequestPositions++;
        } else if (updateRequest != null) {
            updateRequest.stopRepeatingTask();
            updateRequest = null;

            isCurrentReqHandled = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        outState.putString("RESTART_STATE", "true");
        super.onSaveInstanceState(outState);
    }

    public void setWalletInfo() {
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        ((MTextView) findViewById(R.id.walletbalncetxt)).setText(generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE") + ": " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("user_available_balance", userProfileJson)));
    }

    public void setAvgRatingDriver() {

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        String valueRating = generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vAvgRating", userProfileJson));
        String labelRating = generalFunc.retrieveLangLBl("", "LBL_YOUR_RATING");

        ((MTextView) findViewById(R.id.avgratingtxt)).setText( labelRating + ": " + valueRating);
    }

    public void setUserInfo() {
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        ((MTextView) findViewById(R.id.userNameTxt)).setText(generalFunc.getJsonValue("vName", userProfileJson) + " "
                + generalFunc.getJsonValue("vLastName", userProfileJson));

        setWalletInfo();

        setAvgRatingDriver();


        Log.v("ImagemFelipe",generalFunc.retrieveValue("vImgName"));
        if (generalFunc.getJsonValue("vImgName", userProfileJson).equals("") && !generalFunc.retrieveValue("vImgName").isEmpty()) {
            Picasso.with(getActContext())
                    .load(generalFunc.retrieveValue("vImgName"))
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into((SelectableRoundedImageView) findViewById(R.id.userPicImgView));


            Picasso.with(getActContext())
                    .load(generalFunc.retrieveValue("vImgName"))
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into((SelectableRoundedImageView) findViewById(R.id.userImgView));


        } else {
            Picasso.with(getActContext())
                    .load(CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + generalFunc.getMemberId() + "/" + generalFunc.getJsonValue("vImage", userProfileJson))
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into((SelectableRoundedImageView) findViewById(R.id.userPicImgView));


            Picasso.with(getActContext())
                    .load(CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + generalFunc.getMemberId() + "/" + generalFunc.getJsonValue("vImage", userProfileJson))
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into((SelectableRoundedImageView) findViewById(R.id.userImgView));
        }


        changeCarTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE"));


        if (isCarChangeTxt) {
            String iDriverVehicleId = generalFunc.getJsonValueStr("iDriverVehicleId", obj_userProfile);
            setCarInfo(iDriverVehicleId);
        }

        String eEmailVerified = generalFunc.getJsonValueStr("eEmailVerified", obj_userProfile);
        String ePhoneVerified = generalFunc.getJsonValueStr("ePhoneVerified", obj_userProfile);
        if (!eEmailVerified.equalsIgnoreCase("YES") ||
                !ePhoneVerified.equalsIgnoreCase("YES")) {

            Bundle bn = new Bundle();
            if (!eEmailVerified.equalsIgnoreCase("YES") &&
                    !ePhoneVerified.equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_EMAIL_PHONE_VERIFY");
            } else if (!eEmailVerified.equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_EMAIL_VERIFY");
            } else if (!ePhoneVerified.equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_PHONE_VERIFY");
            }

            String eStatus = generalFunc.getJsonValueStr("eStatus", obj_userProfile);

            if (!eStatus.equalsIgnoreCase("inactive")) {
                //  bn.putString("UserProfileJson", userProfileJson);
                showMessageWithAction(onlineOfflineTxtView, generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_TXT"), bn);
            }
        }
    }

    public void showMessageWithAction(View view, String message, final Bundle bn) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE).setAction(generalFunc.retrieveLangLBl("", "LBL_BTN_VERIFY_TXT"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_INFO_REQ_CODE);

                    }
                });
        snackbar.setActionTextColor(getActContext().getResources().getColor(R.color.verfiybtncolor));
        snackbar.setDuration(10000);
        snackbar.show();
    }


    public void setGeneralData() {
        generalFunc.storedata(CommonUtilities.MOBILE_VERIFICATION_ENABLE_KEY, generalFunc.getJsonValueStr("MOBILE_VERIFICATION_ENABLE", obj_userProfile));
        generalFunc.storedata("LOCATION_ACCURACY_METERS", generalFunc.getJsonValueStr("LOCATION_ACCURACY_METERS", obj_userProfile));
        generalFunc.storedata("DRIVER_LOC_UPDATE_TIME_INTERVAL", generalFunc.getJsonValueStr("DRIVER_LOC_UPDATE_TIME_INTERVAL", obj_userProfile));
        generalFunc.storedata(CommonUtilities.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValueStr("REFERRAL_SCHEME_ENABLE", obj_userProfile));

        generalFunc.storedata(CommonUtilities.WALLET_ENABLE, generalFunc.getJsonValueStr("WALLET_ENABLE", obj_userProfile));
        generalFunc.storedata(CommonUtilities.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValueStr("REFERRAL_SCHEME_ENABLE", obj_userProfile));
        generalFunc.storedata(Utils.SMS_BODY_KEY, generalFunc.getJsonValueStr(Utils.SMS_BODY_KEY, obj_userProfile));
        generalFunc.storedata(Utils.PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT, generalFunc.getJsonValueStr("PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT", obj_userProfile));
    }

    public void setCarInfo(String iDriverVehicleId) {
        if (!iDriverVehicleId.equals("") && !iDriverVehicleId.equals("0")) {
            String vLicencePlateNo = generalFunc.getJsonValueStr("vLicencePlateNo", obj_userProfile);
            carNumPlateTxt.setText(vLicencePlateNo);
            carNumPlateTxt.setVisibility(View.VISIBLE);

            String vMake = generalFunc.getJsonValueStr("vMake", obj_userProfile);
            String vModel = generalFunc.getJsonValueStr("vModel", obj_userProfile);

            selectedcar = iDriverVehicleId;

            carNameTxt.setText(vMake + " " + vModel);
            carNameTxt.setVisibility(View.VISIBLE);
            changeCarTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE"));

        } else {
            changeCarTxt.setText(generalFunc.retrieveLangLBl("Choose car", "LBL_CHOOSE_CAR"));
        }
    }

    public void buildMenu() {
        if (list_menu_items == null) {
            list_menu_items = new ArrayList();
            drawerAdapter = new DrawerAdapter(list_menu_items, getActContext(), generalFunc);

            menuListView.setAdapter(drawerAdapter);
            menuListView.setOnItemClickListener(this);
        } else {
            list_menu_items.clear();
        }

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_profile, generalFunc.retrieveLangLBl("", "LBL_MY_PROFILE_HEADER_TXT"), "" + Utils.MENU_PROFILE});

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_doc, generalFunc.retrieveLangLBl("Your Documents", "LBL_MANAGE_DOCUMENT"), "" + Utils.MENU_YOUR_DOCUMENTS});
        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_car, generalFunc.retrieveLangLBl("", "LBL_MANAGE_VEHICLES"), "" + Utils.MENU_MANAGE_VEHICLES});

        String menuMsgYourTrips = generalFunc.retrieveLangLBl("", "LBL_YOUR_TRIPS");

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_yourtrip, menuMsgYourTrips, "" + Utils.MENU_YOUR_TRIPS});

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_bank_detail_icon, generalFunc.retrieveLangLBl("", "LBL_BANK_DETAILS_TXT"), "" + Utils.MENU_BANK_DETAIL});

        if (!generalFunc.getJsonValueStr("APP_PAYMENT_MODE", obj_userProfile).equalsIgnoreCase("Cash")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_card, generalFunc.retrieveLangLBl("Payment", "LBL_PAYMENT"), "" + Utils.MENU_PAYMENT});
        }

        if (!generalFunc.getJsonValueStr(CommonUtilities.WALLET_ENABLE, obj_userProfile).equals("") && generalFunc.getJsonValueStr(CommonUtilities.WALLET_ENABLE, obj_userProfile).equalsIgnoreCase("Yes")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_chart, generalFunc.retrieveLangLBl("", "LBL_LEFT_MENU_WALLET"), "" + Utils.MENU_WALLET});
        }

        list_menu_items.add(new String[]{"" + R.mipmap.ic_waybill, generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL"), "" + Utils.MENU_WAY_BILL});


        if (!generalFunc.getJsonValueStr("eEmailVerified", obj_userProfile).equalsIgnoreCase("YES") ||
                !generalFunc.getJsonValueStr("ePhoneVerified", obj_userProfile).equalsIgnoreCase("YES")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_privacy, generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_TXT"), "" + Utils.MENU_ACCOUNT_VERIFY});
        }


        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_my_heat_view, generalFunc.retrieveLangLBl("", "LBL_MENU_MY_HEATVIEW"), "" + Utils.MENU_MY_HEATVIEW});


        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_emergency, generalFunc.retrieveLangLBl("Emergency Contact", "LBL_EMERGENCY_CONTACT"), "" + Utils.MENU_EMERGENCY_CONTACT});


        //list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_feedback, generalFunc.retrieveLangLBl("Rider Feedback", "LBL_RIDER_FEEDBACK"), "" + Utils.MENU_FEEDBACK});


        //list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_chart, "Meus Ganhos", "" + Utils.MENU_TRIP_STATISTICS});

        if (!generalFunc.getJsonValueStr(CommonUtilities.REFERRAL_SCHEME_ENABLE, obj_userProfile).equals("") && generalFunc.getJsonValueStr(CommonUtilities.REFERRAL_SCHEME_ENABLE, obj_userProfile).equalsIgnoreCase("Yes")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_invite, generalFunc.retrieveLangLBl("Invite Friends", "LBL_INVITE_FRIEND_TXT"), "" + Utils.MENU_INVITE_FRIEND});
        }

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_support, generalFunc.retrieveLangLBl("Support", "LBL_SUPPORT_HEADER_TXT"), "" + Utils.MENU_SUPPORT});

        drawerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if (this.userLocation == null || isShowNearByPassengers == false) {
            return;
        }

        VisibleRegion vr = getMap().getProjection().getVisibleRegion();
        final LatLng mainCenter = vr.latLngBounds.getCenter();
        final LatLng northeast = vr.latLngBounds.northeast;
        final LatLng southwest = vr.latLngBounds.southwest;

        final double radius_map = generalFunc.CalculationByLocation(mainCenter.latitude, mainCenter.longitude, southwest.latitude, southwest.longitude);

        boolean isWithin1m = radius_map > this.radius_map + 0.001;

        if (isWithin1m == true)
            getNearByPassenger(String.valueOf(radius_map), mainCenter.latitude, mainCenter.longitude);

        this.radius_map = radius_map;
    }

    public void configHeatMapView(boolean isShowNearByPassengers) {
        this.isShowNearByPassengers = isShowNearByPassengers;
        userHeatmapBtnImgView.setImageResource(isShowNearByPassengers ? R.mipmap.ic_heatmap_on : R.mipmap.ic_heatmap_off);
        if (mapOverlayList.size() > 0) {
            for (int i = 0; i < mapOverlayList.size(); i++) {
                if (mapOverlayList.get(i) != null) {

                    mapOverlayList.get(i).setVisible(isShowNearByPassengers);

                    if (isShowNearByPassengers) {

                        //handle heat map view
                        if (isfirstZoom) {
                            isfirstZoom = false;
                            getMap().moveCamera(CameraUpdateFactory.zoomTo(14f));
                        }
                    } else {
                        userLocBtnImgView.performClick();
                    }
                }

            }
        }

        if (cameraForUserPosition() != null)
            onCameraChange(cameraForUserPosition());

    }

    public void onMapReady(GoogleMap googleMap) {

        (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.GONE);

        this.gMap = googleMap;

        if (generalFunc.checkLocationPermission(true) == true) {
            getMap().setMyLocationEnabled(false);
            getMap().setPadding(0, 0, 0, Utils.dipToPixels(getActContext(), 90));
            getMap().getUiSettings().setTiltGesturesEnabled(false);
            getMap().getUiSettings().setZoomControlsEnabled(false);
            getMap().getUiSettings().setCompassEnabled(false);
            getMap().getUiSettings().setMyLocationButtonEnabled(false);
        }
        getMap().setOnCameraChangeListener(this);

        getMap().setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            return true;
        });

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
        //GetLocationUpdates.getInstance().setTripStartValue(false, false, "");
        GetLocationUpdates.getInstance().startLocationUpdates(this, this);
    }

    public GoogleMap getMap() {
        return this.gMap;
    }

    public void callgederApi(String egender) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updateUserGender");
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eGender", egender);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

            String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
            if (isDataAvail) {
                generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, message);
                userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
                obj_userProfile = generalFunc.getJsonObject(userProfileJson);
                imgSetting.performClick();
            }
        });
        exeWebServer.execute();
    }

    public void genderDailog() {
        closeDrawer();

        final Dialog builder = new Dialog(getActContext(), R.style.Theme_Dialog);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(R.layout.gender_view);
        builder.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

//        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.gender_view, null);

        final MTextView genderTitleTxt = (MTextView) builder.findViewById(R.id.genderTitleTxt);
        final MTextView maleTxt = (MTextView) builder.findViewById(R.id.maleTxt);
        final MTextView femaleTxt = (MTextView) builder.findViewById(R.id.femaleTxt);
        final ImageView gendercancel = (ImageView) builder.findViewById(R.id.gendercancel);
        final ImageView gendermale = (ImageView) builder.findViewById(R.id.gendermale);
        final ImageView genderfemale = (ImageView) builder.findViewById(R.id.genderfemale);
        final LinearLayout male_area = (LinearLayout) builder.findViewById(R.id.male_area);
        final LinearLayout female_area = (LinearLayout) builder.findViewById(R.id.female_area);

        genderTitleTxt.setText(generalFunc.retrieveLangLBl("Select your gender to continue", "LBL_SELECT_GENDER"));
        maleTxt.setText(generalFunc.retrieveLangLBl("Male", "LBL_MALE_TXT"));
        femaleTxt.setText(generalFunc.retrieveLangLBl("FeMale", "LBL_FEMALE_TXT"));

        gendercancel.setOnClickListener(v -> builder.dismiss());

        male_area.setOnClickListener(v -> {
            callgederApi("Male");
            builder.dismiss();

        });
        female_area.setOnClickListener(v -> {
            callgederApi("Female");
            builder.dismiss();

        });

        builder.show();

    }

    public void goOnlineOffline(final boolean isGoOnline, final boolean isMessageShown) {

        handleNoLocationDial();
        if (isGoOnline == true && (userLocation == null || userLocation.getLatitude() == 0.0 || userLocation.getLongitude() == 0.0)) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Application is not able to get your accurate location. Please try again. \n" +
                    "If you still face the problem, please try again in open sky instead of closed area.", "LBL_NO_LOC_GPS_GENERAL"));
            onlineOfflineSwitch.setChecked(false);
            onlineOfflineSwitch.setThumbColorRes(android.R.color.holo_red_dark);
            onlineOfflineSwitch.setBackColorRes(android.R.color.white);

            ufxonlineOfflineSwitch.setChecked(false);
            ufxonlineOfflineSwitch.setThumbColorRes(R.color.white);
            ufxonlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);
            setOfflineState();
            return;
        }
        isHailRideOptionEnabled();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateDriverStatus");
        parameters.put("iDriverId", generalFunc.getMemberId());

        if (isGoOnline == true) {
            parameters.put("Status", "Available");
            parameters.put("isUpdateOnlineDate", "true");
        } else {
            parameters.put("Status", "Not Available");
        }
        if (userLocation != null) {
            parameters.put("latitude", "" + userLocation.getLatitude());
            parameters.put("longitude", "" + userLocation.getLongitude());
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setCancelAble(false);

        if (isMessageShown == true) {
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        }

        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (isMessageShown == false) {
                return;
            }

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);
                String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                if (message.equals("SESSION_OUT")) {
                    generalFunc.notifySessionTimeOut();
                    Utils.runGC();
                    return;
                }

                if (isDataAvail == true) {

                    HailEnableOnDriverStatus = generalFunc.getJsonValue("Enable_Hailtrip", responseString);

                    if (isGoOnline == true) {

                        if (generalFunc.getJsonValue("isExistUberXServices", responseString).equalsIgnoreCase("Yes")) {
                            workArea.setVisibility(View.VISIBLE);
                            workAreaLine.setVisibility(View.VISIBLE);

                        } else {
                            workArea.setVisibility(View.GONE);
                            workAreaLine.setVisibility(View.GONE);

                        }

                        if (message.equals("REQUIRED_MINIMUM_BALNCE")) {
                            isHailRideOptionEnabled();

                            Bundle bn = new Bundle();
                            bn.putString("UserProfileJson", userProfileJson);
//                            generalFunc.showGeneralMessage("",generalFunc.getJsonValue("Msg", responseString));
                            generalFunc.buildLowBalanceMessage(getActContext(), generalFunc.getJsonValue("Msg", responseString), bn, R.layout.design_cash_balance_dialoge, R.id.addNowTxtArea, R.id.skipTxtArea, R.id.titileTxt);
                        }
                        setOnlineState();

                    } else {
                        workArea.setVisibility(View.GONE);
                        workAreaLine.setVisibility(View.GONE);
                        setOfflineState();
                    }

                    if (generalFunc.getJsonValue("UberX_message", responseString) != null && !generalFunc.getJsonValue("UberX_message", responseString).equalsIgnoreCase("")) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("UberX_message", responseString)));
                    }
                } else {

                    if (generalFunc.getJsonValue("Enable_Hailtrip", responseString) != null & !generalFunc.getJsonValue("Enable_Hailtrip", responseString).equalsIgnoreCase("")) {

                        HailEnableOnDriverStatus = generalFunc.getJsonValue("Enable_Hailtrip", responseString);
                    }

                    isOnlineAvoid = true;

                    if (isGoOnline == true) {
                        onlineOfflineSwitch.setChecked(false);
                    } else {
                        onlineOfflineSwitch.setChecked(true);
                    }

                    Bundle bn = new Bundle();
                    bn.putString("msg", "" + message);
                    String eStatus = generalFunc.getJsonValueStr("eStatus", obj_userProfile);

                    if (!eStatus.equalsIgnoreCase("inactive")) {
                        if (message.equals("DO_EMAIL_PHONE_VERIFY") || message.equals("DO_PHONE_VERIFY") || message.equals("DO_EMAIL_VERIFY")) {
                            accountVerificationAlert(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_TXT"), bn);
                            return;
                        }
                    }

                    if (Utils.checkText(message) && message.equals("REQUIRED_MINIMUM_BALNCE") && isGoOnline) {

                        isHailRideOptionEnabled();
                        bn.putString("UserProfileJson", userProfileJson);

//                        generalFunc.buildLowBalanceMessage(getActContext(), generalFunc.getJsonValue("Msg", responseString), bn);

                        generalFunc.buildLowBalanceMessage(getActContext(), generalFunc.getJsonValue("Msg", responseString), bn, R.layout.design_cash_balance_dialoge, R.id.addNowTxtArea, R.id.skipTxtArea, R.id.titileTxt);
                        return;
                    }


                    if (isGoOnline) {
                        isHailRideOptionEnabled();
                    } else {
                        hileimagview.setVisibility(View.GONE);
                    }

                    if (Utils.checkText(message)) {
                        if (message.equalsIgnoreCase("LBL_INACTIVE_CARS_MESSAGE_TXT")) {
                            hileimagview.setVisibility(View.GONE);
                            GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                            alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", message));
                            alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                            alertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
                            alertBox.setBtnClickList(btn_id -> {

                                alertBox.closeAlertBox();
                                if (btn_id == 0) {
                                    new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                                }
                            });
                            alertBox.showAlertBox();
                        } else {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl(generalFunc.getJsonValue(CommonUtilities.message_str, responseString),
                                            generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                        }
                    }
                }
            } else {

                if (intCheck.isNetworkConnected()) {
                    isOnlineAvoid = true;

                        if (isGoOnline == true) {
                            onlineOfflineSwitch.setChecked(false);
                        } else {
                            onlineOfflineSwitch.setChecked(true);
                        }

                }

                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void setOfflineState() {
        isDriverOnline = false;
        onlineOfflineTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_GO_ONLINE_TXT"));

        generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", "LBL_OFFLINE_HEADER_TXT"));

        hileimagview.setVisibility(View.GONE);
        stopService(startUpdatingStatus);

        generalFunc.storedata(CommonUtilities.DRIVER_ONLINE_KEY, "false");
        String message = generalFunc.retrieveLangLBl("", "LBL_APP_INACTIVE_STATE_ALERT_NOTIFICATION");
        LocalNotification.dispatchLocalNotification(this, message, false);


        ConfigPubNub.getInstance().unSubscribeToCabRequestChannel();
    }
    private CustomChildEventListener customChildEventListener;
    public DatabaseReference rootRef = null;

    public void setOnlineState() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        customChildEventListener = new CustomChildEventListener(generalFunc, rootRef);

        rootRef.child("cab_requests").addChildEventListener(customChildEventListener);
        isHailRideOptionEnabled();
        isDriverOnline = true;
        onlineOfflineTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_GO_OFFLINE_TXT"));

        generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", "LBL_ONLINE_HEADER_TXT"));

        if (!generalFunc.isServiceRunning(UpdateDriverStatus.class)) {
            startUpdatingStatus.setAction(UpdateDriverStatus.ACTION_START_FOREGROUND_SERVICE);
            startService(startUpdatingStatus);
        }

        generalFunc.storedata(CommonUtilities.DRIVER_ONLINE_KEY, "true");

        updateLocationToPubNub();

        ConfigPubNub.getInstance().subscribeToCabRequestChannel();

    }

    public void accountVerificationAlert(String message, final Bundle bn) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 1) {
                generateAlert.closeAlertBox();
                (new StartActProcess(getActContext())).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_INFO_REQ_CODE);
            } else if (btn_id == 0) {
                generateAlert.closeAlertBox();
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TRIP_TXT"));
        generateAlert.showAlertBox();
    }

    public void updateLocationToPubNub() {
        if (isDriverOnline == true && userLocation != null && userLocation.getLongitude() != 0.0 && userLocation.getLatitude() != 0.0) {
            if (lastPublishedLoc != null) {

                if (userLocation.distanceTo(lastPublishedLoc) < PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT) {
                    return;
                } else {
                    lastPublishedLoc = userLocation;
                }

            } else {
                lastPublishedLoc = userLocation;
            }


            ConfigPubNub.getInstance().publishMsg(generalFunc.getLocationUpdateChannel(), generalFunc.buildLocationJson(userLocation));
        }
    }

    public void getNearByPassenger(String radius, double center_lat, double center_long) {

        if (heatMapAsyncTask != null) {
            heatMapAsyncTask.cancel(true);
            heatMapAsyncTask = null;
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadPassengersLocation");
        parameters.put("Radius", radius);
        parameters.put("Latitude", String.valueOf(center_lat));
        parameters.put("Longitude", String.valueOf(center_long));
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        this.heatMapAsyncTask = exeWebServer;

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = generalFunc.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {
                    JSONArray dataLocArr = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);

                    ArrayList<LatLng> listTemp = new ArrayList<LatLng>();
                    ArrayList<LatLng> Online_listTemp = new ArrayList<LatLng>();
                    for (int i = 0; i < dataLocArr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(dataLocArr, i);

                        String type = generalFunc.getJsonValue("Type", obj_temp.toString());

                        double lat = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("Latitude", obj_temp.toString()));
                        double longi = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("Longitude", obj_temp.toString()));


                        if (type.equalsIgnoreCase("Online")) {

                            String iUserId = generalFunc.getJsonValue("iUserId", obj_temp.toString());

                            if (onlinePassengerLocList.containsKey("ID_" + type + "_" + iUserId) == false) {
                                onlinePassengerLocList.put("ID_" + type + "_" + iUserId, "True");

                                Online_listTemp.add(new LatLng(lat, longi));
                            }


                        } else {
                            String iTripId = generalFunc.getJsonValue("iTripId", obj_temp.toString());
                            if (historyLocList.containsKey("ID_" + type + "_" + iTripId) == false) {
                                historyLocList.put("ID_" + type + "_" + iTripId, "True");

                                listTemp.add(new LatLng(lat, longi));
                            }
                        }
                    }

                    if (listTemp.size() > 0) {
                        mapOverlayList.add(getMap().addTileOverlay(new TileOverlayOptions().tileProvider(
                                new HeatmapTileProvider.Builder().gradient(new Gradient(new int[]{Color.rgb(153, 0, 0), Color.WHITE}, new float[]{0.2f, 1.5f})).data(listTemp).build())));
                    }
                    if (Online_listTemp.size() > 0) {
                        mapOverlayList.add(getMap().addTileOverlay(new TileOverlayOptions().tileProvider(
                                new HeatmapTileProvider.Builder().gradient(new Gradient(new int[]{Color.rgb(0, 51, 0), Color.WHITE}, new float[]{0.2f, 1.5f}, 1000)).data(Online_listTemp).build())));
                    }
                    if (!isShowNearByPassengers) {

                        configHeatMapView(false);
                    } else {
                        configHeatMapView(true);
                    }
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void configCarList(final boolean isCarUpdate, final String selectedCarId, final int position) {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        if (isCarUpdate == false) {
            parameters.put("type", "LoadAvailableCars");
        } else {
            parameters.put("type", "SetDriverCarID");
            parameters.put("iDriverVehicleId", selectedCarId);
        }
        parameters.put("iDriverId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    if (isCarUpdate == false) {
                        LoadCarList(generalFunc.getJsonArray(CommonUtilities.message_str, responseString));
                    } else {

                        String vLicencePlateNo = generalFunc.getJsonValue("vLicencePlate", items_txt_car_json.get(position));
                        carNumPlateTxt.setText(vLicencePlateNo);
                        carNumPlateTxt.setVisibility(View.VISIBLE);

                        if (items_isHail_json.get(position).equalsIgnoreCase("Yes")) {
                            if (isDriverOnline) {
                                HailEnableOnDriverStatus = "Yes";
                                isHailRideOptionEnabled();
                            } else {
                                hileimagview.setVisibility(View.GONE);
                            }

                        } else {
                            HailEnableOnDriverStatus = "No";
                            hileimagview.setVisibility(View.GONE);
                        }

                        String vMake = generalFunc.getJsonValue("vMake", items_txt_car_json.get(position));
                        String vModel = generalFunc.getJsonValue("vTitle", items_txt_car_json.get(position));

                        carNameTxt.setText(vMake + " " + vModel);
                        selectedcar = selectedCarId;
                        carNameTxt.setVisibility(View.VISIBLE);
                        changeCarTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE"));

                        generalFunc.showMessage(generalFunc.getCurrentView(MainActivity.this), generalFunc.retrieveLangLBl("", "LBL_INFO_UPDATED_TXT"));
                    }

                } else {
                    String msg = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                    if (msg.equalsIgnoreCase("LBL_INACTIVE_CARS_MESSAGE_TXT")) {
                        GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                        alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", msg));
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
                                generalFunc.retrieveLangLBl("", msg));
                    }
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void LoadCarList(JSONArray array) {

        items_txt_car.clear();
        items_car_id.clear();
        items_txt_car_json.clear();
        items_isHail_json.clear();
        final ArrayList list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(array, i);
            items_txt_car.add(generalFunc.getJsonValue("vMake", obj_temp.toString()) + " " + generalFunc.getJsonValue("vTitle", obj_temp.toString()));

            items_car_id.add(generalFunc.getJsonValue("iDriverVehicleId", obj_temp.toString()));
            items_txt_car_json.add(obj_temp.toString());
            items_isHail_json.add(generalFunc.getJsonValue("Enable_Hailtrip", obj_temp.toString()));

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("car", items_txt_car.get(i).toString());
            map.put("iDriverVehicleId", items_car_id.get(i).toString());
            list.add(map);

        }

        // CharSequence[] cs_currency_txt = items_txt_car.toArray(new CharSequence[items_txt_car.size()]);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_selectcar_view, null);

        final MTextView vehTitleTxt = (MTextView) dialogView.findViewById(R.id.VehiclesTitleTxt);
        final MTextView mangeVehiclesTxt = (MTextView) dialogView.findViewById(R.id.mangeVehiclesTxt);
        final MTextView addVehiclesTxt = (MTextView) dialogView.findViewById(R.id.addVehiclesTxt);
        final RecyclerView vehiclesRecyclerView = (RecyclerView) dialogView.findViewById(R.id.vehiclesRecyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(vehiclesRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL_LIST);
        vehiclesRecyclerView.addItemDecoration(dividerItemDecoration);

        builder.setView(dialogView);
        vehTitleTxt.setText(generalFunc.retrieveLangLBl("Select Your Vehicles", "LBL_SELECT_CAR_TXT"));
        mangeVehiclesTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANAGE_VEHICLES"));

        addVehiclesTxt.setText(generalFunc.retrieveLangLBl("ADD NEW", "LBL_ADD_VEHICLES"));

        ManageVehicleListAdapter adapter = new ManageVehicleListAdapter(getActContext(), list, generalFunc, selectedcar);
        vehiclesRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickList(this);

        mangeVehiclesTxt.setOnClickListener(v -> {
            list_car.dismiss();

            Bundle bn = new Bundle();
            bn.putString("app_type", app_type);
            bn.putString("iDriverVehicleId", generalFunc.getJsonValue("iDriverVehicleId", userProfileJson));
            new StartActProcess(getActContext()).startActWithData(ManageVehiclesActivity.class, bn);

        });

        addVehiclesTxt.setOnClickListener(v -> list_car.dismiss());

        builder.setNegativeButton(generalFunc.retrieveLangLBl("", "LBL_MANAGE_VEHICLES"), (dialog, which) -> {
            dialog.cancel();

            Bundle bn = new Bundle();
            bn.putString("app_type", app_type);
            bn.putString("iDriverVehicleId", generalFunc.getJsonValue("iDriverVehicleId", userProfileJson));
            new StartActProcess(getActContext()).startActWithData(ManageVehiclesActivity.class, bn);

        });

            builder.setPositiveButton(generalFunc.retrieveLangLBl("ADD NEW", "LBL_ADD_VEHICLES"), (dialog, which) -> {

                dialog.cancel();
                Bundle bn = new Bundle();
                bn.putString("app_type", app_type);
                (new StartActProcess(getActContext())).startActWithData(AddVehicleActivity.class, bn);

            });

        list_car = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(list_car);
        }
        list_car.show();
        final Button positiveButton = list_car.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.appThemeColor_1));
        final Button negativeButton = list_car.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.black));
        list_car.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));
    }

    @Override
    public void onLocationUpdate(Location location) {

        try {

            Utils.printELog("onLocationUpdate", "Called");

            if (location == null) {
                return;
            }

            if (isShowNearByPassengers) {
                return;
            }
            if (generalFunc.checkLocationPermission(true) == true && getMap() != null && getMap().isMyLocationEnabled() == false) {
                getMap().setMyLocationEnabled(true);
            }

            this.userLocation = location;
            CameraPosition cameraPosition = cameraForUserPosition();

            if (cameraPosition != null)
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (!isFirstAddressLoaded) {
                getAddressFromLocation.setLocation(userLocation.getLatitude(), userLocation.getLongitude());
                getAddressFromLocation.execute();
                isFirstAddressLoaded = true;
            }

            if (isFirstLocation == true && generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES") &&
                    generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {

                isFirstLocation = false;

                String isGoOnline = generalFunc.retrieveValue(CommonUtilities.GO_ONLINE_KEY);

                if ((isGoOnline != null && !isGoOnline.equals("") && isGoOnline.equals("Yes"))) {
                    long lastTripTime = generalFunc.parseLongValue(0, generalFunc.retrieveValue(CommonUtilities.LAST_FINISH_TRIP_TIME_KEY));
                    long currentTime = Calendar.getInstance().getTimeInMillis();

                    if ((currentTime - lastTripTime) < 25000) {
                        if (generalFunc.isLocationEnabled()) {
                            onlineOfflineSwitch.setChecked(true);
                        }
                    }

                    generalFunc.storedata(CommonUtilities.GO_ONLINE_KEY, "No");
                    generalFunc.storedata(CommonUtilities.LAST_FINISH_TRIP_TIME_KEY, "0");

                }

                if (generalFunc.isLocationEnabled() && generalFunc.getJsonValue("vAvailability", userProfileJson).equals("Available") && isDriverOnline == false) {
                    onlineOfflineSwitch.setChecked(true);
                }
            }
        } catch (Exception e) {

        }
    }

    public void pubNubStatus(PNStatusCategory status) {

    }

    public CameraPosition cameraForUserPosition() {
        double currentZoomLevel = getMap().getCameraPosition().zoom;

        // if (Utils.defaultZomLevel > currentZoomLevel) {
        currentZoomLevel = Utils.defaultZomLevel;
        //}
        if (userLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                    .zoom((float) currentZoomLevel).build();

            return cameraPosition;
        } else {
            return null;
        }
    }

    public void openMenuProfile() {
        Bundle bn = new Bundle();
        // bn.putString("UserProfileJson", userProfileJson);
        bn.putBoolean("isDriverOnline", isDriverOnline);
        new StartActProcess(getActContext()).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, final View view, int position, long l) {
        int itemId = generalFunc.parseIntegerValue(0, list_menu_items.get(position)[2]);
        Bundle bn = new Bundle();
        // bn.putString("UserProfileJson", userProfileJson);

        Utils.hideKeyboard(MainActivity.this);

        drawerAdapter.notifyDataSetChanged();

        switch (itemId) {
            case Utils.MENU_PROFILE:
                openMenuProfile();
                break;

            case Utils.MENU_PAYMENT:
                new StartActProcess(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
                break;

            case Utils.MENU_RIDE_HISTORY:
                new StartActProcess(getActContext()).startActWithData(RideHistoryActivity.class, bn);
                break;

            case Utils.MENU_BOOKINGS:
                new StartActProcess(getActContext()).startActWithData(MyBookingsActivity.class, bn);
                break;

            case Utils.MENU_FEEDBACK:
                new StartActProcess(getActContext()).startActWithData(DriverFeedbackActivity.class, bn);
                break;
            case Utils.MENU_BANK_DETAIL:
                new StartActProcess(getActContext()).startActWithData(BankDetailActivity.class, bn);
                break;

            case Utils.MENU_ABOUT_US:
                new StartActProcess(getActContext()).startAct(StaticPageActivity.class);
                break;
            case Utils.MENU_POLICY:
                (new StartActProcess(getActContext())).openURL(CommonUtilities.SERVER_URL + "privacy-policy");
                break;
            case Utils.MENU_CONTACT_US:
                new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                break;
            case Utils.MENU_YOUR_DOCUMENTS:
                bn.putString("PAGE_TYPE", "Driver");
                bn.putString("iDriverVehicleId", "");
                bn.putString("doc_file", "");
                bn.putString("iDriverVehicleId", "");
                bn.putString("seltype", app_type);
                new StartActProcess(getActContext()).startActWithData(ListOfDocumentActivity.class, bn);

                break;

            case Utils.MENU_TRIP_STATISTICS:
                new StartActProcess(getActContext()).startActWithData(MyEarnings.class, bn);
                break;
            case Utils.MENU_MANAGE_VEHICLES:

                bn.putString("iDriverVehicleId", generalFunc.getJsonValue("iDriverVehicleId", userProfileJson));
                bn.putString("app_type", app_type);
                new StartActProcess(getActContext()).startActWithData(ManageVehiclesActivity.class, bn);
                break;

            case Utils.MENU_HELP:
                new StartActProcess(getActContext()).startAct(HelpActivity.class);
                break;

            case Utils.MENU_WALLET:
                iswallet = true;
                new StartActProcess(getActContext()).startActWithData(MyWalletActivity.class, bn);
                break;

            case Utils.MENU_WAY_BILL:
                new StartActProcess(getActContext()).startActWithData(WayBillActivity.class, bn);

                break;
            case Utils.MENU_ACCOUNT_VERIFY:
                if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES") ||
                        !generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {
                    Bundle bn1 = new Bundle();
                    if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES") &&
                            !generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {
                        bn1.putString("msg", "DO_EMAIL_PHONE_VERIFY");
                    } else if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES")) {
                        bn1.putString("msg", "DO_EMAIL_VERIFY");
                    } else if (!generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {
                        bn1.putString("msg", "DO_PHONE_VERIFY");
                    }

                    new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn1, Utils.VERIFY_INFO_REQ_CODE);

                }
                break;
            case Utils.MENU_INVITE_FRIEND:
                new StartActProcess(getActContext()).startActWithData(InviteFriendsActivity.class, bn);
                break;

            case Utils.MENU_EMERGENCY_CONTACT:
                new StartActProcess(getActContext()).startAct(EmergencyContactActivity.class);
                break;

            case Utils.MENU_SUPPORT:
                new StartActProcess(getActContext()).startAct(SupportActivity.class);
                break;
            case Utils.MENU_YOUR_TRIPS:
                new StartActProcess(getActContext()).startActWithData(HistoryActivity.class, bn);
                break;

            case Utils.MENU_SIGN_OUT:
                logoutFromDevice(getActContext(), generalFunc);
                break;

            case Utils.MENU_MY_HEATVIEW:
                new StartActProcess(getActContext()).startActWithData(MyHeatViewActivity.class, bn);
                break;
        }

        closeDrawer();
    }

    public void logoutFromDevice(Context context, GeneralFunctions generalFunc) {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "callOnLogout");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(context, parameters);
        exeWebServer.setLoaderConfig(context, true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    MyApp.getInstance().removePubSub();
                    generalFunc.logOutUser();
                    generalFunc.restartApp(LauncherActivity.class);

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

    public void checkDrawerState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) == true) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public Context getActContext() {
        return MainActivity.this;
    }

    public void checkIsDriverOnline() {
        if (isDriverOnline == true) {
            stopService(startUpdatingStatus);

            for (int i = 0; i < 1000; i++) {
            }
        }
    }


    public void registerBackgroundAppReceiver() {

        unRegisterBackgroundAppReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonUtilities.BACKGROUND_APP_RECEIVER_INTENT_ACTION);

        registerReceiver(bgAppReceiver, filter);
    }

    public void unRegisterBackgroundAppReceiver() {
        if (bgAppReceiver != null) {
            try {
                unregisterReceiver(bgAppReceiver);
            } catch (Exception e) {

            }
        }
    }

    public void getWalletBalDetails() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetMemberWalletBalance");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", CommonUtilities.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail) {
                    try {
                        String userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
                        JSONObject object = generalFunc.getJsonObject(userProfileJson);
                        object.put("user_available_balance", generalFunc.getJsonValue("MemberBalance", responseString));
                        generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, object.toString());

                        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
                        obj_userProfile = generalFunc.getJsonObject(userProfileJson);

                        setWalletInfo();
                    } catch (Exception e) {

                    }
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCarChangeTxt = false;
        getWalletBalDetails();

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

        buildMenu();

        handleWorkAddress();

        if (isDriverOnline) {
            isHailRideOptionEnabled();
        }
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);

        setUserInfo();
        if (iswallet) {
            userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            iswallet = false;
        }

        if (generalFunc.retrieveValue(CommonUtilities.DRIVER_ONLINE_KEY).equals("false") && isDriverOnline == true) {
            setOfflineState();
            isOnlineAvoid = true;
            onlineOfflineSwitch.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public MyApp getApp() {
        return ((MyApp) getApplication());
    }

    public void configBackground() {

        if (isCurrentReqHandled == false) {
            generalFunc.removeValue(CommonUtilities.DRIVER_ACTIVE_REQ_MSG_KEY);
            return;
        }

        if (!getApp().isMyAppInBackGround()) {
            //aman comment
//            if (getApp().isMyAppInBackGround() == false && isDriverOnline == true) {
//                setOnlineState();
//            }

            if (generalFunc.containsKey(CommonUtilities.DRIVER_ACTIVE_REQ_MSG_KEY)) {

                String msg = generalFunc.retrieveValue(CommonUtilities.DRIVER_ACTIVE_REQ_MSG_KEY);

                generalFunc.removeValue(CommonUtilities.DRIVER_ACTIVE_REQ_MSG_KEY);

                generalFunc.storedata(CommonUtilities.DRIVER_CURRENT_REQ_OPEN_KEY, "true");

                (new FireTripStatusMsg()).fireTripMsg(msg);

            }
        }
    }


    public void removeLocationUpdates() {


        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        this.userLocation = null;
    }

    @Override
    protected void onDestroy() {
        try {
            checkIsDriverOnline();
            removeLocationUpdates();
            unRegisterBackgroundAppReceiver();

            if (getAddressFromLocation != null) {
                getAddressFromLocation.setAddressList(null);
                getAddressFromLocation = null;
            }

            if (gMap != null) {
                this.gMap.setOnCameraChangeListener(null);
                this.gMap = null;
            }

            if (heatMapAsyncTask != null) {
                heatMapAsyncTask.cancel(true);
                heatMapAsyncTask = null;
            }

            if (updateRequest != null) {
                updateRequest.stopRepeatingTask();
                updateRequest = null;
            }

            Utils.runGC();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
            return;
        }

        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
            } else {
                generateAlert.closeAlertBox();
                MainActivity.super.onBackPressed();
            }

        });
        generateAlert.setContentMessage(generalFunc.retrieveLangLBl("Exit App", "LBL_EXIT_APP_TITLE_TXT"), generalFunc.retrieveLangLBl("Are you sure you want to exit?", "LBL_WANT_EXIT_APP_TXT"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
        generateAlert.showAlertBox();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.MY_PROFILE_REQ_CODE && resultCode == RESULT_OK && data != null) {
            // String userProfileJson = data.getStringExtra("UserProfileJson");
            userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            //  this.userProfileJson = userProfileJson;
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            setUserInfo();
            ((MTextView) findViewById(R.id.userNameTxt)).setText(generalFunc.getJsonValue("vName", userProfileJson) + " "
                    + generalFunc.getJsonValue("vLastName", userProfileJson));
        } else if (requestCode == Utils.VERIFY_INFO_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String msgType = data.getStringExtra("MSG_TYPE");
            if (msgType.equalsIgnoreCase("EDIT_PROFILE")) {
                openMenuProfile();
            }
        } else if (requestCode == Utils.VERIFY_INFO_REQ_CODE) {
            this.userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            buildMenu();
        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            this.userProfileJson = userProfileJson;
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
        } else if (requestCode == Utils.REQUEST_CODE_GPS_ON) {
            handleNoLocationDial();
        } else if (requestCode == Utils.REQUEST_CODE_NETWOEK_ON) {
            handleNoNetworkDial();
        } else if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                String worklat = data.getStringExtra("Latitude");
                String worklong = data.getStringExtra("Longitude");
                String workadddress = data.getStringExtra("Address");

                updateWorkLocation(worklat, worklong, workadddress);
            }
        }

    }

    public void updateWorkLocation(String worklat, String worklong, String workaddress) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateDriverWorkLocationUFX");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("vWorkLocationLatitude", worklat);
        parameters.put("vWorkLocationLongitude", worklong);
        parameters.put("vWorkLocation", workaddress);
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    addressTxtView.setText(workaddress);
                    addressTxtViewufx.setText(workaddress);

                    generalFunc.storedata(CommonUtilities.WORKLOCATION, workaddress);

                } else {

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onItemClick(int position, int viewClickId) {
        list_car.dismiss();

        String selected_carId = items_car_id.get(position);

        configCarList(true, selected_carId, position);
    }


    public void handleNoNetworkDial() {
        String eStatus = generalFunc.getJsonValueStr("eStatus", obj_userProfile);
        if (!eStatus.equalsIgnoreCase("inactive")) {

            if (intCheck.isNetworkConnected() && intCheck.check_int()) {

            }

            if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            } else {
                handleNoLocationDial();
            }
        }
    }

    public void handleNoLocationDial() {
        if (!generalFunc.isLocationEnabled() && isDriverOnline == true) {
            onlineOfflineSwitch.setChecked(false);
        }
    }


    @Override
    public void onAddressFound(String address, double latitude, double longitude) {

        if (generalFunc.getJsonValue("PROVIDER_AVAIL_LOC_CUSTOMIZE", userProfileJson).equalsIgnoreCase("Yes") && generalFunc.getJsonValue("eSelectWorkLocation", userProfileJson).equalsIgnoreCase("Fixed")) {
            if (!generalFunc.retrieveValue(CommonUtilities.WORKLOCATION).equals("")) {
                addressTxtView.setText(generalFunc.retrieveValue(CommonUtilities.WORKLOCATION));
            } else {
                addressTxtView.setText(address);
                addressTxtViewufx.setText(address);
            }
        } else {
            addressTxtView.setText(address);
            addressTxtViewufx.setText(address);
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(MainActivity.this);

            if (view.getId() == menuImgView.getId()) {
                checkDrawerState();
            } else if (view.getId() == userLocBtnImgView.getId()) {
                if (userLocation == null) {
                    return;
                }
                CameraPosition cameraPosition = cameraForUserPosition();
                if (cameraPosition != null)
                    getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else if (view.getId() == userHeatmapBtnImgView.getId()) {
                if (userLocation == null) {
                    return;
                }
                isfirstZoom = true;
                configHeatMapView(isShowNearByPassengers ? false : true);
            } else if (view.getId() == changeCarTxt.getId()) {
                configCarList(false, "", 0);
            } else if (view.getId() == imgSetting.getId()) {
                closeDrawer();
                userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
                obj_userProfile = generalFunc.getJsonObject(userProfileJson);

                if (generalFunc.retrieveValue(CommonUtilities.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("yes")) {
                    if (generalFunc.getJsonValue("eGender", userProfileJson).equalsIgnoreCase("feMale")) {
                        new StartActProcess(getActContext()).startAct(PrefranceActivity.class);
                    } else {
                        if (generalFunc.getJsonValue("eGender", userProfileJson).equals("")) {
                            genderDailog();

                        } else {
                            menuListView.performItemClick(view, 0, Utils.MENU_PROFILE);
                        }
                    }
                } else {
                    menuListView.performItemClick(view, 0, Utils.MENU_PROFILE);
                }

            } else if (view.getId() == logoutarea.getId()) {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    if (btn_id == 0) {
                        generateAlert.closeAlertBox();
                    } else {
                        generateAlert.closeAlertBox();
                        logoutFromDevice(getActContext(), generalFunc);
                    }
                });
                generateAlert.setContentMessage(generalFunc.retrieveLangLBl("Logout", "LBL_LOGOUT"), generalFunc.retrieveLangLBl("Are you sure you want to logout?", "LBL_WANT_LOGOUT_APP_TXT"));
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                generateAlert.showAlertBox();

            } else if (view.getId() == hileimagview.getId()) {
                if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
                    generalFunc.showMessage(menuImgView, generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"));
                } else {
                    if (!isBtnClick) {
                        isBtnClick = true;
                        checkHailType();
                    }
                }
            } else if (view.getId() == menuufxImgView.getId()) {
                checkDrawerState();
            } else if (view.getId() == pendingarea.getId()) {
                Bundle bn = new Bundle();
                bn.putBoolean("ispending", true);
                new StartActProcess(getActContext()).startActWithData(HistoryActivity.class, bn);
            } else if (view.getId() == upcomginarea.getId()) {

                Bundle bn = new Bundle();
                bn.putBoolean("isupcoming", true);
                new StartActProcess(getActContext()).startActWithData(HistoryActivity.class, bn);

            } else if (view.getId() == radiusTxtView.getId()) {

            } else if (view.getId() == imageradius.getId()) {
                new StartActProcess(getActContext()).startAct(WorkLocationActivity.class);
            } else if (view.getId() == imageradiusufx.getId()) {
                new StartActProcess(getActContext()).startAct(WorkLocationActivity.class);
            } else if (view.getId() == btn_edit.getId()) {
                new StartActProcess(getActContext()).startAct(WorkLocationActivity.class);
            }
        }
    }

    private void checkHailType() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckVehicleEligibleForHail");
        parameters.put("iDriverId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    isBtnClick = false;
                    Bundle bn = new Bundle();
                    bn.putString("userLocation", userLocation + "");
                    bn.putDouble("lat", userLocation.getLatitude());
                    bn.putDouble("long", userLocation.getLongitude());
                    new StartActProcess(getActContext()).startActWithData(HailActivity.class, bn);
                } else {
                    isBtnClick = false;

                    String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                    if (message.equals("REQUIRED_MINIMUM_BALNCE")) {
                        isHailRideOptionEnabled();

                        Bundle bn = new Bundle();
                        bn.putString("UserProfileJson", userProfileJson);

                        generalFunc.buildLowBalanceMessage(getActContext(), generalFunc.getJsonValue("Msg", responseString), bn, R.layout.design_cash_balance_dialoge, R.id.addNowTxtArea, R.id.skipTxtArea, R.id.titileTxt);
                        return;
                    }
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));

                }
            } else {
                isBtnClick = false;
            }
        });
        exeWebServer.execute();

    }

    public interface OnAlertButtonClickListener {
        void onAlertButtonClick(int buttonId);
    }
}
