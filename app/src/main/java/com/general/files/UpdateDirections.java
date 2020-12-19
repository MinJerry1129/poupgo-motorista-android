package com.general.files;

import android.content.Context;
import android.location.Location;

import com.drawRoute.DirectionsJSONParser;
import com.google.android.gms.maps.model.LatLng;
import com.poupgo.driver.ActiveTripActivity;
import com.poupgo.driver.DriverArrivedActivity;

import com.poupgo.driver.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.general.functions.GeneralFunctions;
import com.maps.Directions;
import com.maps.DirectionsResponse;
import com.maps.MapService;
import com.utilities.general.files.UpdateFrequentTask;
import com.utils.CommonUtilities;
import com.general.functions.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 02-08-2017.
 */

//public class UpdateDirections implements GetLocationUpdates.LocationUpdates, UpdateFrequentTask.OnTaskRunCalled {
public class UpdateDirections implements UpdateFrequentTask.OnTaskRunCalled {

    public GoogleMap googleMap;
    public Location destinationLocation;
    public Context mcontext;
    public Location userLocation;

    GeneralFunctions generalFunctions;

    String serverKey;
    Polyline route_polyLine;

    UpdateFrequentTask updateFreqTask;

    String gMapLngCode = "en";
    String userProfileJson = "";
    String eUnit = "KMs";
    int DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = 3;

    public UpdateDirections(Context mcontext, GoogleMap googleMap, Location userLocation, Location destinationLocation) {
        this.googleMap = googleMap;
        this.destinationLocation = destinationLocation;
        this.mcontext = mcontext;
        this.userLocation = userLocation;

        generalFunctions = new GeneralFunctions(mcontext);

        serverKey = generalFunctions.retrieveValue(CommonUtilities.GOOGLE_SERVER_ANDROID_DRIVER_APP_KEY);

        gMapLngCode = generalFunctions.retrieveValue(CommonUtilities.GOOGLE_MAP_LANGUAGE_CODE_KEY);

        userProfileJson = generalFunctions.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        eUnit = generalFunctions.getJsonValue("eUnit", userProfileJson);
        DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = generalFunctions.parseIntegerValue(3, generalFunctions.getJsonValue("DRIVER_ARRIVED_MIN_TIME_PER_MINUTE", userProfileJson));
    }

    public void scheduleDirectionUpdate() {

        releaseTask();
        String DESTINATION_UPDATE_TIME_INTERVAL = generalFunctions.retrieveValue("DESTINATION_UPDATE_TIME_INTERVAL");
        updateFreqTask = new UpdateFrequentTask((int) (generalFunctions.parseDoubleValue(2, DESTINATION_UPDATE_TIME_INTERVAL) * 60 * 1000));
        updateFreqTask.setTaskRunListener(this);
        updateFreqTask.startRepeatingTask();

    }

    public void releaseTask() {
        Utils.printLog("Task", "::releaseTask called");
        if (updateFreqTask != null) {
            updateFreqTask.stopRepeatingTask();
            updateFreqTask = null;
        }

        Utils.runGC();


    }

