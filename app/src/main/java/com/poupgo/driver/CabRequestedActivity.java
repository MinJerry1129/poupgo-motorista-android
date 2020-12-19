package com.poupgo.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.general.files.GetLocationUpdates;
import com.general.files.ConfigPubNub;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.getUserData;
import com.general.functions.GeneralFunctions;
import com.general.files.MyApp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maps.Directions;
import com.maps.DirectionsResponse;
import com.maps.GeocodeResponse;
import com.maps.MapService;
import com.utils.CommonUtilities;
import com.general.functions.Utils;
import com.utilities.view.CreateRoundedView;
import com.general.functions.GenerateAlertBox;
import com.view.MTextView;
import com.utilities.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings("ResourceType")
public class CabRequestedActivity extends AppCompatActivity implements GenerateAlertBox.HandleAlertBtnClick, OnMapReadyCallback, GetLocationUpdates.LocationUpdatesListener {

    public GeneralFunctions generalFunc;
    MTextView leftTitleTxt;
    MTextView rightTitleTxt;
    ProgressBar mProgressBar;
    RelativeLayout progressLayout;
    String message_str;
    MTextView pNameTxtView, pLabelRunning, pNumberRunning, pNumberStarsTxtView;
    MTextView locationAddressTxt, ufxlocationAddressTxt, distance;
    MTextView destAddressTxt;
    String pickUpAddress = "";
    String destinationAddress = "";

    GenerateAlertBox generateAlert;
    int maxProgressValue = 30;
    MediaPlayer mp = new MediaPlayer();
    private MTextView textViewShowTime, ufxtvTimeCount; // will show the time
    private CountDownTimer countDownTimer; // built in android class
    // CountDownTimer
    private long totalTimeCountInMilliseconds = maxProgressValue * 1 * 1000; // total count down time in
    // milliseconds
    private long timeBlinkInMilliseconds = 10 * 1000; // start time of start blinking
    private boolean blink; // controls the blinking .. on and off

    private MTextView locationAddressHintTxt, ufxlocationAddressHintTxt;
    private MTextView destAddressHintTxt;
    private MTextView serviceType, ufxserviceType;

    SimpleRatingBar ratingBar;
    boolean istimerfinish = false;
    String iCabRequestId = "";
    boolean isloadedAddress = false;
    FrameLayout progressLayout_frame, ufxprogressLayout_frame;
    MTextView specialHintTxt, specialValTxt;
    String specialUserComment = "";

    boolean isUfx = false;
    ImageView backImageView;
    MTextView pkgType;
    double distanceD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generalFunc = new GeneralFunctions(getActContext());
        message_str = getIntent().getStringExtra("Message");
        GetLocationUpdates.getInstance().startLocationUpdates(this, this);

        /**
         * Verifica se existia alguma solicitacao antiga => nao exibe a tela caso o pedido seja antigo
         */
        boolean delayed = checkCabRequestIsDelayed(message_str);
        if (delayed)
            return; //retornar para tela inicial

