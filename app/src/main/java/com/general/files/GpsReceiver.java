package com.general.files;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.ViewGroup;

import com.poupgo.driver.MainActivity;
import com.general.functions.Utils;

/**
 * Created by Admin on 23-11-2016.
 */
public class GpsReceiver extends BroadcastReceiver {
    Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Utils.printELog("IntentAction", "::"+intent.getAction()+"::DATA::"+intent.getData());
        if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            checkGps(context);
        }
    }

    public void checkGps(Context context) {

//        GeneralFunctions generalFunc = new GeneralFunctions(context);
        /*boolean foregroud = false;
        try {
            foregroud = new ForegroundCheckTask().execute(context).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (foregroud == true && generalFunc.isLocationEnabled() == false && isApplicationBroughtToBackground() == false) {
            restartApp();
        }
        if (foregroud == true && generalFunc.isLocationEnabled() == true && isApplicationBroughtToBackground() == false) {
            restartApp();
        }*/


        checkGPSSettings();
    }

    private void checkGPSSettings() {
        Activity currentActivity = MyApp.getInstance().getCurrentAct();

        if (currentActivity != null) {

            if (MyApp.getInstance().driverArrivedAct == null && MyApp.getInstance().activeTripAct == null) {
                MainActivity mainAct = MyApp.getInstance().mainAct;

                if (mainAct != null) {
                    ViewGroup viewGroup = (ViewGroup) mainAct.findViewById(android.R.id.content);
                    handleGPSView(mainAct, viewGroup);
                }
            } else {
                Activity finalActivity = currentActivity;
                if(MyApp.getInstance().activeTripAct != null){
                    finalActivity = MyApp.getInstance().activeTripAct;
                }else if(MyApp.getInstance().driverArrivedAct != null){
                    finalActivity = MyApp.getInstance().driverArrivedAct;
                }
                ViewGroup viewGroup = (ViewGroup) finalActivity.findViewById(android.R.id.content);
                handleGPSView(finalActivity, viewGroup);
            }
        }
    }

    private void handleGPSView(Activity activity, ViewGroup viewGroup) {
        Utils.printELog("AppBackground","GPS");
        try {
            OpenNoLocationView.getInstance(activity, viewGroup).configView(false);
        } catch (Exception e) {

        }
    }

}
