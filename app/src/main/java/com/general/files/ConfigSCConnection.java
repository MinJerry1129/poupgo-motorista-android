package com.general.files;

import android.content.Context;
import android.location.Location;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.general.functions.GeneralFunctions;
import com.utilities.general.files.UpdateFrequentTask;
import com.utils.CommonUtilities;
import com.general.functions.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.sac.BasicListener;
import io.github.sac.Socket;

/**
 * Created by Admin on 20/03/18.
 */

public class ConfigSCConnection implements BasicListener, GetLocationUpdates.LocationUpdatesListener, UpdateFrequentTask.OnTaskRunCalled {

    /**
     * Variable declaration of singleton instance.
     */
    private static ConfigSCConnection configScInstance = null;

    /**
     * client is object of socket cluster. With the help of this object, we can perform any required operations of socket cluster. Socket cluster is running on particular port and mentioned port must be open for all connection (Public port).
     */
    Socket client = new Socket(getGeneralFunc().retrieveValue(Utils.SC_CONNECT_URL_KEY));

    /**
     * This variable check weather a subscribing to one channel is in process or not.
     */
    boolean isChannelSubscribing = false;

    /**
     * This list maintains list of subscribed channels list.
     */
    ArrayList<String> listOfSubscribedList = new ArrayList<>();

    /**
     * This list maintains list of channels list, which are not subscribed.
     */
    ArrayList<String> listOfNotSubscribedList = new ArrayList<>();

    /**
     * This will handle reconnection to socket server if connection failed. Reconnection also takes place in socket cluster library but we added this on top of sockect cluster
     */
    UpdateFrequentTask updateReconnectionTask;

    /**
     * This will contains events which are not published and waiting for last poublish event to be finished.
     */
    ArrayList<String[]> pendingPublish = new ArrayList<>();

    /**
     * This will indicates weather a client is killed or not.
     */
    boolean isClientKilled = false;

    /**
     * Used to get location updates.
     */

    /**
     * A frequent task for driver's status (location + trip status)
     */
    private UpdateFrequentTask updateDriverStatusTask;

    /**
     * Current location of driver
     */
    private Location driverLoc = null;

    /**
     * Used to remove redundant messages (trip message or others). Internal purpose only.
     */
    private HashMap<String, String> listOfCurrentTripMsg_Map = new HashMap<>();

    /**
     * Current trip id if driver is on trip/job.
     */
    private String iTripId = "";

    /**
     * Used to check driver is subscribed to cab request or not.
     */
    private boolean isSubsToCabReq = false;

    /**
     * WebServer task of updateDriver status recurring task.
     */
    private ExecuteWebServerUrl currentExeTask;

    /**
     * This will block code snippet to be run on same time.
     */
    private boolean isDispatchThreadLocked = false;

    int FETCH_TRIP_STATUS_TIME_INTERVAL = 15;

    /**
     * Creating Singleton instance. By using this method will create only one instance of this class.
     */
    public static ConfigSCConnection getInstance() {
        if (configScInstance == null) {
            configScInstance = new ConfigSCConnection();
        }
        return configScInstance;
    }

    /**
     * Fetch Singleton instance. By using this method will return instance of this class.
     */
    public static ConfigSCConnection retrieveInstance() {
        return configScInstance;
    }

    /**
     * This function will create a socket connection with the help of socket cluster with mentioned server. Don't call this function every time. This must be called once on each session of app.
     */
    public void buildConnection() {
        if (client.isconnected()) {
            continueChannelSubscribe();
            return;
        }

        /**
         * This is a basic connection listener. This listener will be called when connection related event occurred.
         */
        client.setListener(this);

        //This will set automatic-reconnection to server with delay of 2 seconds and repeating it for 30 times
//        client.setReconnection(new ReconnectStrategy().setDelay(8000).setMaxAttempts(50));

        // This will disable logging
        client.disableLogging();

        //This will send web socket handshake request to socket cluster-server
        client.connectAsync();

        //Start reconnection task for the first time due to if no connection callback received some time.
        startReConnectionTask();

        // Start driver status update task. This will handle post Location updates to server and also checks for trip status messages.
        startDriverStatusUpdateTask();

    }