        String msgCode = generalFunc.getJsonValue("MsgCode", message_str);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.activity_cab_requested);

        Utils.printLog("CabRequestedActivity", ":: called");


        generalFunc.removeValue(CommonUtilities.DRIVER_ACTIVE_REQ_MSG_KEY);


        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        MyApp.getInstance().stopAlertService();

        generalFunc.storedata(CommonUtilities.DRIVER_REQ_COMPLETED_MSG_CODE_KEY + msgCode, "true");
        generalFunc.storedata(CommonUtilities.DRIVER_REQ_COMPLETED_MSG_CODE_KEY + msgCode, "" + System.currentTimeMillis());

        generalFunc.storedata(CommonUtilities.DRIVER_CURRENT_REQ_OPEN_KEY, "true");

        leftTitleTxt = (MTextView) findViewById(R.id.leftTitleTxt);
        rightTitleTxt = (MTextView) findViewById(R.id.rightTitleTxt);
        pNameTxtView = (MTextView) findViewById(R.id.pNameTxtView);
        pNumberStarsTxtView = (MTextView) findViewById(R.id.pNumberStarsTxtView);
        pLabelRunning = (MTextView) findViewById(R.id.pLabelRunning);
        pNumberRunning = (MTextView) findViewById(R.id.pNumberRunning);
        locationAddressTxt = (MTextView) findViewById(R.id.locationAddressTxt);
        ufxlocationAddressTxt = (MTextView) findViewById(R.id.ufxlocationAddressTxt);
        locationAddressHintTxt = (MTextView) findViewById(R.id.locationAddressHintTxt);
        ufxlocationAddressHintTxt = (MTextView) findViewById(R.id.ufxlocationAddressHintTxt);
        distance = findViewById(R.id.distance);
        destAddressHintTxt = (MTextView) findViewById(R.id.destAddressHintTxt);
        destAddressTxt = (MTextView) findViewById(R.id.destAddressTxt);
        progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
        specialHintTxt = (MTextView) findViewById(R.id.specialHintTxt);
        specialValTxt = (MTextView) findViewById(R.id.specialValTxt);
        backImageView = (ImageView) findViewById(R.id.backImageView);
        pkgType = (MTextView) findViewById(R.id.pkgType);
        backImageView.setVisibility(View.GONE);

        progressLayout_frame = (FrameLayout) findViewById(R.id.progressLayout_frame);
        ufxprogressLayout_frame = (FrameLayout) findViewById(R.id.ufxprogressLayout_frame);


        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);

        textViewShowTime = (MTextView) findViewById(R.id.tvTimeCount);
        ufxtvTimeCount = (MTextView) findViewById(R.id.ufxtvTimeCount);
        serviceType = (MTextView) findViewById(R.id.serviceType);
        ufxserviceType = (MTextView) findViewById(R.id.ufxserviceType);

        (findViewById(R.id.menuImgView)).setVisibility(View.GONE);
        leftTitleTxt.setVisibility(View.VISIBLE);
        rightTitleTxt.setVisibility(View.VISIBLE);


        maxProgressValue = generalFunc.parseIntegerValue(30, generalFunc.getJsonValue("RIDER_REQUEST_ACCEPT_TIME", generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON)));
        totalTimeCountInMilliseconds = maxProgressValue * 1 * 1000; // total count down time in
        textViewShowTime.setText(maxProgressValue + ":" + "00");
        mProgressBar.setMax(maxProgressValue);
        mProgressBar.setProgress(maxProgressValue);


        generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setBtnClickList(this);
        generateAlert.setCancelable(false);

        Log.v("LocaAtual", generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("sourceLatitude", message_str)) + "");

        setData();


        setLabels();

        startTimer();

        progressLayout.setOnClickListener(new setOnClickList());
        leftTitleTxt.setOnClickListener(new setOnClickList());
        rightTitleTxt.setOnClickListener(new setOnClickList());


    }

    /**
     * Verifica se o pedido esta atrasado.
     *
     * @return boolean
     */
    private boolean checkCabRequestIsDelayed(String message) {
        boolean delayed = false;
        Long unixTime = System.currentTimeMillis() / 1000L;
        Long tDelayTolerance = unixTime;

        try {
            tDelayTolerance = Long.parseLong(generalFunc.getJsonValue("tDelayTolerance", message));
        } catch (Exception e) {
        }

        if (unixTime > tDelayTolerance) {
            delayed = true;
            delayedCabRequest();

        }

        return delayed;
    }

    public double getLocationFromAddress(Location location) {


        double distance = Utils.CalculationByLocation(generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("sourceLatitude", message_str)), generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("sourceLongitude", message_str)), location.getLatitude(), location.getLongitude(), "");


        return distance;

    }

    public void setLabels() {
        String REQUEST_TYPE = generalFunc.getJsonValue("REQUEST_TYPE", message_str);
        leftTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DECLINE_TXT"));
        rightTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TXT"));
        if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
            locationAddressHintTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_HEADER_TXT"));
            destAddressHintTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DEST_ADD_TXT"));
        } else {
            locationAddressHintTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SENDER_LOCATION"));
            destAddressHintTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RECEIVER_LOCATION"));
        }
        ufxlocationAddressHintTxt.setText(generalFunc.retrieveLangLBl("Job Location", "LBL_JOB_LOCATION_TXT"));

        ((MTextView) findViewById(R.id.hintTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_HINT_TAP_TXT"));
        specialHintTxt.setText(generalFunc.retrieveLangLBl("Special Instruction", "LBL_SPECIAL_INSTRUCTION_TXT"));

    }

    String REQUEST_TYPE = "";

    public void setData() {

        new CreateRoundedView(Color.parseColor("#000000"), Utils.dipToPixels(getActContext(), 122), 0, Color.parseColor("#FFFFFF"), findViewById(R.id.bgCircle));
        pNameTxtView.setText(generalFunc.getJsonValue("PName", message_str));
        pNumberRunning.setText(generalFunc.getJsonValue("countTrips", message_str));
        pNumberStarsTxtView.setText(generalFunc.getJsonValue("PRating", message_str));

        ratingBar.setRating(generalFunc.parseFloatValue(0, generalFunc.getJsonValue("PRating", message_str)));

        double pickupLat = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("sourceLatitude", message_str));
        double pickupLog = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("sourceLongitude", message_str));

        iCabRequestId = generalFunc.getJsonValue("iCabRequestId", message_str);


        double desLat = 0.0;
        double destLog = 0.0;
        if (!generalFunc.getJsonValue("destLatitude", message_str).isEmpty() && !generalFunc.getJsonValue("destLongitude", message_str).isEmpty()) {

            desLat = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("destLatitude", message_str));
            destLog = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("destLongitude", message_str));

            if (desLat == 0.0 && destLog == 0.0) {
                destAddressTxt.setVisibility(View.GONE);
                destAddressHintTxt.setVisibility(View.GONE);
            } else {
                destAddressTxt.setVisibility(View.VISIBLE);
                destAddressHintTxt.setVisibility(View.VISIBLE);
            }
        }

        LatLng originLocation = new LatLng(pickupLat, pickupLog);
        LatLng destinationLocation = new LatLng((desLat != 0.0 ? desLat : pickupLat), (destLog != 0.0 ? destLog : pickupLog));

        if (iCabRequestId != null && !iCabRequestId.equals("")) {
            // api call

            getAddressFormServer();
        } else {

            //calling geocodeapi to get sourceAddress and destinationAddress inside the directions request
            MapService.getDirections(originLocation, destinationLocation, new MapService.MapServiceCallback() {
                @Override
                public void onDirectionsSuccess(DirectionsResponse response) {

                    Directions[] directions = response.polyline;

                    if(directions != null && directions.length > 0) {
                        MapService.getGeocode(directions[0].lat, directions[0].lng, new MapService.GeocodeCallback() {
                            @Override
                            public void onGeocodeSuccess(GeocodeResponse response) {
                                pickUpAddress = response.CompleteAddress;
                            }

                            @Override
                            public void onGeocodeFailure(GeocodeResponse response) {

                            }
                        });

                        int indexLastDirection = directions.length - 1;
                        MapService.getGeocode(directions[indexLastDirection].lat, directions[indexLastDirection].lng,
                                new MapService.GeocodeCallback() {
                            @Override
                            public void onGeocodeSuccess(GeocodeResponse response) {
                                destinationAddress = response.CompleteAddress;
                            }

                            @Override
                            public void onGeocodeFailure(GeocodeResponse response) {

                            }
                        });
                    }

                    isloadedAddress = true;

                    if (destinationAddress.equalsIgnoreCase("")) {
                        destinationAddress = "----";
                    }
                    destAddressTxt.setText(destinationAddress);
                    locationAddressTxt.setText(pickUpAddress);
                    ufxlocationAddressTxt.setText(pickUpAddress);
                }

                @Override
                public void onDirectionsFailed(DirectionsResponse response) {

                }
            });

        }


        REQUEST_TYPE = generalFunc.getJsonValue("REQUEST_TYPE", message_str);


        Utils.printLog("REQUEST_TYPE", REQUEST_TYPE);

        LinearLayout packageInfoArea = (LinearLayout) findViewById(R.id.packageInfoArea);
        if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            isUfx = true;
            //if (!generalFunc.getJsonValue("eFareType", message_str).equalsIgnoreCase(Utils.CabFaretypeRegular)) {
            progressLayout_frame.setVisibility(View.GONE);
            locationAddressTxt.setVisibility(View.GONE);
            locationAddressHintTxt.setVisibility(View.GONE);
            destAddressHintTxt.setVisibility(View.GONE);
            destAddressTxt.setVisibility(View.GONE);
            ufxlocationAddressTxt.setVisibility(View.VISIBLE);
            ufxlocationAddressHintTxt.setVisibility(View.VISIBLE);
            ufxprogressLayout_frame.setVisibility(View.VISIBLE);
            specialHintTxt.setVisibility(View.VISIBLE);
            specialValTxt.setVisibility(View.VISIBLE);
            //}
            ((MTextView) findViewById(R.id.requestType)).setText(generalFunc.retrieveLangLBl("Job", "LBL_JOB_TXT") + "  " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST"));

            (findViewById(R.id.ufxserviceType)).setVisibility(View.VISIBLE);
            ufxserviceType.setText(generalFunc.getJsonValue("SelectedTypeName", message_str));
            packageInfoArea.setVisibility(View.GONE);
        } else if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            ((MTextView) findViewById(R.id.requestType)).setText(generalFunc.retrieveLangLBl("Job", "LBL_JOB_TXT") + "  " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST"));
            (findViewById(R.id.serviceType)).setVisibility(View.VISIBLE);
            serviceType.setText(generalFunc.getJsonValue("SelectedTypeName", message_str));
            packageInfoArea.setVisibility(View.GONE);
        } else if (REQUEST_TYPE.equals("Deliver")) {
            (findViewById(R.id.packageInfoArea)).setVisibility(View.VISIBLE);
            ((MTextView) findViewById(R.id.packageInfoTxt)).setText(generalFunc.getJsonValue("PACKAGE_TYPE", message_str));

            if (generalFunc.getJsonValue("VehicleTypeName", message_str) != null && !generalFunc.getJsonValue("VehicleTypeName", message_str).equalsIgnoreCase("")) {
                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                        generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST") + " (" + generalFunc.getJsonValue("VehicleTypeName", message_str) + ")");

            } else {
                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                        generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST"));
            }
        } else {
            (findViewById(R.id.packageInfoArea)).setVisibility(View.GONE);

            if (generalFunc.getJsonValue("VehicleTypeName", message_str) != null && !generalFunc.getJsonValue("VehicleTypeName", message_str).equalsIgnoreCase("")) {
                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                        generalFunc.retrieveLangLBl("Ride", "LBL_RIDE") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST") + " (" + generalFunc.getJsonValue("VehicleTypeName", message_str) + ")");

            } else {
                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                        generalFunc.retrieveLangLBl("Ride", "LBL_RIDE") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST"));

            }

        }
    }

    public void getAddressFormServer() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCabRequestAddress");
        parameters.put("iCabRequestId", iCabRequestId);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);

        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {

                        String MessageJson = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                        pickUpAddress = generalFunc.getJsonValue("tSourceAddress", MessageJson);
                        destinationAddress = generalFunc.getJsonValue("tDestAddress", MessageJson);
                        if (isUfx) {
                            if (generalFunc.getJsonValue("tUserComment", MessageJson) != null && !generalFunc.getJsonValue("tUserComment", MessageJson).equals("")) {
                                specialUserComment = generalFunc.getJsonValue("tUserComment", MessageJson);
                                specialValTxt.setText(generalFunc.getJsonValue("tUserComment", MessageJson));
                            } else {
                                specialValTxt.setText("------------");
                            }
                        }


                        if (REQUEST_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                            //}

                            ((MTextView) findViewById(R.id.requestType)).setText(generalFunc.retrieveLangLBl("Job", "LBL_JOB_TXT") + "  " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST"));

                        } else if (REQUEST_TYPE.equals("Deliver")) {

                            if (generalFunc.getJsonValue("VehicleTypeName", MessageJson) != null && !generalFunc.getJsonValue("VehicleTypeName", MessageJson).equalsIgnoreCase("")) {
                                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                                        generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST") + " (" + generalFunc.getJsonValue("VehicleTypeName", MessageJson) + ")");

                            } else {
                                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                                        generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST"));
                            }
                        } else {

                            if (generalFunc.getJsonValue("VehicleTypeName", MessageJson) != null && !generalFunc.getJsonValue("VehicleTypeName", MessageJson).equalsIgnoreCase("")) {
                                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                                        generalFunc.retrieveLangLBl("Ride", "LBL_RIDE") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST") + " (" + generalFunc.getJsonValue("VehicleTypeName", MessageJson) + ")");

                            } else {
                                ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                                        generalFunc.retrieveLangLBl("Ride", "LBL_RIDE") + " " + generalFunc.retrieveLangLBl("Request", "LBL_REQUEST"));

                            }

                            if (generalFunc.getJsonValue("PackageName", MessageJson) != null && !generalFunc.getJsonValue("PackageName", MessageJson).equalsIgnoreCase("")) {
                                pkgType.setVisibility(View.VISIBLE);
                                pkgType.setText(generalFunc.getJsonValue("PackageName", MessageJson));


                                if (generalFunc.getJsonValue("VehicleTypeName", MessageJson) != null && !generalFunc.getJsonValue("VehicleTypeName", MessageJson).equalsIgnoreCase("")) {
                                    ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                                            generalFunc.retrieveLangLBl("", "LBL_RENTAL_RIDE_REQUEST") + " (" + generalFunc.getJsonValue("VehicleTypeName", MessageJson) + ")");

                                } else {
                                    ((MTextView) findViewById(R.id.requestType)).setText(/*generalFunc.retrieveLangLBl("Ride Type", "LBL_RIDE_TYPE") + ": " +*/
                                            generalFunc.retrieveLangLBl("", "LBL_RENTAL_RIDE_REQUEST"));

                                }
                            }

                        }

                        isloadedAddress = true;

                        if (destinationAddress.equalsIgnoreCase("")) {
                            destinationAddress = "----";
                        }
                        destAddressTxt.setText(destinationAddress);
                        locationAddressTxt.setText(pickUpAddress);
                        ufxlocationAddressTxt.setText(pickUpAddress);


                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getAddressFormServer();
                            }
                        }, 2000);

                    }
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getAddressFormServer();
                        }
                    }, 2000);
                }
            }
        });
        exeWebServer.execute();
    }


    public void findAddressByDirectionAPI(final String url) {

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);


        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                if (responseString != null && !responseString.equals("")) {

                    String status = generalFunc.getJsonValue("status", responseString);

                    if (status.equals("OK")) {

                        JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                        if (obj_routes != null && obj_routes.length() > 0) {
                            JSONObject obj_legs = generalFunc.getJsonObject(generalFunc.getJsonArray("legs", generalFunc.getJsonObject(obj_routes, 0).toString()), 0);

                            pickUpAddress = generalFunc.getJsonValue("start_address", obj_legs.toString());
                            destinationAddress = generalFunc.getJsonValue("end_address", obj_legs.toString());

                        }
                        isloadedAddress = true;

                        if (destinationAddress.equalsIgnoreCase("")) {
                            destinationAddress = "----";
                        }
                        destAddressTxt.setText(destinationAddress);
                        locationAddressTxt.setText(pickUpAddress);
                        ufxlocationAddressTxt.setText(pickUpAddress);


                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                findAddressByDirectionAPI(url);
                            }
                        }, 2000);


                    }

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findAddressByDirectionAPI(url);
                        }
                    }, 2000);

                }
            }
        });
        exeWebServer.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (istimerfinish) {

            finish();
            trimCache(getActContext());
            istimerfinish = false;
            backImageView.setVisibility(View.VISIBLE);
        }


