package com.poupgo.driver;

import android.util.Log;

import com.general.files.FireTripStatusMsg;
import com.general.functions.GeneralFunctions;
import com.general.files.MyApp;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.utils.CommonUtilities;
import com.general.functions.Utils;

import java.io.IOException;

/**
 * Created by Admin on 29-07-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String TAG1 = "MyFirebaseIIDService";

    String authorizedEntity ; // Project id from Google Developer Console
    String scope = "GCM"; // e.g. communicating using GCM, but you can use any
    // URL-safe characters up to a maximum of 1000, or
    // you can also leave it blank.

    @Override
    public void onNewToken(String s) {
        // depricated
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (!Utils.checkText(authorizedEntity)) {
            authorizedEntity = new GeneralFunctions(this).retrieveValue(CommonUtilities.APP_GCM_SENDER_ID_KEY);
        }
        String refreshedToken = null;
        try {
            refreshedToken = FirebaseInstanceId.getInstance(FirebaseApp.initializeApp(this)).getToken(authorizedEntity,scope);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Displaying token on logcat
        Log.d(TAG1, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
        super.onNewToken(s);
    }


    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (!Utils.checkText(authorizedEntity)) {
            authorizedEntity = new GeneralFunctions(this).retrieveValue(CommonUtilities.APP_GCM_SENDER_ID_KEY);
        }

        //Displaying data in log
        //It is optional
        // Check if message contains a notification payload.
        if (remoteMessage != null && remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage == null || remoteMessage.getData() == null/* || remoteMessage.getNotification().getBody() == null*/)
            return;

        String message = remoteMessage.getData().get("message");

        new FireTripStatusMsg(MyApp.getInstance()!=null ? MyApp.getInstance().getCurrentAct() : getApplicationContext()).fireTripMsg(message);

    }
}