    /**
     * Called when socket connection is established
     *
     * @param socket  Socket object on which connection is established
     * @param headers List of headers if any
     */
    @Override
    public void onConnected(Socket socket, Map<String, List<String>> headers) {
//        stopReConnectScheduleTask();
        Utils.printLog("SocketApp", "Connected");

        continueChannelSubscribe();
    }

    /**
     * Called when socket connection is disconnected. Reconnection will be executed on several interval
     *
     * @param socket           Socket object on which dis connection is occurred
     * @param serverCloseFrame
     * @param clientCloseFrame
     * @param closedByServer
     */
    @Override
    public void onDisconnected(Socket socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
        if (!isClientKilled) {
//            startReConnectionTask();
        }
        Utils.printLog("SocketApp", "DisConnected");
    }

    /**
     * Called when error occurred in socket connection
     *
     * @param socket    Socket object on which error is occurred.
     * @param exception Error details can be retrieved using this
     */
    @Override
    public void onConnectError(Socket socket, WebSocketException exception) {
        if (!isClientKilled) {
//            startReConnectionTask();
        }
        Utils.printLog("SocketApp", "DisConnected:ConnectError:" + exception.getLocalizedMessage());
    }

    /**
     * By Using this listener we can check user needs to be authenticated OR not.
     *
     * @param socket Socket object on which authentication is set.
     * @param status Authentication status - By using this we can check authentication is successful or not.
     */
    @Override
    public void onAuthentication(Socket socket, Boolean status) {

    }

    /**
     * Called when authentication is successful and authentication token is set by server. Server will set authentication code when user login is verified.
     *
     * @param token  Authentication token (Needs to be store if want to reuse)
     * @param socket Socket object on which authentication is set.
     */
    @Override
    public void onSetAuthToken(String token, Socket socket) {

    }

