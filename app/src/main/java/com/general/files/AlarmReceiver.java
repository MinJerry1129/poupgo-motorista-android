package com.general.files;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.general.functions.GeneralFunctions;

/**
 * Created by Jaison on 17/06/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub


        Log.d(TAG, "onReceive: ");

        //Trigger the notification
        GeneralFunctions generalFunctions = new GeneralFunctions(context);

        String message = generalFunctions.retrieveLangLBl("", "LBL_APP_INACTIVE_STATE_ALERT_NOTIFICATION");
        LocalNotification.dispatchLocalNotification(context, message, true);

    }
}


