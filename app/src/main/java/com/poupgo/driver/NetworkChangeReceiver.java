package com.poupgo.driver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.general.files.MyApp;
import com.general.files.OpenNoLocationView;
import com.general.functions.Utils;

/**
 * Created by Admin on 31-08-2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

//        boolean status = new InternetConnection(context).isNetworkConnected();

        Utils.printELog("NetworkStauts", "::stcheck::"+intent.getAction());
        checkNetworkSettings();

        /*try {
            Activity currentActivity = MyApp.getInstance().getCurrentAct();

            if (currentActivity instanceof MintActivity) {
                ((MainActivity) currentActivity).handleNoNetworkDial();

            } else if (currentActivity instanceof ActiveTripActivity) {
                ((ActiveTripActivity) currentActivity).handleNoNetworkDial();

            } else if (currentActivity instanceof DriverArrivedActivity) {
                ((DriverArrivedActivity) currentActivity).handleNoNetworkDial();
            }

        } catch (Exception e) {

        }*/
    }

    private void checkNetworkSettings() {
        Activity currentActivity = MyApp.getInstance().getCurrentAct();

        if (currentActivity != null) {

            if (MyApp.getInstance().driverArrivedAct == null && MyApp.getInstance().activeTripAct == null) {
                MainActivity mainAct = MyApp.getInstance().mainAct;

                if (mainAct != null) {
                    ViewGroup viewGroup = (ViewGroup) mainAct.findViewById(android.R.id.content);
                    handleNetworkView(mainAct, viewGroup);
                }
            } else {
                Activity finalActivity = currentActivity;
                if (MyApp.getInstance().activeTripAct != null) {
                    finalActivity = MyApp.getInstance().activeTripAct;
                } else if (MyApp.getInstance().driverArrivedAct != null) {
                    finalActivity = MyApp.getInstance().driverArrivedAct;
                }
                ViewGroup viewGroup = (ViewGroup) finalActivity.findViewById(android.R.id.content);
                handleNetworkView(finalActivity, viewGroup);
            }
        }
    }

    private void handleNetworkView(Activity activity, ViewGroup viewGroup) {
        Utils.printELog("AppBackground", "NETWORk");
        try {
            OpenNoLocationView.getInstance(activity, viewGroup).configView(true);
        } catch (Exception e) {

        }
    }
}