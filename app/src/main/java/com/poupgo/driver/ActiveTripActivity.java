package com.poupgo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.adapter.files.OnGoingTripDetailAdapter;
import com.general.files.CancelTripDialog;
import com.general.files.DividerItemDecoration;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.files.OpenPassengerDetailDialog;
import com.general.files.UpdateDirections;
import com.general.files.UpdateDriverLocationService;
import com.general.files.UpdateTripLocationsService;
import com.general.files.UploadProfileImage;
import com.general.files.getUserData;
import com.general.functions.GeneralFunctions;
import com.general.functions.GenerateAlertBox;
import com.general.functions.InternetConnection;
import com.general.functions.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.squareup.picasso.Picasso;
import com.utilities.general.files.StartActProcess;
import com.utilities.general.files.UpdateFrequentTask;
import com.utilities.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.MyProgressDialog;
import com.utilities.view.simpleratingbar.SimpleRatingBar;
import com.utils.AnimateMarker;
import com.utils.CommonUtilities;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ActiveTripActivity extends AppCompatActivity implements OnMapReadyCallback, GetLocationUpdates.LocationUpdatesListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";
    private static final int SELECT_PICTURE = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public Location userLocation;
    public ImageView emeTapImgView;
    public MTextView timeTxt;
    GeneralFunctions generalFunc;
    MTextView titleTxt;
    String tripId = "";
    String eType = "";
    public HashMap<String, String> data_trip;
    SupportMapFragment map;
    GoogleMap gMap;
    boolean isFirstLocation = true;
    Intent startLocationUpdateService;
    MTextView addressTxt;
    boolean isDestinationAdded = false;
    double destLocLatitude = 0.0;
    double destLocLongitude = 0.0;
    Marker destLocMarker = null;
    Polyline route_polyLine;
    ExecuteWebServerUrl routeExeWebServer;
    boolean killRouteDrawn = false;
    Intent tripLocService_intent;
    UpdateTripLocationsService tripLocService;
    boolean mServiceBound = false;
    LinearLayout tripStartBtnArea;
    LinearLayout timerarea;
    LinearLayout tripEndBtnArea;
    boolean isTripCancelPressed = false;
    boolean isTripStart = false;
    String reason = "";
    String comment = "";
    String REQUEST_TYPE = "";
    String deliveryVerificationCode = "";
    android.support.v7.app.AlertDialog deliveryEndDialog;
    String SITE_TYPE = "";
    String SITE_TYPE_DEMO_MSG = "";
    String imageType = "";
    String isFrom = "";
    Dialog uploadServicePicAlertBox = null;
    LinearLayout destLocSearchArea;
    UpdateFrequentTask timerrequesttask;
    ArrayList<HashMap<String, String>> list;
    ArrayList<HashMap<String, String>> tripDetail;
    HashMap<String, String> tempMap;
    OnGoingTripDetailAdapter onGoingTripDetailAdapter;
    RecyclerView onGoingTripsDetailListRecyclerView;
    SimpleRatingBar ratingBar;
    SelectableRoundedImageView user_img;
    ArrayList<Double> additonallist = new ArrayList<>();
    String currencetprice = "0.00";
    String CurrencySymbol = "";
    MTextView userNameTxt, userAddressTxt, progressHinttext, timerHinttext, tollTxtView;
    MTextView txt_TimerHour, txt_TimerMinute, txt_TimerSecond;
    LinearLayout timerlayoutarea;
    String required_str = "";
    String invalid_str = "";
    android.support.v7.app.AlertDialog alertDialog;
    boolean isresume = false;
    int i = 0;
    View slideback;
    ImageView imageslide;
    android.support.v7.app.AlertDialog list_navigation;
    NestedScrollView scrollview;
    Menu menu;
    boolean isendslide = false;
    UpdateDirections updateDirections;
    Marker driverMarker;
    boolean isnotification = false;
    ImageView googleImage;
    InternetConnection intCheck;
    double finaltotal = 0.00;

    double miscfee = 0.00;
    private MTextView tvHour, tvMinute, tvSecond, btntimer;
    private String selectedImagePath = "";
    private String pathForCameraImage = "";
    private Uri fileUri;
    private String TripTimeId = "";
    String userProfileJson = "";
    AnimateMarker animateMarker;

    LinearLayout uploadImgArea;
    boolean isCurrentLocationFocused = false;

    boolean isufx = false;

    String eConfirmByUser = "No";
    String payableAmount = "";

    String latitude = "";
    String longitirude = "";
    String address = "";
    double tollamount = 0.0;
    String tollcurrancy = "";
    boolean istollIgnore = false;
    android.support.v7.app.AlertDialog tolltax_dialog;

    String eTollConfirmByUser = "";

    FrameLayout bottomArea;


    android.support.v7.app.AlertDialog alertDialog_surgeConfirm;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UpdateTripLocationsService.MyBinder myBinder = (UpdateTripLocationsService.MyBinder) service;
            tripLocService = myBinder.getService();
            tripLocService.startUpdate(tripId);
            mServiceBound = true;


        }
    };

    private void defaultAddtionalprice() {
        additonallist.add(0, 0.00);
        additonallist.add(1, 0.00);
        additonallist.add(2, 0.00);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_trip);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        animateMarker = new AnimateMarker();
        generalFunc = new GeneralFunctions(getActContext());
        animateMarker = new AnimateMarker();
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

        isnotification = getIntent().getBooleanExtra("isnotification", isnotification);
        CurrencySymbol = generalFunc.getJsonValue("CurrencySymbol", generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON));

        defaultAddtionalprice();

        intCheck = new InternetConnection(getActContext());

        HashMap<String, String> data = (HashMap<String, String>) getIntent().getSerializableExtra("TRIP_DATA");
        this.data_trip = data;

        //gps view declaration start

        bottomArea = (FrameLayout) findViewById(R.id.bottomArea);

        scrollview = (NestedScrollView) findViewById(R.id.scrollview);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        onGoingTripsDetailListRecyclerView = (RecyclerView) findViewById(R.id.onGoingTripsDetailListRecyclerView);
        userNameTxt = (MTextView) findViewById(R.id.userNameTxt);
        userAddressTxt = (MTextView) findViewById(R.id.userAddressTxt);
        ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);
        tvHour = (MTextView) findViewById(R.id.txtTimerHour);
        tvMinute = (MTextView) findViewById(R.id.txtTimerMinute);
        tvSecond = (MTextView) findViewById(R.id.txtTimerSecond);
        addressTxt = (MTextView) findViewById(R.id.addressTxt);
        progressHinttext = (MTextView) findViewById(R.id.progressHinttext);
        timerHinttext = (MTextView) findViewById(R.id.timerHinttext);
        btntimer = (MTextView) findViewById(R.id.btn_timer);
        btntimer.setOnClickListener(new setOnClickAct());
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);
        tripStartBtnArea = (LinearLayout) findViewById(R.id.tripStartBtnArea);
        timerarea = (LinearLayout) findViewById(R.id.timerarea);
        timerlayoutarea = (LinearLayout) findViewById(R.id.timerlayoutarea);
        tripEndBtnArea = (LinearLayout) findViewById(R.id.tripEndBtnArea);
        destLocSearchArea = (LinearLayout) findViewById(R.id.destLocSearchArea);
        timeTxt = (MTextView) findViewById(R.id.timeTxt);
        timeTxt.setVisibility(View.GONE);
        googleImage = (ImageView) findViewById(R.id.googleImage);

        txt_TimerHour = (MTextView) findViewById(R.id.txt_TimerHour);
        txt_TimerMinute = (MTextView) findViewById(R.id.txt_TimerMinute);
        txt_TimerSecond = (MTextView) findViewById(R.id.txt_TimerSecond);
        tollTxtView = (MTextView) findViewById(R.id.tollTxtView);

        user_img = (SelectableRoundedImageView) findViewById(R.id.user_img);

        emeTapImgView = (ImageView) findViewById(R.id.emeTapImgView);
        emeTapImgView.setOnClickListener(new setOnClickList());

        slideback = (View) findViewById(R.id.slideback);
        imageslide = (ImageView) findViewById(R.id.imageslide);

        (findViewById(R.id.backImgView)).setVisibility(View.GONE);

        tripLocService_intent = new Intent(getActContext(), UpdateTripLocationsService.class);

        generalFunc.storedata(CommonUtilities.IsTripStarted, "No");

        tripLocService_intent.putExtra("GeneratedTripID", "" + data_trip.get("TripId"));

        currencetprice = data_trip.get("fVisitFee");
        startLocationUpdateService = new Intent(getApplicationContext(), UpdateDriverLocationService.class);
        startLocationUpdateService.putExtra("PAppVersion", data_trip.get("PAppVersion"));

        new CreateRoundedView(getResources().getColor(android.R.color.transparent), Utils.dipToPixels(getActContext(), 15), 0,
                Color.parseColor("#00000000"), user_img);

        setData();
        setLabels();

        new CreateRoundedView(getResources().getColor(R.color.colorPrimary), Utils.dipToPixels(getActContext(), 60), 0, 0, findViewById(R.id.slideback));

        map.getMapAsync(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleTxt.getLayoutParams();
        params.setMargins(Utils.dipToPixels(getActContext(), 20), 0, 0, 0);
        titleTxt.setLayoutParams(params);

        tripStartBtnArea.setOnTouchListener(new setOnTouchList());
        tripEndBtnArea.setOnTouchListener(new setOnTouchList());

        new Handler().postDelayed(() ->{
            startLocationUpdateService.setAction(UpdateTripLocationsService.ACTION_START_FOREGROUND_SERVICE);
            startService(startLocationUpdateService);
        }, 4000);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                generalFunc.restartApp(LauncherActivity.class);
            }
        }

        if (generalFunc.isRTLmode()) {
            (findViewById(R.id.navStripImgView)).setRotation(180);
            (findViewById(R.id.endTripBtnArrow)).setRotation(180);
            (findViewById(R.id.startTripBtnArrow)).setRotation(180);
            (findViewById(R.id.bottomArea)).setRotation(180);
        }

        //calculateData("", finalvalTxt);

        if (isnotification) {
            //new OpenPassengerDetailDialog(getActContext(), data_trip, generalFunc, isnotification);
        }

        if (Utils.checkText(generalFunc.retrieveValue("OPEN_CHAT"))) {
            JSONObject OPEN_CHAT_DATA_OBJ = generalFunc.getJsonObject(generalFunc.retrieveValue("OPEN_CHAT"));
            generalFunc.removeValue("OPEN_CHAT");

        /*    Bundle bnChat = new Bundle();

            bnChat.putString("iFromMemberId", data_trip.get("PassengerId"));
            bnChat.putString("FromMemberImageName", data_trip.get("PPicName"));
            bnChat.putString("iTripId", data_trip.get("iTripId"));
            bnChat.putString("FromMemberName", data_trip.get("PName"));*/

            if (OPEN_CHAT_DATA_OBJ!=null)
                new StartActProcess(getActContext()).startActWithData(ChatActivity.class, generalFunc.createChatBundle(OPEN_CHAT_DATA_OBJ));
        }

        // handleNoNetworkDial();
        // handleNoLocationDial();
    }

    public void setTimetext(String distance, String time) {
        try {
            String userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            String distance_str = "";

            timeTxt.setVisibility(View.VISIBLE);
            Utils.printLog("eUnit", "::" + generalFunc.getJsonValue("eUnit", userProfileJson));
            if (userProfileJson != null && !generalFunc.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
                timeTxt.setText(time + " " + generalFunc.retrieveLangLBl("to reach", "LBL_REACH_TXT") + " & " + distance + " " + generalFunc.retrieveLangLBl("", "LBL_MILE_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("away", "LBL_AWAY_TXT"));
            } else {
                timeTxt.setText("Destino a "+ time + " & " + distance + " " + generalFunc.retrieveLangLBl("", "LBL_KM_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("away", "LBL_AWAY_TXT"));
            }


        } catch (Exception e) {

        }

    }

    public void handleNoLocationDial() {
        if (generalFunc.isLocationEnabled()) {
            resetData();
        }
    }

    private void resetData() {
        if (intCheck.isNetworkConnected() && intCheck.check_int() && addressTxt.getText().equals(generalFunc.retrieveLangLBl("Loading address", "LBL_LOAD_ADDRESS"))) {
            setData();
        }

        if (!isCurrentLocationFocused) {
            setData();
            checkUserLocation();
        } else {
            checkUserLocation();
        }

        if (gMap == null && map != null && intCheck.isNetworkConnected() && intCheck.check_int())
            map.getMapAsync(this);
    }

    public void internetIsBack() {
        Utils.printELog("InternetIsBack","Called");
        if (updateDirections != null) {
            updateDirections.scheduleDirectionUpdate();
        }
    }

    public void checkUserLocation() {
        if (generalFunc.isLocationEnabled() && (userLocation == null || userLocation.getLatitude() == 0.0 || userLocation.getLongitude() == 0.0)) {
            showprogress();
        } else {
            hideprogress();
        }
    }

    public void showprogress() {
        isCurrentLocationFocused = false;
        findViewById(R.id.errorLocArea).setVisibility(View.VISIBLE);
        googleImage.setVisibility(View.GONE);


        findViewById(R.id.mProgressBar).setVisibility(View.VISIBLE);
        ((ProgressBar) findViewById(R.id.mProgressBar)).setIndeterminate(true);
        ((ProgressBar) findViewById(R.id.mProgressBar)).getIndeterminateDrawable().setColorFilter(
                getActContext().getResources().getColor(R.color.appThemeColor_1), android.graphics.PorterDuff.Mode.SRC_IN);

    }

    public void hideprogress() {

        findViewById(R.id.errorLocArea).setVisibility(View.GONE);

        if (findViewById(R.id.mProgressBar) != null) {
            findViewById(R.id.mProgressBar).setVisibility(View.GONE);
        }

        if (data_trip != null && (data_trip.get("eFareType").equals(Utils.CabFaretypeFixed) || data_trip.get("eFareType").equals(Utils.CabFaretypeHourly))) {
            googleImage.setVisibility(View.GONE);
        } else {
            if (eType.equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                googleImage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        outState.putString("RESTART_STATE", "true");
        outState.putParcelable("file_uri", fileUri);
        super.onSaveInstanceState(outState);
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("En Route", "LBL_EN_ROUTE_TXT"));//wls
        timeTxt.setText("--" + generalFunc.retrieveLangLBl("to reach", "LBL_REACH_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        invalid_str = generalFunc.retrieveLangLBl("Invalid value", "LBL_DIGIT_REQUIRE");

        ((MTextView) findViewById(R.id.placeTxtView)).setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
        ((MTextView) findViewById(R.id.navigateTxt)).setText(generalFunc.retrieveLangLBl("Navigate", "LBL_NAVIGATE"));

        timerHinttext.setText(generalFunc.retrieveLangLBl("JOB TIMER", "LBL_JOB_TIMER_HINT"));
        progressHinttext.setText(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT"));

        txt_TimerHour.setText(generalFunc.retrieveLangLBl("", "LBL_HOUR_TXT"));
        txt_TimerMinute.setText(generalFunc.retrieveLangLBl("", "LBL_MINUTES_TXT"));
        txt_TimerSecond.setText(generalFunc.retrieveLangLBl("", "LBL_SECONDS_TXT"));

        tollTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_SKIP_HELP"));

        if (eType.equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
            ((MTextView) findViewById(R.id.startTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_BEGIN_TRIP_TXT"));
            ((MTextView) findViewById(R.id.endTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_END_TRIP_TXT"));
        } else if (eType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            ((MTextView) findViewById(R.id.startTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_BEGIN_JOB_TXT"));
            ((MTextView) findViewById(R.id.endTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_END_JOB_TXT"));

            bottomArea.setVisibility(View.GONE);
            ((MTextView) findViewById(R.id.startTripTxt)).setPaddingRelative(Utils.dipToPixels(getActContext(), 45), 0, 0, 0);
            ((MTextView) findViewById(R.id.endTripTxt)).setPaddingRelative(Utils.dipToPixels(getActContext(), 45), 0, 0, 0);
        } else {
            ((MTextView) findViewById(R.id.startTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SLIDE_BEGIN_DELIVERY"));
            ((MTextView) findViewById(R.id.endTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SLIDE_END_DELIVERY"));
            bottomArea.setVisibility(View.GONE);
            googleImage.setVisibility(View.GONE);
            ((MTextView) findViewById(R.id.startTripTxt)).setPaddingRelative(Utils.dipToPixels(getActContext(), 45), 0, 0, 0);
            ((MTextView) findViewById(R.id.endTripTxt)).setPaddingRelative(Utils.dipToPixels(getActContext(), 45), 0, 0, 0);

        }

        setButtonName();

        ((MTextView) findViewById(R.id.errorTitleTxt)).setText(generalFunc.retrieveLangLBl("Waiting for your location.", "LBL_LOCATION_FATCH_ERROR_TXT"));

        ((MTextView) findViewById(R.id.errorSubTitleTxt)).setText(generalFunc.retrieveLangLBl("Try to fetch  your accurate location. \"If you still face the problem, go to open sky instead of closed area\".", "LBL_NO_LOC_GPS_TXT"));
    }

    public void setButtonName() {

        if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
            ((MTextView) findViewById(R.id.startTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_BEGIN_TRIP_TXT"));
            ((MTextView) findViewById(R.id.endTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_END_TRIP_TXT"));
        } else if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            ((MTextView) findViewById(R.id.startTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_BEGIN_JOB_TXT"));
            ((MTextView) findViewById(R.id.endTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SLIDE_END_JOB_TXT"));
        } else {
            ((MTextView) findViewById(R.id.startTripTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SLIDE_BEGIN_DELIVERY"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.gMap = googleMap;
        if (generalFunc.checkLocationPermission(true) == true) {
            getMap().setMyLocationEnabled(false);
        }

        if (generalFunc.isRTLmode()) {
            getMap().setPadding(13, 0, 0, 0);
        } else {
            getMap().setPadding(13, 0, 150, 0);
        }

        getMap().getUiSettings().setTiltGesturesEnabled(false);
        getMap().getUiSettings().setCompassEnabled(false);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);

        if (isDestinationAdded == true) {
            addDestinationMarker();
        }

        if (isDestinationAdded == true && userLocation != null && route_polyLine == null) {
//            drawRoute("" + destLocLatitude, "" + destLocLongitude);
            if (updateDirections != null) {
                Location destLoc = new Location("gps");
                destLoc.setLatitude(destLocLatitude);
                destLoc.setLongitude(destLocLongitude);
                updateDirections.changeUserLocation(destLoc);
            }
        }

        getMap().setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            return true;
        });

        checkUserLocation();


        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
        //GetLocationUpdates.getInstance().setTripStartValue(true, true, data_trip.get("TripId"));
        GetLocationUpdates.getInstance().startLocationUpdates(this, this);
    }


    public void addDestinationMarker() {
        try {
            if (getMap() == null) {
                return;
            }
            if (destLocMarker != null) {
                destLocMarker.remove();

            }
            if (route_polyLine != null) {
                route_polyLine.remove();
            }

            MarkerOptions markerOptions_destLocation = new MarkerOptions();
            markerOptions_destLocation.position(new LatLng(destLocLatitude, destLocLongitude));
            markerOptions_destLocation.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_dest_marker)).anchor(0.5f,
                    0.5f);
            destLocMarker = getMap().addMarker(markerOptions_destLocation);
        } catch (Exception e) {

        }
    }

    public void addSourceMarker() {
        if (getMap() == null) {
            return;
        }
        double latitude = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLatitude"));
        double longitude = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLongitude"));
        MarkerOptions markerOptions_destLocation = new MarkerOptions();
        markerOptions_destLocation.position(new LatLng(latitude, longitude));
        markerOptions_destLocation.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source_marker)).anchor(0.5f,
                0.5f);
        getMap().addMarker(markerOptions_destLocation);
    }

    public GoogleMap getMap() {
        return this.gMap;
    }

    private void setDriverDetail() {

        String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Passenger/" + tripDetail.get(0).get("iDriverId") + "/"
                + tripDetail.get(0).get("driverImage");

        Picasso.with(getActContext())
                .load(image_url)
                .placeholder(R.mipmap.ic_no_pic_user)
                .error(R.mipmap.ic_no_pic_user)
                .into(((ImageView) findViewById(R.id.user_img)));

        userNameTxt.setText(tripDetail.get(0).get("driverName"));
        userAddressTxt.setText(tripDetail.get(0).get("tSaddress"));
        ratingBar.setRating(generalFunc.parseFloatValue(0, tripDetail.get(0).get("driverRating")));

    }

    public void setData() {

        tripId = data_trip.get("TripId");
        eType = data_trip.get("REQUEST_TYPE");
        deliveryVerificationCode = data_trip.get("vDeliveryConfirmCode");

        if (!data_trip.get("DestLocLatitude").equals("") && !data_trip.get("DestLocLatitude").equals("0")
                && !data_trip.get("DestLocLongitude").equals("") && !data_trip.get("DestLocLongitude").equals("0")) {

            setDestinationPoint(data_trip.get("DestLocLatitude"), data_trip.get("DestLocLongitude"), data_trip.get("DestLocAddress"), true);
            (findViewById(R.id.destLocSearchArea)).setVisibility(View.GONE);

        } else {
            (findViewById(R.id.destLocSearchArea)).setOnClickListener(new setOnClickAct());
            (findViewById(R.id.destLocSearchArea)).setVisibility(View.VISIBLE);
            (findViewById(R.id.navigationViewArea)).setVisibility(View.GONE);
            tollTxtView.setVisibility(View.GONE);
        }


        if (!data_trip.get("vTripStatus").equals("Arrived")) {
            tripStartBtnArea.setVisibility(View.GONE);
            tripEndBtnArea.setVisibility(View.VISIBLE);
            (findViewById(R.id.navigateArea)).setVisibility(View.VISIBLE);
            isendslide = true;
            invalidateOptionsMenu();
            imageslide.setImageResource(R.mipmap.ic_trip_btn);

            configTripStartView();

            if (data_trip.get("eFareType").equals(Utils.CabFaretypeHourly)) {

                countDownStart();
                if (data_trip.get("TimeState") != null && !data_trip.get("TimeState").equals("")) {
                    if (data_trip.get("TimeState").equalsIgnoreCase("Resume")) {

                        isresume = true;
                        btntimer.setText(generalFunc.retrieveLangLBl("pause", "LBL_PAUSE_TEXT"));
                        btntimer.setVisibility(View.VISIBLE);

                    } else {
                        if (timerrequesttask != null) {
                            timerrequesttask.stopRepeatingTask();
                            timerrequesttask = null;
                        }

                        isresume = false;
                        btntimer.setText(generalFunc.retrieveLangLBl("resume", "LBL_RESUME_TEXT"));
                        btntimer.setVisibility(View.VISIBLE);

                    }
                }

                if (data_trip.get("TotalSeconds") != null && !data_trip.get("TotalSeconds").equals("")) {
                    i = Integer.parseInt(data_trip.get("TotalSeconds"));
                    setTimerValues();

                }
                if (data_trip.get("iTripTimeId") != null && !data_trip.get("iTripTimeId").equals("")) {
                    TripTimeId = data_trip.get("iTripTimeId");
                    //  countDownStart();
                }
            }

        }

        REQUEST_TYPE = data_trip.get("REQUEST_TYPE");
        SITE_TYPE = data_trip.get("SITE_TYPE");
        deliveryVerificationCode = data_trip.get("vDeliveryConfirmCode");

        setButtonName();
        if (data_trip.get("REQUEST_TYPE").equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            getTripDeliveryLocations();
            if (data_trip.get("eFareType").equals(Utils.CabFaretypeRegular)) {
                timerarea.setVisibility(View.GONE);
                scrollview.setVisibility(View.GONE);
                timerlayoutarea.setVisibility(View.GONE);
            } else if (data_trip.get("eFareType").equals(Utils.CabFaretypeFixed)) {
                timerarea.setVisibility(View.VISIBLE);
                googleImage.setVisibility(View.GONE);
                scrollview.setVisibility(View.VISIBLE);
                timerlayoutarea.setVisibility(View.GONE);
                emeTapImgView.setVisibility(View.GONE);
                //btntimer.setVisibility(View.GONE);
            } else if (data_trip.get("eFareType").equals(Utils.CabFaretypeHourly)) {
                //btntimer.setVisibility(View.VISIBLE);
                timerarea.setVisibility(View.VISIBLE);
                googleImage.setVisibility(View.GONE);
                scrollview.setVisibility(View.VISIBLE);
                emeTapImgView.setVisibility(View.GONE);
                timerlayoutarea.setVisibility(View.VISIBLE);

            } else {
                timerarea.setVisibility(View.GONE);
            }
        } else {
            try {

                timerarea.setVisibility(View.GONE);
                scrollview.setVisibility(View.GONE);
                timerlayoutarea.setVisibility(View.GONE);
                emeTapImgView.setVisibility(View.VISIBLE);
            } catch (Exception e) {

            }
        }
    }


    @Override
    public void onLocationUpdate(Location location) {

        if (location == null) {
            return;
        }


        if (userProfileJson != null && generalFunc.getJsonValue("ENABLE_DIRECTION_SOURCE_DESTINATION_DRIVER_APP", userProfileJson).equalsIgnoreCase("Yes")) {
            if (location != null && (this.userLocation == null || !isCurrentLocationFocused)) {
                this.userLocation = location;
                CameraPosition cameraPosition = cameraForUserPosition(true);
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                CameraPosition cameraPosition = cameraForUserPosition(false);
                getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 800, null);
            }
        } else {
            this.userLocation = location;
            if (!data_trip.get("DestLocLatitude").equals("") && !data_trip.get("DestLocLatitude").equals("0")
                    && !data_trip.get("DestLocLongitude").equals("") && !data_trip.get("DestLocLongitude").equals("0")) {

                double passenger_lat = generalFunc.parseDoubleValue(0.0, data_trip.get("DestLocLatitude"));
                double passenger_lon = generalFunc.parseDoubleValue(0.0, data_trip.get("DestLocLongitude"));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
                builder.include(new LatLng(passenger_lat, passenger_lon));
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));

            } else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
                getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
            }

        }

        updateDriverMarker(new LatLng(location.getLatitude(), location.getLongitude()));

        this.userLocation = location;
        checkUserLocation();

//        if (!data_trip.get("REQUEST_TYPE").equalsIgnoreCase("UberX")) {
        if (data_trip.get("REQUEST_TYPE").equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            if (data_trip.get("eFareType").equals(Utils.CabFaretypeRegular)) {
                if (updateDirections == null) {
                    Location destLoc = new Location("temp");
                    destLoc.setLatitude(destLocLatitude);
                    destLoc.setLongitude(destLocLongitude);
                    updateDirections = new UpdateDirections(getActContext(), gMap, userLocation, destLoc);
                    updateDirections.scheduleDirectionUpdate();
                }

            } else if (data_trip.get("eFareType").equals(Utils.CabFaretypeFixed)) {
                //    timeTxt.setVisibility(View.GONE);
                return;

            } else if (data_trip.get("eFareType").equals(Utils.CabFaretypeHourly)) {
                // timeTxt.setVisibility(View.GONE);
                return;
            } else {
                if (updateDirections == null) {
                    Location destLoc = new Location("temp");
                    destLoc.setLatitude(destLocLatitude);
                    destLoc.setLongitude(destLocLongitude);


                    updateDirections = new UpdateDirections(getActContext(), gMap, userLocation, destLoc);
                    updateDirections.scheduleDirectionUpdate();
                }

            }
        } else {
            if (updateDirections == null) {
                Location destLoc = new Location("temp");
                destLoc.setLatitude(destLocLatitude);
                destLoc.setLongitude(destLocLongitude);


                updateDirections = new UpdateDirections(getActContext(), gMap, userLocation, destLoc);
                updateDirections.scheduleDirectionUpdate();
            }
        }

        if (updateDirections != null) {
            updateDirections.changeUserLocation(location);
        }

    }


    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void updateDriverMarker(final LatLng newLocation) {

        if (driverMarker == null) {

            int iconId = R.mipmap.car_driver;

            if (data_trip.containsKey("vVehicleType")) {
                if (data_trip.get("vVehicleType").equalsIgnoreCase("Bike")) {
                    iconId = R.mipmap.car_driver_1;
                } else if (data_trip.get("vVehicleType").equalsIgnoreCase("Cycle")) {
                    iconId = R.mipmap.car_driver_2;
                } else if (data_trip.get("vVehicleType").equalsIgnoreCase("Truck")) {
                    iconId = R.mipmap.car_driver_4;
                }
            }

            MarkerOptions markerOptions_driver = new MarkerOptions();
            markerOptions_driver.position(newLocation);
            markerOptions_driver.icon(BitmapDescriptorFactory.fromResource(iconId)).anchor(0.5f, 0.5f).flat(true);

            driverMarker = gMap.addMarker(markerOptions_driver);
            driverMarker.setTitle(generalFunc.getMemberId());
        }



        if (this.userLocation != null && newLocation != null) {
            LatLng currentLatLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
            float rotation = driverMarker == null ? 0 : driverMarker.getRotation();

            if (animateMarker.currentLng != null) {
                rotation = (float) animateMarker.bearingBetweenLocations(animateMarker.currentLng, newLocation);
            } else {
                rotation = (float) animateMarker.bearingBetweenLocations(currentLatLng, newLocation);
            }


            if (driverMarker != null) {
                driverMarker.setTitle(generalFunc.getMemberId());
            }

            HashMap<String, String> previousItemOfMarker = animateMarker.getLastLocationDataOfMarker(driverMarker);

            HashMap<String, String> data_map = new HashMap<>();
            data_map.put("vLatitude", "" + newLocation.latitude);
            data_map.put("vLongitude", "" + newLocation.longitude);
            data_map.put("iDriverId", "" + generalFunc.getMemberId());
            data_map.put("RotationAngle", "" + rotation);
            data_map.put("LocTime", "" + System.currentTimeMillis());

            Location location = new Location("marker");
            location.setLatitude(newLocation.latitude);
            location.setLongitude(newLocation.longitude);


            if (animateMarker.toPositionLat.get("" + newLocation.latitude) == null && animateMarker.toPositionLat.get("" + newLocation.longitude) == null) {
                if (previousItemOfMarker.get("LocTime") != null && !previousItemOfMarker.get("LocTime").equals("")) {

                    long previousLocTime = generalFunc.parseLongValue(0, previousItemOfMarker.get("LocTime"));
                    long newLocTime = generalFunc.parseLongValue(0, data_map.get("LocTime"));

                    if (previousLocTime != 0 && newLocTime != 0) {

                        if ((newLocTime - previousLocTime) > 0 && animateMarker.driverMarkerAnimFinished == false) {
                            animateMarker.addToListAndStartNext(driverMarker, this.gMap, location, rotation, 1200, tripId, data_map.get("LocTime"));
                        } else if ((newLocTime - previousLocTime) > 0) {
                            animateMarker.animateMarker(driverMarker, this.gMap, location, rotation, 1200, tripId, data_map.get("LocTime"));
                        }

                    } else if ((previousLocTime == 0 || newLocTime == 0) && animateMarker.driverMarkerAnimFinished == false) {
                        animateMarker.addToListAndStartNext(driverMarker, this.gMap, location, rotation, 1200, tripId, data_map.get("LocTime"));
                    } else {
                        animateMarker.animateMarker(driverMarker, this.gMap, location, rotation, 1200, tripId, data_map.get("LocTime"));
                    }
                } else if (animateMarker.driverMarkerAnimFinished == false) {
                    animateMarker.addToListAndStartNext(driverMarker, this.gMap, location, rotation, 1200, tripId, data_map.get("LocTime"));
                } else {
                    animateMarker.animateMarker(driverMarker, this.gMap, location, rotation, 1200, tripId, data_map.get("LocTime"));
                }
            }
        }
    }


    public CameraPosition cameraForUserPosition(boolean isFirst) {
        double currentZoomLevel = getMap().getCameraPosition().zoom;

        if (isFirst) {
            isCurrentLocationFocused = true;
            currentZoomLevel = Utils.defaultZomLevel;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                .zoom((float) currentZoomLevel).build();

        return cameraPosition;
    }

    public void tripCancelled(String msg) {

        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            generalFunc.saveGoOnlineInfo();
            // generalFunc.restartApp();

            (new getUserData(generalFunc, getActContext())).getData();

        });
        generateAlert.setContentMessage("", msg);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }

    public void getTripDeliveryLocations() {

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getTripDeliveryLocations");
        parameters.put("iTripId", data_trip.get("iTripId"));
        parameters.put("userType", "Driver");

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {


                if (generalFunc.checkDataAvail(CommonUtilities.action_str, responseString) == true) {
                    list = new ArrayList<>();

                    String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);


                    tripDetail = new ArrayList<HashMap<String, String>>();
                    JSONArray tripLocations = generalFunc.getJsonArray("States", message);
                    String driverdetails = generalFunc.getJsonValue("driverDetails", message);
                    tempMap = new HashMap<>();
                    tempMap.put("driverImage", generalFunc.getJsonValue("riderImage", driverdetails));
                    tempMap.put("driverName", generalFunc.getJsonValue("riderName", driverdetails));
                    tempMap.put("driverRating", generalFunc.getJsonValue("riderRating", driverdetails));
                    tempMap.put("tSaddress", generalFunc.getJsonValue("tSaddress", driverdetails));
                    tempMap.put("iDriverId", generalFunc.getJsonValue("iUserId", driverdetails));

                    tripDetail.add(tempMap);


                    list.clear();
                    if (tripLocations != null)
                        for (int i = 0; i < tripLocations.length(); i++) {
                            tempMap = new HashMap<>();

                            JSONObject jobject1 = generalFunc.getJsonObject(tripLocations, i);
                            tempMap.put("status", generalFunc.getJsonValue("type", jobject1.toString()));
                            tempMap.put("iTripId", generalFunc.getJsonValue("text", jobject1.toString()));

                            tempMap.put("value", generalFunc.getJsonValue("timediff", jobject1.toString()));
                            tempMap.put("Booking_LBL", generalFunc.retrieveLangLBl("", "LBL_BOOKING"));
                            tempMap.put("time", generalFunc.getJsonValue("time", jobject1.toString()));
                            tempMap.put("msg", generalFunc.getJsonValue("text", jobject1.toString()));
                            list.add(tempMap);
                        }
                    setView();

                    setDriverDetail();
                } else {

                }
            } else {

            }
        });
        exeWebServer.execute();
    }

    public void getMaskNumber() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCallMaskNumber");
        parameters.put("iTripid", data_trip.get("iTripId"));
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {
                    String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                    call(message);
                } else {
                    call(data_trip.get("PPhone"));

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void call(String phoneNumber) {
        try {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.trip_accept_menu, menu);

        if (REQUEST_TYPE.equals("Deliver")) {

            menu.findItem(R.id.menu_passenger_detail).setTitle(generalFunc.retrieveLangLBl("View Delivery Details", "LBL_VIEW_DELIVERY_DETAILS"));
            if (!isendslide) {
                menu.findItem(R.id.menu_cancel_trip).setTitle(generalFunc.retrieveLangLBl("Cancel Delivery", "LBL_CANCEL_DELIVERY"));
            } else {
                MenuItem item = menu.findItem(R.id.menu_cancel_trip);
                item.setVisible(false);

            }
        } else {
            try {
                if (data_trip.get("eHailTrip").equalsIgnoreCase("Yes")) {
                    menu.findItem(R.id.menu_passenger_detail).setTitle(generalFunc.retrieveLangLBl("View passenger detail", "LBL_VIEW_PASSENGER_DETAIL")).setVisible(false);
                    menu.findItem(R.id.menu_call).setTitle(generalFunc.retrieveLangLBl("Call", "LBL_CALL_ACTIVE_TRIP")).setVisible(false);
                    menu.findItem(R.id.menu_message).setTitle(generalFunc.retrieveLangLBl("Message", "LBL_MESSAGE_ACTIVE_TRIP")).setVisible(false);
                } else {
                    menu.findItem(R.id.menu_passenger_detail).setTitle(generalFunc.retrieveLangLBl("View passenger detail", "LBL_VIEW_PASSENGER_DETAIL")).setVisible(false);
                }
            } catch (Exception e) {
                menu.findItem(R.id.menu_passenger_detail).setTitle(generalFunc.retrieveLangLBl("View passenger detail", "LBL_VIEW_PASSENGER_DETAIL")).setVisible(false);
            }
            menu.findItem(R.id.menu_cancel_trip).setTitle(generalFunc.retrieveLangLBl("Cancel trip", "LBL_CANCEL_TRIP"));
        }

        if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX))

        {
            menu.findItem(R.id.menu_cancel_trip).setTitle(generalFunc.retrieveLangLBl("", "LBL_CANCEL_JOB"));
        }

        menu.findItem(R.id.menu_specialInstruction).setTitle(generalFunc.retrieveLangLBl("Special Instruction", "LBL_SPECIAL_INSTRUCTION_TXT"));
        menu.findItem(R.id.menu_call).setTitle(generalFunc.retrieveLangLBl("Call", "LBL_CALL_ACTIVE_TRIP"));
        menu.findItem(R.id.menu_message).setTitle(generalFunc.retrieveLangLBl("Message", "LBL_MESSAGE_ACTIVE_TRIP"));
        menu.findItem(R.id.menu_sos).setTitle(generalFunc.retrieveLangLBl("Emergency or SOS", "LBL_EMERGENCY_SOS_TXT"));


        if (REQUEST_TYPE.equals(Utils.CabGeneralType_UberX)) {
            menu.findItem(R.id.menu_passenger_detail).setVisible(false);
            menu.findItem(R.id.menu_call).setVisible(true);
            menu.findItem(R.id.menu_message).setVisible(true);
            menu.findItem(R.id.menu_sos).setVisible(true);
            menu.findItem(R.id.menu_specialInstruction).setVisible(true);
            // if (!data_trip.get("eFareType").equals(Utils.CabFaretypeRegular)) {
            menu.findItem(R.id.menu_waybill_trip).setTitle(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL")).setVisible
                    (false);

            // }

            if (data_trip.get("eFareType").equals(Utils.CabFaretypeRegular)) {
                menu.findItem(R.id.menu_sos).setVisible(false);
            }


        } else {
            if (!data_trip.get("eHailTrip").equalsIgnoreCase("Yes")) {
                menu.findItem(R.id.menu_passenger_detail).setVisible(true);
                menu.findItem(R.id.menu_call).setVisible(false);
                menu.findItem(R.id.menu_message).setVisible(false);
                menu.findItem(R.id.menu_sos).setVisible(false);
                menu.findItem(R.id.menu_waybill_trip).setTitle(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL")).setVisible(true);
            } else {
                menu.findItem(R.id.menu_passenger_detail).setVisible(false);
                menu.findItem(R.id.menu_call).setVisible(false);
                menu.findItem(R.id.menu_message).setVisible(false);
                menu.findItem(R.id.menu_sos).setVisible(false);
                menu.findItem(R.id.menu_waybill_trip).setTitle(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL")).setVisible(true);

            }


        }

        Utils.setMenuTextColor(menu.findItem(R.id.menu_passenger_detail), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_cancel_trip), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_waybill_trip), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_sos), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_call), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_message), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_specialInstruction), getResources().getColor(R.color.appThemeColor_TXT_1));
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            // perform your desired action here

            // return 'true' to prevent further propagation of the key event
            return true;
        }

        // let the system handle all other key events
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_passenger_detail:
                new OpenPassengerDetailDialog(getActContext(), data_trip, generalFunc, false);
                return true;

            case R.id.menu_cancel_trip:
                new CancelTripDialog(getActContext(), data_trip, generalFunc, isTripStart);
                return true;

            case R.id.menu_waybill_trip:
                Bundle bn4 = new Bundle();
                bn4.putSerializable("data_trip", data_trip);
                new StartActProcess(getActContext()).startActWithData(WayBillActivity.class, bn4);
                return true;

            case R.id.menu_sos:
                Bundle bn = new Bundle();
                bn.putString("TripId", tripId);
                new StartActProcess(getActContext()).startActWithData(ConfirmEmergencyTapActivity.class, bn);
                return true;


            case R.id.menu_call:

                try {
                    getMaskNumber();
                } catch (Exception e) {
                }

                return true;
            case R.id.menu_message:

                Bundle bnChat = new Bundle();

                bnChat.putString("iFromMemberId", data_trip.get("PassengerId"));
                bnChat.putString("FromMemberImageName", data_trip.get("PPicName"));
                bnChat.putString("iTripId", data_trip.get("iTripId"));
                bnChat.putString("FromMemberName", data_trip.get("PName"));

                new StartActProcess(getActContext()).startActWithData(ChatActivity.class, bnChat);

                return true;

            case R.id.menu_specialInstruction:

                if (data_trip.get("tUserComment") != null && !data_trip.get("tUserComment").equals("")) {
                    generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Special Instruction", "LBL_SPECIAL_INSTRUCTION_TXT"), data_trip.get("tUserComment"));
                } else {
                    generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Special Instruction", "LBL_SPECIAL_INSTRUCTION_TXT"), generalFunc.retrieveLangLBl("", "LBL_NO_SPECIAL_INSTRUCTION"));

                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Context getActContext() {
        return ActiveTripActivity.this; // Must be context of activity not application
    }

    public void addDestination(final String latitude, final String longitude, final String address) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "addDestination");
        //  parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("Latitude", latitude);
        parameters.put("Longitude", longitude);
        parameters.put("Address", address);
        //   parameters.put("UserId", data_trip.get("PassengerId"));
        parameters.put("eConfirmByUser", eConfirmByUser);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);
        parameters.put("TripId", tripId);
        parameters.put("eTollConfirmByUser", eTollConfirmByUser);
        parameters.put("fTollPrice", tollamount + "");
        parameters.put("vTollPriceCurrencyCode", tollcurrancy);
        String tollskiptxt = "";
        if (istollIgnore) {
            tollamount = 0;
            tollskiptxt = "Yes";

        } else {
            tollskiptxt = "No";
        }
        parameters.put("eTollSkipped", tollskiptxt);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    if (istollIgnore) {
                        (new getUserData(generalFunc, getActContext())).getData();
                        return;
                    }

                    setDestinationPoint(latitude, longitude, address, true);

                    Location destLoc = new Location("gps");
                    destLoc.setLatitude(GeneralFunctions.parseDoubleValue(0.0, latitude));
                    destLoc.setLongitude(GeneralFunctions.parseDoubleValue(0.0, longitude));

                    if (updateDirections == null) {
                        updateDirections = new UpdateDirections(getActContext(), gMap, userLocation, destLoc);
                        updateDirections.scheduleDirectionUpdate();
                    } else {
                        updateDirections.changeDestLoc(destLoc);
                        updateDirections.updateDirections();

                    }
                    addDestinationMarker();
//                        drawRoute(latitude, longitude);
//                        if (updateDirections != null) {
//                            updateDirections.changeDestLoc(destLoc);
//                        }
                } else {

                    String msg_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);


                    if (msg_str.equalsIgnoreCase("LBL_DROP_LOCATION_NOT_ALLOW")) {
                        tollamount = 0.0;
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DROP_LOCATION_NOT_ALLOW"));
                        return;
                    }

                    if (msg_str.equalsIgnoreCase("Yes")) {
                        if (generalFunc.getJsonValue("SurgePrice", responseString).equalsIgnoreCase("")) {
                            openFixChargeDialog(responseString, false);
                        } else {
                            openFixChargeDialog(responseString, true);
                        }
                        return;
                    }

                    if (tollamount != 0.0 && tollamount != 0 && tollamount != 0.00) {

                        if (generalFunc.getJsonValue("SurgePrice", responseString).equalsIgnoreCase("")) {
                            TollTaxDialog();
                        } else {
                            TollTaxDialog();
                        }

                        return;
                    }


                    if (msg_str.equals(CommonUtilities.GCM_FAILED_KEY) || msg_str.equals(CommonUtilities.APNS_FAILED_KEY) || msg_str.equals("LBL_SERVER_COMM_ERROR")) {
                        generalFunc.restartApp(LauncherActivity.class);
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", msg_str));
                    }
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }


    public void openFixChargeDialog(String responseString, boolean isSurCharge) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);
        MTextView payableAmountTxt;
        MTextView payableTxt;

        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("", "LBL_FIX_FARE_HEADER")));


        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));
        payableTxt = (MTextView) dialogView.findViewById(R.id.payableTxt);
        payableAmountTxt = (MTextView) dialogView.findViewById(R.id.payableAmountTxt);
        if (!generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString).equalsIgnoreCase("")) {
            payableAmountTxt.setVisibility(View.VISIBLE);
            payableTxt.setVisibility(View.GONE);

            if (isSurCharge) {
                payableAmount = generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString) + " " + "(" + generalFunc.retrieveLangLBl("", "LBL_AT_TXT") + " " +
                        generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("SurgePrice", responseString)) + ")";
                ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(payableAmount));
            } else {
                payableAmount = generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString);
                ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(payableAmount));

            }
        } else {
            payableAmountTxt.setVisibility(View.GONE);
            payableTxt.setVisibility(View.VISIBLE);

        }

        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TXT"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {
            alertDialog_surgeConfirm.dismiss();
            eConfirmByUser = "Yes";
            addDestination(latitude, longitirude, address);
        });
        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(view -> {
            tollamount = 0.0;
            alertDialog_surgeConfirm.dismiss();

        });


        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.show();
    }

    public void setDestinationPoint(String latitude, String longitude, String address, boolean isDestinationAdded) {
        double dest_lat = generalFunc.parseDoubleValue(0.0, latitude);
        double dest_lon = generalFunc.parseDoubleValue(0.0, longitude);

        (findViewById(R.id.destLocSearchArea)).setVisibility(View.GONE);
        (findViewById(R.id.navigationViewArea)).setVisibility(View.VISIBLE);
        (findViewById(R.id.navigateArea)).setVisibility(View.VISIBLE);
        try {
            if (data_trip.get("eTollSkipped").equalsIgnoreCase("yes")) {
                tollTxtView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }

        if (address.equals("")) {
            addressTxt.setText(generalFunc.retrieveLangLBl("Loading address", "LBL_LOAD_ADDRESS"));
            GetAddressFromLocation getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
            getAddressFromLocation.setLocation(dest_lat, dest_lon);
            getAddressFromLocation.setAddressList((address1, latitude1, longitude1) -> addressTxt.setText(address1));
            getAddressFromLocation.execute();
        } else {
            addressTxt.setText(address);
        }

        (findViewById(R.id.navigateArea)).setOnClickListener(new setOnClickAct("" + dest_lat, "" + dest_lon));

        this.isDestinationAdded = isDestinationAdded;
        this.destLocLatitude = dest_lat;
        this.destLocLongitude = dest_lon;
    }

    public void setTripStart() {

        if (!TextUtils.isEmpty(isFrom) && imageType.equalsIgnoreCase("before")) {

            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("type", "StartTrip"));
            paramsList.add(generalFunc.generateImageParams("iDriverId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("TripID", tripId));
            paramsList.add(generalFunc.generateImageParams("iUserId", data_trip.get("PassengerId")));
            paramsList.add(generalFunc.generateImageParams("UserType", CommonUtilities.app_type));
            paramsList.add(generalFunc.generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("MemberType", CommonUtilities.app_type));
            paramsList.add(generalFunc.generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
            paramsList.add(generalFunc.generateImageParams("GeneralUserType", CommonUtilities.app_type));
            paramsList.add(generalFunc.generateImageParams("GeneralMemberId", generalFunc.getMemberId()));

            Utils.printLog("Api", "selectedImagePath" + selectedImagePath);

            new UploadProfileImage(ActiveTripActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, imageType).execute();

        } else {


            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "StartTrip");
            parameters.put("iDriverId", generalFunc.getMemberId());
            parameters.put("TripID", tripId);
            parameters.put("iUserId", data_trip.get("PassengerId"));
            parameters.put("UserType", CommonUtilities.app_type);


            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> startTripResponse(responseString));
            exeWebServer.execute();
        }
    }

    private void startTripResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            if (eType.equals("UberX")) {
                getTripDeliveryLocations();

            } else {
                try {
                    if (data_trip.get("eFareType") != null && !data_trip.get("eFareType").equals("")) {
                        if (data_trip.get("eFareType").equals(Utils.CabFaretypeFixed)) {

                            getTripDeliveryLocations();
                        } else if (data_trip.get("eFareType").equals(Utils.CabFaretypeHourly)) {
                            getTripDeliveryLocations();

                        }
                    }
                } catch (Exception e) {

                }
            }

            boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

            if (isDataAvail == true) {
                closeuploadServicePicAlertBox();

                currencetprice = generalFunc.getJsonValue("fVisitFee", responseString);
                if (REQUEST_TYPE.equals("Deliver")) {
                    SITE_TYPE = generalFunc.getJsonValue("SITE_TYPE", responseString);
                    deliveryVerificationCode = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                }
                if (data_trip.get("eFareType").equals(Utils.CabFaretypeHourly)) {
                    TripTimeId = generalFunc.getJsonValue("iTripTimeId", responseString);
//                    callsetTimeApi(true);
                    countDownStart();
                }
                configTripStartView();
                // countDownStart();
            } else {
                String msg_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                if (msg_str.equals(CommonUtilities.GCM_FAILED_KEY) || msg_str.equals(CommonUtilities.APNS_FAILED_KEY) || msg_str.equals("LBL_SERVER_COMM_ERROR")) {
                    generalFunc.restartApp(LauncherActivity.class);
                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", msg_str));
                }

            }
        } else {
            generalFunc.showError();
        }

    }

    public void configTripStartView() {

        isresume = true;
        btntimer.setVisibility(View.VISIBLE);
        //countDownStart();

        isTripStart = true;
        tripStartBtnArea.setVisibility(View.GONE);
        tripEndBtnArea.setVisibility(View.VISIBLE);
        (findViewById(R.id.navigateArea)).setVisibility(View.VISIBLE);
        isendslide = true;
        invalidateOptionsMenu();
        imageslide.setImageResource(R.mipmap.ic_trip_btn);
        tripLocService_intent.setAction(UpdateTripLocationsService.ACTION_START_FOREGROUND_SERVICE);
        startService(tripLocService_intent);
        bindService();
    }

    public void bindService() {
        bindService(tripLocService_intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void cancelTrip(String reason, String comment) {
        isTripCancelPressed = true;
        this.reason = reason;
        this.comment = comment;
        endTrip();

    }

    public void endTrip() {

        if (userLocation == null) {
            generalFunc.showMessage(generalFunc.getCurrentView(ActiveTripActivity.this), generalFunc.retrieveLangLBl("", "LBL_NO_LOCATION_FOUND_TXT"));
            return;
        }

        ArrayList<LatLng> store_locations = new ArrayList<>();
        ArrayList<String> store_locations_latitude = new ArrayList<String>();
        ArrayList<String> store_locations_longitude = new ArrayList<String>();

        if (tripLocService != null) {
            tripLocService.endTrip();
            store_locations = tripLocService.getListOfLocations();
        }

        if (store_locations.size() > 0) {

            for (int i = 0; i < store_locations.size(); i++) {

                LatLng locations = store_locations.get(i);

                double latitude = locations.latitude;
                double longitude = locations.longitude;

                store_locations_latitude.add("" + latitude);
                store_locations_longitude.add("" + longitude);
            }

        }

        if (userLocation != null) {
            getDestinationAddress(store_locations_latitude, store_locations_longitude, "" + userLocation.getLatitude(), "" + userLocation.getLongitude());
        }
    }

    public void getDestinationAddress(final ArrayList<String> store_locations_latitude, final ArrayList<String> store_locations_longitude,
                                      String endLatitude, String endLongitude) {

        final MyProgressDialog myPDialog = showLoader();

        GetAddressFromLocation getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
        getAddressFromLocation.setLocation(generalFunc.parseDoubleValue(0.0, endLatitude), generalFunc.parseDoubleValue(0.0, endLongitude));
        getAddressFromLocation.setIsDestination(true);
        getAddressFromLocation.setAddressList((address, latitude, longitude) -> {

            closeLoader(myPDialog);

            if (address.equals("")) {
                generalFunc.showError();
            } else {
                setTripEnd(store_locations_latitude, store_locations_longitude,
                        "" + userLocation.getLatitude(), "" + userLocation.getLongitude(), address);
            }
        });
        getAddressFromLocation.execute();
    }

    public MyProgressDialog showLoader() {
        MyProgressDialog myPDialog = new MyProgressDialog(getActContext(), false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        myPDialog.show();

        return myPDialog;
    }

    public void closeLoader(MyProgressDialog myPDialog) {
        myPDialog.close();
    }

    public void setTripEnd(ArrayList<String> store_locations_latitude, ArrayList<String> store_locations_longitude, String endLatitude, String endLongitude, String destAddress) {

        if (!TextUtils.isEmpty(isFrom) && imageType.equalsIgnoreCase("after")) {

            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("type", "ProcessEndTrip"));
            paramsList.add(generalFunc.generateImageParams("TripId", tripId));
            paramsList.add(generalFunc.generateImageParams("latList", store_locations_latitude.toString().replace("[", "").replace("]", "")));
            paramsList.add(generalFunc.generateImageParams("lonList", store_locations_longitude.toString().replace("[", "").replace("]", "")));
            paramsList.add(generalFunc.generateImageParams("PassengerId", data_trip.get("PassengerId")));
            paramsList.add(generalFunc.generateImageParams("DriverId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("dAddress", destAddress));
            paramsList.add(generalFunc.generateImageParams("dest_lat", endLatitude));
            paramsList.add(generalFunc.generateImageParams("dest_lon", endLongitude));
            paramsList.add(generalFunc.generateImageParams("waitingTime", "" + getWaitingTime()));
            paramsList.add(generalFunc.generateImageParams("fMaterialFee", additonallist.get(0).toString()));
            paramsList.add(generalFunc.generateImageParams("fMiscFee", additonallist.get(1).toString()));
            paramsList.add(generalFunc.generateImageParams("fDriverDiscount", additonallist.get(2).toString()));
            paramsList.add(generalFunc.generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("MemberType", CommonUtilities.app_type));
            paramsList.add(generalFunc.generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
            paramsList.add(generalFunc.generateImageParams("GeneralUserType", CommonUtilities.app_type));
            paramsList.add(generalFunc.generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
            if (isTripCancelPressed == true) {
                paramsList.add(generalFunc.generateImageParams("isTripCanceled", "true"));
                paramsList.add(generalFunc.generateImageParams("Comment", comment));
                paramsList.add(generalFunc.generateImageParams("Reason", reason));
            }

            Utils.printLog("selectedImagePath","::"+selectedImagePath);
            new UploadProfileImage(ActiveTripActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, imageType).execute();

        } else {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "ProcessEndTrip");
            parameters.put("TripId", tripId);
            parameters.put("latList", store_locations_latitude.toString().replace("[", "").replace("]", ""));
            parameters.put("lonList", store_locations_longitude.toString().replace("[", "").replace("]", ""));
            parameters.put("PassengerId", data_trip.get("PassengerId"));
            parameters.put("DriverId", generalFunc.getMemberId());
            parameters.put("dAddress", destAddress);
            parameters.put("dest_lat", endLatitude);
            parameters.put("dest_lon", endLongitude);
            parameters.put("waitingTime", "" + getWaitingTime());

            parameters.put("fMaterialFee", additonallist.get(0).toString());
            parameters.put("fMiscFee", additonallist.get(1).toString());
            parameters.put("fDriverDiscount", additonallist.get(2).toString());


            if (isTripCancelPressed == true) {
                parameters.put("isTripCanceled", "true");
                parameters.put("Comment", comment);
                parameters.put("Reason", reason);
            }

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> endTripResponse(responseString));
            exeWebServer.execute();
        }
    }

    private void endTripResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

            if (isDataAvail == true) {
                generalFunc.saveGoOnlineInfo();
                if (timerrequesttask != null) {
                    try {
                        timerrequesttask.stopRepeatingTask();
                        timerrequesttask = null;
                    } catch (Exception e) {

                    }
                }
                closeuploadServicePicAlertBox();
                stopProcess();

                (new getUserData(generalFunc, getActContext())).getData();
            } else {
                String msg_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                if (msg_str.equals(CommonUtilities.GCM_FAILED_KEY) || msg_str.equals(CommonUtilities.APNS_FAILED_KEY) || msg_str.equals("LBL_SERVER_COMM_ERROR")) {
                    generalFunc.restartApp(LauncherActivity.class);
                } else {
                    if (tripLocService != null) {
                        tripLocService.tripEndRevoked();
                    }
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                }

            }
        } else {
            if (tripLocService != null) {
                tripLocService.tripEndRevoked();
            }
            generalFunc.showError();
        }
    }

    private long getWaitingTime() {
        long waitingTime = generalFunc.parseLongValue(0, generalFunc.retrieveValue(CommonUtilities.DriverWaitingTime)) / 60000;
        return waitingTime;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServiceBound == false && isTripStart == true) {
            bindService();
        }


        if (updateDirections != null) {
            updateDirections.scheduleDirectionUpdate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (updateDirections != null) {
            updateDirections.releaseTask();
        }
    }

    public void stopProcess() {
        if (mServiceBound) {
            unbindService(mConnection);
            mServiceBound = false;
        }

        if (updateDirections != null) {
            updateDirections.releaseTask();
            updateDirections = null;
        }

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
        stopDriverLocationUpdateService();
        Utils.runGC();
    }

    public void stopDriverLocationUpdateService() {
        try {
            stopService(startLocationUpdateService);
            stopService(tripLocService_intent);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        stopAllProcess();
        super.onDestroy();
    }

    private void stopAllProcess() {
        stopProcess();
    }

    public void chooseFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

//    OVER UPLOAD SERVICE PIC AREA

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Utils.printLog(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            pathForCameraImage = mediaFile.getAbsolutePath();
        } else {
            return null;
        }

        return mediaFile;
    }

    public void chooseFromGallery() {
        // System.out.println("Gallery pressed");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void closeuploadServicePicAlertBox() {
        if (uploadServicePicAlertBox != null) {
            uploadServicePicAlertBox.dismiss();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Utils.SEARCH_DEST_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {
            latitude = data.getStringExtra("Latitude");
            longitirude = data.getStringExtra("Longitude");
            address = data.getStringExtra("Address");

            //addDestination(latitude, longitirude, address);
            getTollcostValue();
        } else {
            boolean isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
            if (!isStoragePermissionAvail) {
                return;
            }

            if (requestCode == Utils.REQUEST_CODE_GPS_ON) {
                handleNoLocationDial();
            }
        }
    }


    public void getTollcostValue() {

        if (generalFunc.retrieveValue(CommonUtilities.ENABLE_TOLL_COST).equalsIgnoreCase("Yes")) {

            double sourcelatitude = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLatitude"));
            double sourcelongitude = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLongitude"));

            String url = CommonUtilities.TOLLURL + generalFunc.retrieveValue(CommonUtilities.TOLL_COST_APP_ID)
                    + "&app_code=" + generalFunc.retrieveValue(CommonUtilities.TOLL_COST_APP_CODE) + "&waypoint0=" + sourcelatitude
                    + "," + sourcelongitude + "&waypoint1=" + latitude + "," + longitirude + "&mode=fastest;car";

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> {

                if (responseString != null && !responseString.equals("")) {

                    if (generalFunc.getJsonValue("onError", responseString).equalsIgnoreCase("FALSE")) {
                        try {

                            String costs = generalFunc.getJsonValue("costs", responseString);

                            String currency = generalFunc.getJsonValue("currency", costs);
                            String details = generalFunc.getJsonValue("details", costs);
                            String tollCost = generalFunc.getJsonValue("tollCost", details);
                            if (!currency.equals("") && currency != null) {
                                tollcurrancy = currency;
                            }
                            if (!tollCost.equals("") && tollCost != null && !tollCost.equals("0.0")) {
                                tollamount = generalFunc.parseDoubleValue(0.0, tollCost);
                            }

                            addDestination(latitude, longitirude, address);
                        } catch (Exception e) {
                            tollcurrancy = "";
                        }
                    } else {
                        tollcurrancy = "";
                    }
                } else {
                    tollcurrancy = "";
                }

            });
            exeWebServer.execute();


        } else {
            addDestination(latitude, longitirude, address);
        }

    }


    public void TollTaxDialog() {

        if (tollamount != 0.0 && tollamount != 0 && tollamount != 0.00) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

            LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.dialog_tolltax, null);

            final MTextView tolltaxTitle = (MTextView) dialogView.findViewById(R.id.tolltaxTitle);
            final MTextView tollTaxMsg = (MTextView) dialogView.findViewById(R.id.tollTaxMsg);
            final MTextView tollTaxpriceTxt = (MTextView) dialogView.findViewById(R.id.tollTaxpriceTxt);
            final MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);

            final CheckBox checkboxTolltax = (CheckBox) dialogView.findViewById(R.id.checkboxTolltax);

            checkboxTolltax.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (checkboxTolltax.isChecked()) {
                    istollIgnore = true;
                } else {
                    istollIgnore = false;
                }

            });


            MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
            int submitBtnId = Utils.generateViewId();
            btn_type2.setId(submitBtnId);
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
            btn_type2.setOnClickListener(v -> {
                tolltax_dialog.dismiss();
                eTollConfirmByUser = "Yes";

                addDestination(latitude, longitirude, address);


            });


            builder.setView(dialogView);
            tolltaxTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_ROUTE"));
            tollTaxMsg.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_PRICE_DESC"));

            tollTaxMsg.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_PRICE_DESC"));

