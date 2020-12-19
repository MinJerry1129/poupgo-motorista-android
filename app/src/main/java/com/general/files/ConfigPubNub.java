package com.general.files;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import com.poupgo.driver.MainActivity;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.general.functions.GeneralFunctions;
import com.general.functions.InternetConnection;
import com.utilities.general.files.UpdateFrequentTask;
import com.utils.CommonUtilities;
import com.general.functions.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Admin on 05-10-2016.
 */
public class ConfigPubNub extends SubscribeCallback implements GetLocationUpdates.LocationUpdatesListener, UpdateFrequentTask.OnTaskRunCalled {

    private static ConfigPubNub instance = null;

    public boolean isSubsToCabReq = false;
    public Location driverLoc = null;
    Context mContext;
    PubNub pubnub;
    GeneralFunctions generalFunc;

    ArrayList<String[]> listOfPublishMsg = new ArrayList<>();
    boolean isCurrentMsgPublished = true;

    private InternetConnection intCheck;
    private GetLocationUpdates getLocUpdates;
    private UpdateFrequentTask updatedriverStatustask;

    private ExecuteWebServerUrl currentExeTask;
    public boolean isSessionout = false;
    int FETCH_TRIP_STATUS_TIME_INTERVAL = 15;

    public static ConfigPubNub getInstance() {
        if (instance == null) {
            instance = new ConfigPubNub(MyApp.getInstance().getCurrentAct());
        }
        return instance;
    }

    public static ConfigPubNub getInstance(boolean isForceReset) {

        if (instance != null) {
            instance.releaseInstances();
        }

        instance = new ConfigPubNub(MyApp.getInstance().getCurrentAct());

        return instance;
    }

    public static ConfigPubNub retrieveInstance() {
        return instance;
    }

    public ConfigPubNub(Context mContext) {
        this.mContext = mContext;
    }

    public void buildPubSub() {
        releaseInstances();

        if (mContext == null) {
            return;
        }

        generalFunc = new GeneralFunctions(mContext);
        FETCH_TRIP_STATUS_TIME_INTERVAL = GeneralFunctions.parseIntegerValue(15, generalFunc.retrieveValue(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY));
        intCheck = new InternetConnection(mContext);

        String ENABLE_SOCKET_CLUSTER = generalFunc.retrieveValue(Utils.ENABLE_SOCKET_CLUSTER_KEY);
        String PUBNUB_DISABLED = generalFunc.retrieveValue(Utils.PUBNUB_DISABLED_KEY);

        if (PUBNUB_DISABLED.equalsIgnoreCase("Yes") && ENABLE_SOCKET_CLUSTER.equalsIgnoreCase("Yes")) {
            ConfigSCConnection.getInstance().buildConnection();
            return;
        }

        if (!PUBNUB_DISABLED.equalsIgnoreCase("Yes")) {
            PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setUuid((generalFunc.retrieveValue(Utils.DEVICE_SESSION_ID_KEY).equals("") ? generalFunc.getMemberId() : generalFunc.retrieveValue(Utils.DEVICE_SESSION_ID_KEY)));

            pnConfiguration.setSubscribeKey(generalFunc.retrieveValue(CommonUtilities.PUBNUB_SUB_KEY));
            pnConfiguration.setPublishKey(generalFunc.retrieveValue(CommonUtilities.PUBNUB_PUB_KEY));
            pnConfiguration.setSecretKey(generalFunc.retrieveValue(CommonUtilities.PUBNUB_SEC_KEY));
            pnConfiguration.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
//        pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);

            pubnub = new PubNub(pnConfiguration);

            addListener();

            subscribeToPrivateChannel();
            reConnectPubNub(10000);
            reConnectPubNub(20000);
            reConnectPubNub(30000);
        }

        getPassenegerMsgPubNubOff();
    }


    public void reConnectPubNub(int duration) {
        new Handler().postDelayed(() -> connectToPubNub(), duration);
    }

    public void getPassenegerMsgPubNubOff() {
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
        GetLocationUpdates.getInstance().startLocationUpdates(this, this);

        if (updatedriverStatustask == null) {

            updatedriverStatustask = new UpdateFrequentTask(generalFunc.parseIntegerValue(15, generalFunc.retrieveValue(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY)) * 1000);
            updatedriverStatustask.setTaskRunListener(this);
            updatedriverStatustask.startRepeatingTask();
        }

    }

    public void connectToPubNub(int interval) {
        new Handler().postDelayed(() -> {
            if (pubnub != null) {
                pubnub.reconnect();
            }
        }, interval);
    }

    public void connectToPubNub() {
        new Handler().postDelayed(() -> {
            if (pubnub != null) {
                pubnub.reconnect();
            }
        }, 10000);
    }

    public void connectToPubNub(final PubNub pubNub) {

        new Handler().postDelayed(() -> {
            if (pubNub != null) {
                pubNub.reconnect();
            }
        }, 10000);
    }