    public void changeDestLoc(Location destinationLocation) {
        this.destinationLocation = destinationLocation;

    }

    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }


    public String getTimeTxt(int duration) {

        if (duration < 1) {
            duration = 1;
        }
        String durationTxt = "";
        String timeToreach = duration == 0 ? "--" : "" + duration;

        timeToreach = duration > 60 ? formatHoursAndMinutes(duration) : timeToreach;


        durationTxt = (duration < 60 ? generalFunctions.retrieveLangLBl("", "LBL_MINS_SMALL") : generalFunctions.retrieveLangLBl("", "LBL_HOUR_TXT"));

        durationTxt = duration == 1 ? generalFunctions.retrieveLangLBl("", "LBL_MIN_SMALL") : durationTxt;
        durationTxt = duration > 120 ? generalFunctions.retrieveLangLBl("", "LBL_HOURS_TXT") : durationTxt;

        Utils.printLog("durationTxt", "::" + durationTxt);
        return timeToreach + " " + durationTxt;
    }

    public void updateDirections() {

        if (userLocation == null || destinationLocation == null) {
            return;
        }

        if (userProfileJson != null && !generalFunctions.getJsonValue("ENABLE_DIRECTION_SOURCE_DESTINATION_DRIVER_APP", userProfileJson).equalsIgnoreCase("Yes")) {


            if (destinationLocation != null) {
                double distance = Utils.CalculationByLocation(userLocation.getLatitude(), userLocation.getLongitude(), destinationLocation.getLatitude(), destinationLocation.getLongitude(), "");

                int lowestTime = ((int) (distance * DRIVER_ARRIVED_MIN_TIME_PER_MINUTE));

                if (lowestTime < 1) {
                    lowestTime = 1;
                }


                if (userProfileJson != null && !generalFunctions.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
                    distance = distance * 0.000621371;
                }

                distance = generalFunctions.round(distance, 2);

                if (mcontext instanceof DriverArrivedActivity) {
                    DriverArrivedActivity driverArrivedActivity = (DriverArrivedActivity) mcontext;
//                                driverArrivedActivity.setTimetext(String.format("%.2f", (float) distance_final) + "", time);
                    driverArrivedActivity.setTimetext(generalFunctions.formatUpto2Digit(distance) + "", getTimeTxt(lowestTime));
                    driverArrivedActivity.getMap().setPadding(15,15,15,15);
                } else if (mcontext instanceof ActiveTripActivity) {
                    if (destinationLocation.getLatitude() > 0) {
                        ActiveTripActivity activeTripActivity = (ActiveTripActivity) mcontext;

//                                activeTripActivity.setTimetext(String.format("%.2f", (float) distance_final) + "", time);
                        activeTripActivity.setTimetext(generalFunctions.formatUpto2Digit(distance) + "", getTimeTxt(lowestTime));
                        activeTripActivity.getMap().setPadding(15,15,15,15);
                    }
                }


            }


            return;

        }

        LatLng originLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        LatLng destLocation = new LatLng(destinationLocation.getLatitude(), destinationLocation.getLongitude());

        MapService.getDirections(originLocation, destLocation, new MapService.MapServiceCallback() {
            @Override
            public void onDirectionsSuccess(DirectionsResponse response) {
                Directions[] directions = response.polyline;

                if(directions != null && directions.length > 0){
                    String distance = String.valueOf(response.distance);
                    String time = String.valueOf(response.travelTime);

                    Double finalDistance = generalFunctions.parseDoubleValue(0.0, distance);

                    if (userProfileJson != null && !generalFunctions.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
                        finalDistance = finalDistance * 0.000621371;
                    } else {
                        finalDistance = finalDistance * 0.00099999969062399994;
                    }

                    finalDistance = generalFunctions.round(finalDistance, 2);

                    if (mcontext instanceof DriverArrivedActivity) {
                        DriverArrivedActivity driverArrivedActivity = (DriverArrivedActivity) mcontext;
//                                driverArrivedActivity.setTimetext(String.format("%.2f", (float) distance_final) + "", time);

                        driverArrivedActivity.setTimetext(generalFunctions.formatUpto2Digit(finalDistance) + "", getTimeTxt((GeneralFunctions.parseIntegerValue(0, time) % 3600) / 60));
                    } else if (mcontext instanceof ActiveTripActivity) {
                        ActiveTripActivity activeTripActivity = (ActiveTripActivity) mcontext;

//                                activeTripActivity.setTimetext(String.format("%.2f", (float) distance_final) + "", time);
                        activeTripActivity.setTimetext(generalFunctions.formatUpto2Digit(finalDistance) + "", getTimeTxt((GeneralFunctions.parseIntegerValue(0, time) % 3600) / 60));
                    }

                    PolylineOptions lineOptions = getGoogleRouteOptions(directions, Utils.dipToPixels(mcontext, 5), mcontext.getResources().getColor(R.color.black));

                    if (lineOptions != null) {
                        if (route_polyLine != null) {
                            route_polyLine.remove();
                        }
                        route_polyLine = googleMap.addPolyline(lineOptions);

                    }
                }
            }

            @Override
            public void onDirectionsFailed(DirectionsResponse response) {

            }
        });

    }

    public static PolylineOptions getGoogleRouteOptions(Directions[] directions, int width, int color) {
        PolylineOptions lineOptions = new PolylineOptions();

        ArrayList<LatLng> points = new ArrayList<LatLng>();

        for (Directions dir : directions) {
            LatLng position = new LatLng(dir.lat, dir.lng);
            points.add(position);
        }

        lineOptions.addAll(points);
        lineOptions.width(width);
        lineOptions.color(color);

        return lineOptions;
    }


    @Override
    public void onTaskRun() {
        Utils.runGC();
        Utils.printLog("Task", "::onTask called");
        updateDirections();
    }

    public void changeUserLocation(Location location) {
        if (location != null) {
            this.userLocation = location;
        }
    }
}
