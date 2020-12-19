package com.poupgo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.general.files.CancelTripDialog;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.getUserData;
import com.general.functions.GeneralFunctions;
import com.general.files.GetLocationUpdates;
import com.general.functions.InternetConnection;
import com.general.files.OpenPassengerDetailDialog;
import com.utilities.general.files.StartActProcess;
import com.general.files.UpdateDirections;
import com.general.files.UpdateDriverLocationService;
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
import com.utils.AnimateMarker;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.general.functions.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

import java.util.HashMap;

public class DriverArrivedActivity extends AppCompatActivity implements OnMapReadyCallback, GetLocationUpdates.LocationUpdatesListener {

    public String tripId = "";
    public ImageView emeTapImgView;
    public MTextView timeTxt;
    GeneralFunctions generalFunc;
    MTextView titleTxt;
    MButton btn_type2;
    public HashMap<String, String> data_trip;
    SupportMapFragment map;
    GoogleMap gMap;
    Location userLocation;
    Intent startLocationUpdateService;
    MTextView addressTxt;
    String REQUEST_TYPE = "";
    android.support.v7.app.AlertDialog list_navigation;
    // public MTextView timeTxt;
    UpdateDirections updateDirections;
    Marker driverMarker;
    boolean isnotification = false;

    InternetConnection intCheck;
    AnimateMarker animateMarker;