    public void subscribeToPrivateChannel() {
        if (pubnub != null) {
            pubnub.subscribe()
                    .channels(Arrays.asList("DRIVER_" + generalFunc.getMemberId())) // subscribe to channels
                    .execute();
        }
        if (ConfigSCConnection.retrieveInstance() != null) {
            ConfigSCConnection.getInstance().subscribeToChannels("DRIVER_" + generalFunc.getMemberId());
        }
    }

    public void unSubscribeToPrivateChannel() {
        if (pubnub != null) {
            pubnub.unsubscribe()
                    .channels(Arrays.asList("DRIVER_" + generalFunc.getMemberId())) // subscribe to channels
                    .execute();
        }
        if (ConfigSCConnection.retrieveInstance() != null) {
            ConfigSCConnection.getInstance().unSubscribeFromChannels("DRIVER_" + generalFunc.getMemberId());
        }
    }

    public void releasePubSubInstance() {
        releaseInstances();
    }

    private void releaseInstances() {

        try {
            if (pubnub != null) {
                pubnub.removeListener(this);
                pubnub.forceDestroy();
            }

            if (ConfigSCConnection.retrieveInstance() != null) {
                ConfigSCConnection.getInstance().forceDestroy();
            }

            if (updatedriverStatustask != null) {
                updatedriverStatustask.stopRepeatingTask();
                updatedriverStatustask = null;
            }


            if (GetLocationUpdates.retrieveInstance() != null) {
                GetLocationUpdates.getInstance().stopLocationUpdates(this);
            }


            Utils.runGC();
        } catch (Exception e) {

        }
    }


    public void addListener() {

        if (pubnub != null) {
            pubnub.removeListener(this);
            pubnub.addListener(this);

            pubnub.reconnect();
        }

        connectToPubNub();

    }

    private void dispatchMsg(String jsonMsg) {
        (new FireTripStatusMsg()).fireTripMsg(jsonMsg);
    }

    public void subscribeToChannels(ArrayList<String> channels) {
        Utils.printELog("SubscribdChannels", ":::" + channels.toString());
        if (pubnub != null) {
            pubnub.subscribe()
                    .channels(channels) // subscribe to channels
                    .execute();
        }
        if (ConfigSCConnection.retrieveInstance() != null) {
            for (int i = 0; i < channels.size(); i++) {
                ConfigSCConnection.getInstance().subscribeToChannels(channels.get(i));
            }
        }
    }

    public void unSubscribeToChannels(ArrayList<String> channels) {
        Utils.printELog("UnSubscribdChannels", ":::" + channels.toString());
        if (pubnub != null) {
            pubnub.unsubscribe()
                    .channels(channels)
                    .execute();
        }
        if (ConfigSCConnection.retrieveInstance() != null) {
            for (int i = 0; i < channels.size(); i++) {
                ConfigSCConnection.getInstance().unSubscribeFromChannels(channels.get(i));
            }
        }
    }

    public void subscribeToCabRequestChannel() {
        isSubsToCabReq = true;

        if (pubnub != null) {
            pubnub.subscribe()
                    .channels(Arrays.asList("CAB_REQUEST_DRIVER_" + generalFunc.getMemberId())) // subscribe to channels
                    .execute();
        }
        if (ConfigSCConnection.retrieveInstance() != null) {
            ConfigSCConnection.getInstance().unSubscribeFromChannels("CAB_REQUEST_DRIVER_" + generalFunc.getMemberId());
        }
    }


    public void unSubscribeToCabRequestChannel() {
        isSubsToCabReq = false;

        if (pubnub != null) {
            pubnub.unsubscribe()
                    .channels(Arrays.asList("CAB_REQUEST_DRIVER_" + generalFunc.getMemberId())) // subscribe to channels
                    .execute();
        }
        if (ConfigSCConnection.retrieveInstance() != null) {
            ConfigSCConnection.getInstance().subscribeToChannels("CAB_REQUEST_DRIVER_" + generalFunc.getMemberId());
        }
    }


    public void publishMsg(String channel, String message) {
        if (message == null) {
            return;
        }

        if (pubnub != null) {
            if (!isCurrentMsgPublished) {
                String[] arr = {channel, message};
                listOfPublishMsg.add(arr);
                return;
            }

            continuePublish(channel, message);
        }

        if (ConfigSCConnection.retrieveInstance() != null) {
            ConfigSCConnection.getInstance().publishMsg(channel, message);
        }

    }

