package com.general.files;

import android.content.Context;
import android.location.Location;

import com.general.functions.GeneralFunctions;
import com.utilities.general.files.UpdateFrequentTask;
import com.utilities.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class UpdateDriverLocation implements UpdateFrequentTask.OnTaskRunCalled {
    GeneralFunctions generalFunc;
    Context mContext;

    Location driverLocation;
    Location lastPublishedLocation;
    String iDriverId = "";

    Location lastPublishedLoc = null;
    double PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = 5;
    DispatchDemoLocations dispatchDemoLoc;

    UpdateFrequentTask updateDriverStatusTask;
    ExecuteWebServerUrl currentDriverStatusExeTask;

    UpdateFrequentTask updateTripLocationsTask;
    ExecuteWebServerUrl currentTripLocationExeTask;

    int UPDATE_TIME_INTERVAL = 30 * 1000;
    int LOCATION_ACCURACY_METERS = 200;

    boolean isStartLocationStorage = false;
    boolean isTripStarted = false;
    JSONObject userProfileJsonObj;
    String iTripId = "";

    ArrayList<Location> listOfTripLocations;
    long lastLocationUpdateTimeInMill = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1;
    long waitingTime = 0;
    public boolean isDataUploading = false;

    OnDemoLocationListener onDemoLocationListener;

    protected UpdateDriverLocation(GeneralFunctions generalFunc, Context mContext, boolean isStartLocationStorage, boolean isTripStarted, String iTripId, OnDemoLocationListener onDemoLocationListener) {
        this.generalFunc = generalFunc;
        this.mContext = mContext;
        this.isStartLocationStorage = isStartLocationStorage;
        this.isTripStarted = isTripStarted;
        this.iTripId = iTripId;
        this.onDemoLocationListener = onDemoLocationListener;
    }

    public void executeProcess() {
        if (mContext == null) {
            return;
        }

        iDriverId = generalFunc.getMemberId();

        PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT = GeneralFunctions.parseDoubleValue(5, generalFunc.retrieveValue(Utils.PUBSUB_PUBLISH_DRIVER_LOC_DISTANCE_LIMIT));
        LOCATION_ACCURACY_METERS = generalFunc.parseIntegerValue(200, generalFunc.retrieveValue("LOCATION_ACCURACY_METERS"));

        userProfileJsonObj = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        setTripStartValue(isStartLocationStorage, isTripStarted, iTripId);
    }

    public void setTripStartValue(boolean isStartLocationStorage, boolean isTripStarted, String iTripId) {
        this.isStartLocationStorage = isStartLocationStorage;
        this.isTripStarted = isTripStarted;
        this.iTripId = iTripId;

        if (dispatchDemoLoc != null) {
            dispatchDemoLoc.stopDispatchingDemoLocations();
            dispatchDemoLoc = null;
        }

        if (isTripStarted && generalFunc.getJsonValueStr("eEnableDemoLocDispatch", userProfileJsonObj).equalsIgnoreCase("Yes")) {
//            dispatchDemoLoc = new DispatchDemoLocations(mContext);
//            dispatchDemoLoc.startDispatchingLocations((latitude, longitude) -> {
//                Location loc = new Location("gps");
//                loc.setLatitude(latitude);
//                loc.setLongitude(longitude);
//                onLocationUpdate(loc);
//
//                if(onDemoLocationListener != null){
//                    onDemoLocationListener.onReceiveDemoLocation(loc);
//                }
//            });
        }

        if (updateDriverStatusTask != null) {
            updateDriverStatusTask.stopRepeatingTask();
            updateDriverStatusTask = null;
        }

        if (updateTripLocationsTask != null) {
            updateTripLocationsTask.stopRepeatingTask();
            updateTripLocationsTask = null;
        }

        if (isTripStarted && isStartLocationStorage) {
            listOfTripLocations = new ArrayList<Location>();
            updateTripLocationsTask = new UpdateFrequentTask(UPDATE_TIME_INTERVAL);
            updateTripLocationsTask.setTaskRunListener(this);
            updateTripLocationsTask.startRepeatingTask();
        } else if (!isTripStarted && MyApp.getInstance().mainAct != null) {
            updateDriverStatusTask = new UpdateFrequentTask(2 * 60 * 1000);
            updateDriverStatusTask.setTaskRunListener(this);
            updateDriverStatusTask.startRepeatingTask();
        }
    }

    public void onLocationUpdate(Location location) {
        if (location == null) {
            return;
        }

        if (location != null /*&& location.getAccuracy() < LOCATION_ACCURACY_METERS*/ && listOfTripLocations != null) {
            if (listOfTripLocations.size() > 0 && location.distanceTo(listOfTripLocations.get(listOfTripLocations.size() - 1)) > Utils.LOCATION_POST_MIN_DISTANCE_IN_MITERS) {
                listOfTripLocations.add(location);
            } else if (listOfTripLocations.size() == 0) {
                listOfTripLocations.add(location);
            }
        }

        this.driverLocation = location;

        if (isTripStarted) {
            if (lastLocationUpdateTimeInMill == 0) {
                lastLocationUpdateTimeInMill = System.currentTimeMillis();
            } else {
                long currentTimeInMill = System.currentTimeMillis();
                if ((currentTimeInMill - lastLocationUpdateTimeInMill) > MIN_TIME_BW_UPDATES) {
                    waitingTime = waitingTime + (currentTimeInMill - lastLocationUpdateTimeInMill);
                    lastLocationUpdateTimeInMill = currentTimeInMill;
                }
                generalFunc.storedata(Utils.DriverWaitingTime,waitingTime + "");
            }
        }

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

                ConfigPubNub.getInstance().publishMsg(generalFunc.getLocationUpdateChannel(), isTripStarted ? generalFunc.buildLocationJson(driverLocation, "LocationUpdateOnTrip") : generalFunc.buildLocationJson(driverLocation));
            }
        }
    }

    public void stopProcess() {
        if (dispatchDemoLoc != null) {
            dispatchDemoLoc.stopDispatchingDemoLocations();
            dispatchDemoLoc = null;
        }

        if (updateDriverStatusTask != null) {
            updateDriverStatusTask.stopRepeatingTask();
            updateDriverStatusTask = null;
        }

        if (updateTripLocationsTask != null) {
            updateTripLocationsTask.stopRepeatingTask();
            updateTripLocationsTask = null;
        }

        if (!isTripStarted) {
            updateOnlineAvailability("Not Available");
        }

        for (int i = 0; i < 2500; i++) {

        }
    }

    @Override
    public void onTaskRun() {
        if (isTripStarted) {
            updateDriverLocations();
        } else {
            updateOnlineAvailability("");
        }
    }

    public void updateOnlineAvailability(String status) {
        if (isTripStarted) {
            if (updateDriverStatusTask != null) {
                updateDriverStatusTask.stopRepeatingTask();
                updateDriverStatusTask = null;
            }
            return;
        }
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

        if (this.currentDriverStatusExeTask != null) {
            this.currentDriverStatusExeTask.cancel(true);
            this.currentDriverStatusExeTask = null;
            Utils.runGC();
        }

        parameters.put("isUpdateOnlineDate", "true");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        this.currentDriverStatusExeTask = exeWebServer;
        exeWebServer.setDataResponseListener(responseString -> {

        });
        exeWebServer.execute();
    }

    public void updateDriverLocations() {
        if (iTripId.equalsIgnoreCase("")) {
            return;
        }

        if (listOfTripLocations.size() > 0 && isDataUploading == false) {
            isDataUploading = true;

            final ArrayList<Location> tempList = new ArrayList<>();
            tempList.addAll(listOfTripLocations);

            String store_locations_latitude_str = "";
            String store_locations_longitude_str = "";

            for (int i = 0; i < tempList.size(); i++) {

                double location_latitude = tempList.get(i).getLatitude();
                double location_longitude = tempList.get(i).getLongitude();

                if (i != listOfTripLocations.size() - 1) {
                    store_locations_latitude_str = store_locations_latitude_str + location_latitude + ",";
                    store_locations_longitude_str = store_locations_longitude_str + location_longitude + ",";
                } else {
                    store_locations_latitude_str = store_locations_latitude_str + location_latitude;
                    store_locations_longitude_str = store_locations_longitude_str + location_longitude;
                }

            }
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "updateTripLocations");
            parameters.put("TripId", iTripId);
            parameters.put("latList", store_locations_latitude_str);
            parameters.put("lonList", store_locations_longitude_str);

           /* if (this.currentExeTask != null) {
                this.currentExeTask.cancel(true);
            }*/

            if (this.currentTripLocationExeTask != null) {
                this.currentTripLocationExeTask.cancel(true);
                this.currentTripLocationExeTask = null;
                Utils.runGC();
            }


            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
            this.currentTripLocationExeTask = exeWebServer;
            exeWebServer.setDataResponseListener(responseString -> {

                boolean isDataAvail = generalFunc.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    for (int i = 0; i < tempList.size(); i++) {
                        listOfTripLocations.remove(0);
                    }
                }

                isDataUploading = false;
            });
            exeWebServer.execute();
        }
    }

    public ArrayList<Location> getListOfLocations() {

        if (this.currentTripLocationExeTask != null) {
            this.currentTripLocationExeTask.cancel(true);
            this.currentTripLocationExeTask = null;
            Utils.runGC();
        }
        return listOfTripLocations;
    }

    protected interface OnDemoLocationListener{
        void onReceiveDemoLocation(Location location);
    }
}
