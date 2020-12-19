package com.general.files;

import android.content.Context;
import android.location.Location;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.general.functions.GeneralFunctions;
import com.general.functions.InternetConnection;
import com.utilities.general.files.UpdateFrequentTask;
import com.utilities.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class ConfigDriverTripStatus implements GetLocationUpdates.LocationUpdatesListener, UpdateFrequentTask.OnTaskRunCalled {
    Context mContext;

    /**
     * Variable declaration of singleton instance.
     */
    private static ConfigDriverTripStatus configDriverInstance = null;

    GeneralFunctions generalFunc;
    InternetConnection intCheck;
    private ExecuteWebServerUrl currentExeTask;
    int FETCH_TRIP_STATUS_TIME_INTERVAL = 15;

    public Location driverLoc = null;

    private UpdateFrequentTask updateDriverStatusTask;

    FirebaseJobDispatcher dispatcher;

    JobFinishedListener jobFinishedListener;

    /**
     * Creating Singleton instance. By using this method will create only one instance of this class.
     */
    public static ConfigDriverTripStatus getInstance() {
        if (configDriverInstance == null) {
            configDriverInstance = new ConfigDriverTripStatus(MyApp.getInstance().getApplicationContext());
        }
        return configDriverInstance;
    }

    public ConfigDriverTripStatus(Context mContext) {
        this.mContext = mContext;

        generalFunc = MyApp.getInstance().getGeneralFun(this.mContext);
        intCheck = new InternetConnection(this.mContext);
    }

    /**
     * Used to start driver's status task. This will initialize frequent task to update device's current location to server and trip status messages.
     */
    public void startDriverStatusUpdateTask() {

        stopLocationUpdateTask();

        Context mContext = MyApp.getInstance().getApplicationContext();

        if (mContext != null) {
            if (GetLocationUpdates.retrieveInstance() != null) {
                GetLocationUpdates.getInstance().stopLocationUpdates(this);
            }
            GetLocationUpdates.getInstance().startLocationUpdates(this, this);

            stopDriverStatusUpdateTask();

            FETCH_TRIP_STATUS_TIME_INTERVAL = GeneralFunctions.parseIntegerValue(15, GeneralFunctions.retrieveValue(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY, mContext));

            updateDriverStatusTask = new UpdateFrequentTask(FETCH_TRIP_STATUS_TIME_INTERVAL * 1000);

            updateDriverStatusTask.setTaskRunListener(this);
            updateDriverStatusTask.startRepeatingTask();

            try {
                if (dispatcher != null) {
                    dispatcher.cancel(ConfigDriverTripStatusJobService.class.getSimpleName());
                }
                dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MyApp.getInstance().getApplicationContext()));

                Job myJob = dispatcher.newJobBuilder()
                        .setService(ConfigDriverTripStatusJobService.class)
                        .setTag(ConfigDriverTripStatusJobService.class.getSimpleName())
                        .setRecurring(true)
                        .setLifetime(Lifetime.FOREVER)
                        .setTrigger(Trigger.executionWindow(0, FETCH_TRIP_STATUS_TIME_INTERVAL))
                        .setReplaceCurrent(true)
                        .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .build();

                dispatcher.mustSchedule(myJob);
            } catch (Exception e) {

            }
        }
    }

    private void setAppInactiveNotification() {
        if (MyApp.getInstance() == null) {
            return;
        }
        if (!GeneralFunctions.retrieveValue(Utils.DRIVER_ONLINE_KEY, mContext).equalsIgnoreCase("") && GeneralFunctions.retrieveValue(Utils.DRIVER_ONLINE_KEY, mContext).equalsIgnoreCase("true")) {
            Calendar calendar = Calendar.getInstance();
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minutesOfDay = calendar.get(Calendar.MINUTE);
            int seondsOfDay = calendar.get(Calendar.SECOND);
            if (MyApp.getInstance() != null && MyApp.getInstance().getApplicationContext() != null) {
                NotificationScheduler.setReminder(MyApp.getInstance().getApplicationContext(), AlarmReceiver.class, hourOfDay, minutesOfDay, seondsOfDay + FETCH_TRIP_STATUS_TIME_INTERVAL + 5);
            }
        } else {
            NotificationScheduler.cancelReminder(MyApp.getInstance().getApplicationContext(), AlarmReceiver.class);
        }

    }

    public void forceDestroy() {
        stopLocationUpdateTask();
        stopDriverStatusUpdateTask();
    }

    /**
     * Used to stop location updates.
     */
    private void stopLocationUpdateTask() {
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
    }

    /**
     * Used to stop driver status update task (A frequent task).
     */
    private void stopDriverStatusUpdateTask() {
        if (updateDriverStatusTask != null) {
            updateDriverStatusTask.stopRepeatingTask();
            updateDriverStatusTask = null;
        }


        if (dispatcher != null) {
            try {
                dispatcher.cancel(ConfigDriverTripStatusJobService.class.getSimpleName());
            } catch (Exception e) {

            }
        }
    }


    private void updateDriverTripStatus() {

        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            callToJobFinish();
            return;
        }

        if (generalFunc.getMemberId().equalsIgnoreCase("")) {
            stopDriverStatusUpdateTask();
            return;
        }

        String iTripId = "";

        if (MyApp.getInstance() != null) {
            if (MyApp.getInstance().driverArrivedAct != null || MyApp.getInstance().activeTripAct != null) {

                if (MyApp.getInstance().activeTripAct != null) {
                    if (MyApp.getInstance().activeTripAct.data_trip == null) {
                        return;
                    }
                    iTripId = MyApp.getInstance().activeTripAct.data_trip.get("TripId");
                } else {
                    if (MyApp.getInstance().driverArrivedAct.data_trip == null) {
                        return;
                    }
                    iTripId = MyApp.getInstance().driverArrivedAct.data_trip.get("TripId");
                }
            }
        }
        generalFunc.storedata("CONFIG_DRIVER_TRIP_STATUS_CALLED",System.currentTimeMillis() +"");

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "configDriverTripStatus");
        parameters.put("iTripId", iTripId);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);

        if (GetLocationUpdates.retrieveInstance() != null && GetLocationUpdates.getInstance().getLastLocation() != null) {
            Location loc_driver = GetLocationUpdates.getInstance().getLastLocation();
            parameters.put("vLatitude", "" + loc_driver.getLatitude());
            parameters.put("vLongitude", "" + loc_driver.getLongitude());
        }

        if (MyApp.getInstance().mainAct != null) {
            parameters.put("isSubsToCabReq", "" + MyApp.getInstance().mainAct.isDriverOnline);
        }

        if (this.currentExeTask != null) {
            this.currentExeTask.cancel(true);
            this.currentExeTask = null;
            Utils.runGC();
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        this.currentExeTask = exeWebServer;
        String finalITripId = iTripId;
        exeWebServer.setDataResponseListener(responseString -> {

            callToJobFinish();

            if (responseString != null && Utils.checkText(responseString)) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                if (isDataAvail) {

                    if (!finalITripId.isEmpty()) {
                        String message_str = generalFunc.getJsonValue(Utils.message_str, responseString);

                        dispatchMsg(message_str);
                    } else {
                        JSONArray message_arr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        if (message_arr != null) {
                            for (int i = 0; i < message_arr.length(); i++) {
                                JSONObject obj_tmp = generalFunc.getJsonObject(message_arr, i);

                                if (obj_tmp != null) {
                                    dispatchMsg(obj_tmp.toString());
                                } else {
                                    Object obj_tmp_chk = generalFunc.getJsonValue(message_arr, i);
                                    if (obj_tmp_chk != null && obj_tmp_chk instanceof String) {
                                        dispatchMsg((String) obj_tmp_chk);
                                    }
                                }
                            }
                        }

                    }

                } else {
                    String message_str = generalFunc.getJsonValue(Utils.message_str, responseString);

                    if (message_str.equalsIgnoreCase("SESSION_OUT")) {
//                        releaseInstances();

                        if (ConfigPubNub.retrieveInstance() != null) {
                            ConfigPubNub.getInstance().releasePubSubInstance();
                        }

                        if (ConfigSCConnection.retrieveInstance() != null) {
                            ConfigSCConnection.getInstance().forceDestroy();
                        }

                        if (MyApp.getInstance().getCurrentAct() != null && !MyApp.getInstance().getCurrentAct().isFinishing()) {
                           // MyApp.getInstance().notifySessionTimeOut();
                        }
                    }
                }
            }
        });
        exeWebServer.execute();
    }

    private void dispatchMsg(String jsonMsg) {
        (new FireTripStatusMsg(this.mContext)).fireTripMsg(jsonMsg);
    }

    @Override
    public void onLocationUpdate(Location location) {
        if (location == null) {
            return;
        }
        this.driverLoc = location;
    }

    @Override
    public void onTaskRun() {
        generalFunc.sendHeartBeat();

        try {
            setAppInactiveNotification();
        } catch (Exception e) {

        }

        updateDriverTripStatus();
    }

    public void executeTaskRun(JobFinishedListener jobFinishedListener) {
        this.jobFinishedListener = jobFinishedListener;

        if (generalFunc == null) {
            callToJobFinish();
            return;
        }

        String LAST_CALLED_MILLI = generalFunc.retrieveValue("CONFIG_DRIVER_TRIP_STATUS_CALLED");
        if (LAST_CALLED_MILLI.equalsIgnoreCase("") || ((System.currentTimeMillis() - GeneralFunctions.parseLongValue(0, LAST_CALLED_MILLI)) >= (FETCH_TRIP_STATUS_TIME_INTERVAL * 1000))) {
            onTaskRun();
        } else {
            callToJobFinish();
        }
    }

    private void callToJobFinish() {
        if (jobFinishedListener != null) {
            jobFinishedListener.onJobFinished();
        }
    }

    public interface JobFinishedListener {
        void onJobFinished();
    }
}