//        playMedia();
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCustoNotiSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeSound();
    }

    @Override
    public void handleBtnClick(int btn_id) {
        Utils.hideKeyboard(CabRequestedActivity.this);

        cancelRequest();
    }

    public void acceptRequest() {

        progressLayout.setClickable(false);
        rightTitleTxt.setEnabled(false);
        leftTitleTxt.setEnabled(false);
        generateTrip();
    }

    public void generateTrip() {

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), generateTripParams());
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {


                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        removeCustoNotiSound();

                        (new getUserData(generalFunc, getActContext())).getData();

                    } else {

                        final String msg_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        removeCustoNotiSound();

                        GenerateAlertBox alertBox = generalFunc.notifyRestartApp("", generalFunc.retrieveLangLBl("", msg_str));
                        alertBox.setCancelable(false);
                        alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                            @Override
                            public void handleBtnClick(int btn_id) {
                                if (msg_str.equals(CommonUtilities.GCM_FAILED_KEY) || msg_str.equals(CommonUtilities.APNS_FAILED_KEY) || msg_str.equals("LBL_SERVER_COMM_ERROR")) {
                                    (new getUserData(generalFunc, getActContext())).getData();
                                } else {
                                    CabRequestedActivity.super.onBackPressed();
                                }


                            }
                        });


                    }
                } else {
                    rightTitleTxt.setEnabled(true);
                    leftTitleTxt.setEnabled(true);
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void declineTripRequest() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "DeclineTripRequest");
        parameters.put("DriverID", generalFunc.getMemberId());
        parameters.put("PassengerID", generalFunc.getJsonValue("PassengerId", message_str));
        parameters.put("vMsgCode", generalFunc.getJsonValue("MsgCode", message_str));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                cancelRequest();
            }
        });
        exeWebServer.execute();
    }

    public void receiveCabRequest() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "UpdateDriverRequestStatus");
        parameters.put("DriverID", generalFunc.getMemberId());
        parameters.put("PassengerID", generalFunc.getJsonValue("PassengerId", message_str));
        parameters.put("vMsgCode", generalFunc.getJsonValue("MsgCode", message_str));
        parameters.put("eStatus", "Received");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.execute();
    }

    public void delayedCabRequest() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "UpdateDriverRequestStatus");
        parameters.put("DriverID", generalFunc.getMemberId());
        parameters.put("PassengerID", generalFunc.getJsonValue("PassengerId", message_str));
        parameters.put("vMsgCode", generalFunc.getJsonValue("MsgCode", message_str));
        parameters.put("eStatus", "Delayed");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                cancelRequest();
            }
        });

        exeWebServer.execute();
    }

    public void timeoutCabRequest() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "UpdateDriverRequestStatus");
        parameters.put("DriverID", generalFunc.getMemberId());
        parameters.put("PassengerID", generalFunc.getJsonValue("PassengerId", message_str));
        parameters.put("vMsgCode", generalFunc.getJsonValue("MsgCode", message_str));
        parameters.put("eStatus", "Timeout");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                cancelRequest();
            }
        });

        exeWebServer.execute();
    }

    public HashMap<String, String> generateTripParams() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GenerateTrip");
        parameters.put("DriverID", generalFunc.getMemberId());
        parameters.put("PassengerID", generalFunc.getJsonValue("PassengerId", message_str));
        parameters.put("start_lat", generalFunc.getJsonValue("sourceLatitude", message_str));
        parameters.put("start_lon", generalFunc.getJsonValue("sourceLongitude", message_str));
        parameters.put("iCabBookingId", generalFunc.getJsonValue("iBookingId", message_str));
        parameters.put("iCabRequestId", generalFunc.getJsonValue("iCabRequestId", message_str));
        parameters.put("sAddress", pickUpAddress);
        parameters.put("GoogleServerKey", generalFunc.retrieveValue(CommonUtilities.GOOGLE_SERVER_ANDROID_DRIVER_APP_KEY));
        parameters.put("vMsgCode", generalFunc.getJsonValue("MsgCode", message_str));
        parameters.put("UserType", CommonUtilities.app_type);
