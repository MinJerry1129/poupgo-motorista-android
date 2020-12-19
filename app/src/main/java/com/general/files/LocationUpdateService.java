package com.general.files;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.general.functions.GeneralFunctions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.utilities.utils.Utils;

import java.util.ArrayList;

import com.poupgo.driver.R;

public class LocationUpdateService extends Service implements UpdateDriverLocation.OnDemoLocationListener {
    private WindowManager mWindowManager;
    private View mAppBgHeadView;

    private static String LOG_TAG = "LocationUpdateService";

    private  String CHANNEL_ID = BuildConfig.APPLICATION_ID;
    private static final int FOREGROUND_SERVICE_ID = 126;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 126;

    Context mContext;
    GeneralFunctions generalFunc;

    boolean isPermissionDialogShown = false;

    private int UPDATE_INTERVAL = 2000;
    private int FATEST_INTERVAL = 1000;
    private int DISPLACEMENT = 8;

    boolean isResolutionFirstTime = true;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    LocationUpdates locationsUpdates;

    Location mLastLocation;

    private IBinder mBinder = new LocUpdatesBinder();
    NotificationManager mNotificationManager = null;

    UpdateDriverLocation updateDriverLoc;

    public class LocUpdatesBinder extends Binder {
        public LocationUpdateService getService() {
            return LocationUpdateService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this.getApplicationContext();

        generalFunc = MyApp.getInstance().getGeneralFun(mContext);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.mContext);
        mSettingsClient = LocationServices.getSettingsClient(this.mContext);

        runAsForeground();

        ConfigDriverTripStatus.getInstance().startDriverStatusUpdateTask();

        return Service.START_STICKY;
    }

    protected void configureDriverLocUpdates(boolean isStartLocationStorage, boolean isTripStarted, String iTripId) {
        if (updateDriverLoc == null) {
            updateDriverLoc = new UpdateDriverLocation(generalFunc, this, isStartLocationStorage, isTripStarted, iTripId, this);
            updateDriverLoc.executeProcess();
        } else {
            updateDriverLoc.setTripStartValue(isStartLocationStorage, isTripStarted, iTripId);
        }
    }