//            tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl(
//                    "Current Fare", "LBL_CURRENT_FARE") + ": " + payableAmount + "\n" + "+" + "\n" +
//                    generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + tollamount);


            tollTaxpriceTxt.setText(
                    generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + tollamount);


            checkboxTolltax.setText(generalFunc.retrieveLangLBl("", "LBL_IGNORE_TOLL_ROUTE"));
            cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

            cancelTxt.setOnClickListener(v -> {
                tolltax_dialog.dismiss();
                istollIgnore = false;


            });

            tolltax_dialog = builder.create();
            if (generalFunc.isRTLmode() == true) {
                generalFunc.forceRTLIfSupported(tolltax_dialog);
            }
            tolltax_dialog.show();
        } else {
            addDestination(latitude, longitirude, address);
        }

    }

    public void handleImgUploadResponse(String responseString, String imageUploadedType) {

        if (responseString != null && !responseString.equals("")) {

            if (imageType.equalsIgnoreCase("after")) {
                endTripResponse(responseString);
            } else if (imageType.equalsIgnoreCase(imageUploadedType)) {
                startTripResponse(responseString);
            }
        } else {
            generalFunc.showError();
        }
    }

    public void countDownStop() {
        if (timerrequesttask != null) {
            callsetTimeApi(false);
        }
    }

    public void countDownStart() {
        if (timerrequesttask != null) {
            timerrequesttask.stopRepeatingTask();
            timerrequesttask = null;
        }

        timerrequesttask = new UpdateFrequentTask(1000);
        timerrequesttask.startRepeatingTask();
        timerrequesttask.setTaskRunListener(() -> {
            i++;
            setTimerValues();
        });

    }

    private void setTimerValues() {
        tvHour.setText("" + String.format("%02d", i / 3600));
        tvMinute.setText("" + String.format("%02d", (i % 3600) / 60));
        tvSecond.setText("" + String.format("%02d", i % 60));
    }

    private void setView() {
        onGoingTripDetailAdapter = new OnGoingTripDetailAdapter(getActContext(), list, generalFunc);
        onGoingTripsDetailListRecyclerView.setAdapter(onGoingTripDetailAdapter);
        onGoingTripDetailAdapter.notifyDataSetChanged();

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActContext(), DividerItemDecoration.VERTICAL_LIST);
        onGoingTripsDetailListRecyclerView.addItemDecoration(itemDecoration);
    }

    private void callsetTimeApi(final boolean isresumeGet) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "SetTimeForTrips");
        parameters.put("iTripId", tripId);
        if (!isresumeGet) {
            parameters.put("iTripTimeId", TripTimeId);
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

            if (isDataAvail == true) {
                String msg_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                if (!msg_str.equals("true") && !msg_str.equals("") && msg_str != null) {
                    TripTimeId = msg_str;
                }
                String temptime = generalFunc.getJsonValue("totalTime", responseString);
                i = Integer.parseInt(temptime);
                setTimerValues();

                if (isresumeGet) {
                    countDownStart();
                    btntimer.setText(generalFunc.retrieveLangLBl("pause", "LBL_PAUSE_TEXT"));
                } else {
                    if (timerrequesttask != null) {
                        timerrequesttask.stopRepeatingTask();
                        timerrequesttask = null;
                    }
                }

            }
        });
        exeWebServer.execute();
    }

    public void openNavigationDialog(final String dest_lat, final String dest_lon) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_selectnavigation_view, null);

        final MTextView NavigationTitleTxt = (MTextView) dialogView.findViewById(R.id.NavigationTitleTxt);
        final MTextView wazemapTxtView = (MTextView) dialogView.findViewById(R.id.wazemapTxtView);
        final MTextView googlemmapTxtView = (MTextView) dialogView.findViewById(R.id.googlemmapTxtView);
        final RadioButton radiogmap = (RadioButton) dialogView.findViewById(R.id.radiogmap);
        final RadioButton radiowazemap = (RadioButton) dialogView.findViewById(R.id.radiowazemap);

        radiogmap.setOnClickListener(v -> googlemmapTxtView.performClick());
        radiowazemap.setOnClickListener(v -> wazemapTxtView.performClick());

        builder.setView(dialogView);
        NavigationTitleTxt.setText(generalFunc.retrieveLangLBl("Choose Option", "LBL_CHOOSE_OPTION"));
        googlemmapTxtView.setText(generalFunc.retrieveLangLBl("Google map navigation", "LBL_NAVIGATION_GOOGLE_MAP"));
        wazemapTxtView.setText(generalFunc.retrieveLangLBl("Waze navigation", "LBL_NAVIGATION_WAZE"));


        googlemmapTxtView.setOnClickListener(v -> {

            try {
                String url_view = "http://maps.google.com/maps?daddr=" + dest_lat + "," + dest_lon;
                (new StartActProcess(getActContext())).openURL(url_view, "com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                list_navigation.dismiss();
            } catch (Exception e) {
                generalFunc.showMessage(wazemapTxtView, generalFunc.retrieveLangLBl("Please install Google Maps in your device.", "LBL_INSTALL_GOOGLE_MAPS"));
            }

        });

        wazemapTxtView.setOnClickListener(v -> {
            try {

                String uri = "waze://?ll=" + dest_lat + "," + dest_lon + "&navigate=yes";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                list_navigation.dismiss();
            } catch (Exception e) {

                generalFunc.showMessage(wazemapTxtView, generalFunc.retrieveLangLBl("Please install Waze navigation app in your device.", "LBL_INSTALL_WAZE"));


            }


        });


        list_navigation = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(list_navigation);
        }
        list_navigation.show();
        list_navigation.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(ActiveTripActivity.this);
            if (view.getId() == emeTapImgView.getId()) {
                Bundle bn = new Bundle();
                bn.putString("TripId", tripId);
                new StartActProcess(getActContext()).startActWithData(ConfirmEmergencyTapActivity.class, bn);
            }
        }
    }

    public class setOnClickAct implements View.OnClickListener {

        String dest_lat = "";
        String dest_lon = "";

        public setOnClickAct() {
        }


        public setOnClickAct(String dest_lat, String dest_lon) {
            this.dest_lat = dest_lat;
            this.dest_lon = dest_lon;
        }

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.navigateArea) {
                if (!isTripStart) {

                    if (data_trip.get("REQUEST_TYPE").equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NAVIGATION_ALERT"));
                    } else if (data_trip.get("REQUEST_TYPE").equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NAVIGATION_BOOKING_ALERT"));
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NAVIGATION_DELIVERY_ALERT"));

                    }

                } else {
                    openNavigationDialog(dest_lat, dest_lon);
                }
            } else if (i == R.id.destLocSearchArea) {
                Bundle bn = new Bundle();
                bn.putString("isPickUpLoc", "false");

                if (userLocation != null) {
                    bn.putString("PickUpLatitude", "" + userLocation.getLatitude());
                    bn.putString("PickUpLongitude", "" + userLocation.getLongitude());
                }
                new StartActProcess(getActContext()).startActForResult(SearchPickupLocationActivity.class,
                        bn, Utils.SEARCH_DEST_LOC_REQ_CODE);
            } else if (i == tripStartBtnArea.getId()) {
                imageType = "before";
                setTripStart();

            } else if (i == tripEndBtnArea.getId()) {
                imageType = "after";

                isTripCancelPressed = false;
                reason = "";
                comment = "";
                endTrip();
            } else if (i == btntimer.getId()) {

                if (!isresume) {
                    callsetTimeApi(true);
                    btntimer.setText(generalFunc.retrieveLangLBl("pause", "LBL_PAUSE_TEXT"));
                    isresume = true;

                } else {
                    countDownStop();
                    btntimer.setText(generalFunc.retrieveLangLBl("resume", "LBL_RESUME_TEXT"));
                    isresume = false;
                }


            }
        }
    }

    public void finishTrip() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setContentMessage("","Deseja realmente finalizar a corrida?");

        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
            } else {
                endTrip();
            }

        });

        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_YES_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_NO_TXT"));
        generateAlert.showAlertBox();
    }


    public class setOnTouchList implements View.OnTouchListener {
        float x1, x2, y1, y2, startX, movedX;

        DisplayMetrics display = getResources().getDisplayMetrics();

        final int width = display.widthPixels;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Utils.printLog("onTouch", "called");
            switch (event.getAction()) {
                // when user first touches the screen we get x and y coordinate
                case MotionEvent.ACTION_DOWN: {
                    x1 = event.getX();
                    y1 = event.getY();

                    startX = event.getRawX();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    x2 = event.getX();
                    y2 = event.getY();
                    movedX = generalFunc.isRTLmode() ? startX - event.getRawX() : event.getRawX() - startX;

                    if (movedX > width / 2) {

                        if (generalFunc.isRTLmode() ? (x1 > x2) : (x1 < x2)) {

                            isTripCancelPressed = false;

                            if (view.getId() == tripStartBtnArea.getId()) {
                                // Trip start btn called
                                setTripStart();

                            } else if (view.getId() == tripEndBtnArea.getId()) {
                                // Trip end btn called
                                finishTrip();
                            }

                        }
                    }

                    break;
                }
            }
            return false;
        }
    }

    class ImageSourceDialog implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub

            final Dialog dialog_img_update = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);

            dialog_img_update.setContentView(R.layout.design_image_source_select);

            MTextView chooseImgHTxt = (MTextView) dialog_img_update.findViewById(R.id.chooseImgHTxt);
            chooseImgHTxt.setText(generalFunc.retrieveLangLBl("Choose option", "LBL_CHOOSE_OPTION"));

            SelectableRoundedImageView cameraIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.cameraIconImgView);
            SelectableRoundedImageView galleryIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.galleryIconImgView);

            ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

            closeDialogImgView.setOnClickListener(v -> {
                // TODO Auto-generated method stub

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
            });

            new CreateRoundedView(getResources().getColor(R.color.appThemeColor_Dark_1), Utils.dipToPixels(getActContext(), 25), 0,
                    Color.parseColor("#00000000"), cameraIconImgView);

            cameraIconImgView.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));

            new CreateRoundedView(getResources().getColor(R.color.appThemeColor_Dark_1), Utils.dipToPixels(getActContext(), 25), 0,
                    Color.parseColor("#00000000"), galleryIconImgView);

            galleryIconImgView.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));


            cameraIconImgView.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }

                if (!isDeviceSupportCamera()) {
                    generalFunc.showMessage(generalFunc.getCurrentView(ActiveTripActivity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                } else {
                    chooseFromCamera();
                }

            });

            galleryIconImgView.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                chooseFromGallery();
            });

            dialog_img_update.setCanceledOnTouchOutside(true);

            Window window = dialog_img_update.getWindow();
            window.setGravity(Gravity.BOTTOM);

            window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog_img_update.show();

        }

    }
}
