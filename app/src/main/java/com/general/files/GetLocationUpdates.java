package com.general.files;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;

import com.poupgo.driver.ActiveTripActivity;
import com.poupgo.driver.DriverArrivedActivity;
import com.poupgo.driver.HailActivity;
import com.poupgo.driver.MainActivity;
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
import com.general.functions.GeneralFunctions;
import com.general.functions.Utils;
import com.utilities.general.files.StartActProcess;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 27-06-2016.
 */

public class GetLocationUpdates implements LocationUpdateService.LocationUpdates {

    Location mLocation;
    private static GetLocationUpdates instance;

    boolean mServiceBound = false;

    LocationUpdateService locUpdateService;

    HashMap<String, LocationUpdatesListener> listOfListener = new HashMap<>();
    Intent locUpdateServiceIntent;

    boolean isTripStarted = false;
    boolean isStartLocationStorage = false;
    String iTripId = "";

    public static GetLocationUpdates getInstance() {
        if (instance == null) {
            instance = new GetLocationUpdates();
        }
        return instance;
    }

    public static GetLocationUpdates retrieveInstance() {
        return instance;
    }


    public GetLocationUpdates() {
        locUpdateServiceIntent = new Intent(MyApp.getInstance().getApplicationContext(), LocationUpdateService.class);

        locUpdateServiceIntent = new StartActProcess(MyApp.getInstance().getApplicationContext()).startForegroundService(LocationUpdateService.class);
        MyApp.getInstance().getApplicationContext().bindService(locUpdateServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                String pkg = MyApp.getInstance().getApplicationContext().getPackageName();
                PowerManager pm = MyApp.getInstance().getApplicationContext().getSystemService(PowerManager.class);

                if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                    Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:" + pkg));
                    MyApp.getInstance().getApplicationContext().startActivity(i);
                }
            }
        } catch (Exception e) {

        }
    }

    protected void showAppBadgeFloat() {
        if (locUpdateService == null) {
            return;
        }
        locUpdateService.showAppBadgeFloat(null);
    }

    protected void hideAppBadgeFloat() {
        if (locUpdateService == null) {
            return;
        }
        locUpdateService.hideAppBadgeFloat();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdateService.LocUpdatesBinder locUpdatesBinder = (LocationUpdateService.LocUpdatesBinder) service;

            locUpdateService = locUpdatesBinder.getService();
            locUpdateService.createLocationRequest(Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, GetLocationUpdates.this);

            locUpdateService.configureDriverLocUpdates(isStartLocationStorage, isTripStarted, iTripId);

            mServiceBound = true;
        }
    };

    public void setTripStartValue(boolean isStartLocationStorage, boolean isTripStarted, String iTripId) {
        this.isStartLocationStorage = isStartLocationStorage;
        this.isTripStarted = isTripStarted;
        this.iTripId = iTripId;
        if (locUpdateService != null) {
            locUpdateService.configureDriverLocUpdates(isStartLocationStorage, isTripStarted, iTripId);
        }
    }

    public ArrayList<Location> getListOfTripLocations() {
        ArrayList<Location> listOfTripLoc = new ArrayList<>();
        if (locUpdateService != null) {
            listOfTripLoc.addAll(locUpdateService.getListOfTripLocations());
        }
        return listOfTripLoc;
    }

    public Location getLastLocation() {
        if (mLocation == null && locUpdateService != null) {
            mLocation = locUpdateService.getLastLocation();
        }
        return mLocation;
    }

    @Override
    public void onLocationUpdate(Location location) {
        this.mLocation = location;

        ArrayList<String> keyOfListenerList = new ArrayList<>();
        for (String currentKey : listOfListener.keySet()) {
            try {
                if (listOfListener.get(currentKey) != null) {
                    LocationUpdatesListener listener = listOfListener.get(currentKey);
                    listener.onLocationUpdate(location);
                }
            } catch (Exception e) {
                try {
                    keyOfListenerList.add(currentKey);
                } catch (Exception e1) {
                }
            }
        }

        try {

            if (keyOfListenerList.size() > 0) {
                for (int i = 0; i < keyOfListenerList.size(); i++) {
                    listOfListener.remove(keyOfListenerList.get(i));
                }
            }
        } catch (Exception e1) {
        }

    }

    public void startLocationUpdates(Object obj, LocationUpdatesListener locUpdatesListener) {
        listOfListener.put(obj.getClass().getSimpleName(), locUpdatesListener);

        if (mLocation != null) {
            new Handler().postDelayed(() -> locUpdatesListener.onLocationUpdate(mLocation), 500);
        }
    }

    public void stopLocationUpdates(Object obj) {
        if (instance == null || obj == null) {
            return;
        }
        listOfListener.remove(obj.getClass().getSimpleName());
    }

    public void destroyLocUpdates(Object obj) {
        if (obj == null) {
            throw new RuntimeException("Object should not be null");
        }
        try {
            listOfListener.clear();
            if (obj != null && (obj instanceof LocationUpdateService) == false) {
                locUpdateService.stopLocationUpdateService(obj);
            }
            MyApp.getInstance().unbindService(mConnection);
            instance = null;
        } catch (Exception e) {

        }
    }

    public interface LocationUpdatesListener {
        void onLocationUpdate(Location location);
    }
}