    protected ArrayList<Location> getListOfTripLocations() {
        ArrayList<Location> listOfTripLoc = new ArrayList<>();
        if (updateDriverLoc != null) {
            listOfTripLoc.addAll(updateDriverLoc.getListOfLocations());
        }
        return listOfTripLoc;
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    public void createLocationRequest(int displacement, LocationUpdates locationsUpdates) {
        this.locationsUpdates = locationsUpdates;
        this.DISPLACEMENT = displacement;
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);

        mLocationRequest.setMaxWaitTime(UPDATE_INTERVAL);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        buildLocationSettingsRequest();
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        mLocationSettingsRequest = builder.build();

        startLocationUpdateService();

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Location mCurrentLocation = locationResult.getLastLocation();

          /*  if (mCurrentLocation == null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && mCurrentLocation.isFromMockProvider()) || (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 && isMockSettingsON(mContext))) {
                return;
            }*/

            if (updateDriverLoc != null) {
                updateDriverLoc.onLocationUpdate(mCurrentLocation);
            }

            if (locationsUpdates != null) {
                locationsUpdates.onLocationUpdate(mCurrentLocation);
            }

            mLastLocation = mCurrentLocation;
        }
    };

    private void startLocationUpdateService() {
        if (MyApp.getInstance() == null) {
            return;
        }

        boolean isLocationPermissionGranted = MyApp.getInstance().getGeneralFun(MyApp.getInstance().getCurrentAct()).checkLocationPermission(isPermissionDialogShown);

        if (isLocationPermissionGranted) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {

                    })
                    .addOnFailureListener(e -> {
                        int statusCode = ((ApiException) e).getStatusCode();
                        //  mFusedLocationClient.requestLocationUpdates(mLocationRequest, this, Looper.myLooper());

                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    if (isResolutionFirstTime/*(mContext instanceof MainActivity || mContext instanceof DriverArrivedActivity || mContext instanceof ActiveTripActivity || mContext instanceof HailActivity)*/) {
                                        Activity currentAct = MyApp.getInstance().getCurrentAct();
                                        if (currentAct != null) {
                                            ResolvableApiException rae = (ResolvableApiException) e;
                                            rae.startResolutionForResult(currentAct, REQUEST_CHECK_SETTINGS);
                                        }

                                        isResolutionFirstTime = false;
                                    }
                                } catch (Exception sie) {
                                }
                                break;
                        }
                    });


        } else {
            isPermissionDialogShown = true;
        }

    }

    private boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }


    public Location getLastLocation() {
        try {
            if (generalFunc.checkLocationPermission(true)) {
                if (mFusedLocationClient != null) {
                    return mFusedLocationClient.getLastLocation().getResult();
                }
            }
        } catch (Exception e) {
        }

        return this.mLastLocation;
    }

    public void stopLocationUpdateService(Object obj) {
        try {
            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }

            if (updateDriverLoc != null) {
                updateDriverLoc.stopProcess();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.stopForeground(STOP_FOREGROUND_DETACH);
            }

            ConfigDriverTripStatus.getInstance().forceDestroy();

            hideAppBadgeFloat();

            if (obj != null && (obj instanceof GetLocationUpdates) == false) {
                if (GetLocationUpdates.retrieveInstance() != null) {
                    GetLocationUpdates.retrieveInstance().destroyLocUpdates(LocationUpdateService.this);
                }
            }


            this.stopSelf();

            if (mNotificationManager != null) {
                mNotificationManager.cancelAll();
                mNotificationManager = null;
            }

        } catch (Exception e) {
        }
    }

    private void runAsForeground() {
        Context mContext = this;

        Intent intent = null;
        if (Utils.getPreviousIntent(mContext) != null) {
            intent = Utils.getPreviousIntent(mContext);
        } else {
            intent = mContext
                    .getPackageManager()
                    .getLaunchIntentForPackage(mContext.getPackageName());

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID = createNotificationChannel("my_service", "My Background Service");
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(com.general.files.R.mipmap.ic_arrow_right)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(mContext.getString(com.general.files.R.string.app_name))
                .setContentText(generalFunc.retrieveLangLBl("Using Location", "LBL_USING_LOC"))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(generalFunc.retrieveLangLBl("Using Location", "LBL_USING_LOC")))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        startForeground(FOREGROUND_SERVICE_ID, mBuilder.build());

        if (MyApp.getInstance().isMyAppInBackGround()) {
            showAppBadgeFloat(intent);
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel( String channelId, String channelName) {

        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
    protected void showAppBadgeFloat(Intent intent) {

        if (!generalFunc.canDrawOverlayViews(mContext) || Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            return;
        }

        if (intent == null) {
            if (Utils.getPreviousIntent(mContext) != null) {
                intent = Utils.getPreviousIntent(mContext);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            } else {
                intent = mContext
                        .getPackageManager()
                        .getLaunchIntentForPackage(mContext.getPackageName());

                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            }
        }

        hideAppBadgeFloat();

        mAppBgHeadView = LayoutInflater.from(this).inflate(R.layout.design_float_bg, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 15;
        params.y = 100;
        params.windowAnimations = android.R.style.Animation_Translucent;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        if (mWindowManager == null) {
            return;
        }

        mWindowManager.addView(mAppBgHeadView, params);

        final ImageView appBgHeadImgView = (ImageView) mAppBgHeadView.findViewById(R.id.appBgHeadImgView);
        Intent finalIntent = intent;

        appBgHeadImgView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        startX = event.getX();
                        startY = event.getY();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();
                        if (shouldClickActionWork(startX, endX, startY, endY)) {
                            LocationUpdateService.this.startActivity(finalIntent);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if (mWindowManager != null) {
                            mWindowManager.updateViewLayout(mAppBgHeadView, params);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    protected void hideAppBadgeFloat() {
        if (mWindowManager != null && mAppBgHeadView != null) {
            if (mAppBgHeadView.findViewById(R.id.appBgHeadImgView) != null) {
                (mAppBgHeadView.findViewById(R.id.appBgHeadImgView)).setOnTouchListener(null);
            }
            mAppBgHeadView.setVisibility(View.GONE);
            mWindowManager.removeView(mAppBgHeadView);
            mAppBgHeadView = null;
            mWindowManager = null;
        }
    }

    private boolean shouldClickActionWork(float startX, float endX, float startY, float endY) {
        float CLICK_ACTION_THRESHOLD = 5;

        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        if (MyApp.getInstance().isMyAppInBackGround()) {
        stopLocationUpdateService(LocationUpdateService.this);
//        }
        super.onTaskRemoved(rootIntent);
    }

    public interface LocationUpdates {
        void onLocationUpdate(Location location);
    }

    @Override
    public void onTrimMemory(int level) {
        //stopLocationUpdateService();
        super.onTrimMemory(level);
    }

    @Override
    public void onDestroy() {
        stopLocationUpdateService(LocationUpdateService.this);
        super.onDestroy();
    }

    @Override
    public void onReceiveDemoLocation(Location location) {
        if(location != null && locationsUpdates != null){
            locationsUpdates.onLocationUpdate(location);
        }
    }
}