    String userProfileJson = "";
    boolean isCurrentLocationFocused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_arrived);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        generalFunc = new GeneralFunctions(getActContext());
        animateMarker = new AnimateMarker();

        animateMarker.driverMarkerAnimFinished = true;

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

        generalFunc.storedata(CommonUtilities.DRIVER_ONLINE_KEY, "false");
        isnotification = getIntent().getBooleanExtra("isnotification", false);

        intCheck = new InternetConnection(getActContext());


        //gps view declaration end

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        addressTxt = (MTextView) findViewById(R.id.addressTxt);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);

        (findViewById(R.id.backImgView)).setVisibility(View.GONE);
        btn_type2.setId(Utils.generateViewId());

        emeTapImgView = (ImageView) findViewById(R.id.emeTapImgView);
        emeTapImgView.setOnClickListener(new setOnClickList());

        timeTxt = (MTextView) findViewById(R.id.timeTxt);

        timeTxt.setVisibility(View.GONE);


        setData();

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


        setLabels();

        generalFunc.storedata(CommonUtilities.DriverWaitingTime, "0");
        generalFunc.storedata(CommonUtilities.DriverWaitingSecTime, "0");

        startLocationUpdateService = new Intent(getApplicationContext(), UpdateDriverLocationService.class);
        startLocationUpdateService.putExtra("PAppVersion", data_trip.get("PAppVersion"));

        map.getMapAsync(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleTxt.getLayoutParams();
        params.setMargins(Utils.dipToPixels(getActContext(), 20), 0, 0, 0);
        titleTxt.setLayoutParams(params);

        btn_type2.setOnClickListener(new setOnClickAct());
        startLocationUpdateService.setAction(UpdateDriverLocationService.ACTION_START_FOREGROUND_SERVICE);
        startService(startLocationUpdateService);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                generalFunc.restartApp(LauncherActivity.class);
            }
        }

        if (generalFunc.isRTLmode()) {
            (findViewById(R.id.navStripImgView)).setRotation(180);
        }

        if (getIntent().getSerializableExtra("CANCEL") != null){
            dialogCancel((String) getIntent().getSerializableExtra("vTitle") );
        }


    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(updateDirections!=null)
        {
            updateDirections.scheduleDirectionUpdate();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(updateDirections!=null)
        {
            updateDirections.releaseTask();

        }
    }


    public void setTimetext(String distance, String time) {
        try {
            timeTxt.setVisibility(View.VISIBLE);
            String userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            String distance_str = "";
            if (userProfileJson != null && !generalFunc.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
                timeTxt.setText(time + " " + generalFunc.retrieveLangLBl("to reach", "LBL_REACH_TXT") + " & " + distance + " " + generalFunc.retrieveLangLBl("", "LBL_MILE_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("away", "LBL_AWAY_TXT"));
            } else {
                timeTxt.setText("Passageiro a "+ time + " & " + distance + " " + generalFunc.retrieveLangLBl("", "LBL_KM_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("away", "LBL_AWAY_TXT"));

            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        outState.putString("RESTART_STATE", "true");
        super.onSaveInstanceState(outState);
    }


    public void handleNoLocationDial() {

        if (generalFunc.isLocationEnabled()) {
            resetData();
        }

    }


    public void checkUserLocation() {
        if (generalFunc.isLocationEnabled() && (userLocation == null || userLocation.getLatitude() == 0.0 || userLocation.getLongitude() == 0.0)) {
            showprogress();
        } else {
            hideprogress();
        }
    }

    public void internetIsBack() {
        Utils.printELog("InternetIsBack","Called");
        if (updateDirections != null) {
            updateDirections.scheduleDirectionUpdate();
        }
    }

    public void showprogress() {
        isCurrentLocationFocused = false;
        findViewById(R.id.errorLocArea).setVisibility(View.VISIBLE);
        findViewById(R.id.googleImage).setVisibility(View.GONE);

        findViewById(R.id.mProgressBar).setVisibility(View.VISIBLE);
        ((ProgressBar) findViewById(R.id.mProgressBar)).setIndeterminate(true);
        ((ProgressBar) findViewById(R.id.mProgressBar)).getIndeterminateDrawable().setColorFilter(
                getActContext().getResources().getColor(R.color.appThemeColor_1), android.graphics.PorterDuff.Mode.SRC_IN);

    }

    public void hideprogress() {

        findViewById(R.id.errorLocArea).setVisibility(View.GONE);
        findViewById(R.id.googleImage).setVisibility(View.VISIBLE);

        if (findViewById(R.id.mProgressBar) != null) {
            findViewById(R.id.mProgressBar).setVisibility(View.GONE);
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


    public void setLabels() {

        setPageName();
        timeTxt.setText("--" + generalFunc.retrieveLangLBl("to reach", "LBL_REACH_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_ARRIVED_TXT"));
        ((MTextView) findViewById(R.id.navigateTxt)).setText(generalFunc.retrieveLangLBl("Navigate", "LBL_NAVIGATE"));


        // No location found but gps is on

        ((MTextView) findViewById(R.id.errorTitleTxt)).setText(generalFunc.retrieveLangLBl("Waiting for your location.", "LBL_LOCATION_FATCH_ERROR_TXT"));

        ((MTextView) findViewById(R.id.errorSubTitleTxt)).setText(generalFunc.retrieveLangLBl("Try to fetch  your accurate location. \"If you still face the problem, go to open sky instead of closed area\".", "LBL_NO_LOC_GPS_TXT"));

    }

    public void setPageName() {
        if (REQUEST_TYPE.equals("Deliver")) {
            titleTxt.setText(generalFunc.retrieveLangLBl("Pickup Delivery", "LBL_PICKUP_DELIVERY"));
        } else if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT"));
        } else {
            titleTxt.setText(generalFunc.retrieveLangLBl("Pick Up Passenger", "LBL_PICK_UP_PASSENGER"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.gMap = googleMap;
        if (generalFunc.checkLocationPermission(true) == true) {
            getMap().setMyLocationEnabled(false);
        }

        getMap().getUiSettings().setTiltGesturesEnabled(false);
        getMap().getUiSettings().setCompassEnabled(false);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);

        getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();
                return true;
            }
        });

        double passenger_lat = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLatitude"));
        double passenger_lon = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLongitude"));

        MarkerOptions marker_passenger_opt = new MarkerOptions()
                .position(new LatLng(passenger_lat, passenger_lon));

        int icon = R.drawable.taxi_passanger;
        marker_passenger_opt.icon(BitmapDescriptorFactory.fromResource(icon)).anchor(0.5f,
                1);
        getMap().addMarker(marker_passenger_opt).setFlat(true);


        checkUserLocation();
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
        //GetLocationUpdates.getInstance().setTripStartValue(false, true, data_trip.get("TripId"));
        GetLocationUpdates.getInstance().startLocationUpdates(this, this);
    }

    public GoogleMap getMap() {
        return this.gMap;
    }

    public void dialogCancel(String vTitle){
        final GenerateAlertBox generateAlert = new GenerateAlertBox(this);
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            (new getUserData(generalFunc, this)).getData();
        });
        generateAlert.setContentMessage("", vTitle);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }


    public void setData() {

        HashMap<String, String> data = (HashMap<String, String>) getIntent().getSerializableExtra("TRIP_DATA");

        this.data_trip = data;

        double passenger_lat = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLatitude"));
        double passenger_lon = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLongitude"));

        addressTxt.setText(generalFunc.retrieveLangLBl("Loading address", "LBL_LOAD_ADDRESS"));
        addressTxt.setText(data_trip.get("tSaddress"));


        (findViewById(R.id.navigateArea)).setOnClickListener(new setOnClickAct("" + passenger_lat, "" + passenger_lon));

        REQUEST_TYPE = data_trip.get("REQUEST_TYPE");

        setPageName();
    }


    @Override
    public void onLocationUpdate(Location location) {
        if (location == null) {
            isCurrentLocationFocused = false;
            return;
        }

        if (userProfileJson != null && generalFunc.getJsonValue("ENABLE_DIRECTION_SOURCE_DESTINATION_DRIVER_APP", userProfileJson).equalsIgnoreCase("Yes"))
        {
            if (location != null && (this.userLocation == null || !isCurrentLocationFocused)) {
                isCurrentLocationFocused = true;
                this.userLocation = location;
                CameraPosition cameraPosition = cameraForUserPosition(true);
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                isCurrentLocationFocused = true;
                CameraPosition cameraPosition = cameraForUserPosition(false);
                getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1500, null);
            }
        }
        else
        {

            try {
                this.userLocation = location;
                double passenger_lat = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLatitude"));
                double passenger_lon = generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLongitude"));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
                builder.include(new LatLng(passenger_lat, passenger_lon));
                getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
            }
            catch (Exception e)
            {

            }


        }

        updateDriverMarker(new LatLng(location.getLatitude(), location.getLongitude()));

        this.userLocation = location;
        checkUserLocation();

        if (updateDirections == null) {
            Utils.printELog("OnLocationUpdate", ":DirectionUpdate:");
            Location destLoc = new Location("gps");
            destLoc.setLatitude(generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLatitude")));
            destLoc.setLongitude(generalFunc.parseDoubleValue(0.0, data_trip.get("sourceLongitude")));
            updateDirections = new UpdateDirections(getActContext(), gMap, userLocation, destLoc);
            updateDirections.scheduleDirectionUpdate();

        } else if (updateDirections != null) {
            updateDirections.changeUserLocation(location);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_CODE_GPS_ON) {
            handleNoLocationDial();
        }
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
//                markerOptions_driver.icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_driver)).anchor(0.5f, 0.5f).flat(true);
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

    public CameraPosition cameraForUserPosition(boolean isFirst) {
        double currentZoomLevel = getMap().getCameraPosition().zoom;

        if (isFirst) {
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

    public void buildMsgOnArrivedBtn() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                btn_type2.setEnabled(true);
                generateAlert.closeAlertBox();
            } else {
                btn_type2.setEnabled(true);
                setDriverStatusToArrived();
            }

        });

        String msg = "";
        if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            msg = generalFunc.retrieveLangLBl("", "LBL_ARRIVED_CONFIRM_DIALOG_SERVICES");
        } else if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
            msg = generalFunc.retrieveLangLBl("", "LBL_ARRIVED_CONFIRM_DIALOG_TXT");
        } else if (REQUEST_TYPE.equalsIgnoreCase("Deliver")) {
            msg = generalFunc.retrieveLangLBl("", "LBL_ARRIVED_CONFIRM_DIALOG_DELIVERY");
        }
        generateAlert.setContentMessage("", msg);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("ok", "LBL_YES"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("cancel", "LBL_NO"));

        generateAlert.showAlertBox();
    }

    public void setDriverStatusToArrived() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "DriverArrived");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("TripId", data_trip.get("TripId"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail == true) {

                    stopDriverLocationUpdateService();

                    String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                    data_trip.put("DestLocLatitude", generalFunc.getJsonValue("DLatitude", message));
                    data_trip.put("DestLocLongitude", generalFunc.getJsonValue("DLongitude", message));
                    data_trip.put("DestLocAddress", generalFunc.getJsonValue("DAddress", message));
                    data_trip.put("eTollSkipped", generalFunc.getJsonValue("eTollSkipped", message));
                    data_trip.put("vTripStatus", "Arrived");

                    if (updateDirections != null) {
                        updateDirections.releaseTask();
                        updateDirections = null;
                    }

                    stopProcess();
                    (new getUserData(generalFunc, getActContext())).getData();

                } else {
                    String msg_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                    if (msg_str.equals("DO_RESTART") ||
                            msg_str.equals(CommonUtilities.GCM_FAILED_KEY) || msg_str.equals(CommonUtilities.APNS_FAILED_KEY) || msg_str.equals("LBL_SERVER_COMM_ERROR")) {
                        generalFunc.restartApp(LauncherActivity.class);
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.trip_accept_menu, menu);

        if (REQUEST_TYPE.equals("Deliver")) {
            menu.findItem(R.id.menu_passenger_detail).setTitle(generalFunc.retrieveLangLBl("View Delivery Details", "LBL_VIEW_DELIVERY_DETAILS"));
            menu.findItem(R.id.menu_cancel_trip).setTitle(generalFunc.retrieveLangLBl("Cancel Delivery", "LBL_CANCEL_DELIVERY"));
        } else {
            String msg = "";
            String msgCancel = "";
            if (REQUEST_TYPE != null && REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                msg = generalFunc.retrieveLangLBl("", "LBL_VIEW_USER_DETAIL");
                msgCancel = generalFunc.retrieveLangLBl("", "LBL_CANCEL_JOB");
            } else if (REQUEST_TYPE != null && REQUEST_TYPE.equalsIgnoreCase("Deliver")) {
                msg = generalFunc.retrieveLangLBl("", "LBL_VIEW_DELIVERY_DETAILS");
                msgCancel = generalFunc.retrieveLangLBl("", "LBL_CANCEL_DELIVERY");
            } else {
                msg = generalFunc.retrieveLangLBl("", "LBL_VIEW_PASSENGER_DETAIL");
                msgCancel = generalFunc.retrieveLangLBl("", "LBL_CANCEL_TRIP");

            }

            menu.findItem(R.id.menu_passenger_detail).setTitle(msg);
            menu.findItem(R.id.menu_cancel_trip).setTitle(msgCancel);
        }

        if (!REQUEST_TYPE.equals(Utils.CabGeneralType_UberX)) {
            menu.findItem(R.id.menu_waybill_trip).setTitle(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL")).setVisible(true);
        } else {
            // if (!data_trip.get("eFareType").equals(Utils.CabFaretypeRegular)) {
            menu.findItem(R.id.menu_waybill_trip).setTitle(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL")).setVisible(false);
            // }
        }

        menu.findItem(R.id.menu_call).setTitle(generalFunc.retrieveLangLBl("Call", "LBL_CALL_ACTIVE_TRIP"));
        if (REQUEST_TYPE.equals(Utils.CabGeneralType_UberX)) {
            menu.findItem(R.id.menu_specialInstruction).setTitle(generalFunc.retrieveLangLBl("Special Instruction", "LBL_SPECIAL_INSTRUCTION_TXT"));
        }
        menu.findItem(R.id.menu_message).setTitle(generalFunc.retrieveLangLBl("Message", "LBL_MESSAGE_ACTIVE_TRIP"));
        menu.findItem(R.id.menu_sos).setTitle(generalFunc.retrieveLangLBl("Emergency or SOS", "LBL_EMERGENCY_SOS_TXT")).setVisible(false);

        menu.findItem(R.id.menu_sos).setVisible(false);
        if (REQUEST_TYPE.equals(Utils.CabGeneralType_UberX)) {
            menu.findItem(R.id.menu_passenger_detail).setVisible(true);
            menu.findItem(R.id.menu_call).setVisible(false);
            menu.findItem(R.id.menu_message).setVisible(false);
            menu.findItem(R.id.menu_specialInstruction).setVisible(true);
        } else if (REQUEST_TYPE.equals(Utils.CabGeneralType_UberX)) {
            menu.findItem(R.id.menu_passenger_detail).setVisible(false);
            menu.findItem(R.id.menu_call).setVisible(true);
            menu.findItem(R.id.menu_message).setVisible(true);
            menu.findItem(R.id.menu_waybill_trip).setTitle(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL")).setVisible(true);
        } else {
            menu.findItem(R.id.menu_passenger_detail).setVisible(true);
            menu.findItem(R.id.menu_call).setVisible(false);
            menu.findItem(R.id.menu_message).setVisible(false);
            menu.findItem(R.id.menu_specialInstruction).setVisible(false);
            menu.findItem(R.id.menu_waybill_trip).setTitle(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL")).setVisible(true);
        }
        Utils.setMenuTextColor(menu.findItem(R.id.menu_sos), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_call), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_message), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_passenger_detail), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_cancel_trip), getResources().getColor(R.color.appThemeColor_TXT_1));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_waybill_trip), getResources().getColor(R.color.appThemeColor_TXT_1));
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

    public void getMaskNumber() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCallMaskNumber");
        parameters.put("iTripid", data_trip.get("iTripId"));
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_passenger_detail:
                new OpenPassengerDetailDialog(getActContext(), data_trip, generalFunc, false);
                return true;


            case R.id.menu_cancel_trip:
                new CancelTripDialog(getActContext(), data_trip, generalFunc, false);
                return true;

            case R.id.menu_waybill_trip:
                // new StartActProcess(getActContext()).startAct(WayBillActivity.class);
                Bundle bn = new Bundle();
                bn.putSerializable("data_trip", data_trip);
                new StartActProcess(getActContext()).startActWithData(WayBillActivity.class, bn);
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


    public void stopDriverLocationUpdateService() {
        try {

            stopService(startLocationUpdateService);
        } catch (Exception e) {

        }
    }


    @Override
    protected void onDestroy() {
        stopProcess();
        super.onDestroy();
    }

    public void stopProcess() {
        stopDriverLocationUpdateService();
        if (updateDirections != null) {
            updateDirections.releaseTask();
            updateDirections = null;
        }

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
    }

    public Context getActContext() {
        return DriverArrivedActivity.this; // Must be context of activity not application
    }

    public void openNavigationDialog(final String passenger_lat, final String passenger_lon) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_selectnavigation_view, null);

        final MTextView NavigationTitleTxt = (MTextView) dialogView.findViewById(R.id.NavigationTitleTxt);
        final MTextView wazemapTxtView = (MTextView) dialogView.findViewById(R.id.wazemapTxtView);
        final MTextView googlemmapTxtView = (MTextView) dialogView.findViewById(R.id.googlemmapTxtView);
        final RadioButton radiogmap = (RadioButton) dialogView.findViewById(R.id.radiogmap);
        final RadioButton radiowazemap = (RadioButton) dialogView.findViewById(R.id.radiowazemap);

        radiogmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radiogmap.setChecked(true);
                radiowazemap.setChecked(false);
                googlemmapTxtView.performClick();

            }
        });
        radiowazemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radiogmap.setChecked(false);
                radiowazemap.setChecked(true);
                wazemapTxtView.performClick();

            }
        });

        builder.setView(dialogView);
        NavigationTitleTxt.setText(generalFunc.retrieveLangLBl("Choose Option", "LBL_CHOOSE_OPTION"));
        googlemmapTxtView.setText(generalFunc.retrieveLangLBl("Google map navigation", "LBL_NAVIGATION_GOOGLE_MAP"));
        wazemapTxtView.setText(generalFunc.retrieveLangLBl("Waze navigation", "LBL_NAVIGATION_WAZE"));


        googlemmapTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Utils.printLog("passenger_lat", passenger_lat + "");
                    Utils.printLog("passenger_lon", passenger_lon + "");
                    String url_view = "http://maps.google.com/maps?daddr=" + passenger_lat + "," + passenger_lon;
                    (new StartActProcess(getActContext())).openURL(url_view, "com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    list_navigation.dismiss();
                } catch (Exception e) {
                    generalFunc.showMessage(wazemapTxtView, generalFunc.retrieveLangLBl("Please install Google Maps in your device.", "LBL_INSTALL_GOOGLE_MAPS"));
                }

            }
        });

        wazemapTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String uri = "waze://?ll=" + passenger_lat + "," + passenger_lon + "&navigate=yes";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                    list_navigation.dismiss();
                } catch (Exception e) {

                    generalFunc.showMessage(wazemapTxtView, generalFunc.retrieveLangLBl("Please install Waze navigation app in your device.", "LBL_INSTALL_WAZE"));


                }


            }
        });


        list_navigation = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(list_navigation);
        }
        list_navigation.show();
        list_navigation.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Utils.hideKeyboard(getActContext());
            }
        });
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {


            if (view.getId() == emeTapImgView.getId()) {
                Bundle bn = new Bundle();

                bn.putString("TripId", tripId);
                new StartActProcess(getActContext()).startActWithData(ConfirmEmergencyTapActivity.class, bn);
            }

        }
    }

    public class setOnClickAct implements View.OnClickListener {

        String passenger_lat = "";
        String passenger_lon = "";

        public setOnClickAct() {
        }

        public setOnClickAct(String passenger_lat, String passenger_lon) {
            this.passenger_lat = passenger_lat;
            this.passenger_lon = passenger_lon;
        }

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(DriverArrivedActivity.this);
            if (i == btn_type2.getId()) {
                btn_type2.setEnabled(false);
                buildMsgOnArrivedBtn();
            } else if (i == R.id.navigateArea) {
                openNavigationDialog(passenger_lat, passenger_lon);
            }
        }
    }

}
