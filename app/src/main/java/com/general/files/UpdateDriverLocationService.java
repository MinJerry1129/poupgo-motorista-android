package com.general.files;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.poupgo.driver.R;
import com.general.functions.GeneralFunctions;
import com.utilities.general.files.UpdateFrequentTask;
import com.general.functions.Utils;

import java.util.HashMap;

/**
 * Created by Admin on 20-07-2016.
 */
public class UpdateDriverLocationService extends Service implements UpdateFrequentTask.OnTaskRunCalled, GetLocationUpdates.LocationUpdatesListener {

    GetLocationUpdates getLocationUpdates;
    Location driverLocation;
    Location lastPublishedLocation;
    String iDriverId = "";
    ExecuteWebServerUrl currentExeTask;

    GeneralFunctions generalFunc;
    Location lastPublishedLoc = null;
    double PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = 5;

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_PAUSE = "ACTION_PAUSE";

    public static final String ACTION_PLAY = "ACTION_PLAY";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        iDriverId = (new GeneralFunctions(this)).getMemberId();

        generalFunc = new GeneralFunctions(this);
        PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = GeneralFunctions.parseDoubleValue(5, generalFunc.retrieveValue(Utils.PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT));

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        GetLocationUpdates.getInstance().startLocationUpdates(this, this);

        return Service.START_STICKY;

    }

    @Override
    public void onTaskRun() {
        updateDriverLocations();
    }

    public void updateDriverLocations() {
        if (driverLocation == null) {
            return;
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateDriverLocations");
        parameters.put("iDriverId", iDriverId);
        parameters.put("latitude", "" + driverLocation.getLatitude());
        parameters.put("longitude", "" + driverLocation.getLongitude());

        if (this.currentExeTask != null) {
            this.currentExeTask.cancel(true);
            this.currentExeTask = null;
            Utils.runGC();
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getApplicationContext(), parameters);
        this.currentExeTask = exeWebServer;
        exeWebServer.setDataResponseListener(responseString -> Utils.printLog("Api", "Update Locations Response ::" + responseString));
        exeWebServer.execute();
    }

    @Override
    public void onLocationUpdate(Location location) {
        this.driverLocation = location;

        if (generalFunc != null && driverLocation != null) {

            if (lastPublishedLocation == null || (lastPublishedLocation.distanceTo(driverLocation) > 2)) {
                lastPublishedLocation = driverLocation;
                if (lastPublishedLoc != null) {

                    if (driverLocation.distanceTo(lastPublishedLoc) < PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT) {
                        return;
                    } else {
                        lastPublishedLoc = driverLocation;
                    }

                } else {
                    lastPublishedLoc = driverLocation;
                }


                ConfigPubNub.getInstance().publishMsg(generalFunc.getLocationUpdateChannel(), generalFunc.buildLocationJson(driverLocation, "LocationUpdateOnTrip"));
            }
        }
    }


    public void stopFreqTask() {
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFreqTask();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopFreqTask();
    }
}