    private void continuePublish(String channel, String message) {
        isCurrentMsgPublished = false;

        if (pubnub != null) {
            pubnub.publish()
                    .message(message)
                    .channel(channel)
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            isCurrentMsgPublished = true;

                            if (listOfPublishMsg.size() > 0) {
                                String[] arr = listOfPublishMsg.get(0);
                                listOfPublishMsg.remove(0);
                                continuePublish(arr[0], arr[1]);
                            }
                        }
                    });
        }

    }


    private void getUpdatedDriverStatus() {

        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
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

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "configDriverTripStatus");
        parameters.put("iTripId", iTripId);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);

        if (driverLoc != null) {
            parameters.put("vLatitude", "" + driverLoc.getLatitude());
            parameters.put("vLongitude", "" + driverLoc.getLongitude());
        }

        parameters.put("isSubsToCabReq", "" + isSubsToCabReq);

        if (this.currentExeTask != null) {
            this.currentExeTask.cancel(true);
            this.currentExeTask = null;
            Utils.runGC();
        }


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        this.currentExeTask = exeWebServer;
        String finalITripId = iTripId;
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && Utils.checkText(responseString)) {

                boolean isDataAvail = generalFunc.checkDataAvail(CommonUtilities.action_str, responseString);
                if (isDataAvail) {

                    if (!finalITripId.isEmpty()) {
                        String message_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                        dispatchMsg(message_str);
                    } else {
                        JSONArray message_arr = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);

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
                    String message_str = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                    if (message_str.equalsIgnoreCase("SESSION_OUT")) {
                        releaseInstances();

                        if (MyApp.getInstance().getCurrentAct() != null && !MyApp.getInstance().getCurrentAct().isFinishing()) {
                            GeneralFunctions generalFuncAct = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
                            generalFuncAct.notifySessionTimeOut();
                        }
                    }
                }

                /*boolean isDataAvail = generalFunc.checkDataAvail(CommonUtilities.action_str, responseString);
                if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString) == null) {
                    return;
                }

                String message = Utils.checkText(responseString) ? generalFunc.getJsonValue(CommonUtilities.message_str, responseString) : null;

                if (mContext != null && mContext instanceof Activity) {
                    Activity act = (Activity) mContext;
                    if (!act.isFinishing()) {
                        if (message != null && message.equals("SESSION_OUT")) {

                            isSessionout = true;

                            if (currentExeTask != null) {
                                currentExeTask.cancel(true);
                                currentExeTask = null;
                            }

                            releaseInstances();

                            generalFunc.notifySessionTimeOut();
                            return;
                        }

                    }
                }


                if (isDataAvail == true) {

                    if (GeneralFunctions.getTypeOfJson(generalFunc.getJsonValue(CommonUtilities.message_str, responseString)) != null) {

                        if (GeneralFunctions.getTypeOfJson(generalFunc.getJsonValue(CommonUtilities.message_str, responseString)) instanceof JSONObject) {
                            dispatchMsg(generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        } else if (GeneralFunctions.getTypeOfJson(generalFunc.getJsonValue(CommonUtilities.message_str, responseString)) instanceof JSONArray) {
                            JSONArray msgArr = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);

                            if (msgArr != null) {
                                for (int i = 0; i < msgArr.length(); i++) {

                                    String tempStr = ((String) generalFunc.getValueFromJsonArr(msgArr, i)).replaceAll("^\"|\"$", "");

                                    dispatchMsg(tempStr);
                                }
                            }
                        }
                    }

                }*/
            }
        });
        exeWebServer.execute();
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
        Utils.runGC();

        if (!isSessionout) {
            generalFunc.sendHeartBeat();

            getUpdatedDriverStatus();

            setAppInactiveNotification();


        }
    }
    private void setAppInactiveNotification() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minutesOfDay = calendar.get(Calendar.MINUTE);
        int seondsOfDay = calendar.get(Calendar.SECOND);
        if (MyApp.getInstance() != null && MyApp.getInstance().getCurrentAct() != null) {
            NotificationScheduler.setReminder(MyApp.getInstance().getCurrentAct(), AlarmReceiver.class, hourOfDay, minutesOfDay, seondsOfDay + FETCH_TRIP_STATUS_TIME_INTERVAL + 5);
        }
    }
    @Override
    public void status(PubNub pubnub, PNStatus status) {
        if (pubnub == null || status == null || status.getCategory() == null) {
            connectToPubNub();
            return;
        }

        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).pubNubStatus(status.getCategory());
        }
        switch (status.getCategory()) {
            case PNMalformedResponseCategory:
            case PNUnexpectedDisconnectCategory:
            case PNTimeoutCategory:
            case PNNetworkIssuesCategory:
            case PNDisconnectedCategory:
                connectToPubNub(pubnub);
                break;
            case PNConnectedCategory:
                // Connect event. You can do stuff like publish, and know you'll get it.
                // Or just use the connected event to confirm you are subscribed for
                // UI / internal notifications, etc
                break;

            default:
                break;

        }
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        Utils.printELog("PubNubMsg", ":MSG:" + message.getMessage().toString());
        dispatchMsg(message.getMessage().toString());
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

    }
}