    /**
     * This will stop reconnection task.
     */
    private void stopReConnectScheduleTask() {

        pendingPublish.clear();

        if (MyApp.getInstance().getCurrentAct() != null) {
            MyApp.getInstance().getCurrentAct().runOnUiThread(() -> {

                if (updateReconnectionTask != null) {
                    updateReconnectionTask.stopRepeatingTask();
                    updateReconnectionTask = null;
                }
            });
        } else {
            try {
                if (updateReconnectionTask != null) {
                    updateReconnectionTask.stopRepeatingTask();
                    updateReconnectionTask = null;
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * Function used to reconnect socket connection if conncetion failure/disconnect.
     */
    private void startReConnectionTask() {

        pendingPublish.clear();

        if (MyApp.getInstance().getCurrentAct() != null && updateReconnectionTask == null) {
            MyApp.getInstance().getCurrentAct().runOnUiThread(() -> {
                stopReConnectScheduleTask();

                updateReconnectionTask = new UpdateFrequentTask(19000);
                updateReconnectionTask.avoidFirstRun();
                updateReconnectionTask.setTaskRunListener(() -> {
                    if (!isClientKilled) {

                        if (!client.isconnected()) {
                            client.connectAsync();
                        } else {
                            continueChannelSubscribe();
                        }
                    } else {
                        stopReConnectScheduleTask();
                    }
                });
                updateReconnectionTask.startRepeatingTask();
            });
        }
    }


    /**
     * Used to start driver's status task. This will initialize frequent task to update device's current location to server and trip status messages.
     */
    private void startDriverStatusUpdateTask() {

        stopLocationUpdateTask();

        Context mContext = MyApp.getInstance().getApplicationContext();

        if (mContext != null) {
            GetLocationUpdates.getInstance().startLocationUpdates(this, this);

            stopDriverStatusUpdateTask();

            updateDriverStatusTask = new UpdateFrequentTask(GeneralFunctions.parseIntegerValue(15, GeneralFunctions.retrieveValue(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY, mContext)) * 1000);
            FETCH_TRIP_STATUS_TIME_INTERVAL = GeneralFunctions.parseIntegerValue(15, GeneralFunctions.retrieveValue(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY, mContext));

            updateDriverStatusTask.setTaskRunListener(this);
            updateDriverStatusTask.startRepeatingTask();

        }

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
    }

    /**
     * Used to set current trip id. This will used to fetch trip status messages from server.
     */
    protected void setTripId(String iTripId) {
        this.iTripId = iTripId;
    }

    /**
     * This function is used to subscribe channels, which are not due to not connected to server.
     */
    private void continueChannelSubscribe() {
        isChannelSubscribing = false;

        try {
            System.gc();
        } catch (Exception e) {

        }

        if (getGeneralFunc().getMemberId().trim().equals("")) {
            forceDestroy();
            return;
        }

        //Subscribe to private channel
        ConfigSCConnection.getInstance().subscribeToChannels("DRIVER_" + getGeneralFunc().getMemberId());

        // Resubscribe to all previously subscribed channels.
        for (int i = 1; i < listOfSubscribedList.size(); i++) {
            if (!listOfSubscribedList.get(i).equals("DRIVER_" + getGeneralFunc().getMemberId())) {
                ConfigSCConnection.getInstance().subscribeToChannels(listOfSubscribedList.get(i));
            }
        }

        if (pendingPublish.size() > 0) {
            for (String[] itemArr : pendingPublish) {
                publishMsg(itemArr[0], itemArr[1]);
            }

            pendingPublish.clear();
        }
    }

    /**
     * Function used to get general function object
     *
     * @return GeneralFunction object is returned.
     */
    private GeneralFunctions getGeneralFunc() {
        GeneralFunctions generalFunc = new GeneralFunctions(MyApp.getInstance().getApplicationContext());

        return generalFunc;
    }

    /**
     * This function is used to subscribe to channels. (Publish-Subscribe module)
     *
     * @param channelName Subscribe channel name.
     */
    public void subscribeToChannels(String channelName) {
        // Add any new Channels to subscribed channels list. We will use this list to un subscribe channels.
        if (!listOfSubscribedList.contains(channelName)) {
            listOfSubscribedList.add(channelName);
        }

        if (this.client.isconnected() == false) {
            return;
        }

        if (this.isChannelSubscribing == true) {
            listOfNotSubscribedList.add(channelName);
            return;
        }

        isChannelSubscribing = true;


        Socket.Channel channel = null;

        if (client.getChannelByName(channelName) != null) {
            channel = client.getChannelByName(channelName);
        } else {
            channel = client.createChannel(channelName);
        }

        Socket.Channel finalChannel = channel;
        channel.subscribe((name, error, data) -> {
            isChannelSubscribing = false;

            if (error == null) {

                addToChannelsObjList(channelName);

                if (channelName.contains("CAB_REQUEST_DRIVER_")) {
                    isSubsToCabReq = true;
                }

                //For listening to channel event
                finalChannel.onMessage((name1, data1) -> {
                   dispatchMsg((String) data1);
                });

                if (listOfNotSubscribedList.size() > 0) {
                    String channelName1 = listOfNotSubscribedList.get(0);
                    listOfNotSubscribedList.remove(0);
                    subscribeToChannels(channelName1);
                }

//                    Utils.printLog("SocketApp", "ChannelSub.Success::" + channelName);
            } else {
                // If error appears then try to resubscribe to channel. A recursive call will be called after 2 seconds delay.
                subscribeToChannels(channelName);

//                    Utils.printELog("SocketApp", ":Error:Subscribe:" + error.toString());
            }
        });
    }

    /**
     * This function used to keep track of any successful subscribed channels
     *
     * @param channel Channel name which is successfully subscribed.
     */
    private void addToChannelsObjList(String channel) {

        if (!listOfSubscribedList.contains(channel)) {
            listOfSubscribedList.add(channel);
        }

    }


    /**
     * Function used to unSubscribe from particular channel.
     *
     * @param channelName from which users will be un subscribed
     */
    public void unSubscribeFromChannels(String channelName) {
        if (client.getChannelByName(channelName) != null) {
            unSubscribeFromChannels(client.getChannelByName(channelName));
        }
    }

    /**
     * Function used to unSubscribe user from particular channel.
     *
     * @param channel unSubscribe From channel
     */
    private void unSubscribeFromChannels(Socket.Channel channel) {
        channel.unsubscribe((name, error, data) -> {
            if (error != null && error instanceof JSONObject) {
                String errorName = getGeneralFunc().getJsonValueStr("name", (JSONObject) error);
                if (!errorName.equals("BrokerError")) {
                    unSubscribeFromChannels(channel);
                }
            } else {
//                    Utils.printLog("SocketApp", "ChannelUnSub.Success::" + channel.getChannelName());
                if (channel.getChannelName().contains("CAB_REQUEST_DRIVER_")) {
                    isSubsToCabReq = false;
                }
            }
        });
    }

    /**
     * Function used to publish message on particular channel.
     *
     * @param channelName Name of channel on which message will be published.
     * @param message     Message needs to be published.
     */
    public void publishMsg(String channelName, String message) {

        if (client.isconnected() == false) {
            pendingPublish.add(new String[]{channelName, message});
            return;
        }

        Socket.Channel tmp_channel = client.getChannelByName(channelName);

        if (tmp_channel == null) {
            tmp_channel = client.createChannel(channelName);
        }

        tmp_channel.publish(message, (channelName1, error, data) -> {

            if (error == null) {
                System.out.println("Published message to channel " + channelName + " successfully:message:" + message);
            }
            if (pendingPublish.size() > 0) {
                String[] tmpMsgData = pendingPublish.get(0);
                pendingPublish.remove(0);
                publishMsg(tmpMsgData[0], tmpMsgData[1]);
            }
        });
    }

    /**
     * Function used to un subscribe all channels. Generally this will be done when app is going to be terminate.
     */
    public void releaseAllChannels() {

        isClientKilled = true;

        for (int i = 1; i < listOfSubscribedList.size(); i++) {
            ConfigSCConnection.getInstance().unSubscribeFromChannels(listOfSubscribedList.get(i));
        }

        listOfSubscribedList.clear();
    }

    /**
     * Function used to destroy current instance. Generally this will be done when app is going to be terminate OR instance of this class is no longer required.
     */
    public void forceDestroy() {
//        Utils.printLog("SocketApp", "::ForceDestroy::");
        releaseAllChannels();
        ConfigSCConnection.getInstance().client.disconnect();

        stopLocationUpdateTask();
        stopDriverStatusUpdateTask();

        configScInstance = null;

    }

    /**
     * Function used to dispatch message to user when received some event on particular channel.
     */
    private void dispatchMsg(String message) {
        Utils.printLog("SCdispatchMsg", "::" + message);
        if (!isDispatchThreadLocked) {
            isDispatchThreadLocked = true;

            Utils.printLog("MSG", "1::" + listOfCurrentTripMsg_Map);
            Utils.printLog("MSG", "2::" + message);
            if (listOfCurrentTripMsg_Map.get(message) == null) {
//                Utils.printLog("SocketApp", "1:CalledFrom:ConfigSCConnection");
                listOfCurrentTripMsg_Map.put(message, "Yes");
                (new FireTripStatusMsg()).fireTripMsg(message);
            }

            isDispatchThreadLocked = false;
        }
    }


    /**
     * This will dispatch location updates
     *
     * @param location Current device's location
     */
    @Override
    public void onLocationUpdate(Location location) {
        this.driverLoc = location;
    }

    /**
     * Called when frequent task run.
     */
    @Override
    public void onTaskRun() {
        configDriverStatus();

    }

    /**
     * This will dispatch updates regarding trip status OR used to update current driver location to server.
     */
    public void configDriverStatus() {
        Context mContext = MyApp.getInstance().getCurrentAct();
        if (mContext == null) {
            return;
        }
        GeneralFunctions generalFunc = getGeneralFunc();
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
        exeWebServer.setDataResponseListener(responseString -> {

            Utils.printLog("responseString", "::" + responseString);
            if (responseString != null && Utils.checkText(responseString)) {
                boolean isDataAvail = generalFunc.checkDataAvail(CommonUtilities.action_str, responseString);

                if (isDataAvail) {

                    if (!iTripId.isEmpty()) {
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
                        forceDestroy();

                        if (MyApp.getInstance().getCurrentAct() != null && !MyApp.getInstance().getCurrentAct().isFinishing()) {
                            GeneralFunctions generalFuncAct = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
                            generalFuncAct.notifySessionTimeOut();
                        }
                    }
                }
            }
        });

        exeWebServer.execute();
    }

}
