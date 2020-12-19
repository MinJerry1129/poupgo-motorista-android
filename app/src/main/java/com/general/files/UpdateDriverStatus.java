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
 * Created by Admin on 18-07-2016.
 */
public class UpdateDriverStatus extends Service implements UpdateFrequentTask.OnTaskRunCalled, GetLocationUpdates.LocationUpdatesListener {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    UpdateFrequentTask updateDriverStatusTask;
    GetLocationUpdates getLastLocation;
    Location driverLocation;
    String iDriverId = "";
    ExecuteWebServerUrl currentExeTask;
    GeneralFunctions generalFunc;
    Location lastPublishedLoc;
    double PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = 5;
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_PAUSE = "ACTION_PAUSE";

    public static final String ACTION_PLAY = "ACTION_PLAY";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void stopForegroundService()
    {
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
        updateDriverStatusTask = new UpdateFrequentTask(2 * 60 * 1000);
        updateDriverStatusTask.setTaskRunListener(this);
        updateDriverStatusTask.startRepeatingTask();

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        GetLocationUpdates.getInstance().startLocationUpdates(this, this);


        return Service.START_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.poupgo.driver";
        String channelName = "My Backgrossund Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_driver_logo)
                .setContentTitle("Executando em background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public void onTaskRun() {
        updateOnlineAvailability("");
    }

    public void updateOnlineAvailability(String status) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateDriverStatus");
        parameters.put("iDriverId", iDriverId);

        if (driverLocation != null) {
            parameters.put("latitude", "" + driverLocation.getLatitude());
            parameters.put("longitude", "" + driverLocation.getLongitude());
        }

        if (status.equals("Not Available")) {
            parameters.put("Status", "Not Available");
        }

        if (this.currentExeTask != null) {
            this.currentExeTask.cancel(true);
            this.currentExeTask = null;
            Utils.runGC();
        }

        parameters.put("isUpdateOnlineDate", "true");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getApplicationContext(), parameters);
        this.currentExeTask = exeWebServer;
        exeWebServer.setDataResponseListener(responseString -> {

        });
        exeWebServer.execute();
    }

    @Override
    public void onLocationUpdate(Location location) {
        this.driverLocation = location;

        updateLocationToPubNubBeforeTrip();
    }

    public void stopFreqTask() {

        if (updateDriverStatusTask != null) {
            updateDriverStatusTask.stopRepeatingTask();
            updateDriverStatusTask = null;
        }

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }


        Utils.runGC();
    }


    public void updateLocationToPubNubBeforeTrip() {
        if (driverLocation != null && driverLocation.getLongitude() != 0.0 && driverLocation.getLatitude() != 0.0) {


            if (lastPublishedLoc != null) {

                if (driverLocation.distanceTo(lastPublishedLoc) < PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT) {
                    return;
                } else {
                    lastPublishedLoc = driverLocation;
                }

            } else {
                lastPublishedLoc = driverLocation;
            }


            ConfigPubNub.getInstance().publishMsg(generalFunc.getLocationUpdateChannel(), generalFunc.buildLocationJson(driverLocation));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.printLog("UpdateDriverStatus", "onDestroy >> Yes");


        stopFreqTask();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Utils.printLog("UpdateDriverStatus", "OnTaskRemoved >> Yes");

        updateOnlineAvailability("Not Available");

        for (int i = 0; i < 100; i++) {

        }

        stopFreqTask();
    }
}