//        parameters.put("TimeZone", generalFunc.getTimezone());


        return parameters;
    }

    public void cancelRequest() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        generalFunc.storedata(CommonUtilities.DRIVER_CURRENT_REQ_OPEN_KEY, "false");

        cancelCabReq();

        try {
            CabRequestedActivity.super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startTimer() {
//        playMedia();

        receiveCabRequest();

        Utils.printLog("startTimer", ":: called");
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1000) {
            // 1000 means, onTick function will be called at every 1000
            // milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                Utils.printLog("startTimer", ":: onTick");
                long seconds = leftTimeInMilliseconds / 1000;
                // i++;
                // Setting the Progress Bar to decrease wih the timer
                mProgressBar.setProgress((int) (leftTimeInMilliseconds / 1000));
                textViewShowTime.setTextAppearance(getActContext(), android.R.color.holo_green_dark);

                if ((seconds % 5) == 0) {
                    try {
                        Utils.printLog("startTimer", ":: play");
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                        r.play();
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.musica);
                        mediaPlayer.start();


                    } catch (Exception e) {
                        Utils.printLog("startTimer", "::" + e.toString());
                        e.printStackTrace();
                    }
                }
                if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {

                    if (blink) {
                        textViewShowTime.setVisibility(View.VISIBLE);
                        ufxtvTimeCount.setVisibility(View.VISIBLE);
                    } else {
                        textViewShowTime.setVisibility(View.INVISIBLE);
                        ufxtvTimeCount.setVisibility(View.INVISIBLE);
                    }

                    blink = !blink;
                }

                textViewShowTime
                        .setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
                ufxtvTimeCount
                        .setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));

            }

            @Override
            public void onFinish() {
                Utils.printLog("startTimer", ":: finish");
                istimerfinish = true;
                textViewShowTime.setVisibility(View.VISIBLE);
//                textViewShowTime.setText("" + generalFunc.retrieveLangLBl("", "LBL_TIMER_FINISHED_TXT"));
                progressLayout.setClickable(false);
                rightTitleTxt.setEnabled(false);
                timeoutCabRequest();
                //TODO: se houver um erro no delayedCabRequest => chamar cancelCabReq();

                //cancelRequest();
            }

        }.start();

    }


    public void playMedia() {
        removeSound();
        try {
            mp = new MediaPlayer();
            AssetFileDescriptor afd;
            afd = getAssets().openFd("ringtone.mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.setLooping(true);
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //milan code for working all app

//        try { Utils.printLog("MediaPlayer", "MediaPlayer");
//            mp = MediaPlayer.create(getActContext(), R.raw.ringdriver); mp.setLooping(true); mp.start(); }
//        catch (IllegalStateException e) { } catch (Exception e) { }
    }


    private void removeCustoNotiSound() {
        if (mp != null) {
            mp.stop();
            mp = null;
        }


        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }

    public void removeSound() {
        if (mp != null) {
            mp.stop();
        }

    }

    public void cancelCabReq() {
        ConfigPubNub.getInstance().publishMsg("PASSENGER_" + generalFunc.getJsonValue("PassengerId", message_str),
                generalFunc.buildRequestCancelJson(generalFunc.getJsonValue("PassengerId", message_str), generalFunc.getJsonValue("MsgCode", message_str)));
        generalFunc.storedata(CommonUtilities.DRIVER_CURRENT_REQ_OPEN_KEY, "false");
    }

    public Context getActContext() {
        return CabRequestedActivity.this;
    }

    @Override
    public void onBackPressed() {
        cancelCabReq();
        removeCustoNotiSound();
        super.onBackPressed();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double user_lat = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("sourceLatitude", message_str));
        double user_lon = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("sourceLongitude", message_str));

        googleMap.getUiSettings().setZoomControlsEnabled(false);

        MarkerOptions marker_opt = new MarkerOptions().position(new LatLng(user_lat, user_lon));

        marker_opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi_passanger)).anchor(0.5f, 0.5f);

        googleMap.addMarker(marker_opt);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(user_lat, user_lon))
                .zoom(16).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    Location userLocation;


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationUpdate(Location location) {
        this.userLocation = location;
        distanceD = getLocationFromAddress(userLocation);
        if(distance != null){
            distance.setText("Estimativa: " + generalFunc.round(distanceD, 2) + " km");
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(CabRequestedActivity.this);
            switch (view.getId()) {
                case R.id.progressLayout:
                    acceptRequest();
                    break;
                case R.id.leftTitleTxt:
                    //cancelRequest();
                    declineTripRequest();
                    break;
                case R.id.rightTitleTxt:
                    acceptRequest();
                    break;
            }
        }
    }

}
