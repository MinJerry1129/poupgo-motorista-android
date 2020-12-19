package com.general.files;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.poupgo.driver.ActiveTripActivity;
import com.poupgo.driver.DriverArrivedActivity;
import com.poupgo.driver.LauncherActivity;
import com.poupgo.driver.MainActivity;
import com.poupgo.driver.NetworkChangeReceiver;
import com.poupgo.driver.R;
import com.facebook.appevents.AppEventsLogger;
import com.general.functions.GeneralFunctions;
import com.general.functions.Utils;
import com.splunk.mint.Mint;
import com.utils.CommonUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Admin on 28-06-2016.
 */
public class MyApp extends Application {
    GeneralFunctions generalFun;
    private GpsReceiver mGpsReceiver;

    private static MyApp mMyApp;

    boolean isAppInBackground = true;

    private Activity currentAct = null;

    public MainActivity mainAct;
    public DriverArrivedActivity driverArrivedAct;
    public ActiveTripActivity activeTripAct;
    private NetworkChangeReceiver mNetWorkReceiver = null;

    public GeneralFunctions getGeneralFun(Context mContext) {
        return new GeneralFunctions(mContext);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GeneralFunctions.storeData("SERVERURL",CommonUtilities.SERVER, this);
        setScreenOrientation();
        Mint.initAndStartSession(this, CommonUtilities.MINT_APP_ID);
        mMyApp = (MyApp) this.getApplicationContext();
        try {
            AppEventsLogger.activateApp(this);
        } catch (Exception e) {
            Utils.printLog("FBError", "::" + e.toString());
        }
        generalFun = new GeneralFunctions(this);


        Mint.initAndStartSession(this, CommonUtilities.MINT_APP_ID);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (mGpsReceiver == null)
            registerReceiver();


        if (generalFun != null && generalFun.getMemberId() != null) {
            Mint.addExtraData("iMemberId", "" + generalFun.getMemberId());
        }

//        // Setup handler for uncaught exceptions.
//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                Utils.printLog("Api","in handleUncaughtException");
//                handleUncaughtException (thread, e);
//            }
//        });

    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        try {
            extractLogToFile();

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public static synchronized MyApp getInstance() {
        return mMyApp;
    }


    public void stopAlertService() {
        stopService(new Intent(getBaseContext(), ChatHeadService.class));
    }


    public boolean isMyAppInBackGround() {
        return this.isAppInBackground;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Utils.printLog("Api", "Object Destroyed >> MYAPP onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Utils.printLog("Api", "Object Destroyed >> MYAPP onTrimMemory");
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Utils.printLog("Api", "Object Destroyed >> MYAPP onTerminate");
        removePubSub();
    }

    public void removePubSub() {
        releaseGpsReceiver();
        removeAllRunningInstances();
        terminatePuSubInstance();
    }


    private void removeAllRunningInstances() {
        Utils.printELog("NetWorkDEMO", "removeAllRunningInstances called");
        connectReceiver(false);
    }

    private void releaseGpsReceiver() {
        if (mGpsReceiver != null)
            this.unregisterReceiver(mGpsReceiver);
        this.mGpsReceiver = null;

    }

    private void registerReceiver() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            this.mGpsReceiver = new GpsReceiver();
            this.registerReceiver(this.mGpsReceiver, mIntentFilter);
        }
    }

    private void registerNetWorkReceiver() {

        if (mNetWorkReceiver == null) {
            try {
                Utils.printELog("NetWorkDemo", "Network connectivity registered");
                IntentFilter mIntentFilter = new IntentFilter();
                mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
                /*Extra Filter Started */
                mIntentFilter.addAction(ConnectivityManager.EXTRA_IS_FAILOVER);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_REASON);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_EXTRA_INFO);
                /*Extra Filter Ended */
//                mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//                mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

                this.mNetWorkReceiver = new NetworkChangeReceiver();
                this.registerReceiver(this.mNetWorkReceiver, mIntentFilter);
            } catch (Exception e) {
                Utils.printELog("NetWorkDemo", "Network connectivity register error occurred");
            }
        }
    }

    private void unregisterNetWorkReceiver() {

        if (mNetWorkReceiver != null)
            try {
                Utils.printELog("NetWorkDemo", "Network connectivity unregistered");
                this.unregisterReceiver(mNetWorkReceiver);
                this.mNetWorkReceiver = null;
            } catch (Exception e) {
                Utils.printELog("NetWorkDemo", "Network connectivity register error occurred");
                e.printStackTrace();
            }

    }

    public void setScreenOrientation() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {
                if (Build.VERSION.SDK_INT == 26) {
                    Log.v("Facebook",activity.getClass().getSimpleName());
                    if (activity.getClass().getSimpleName().equals("FacebookActivity") || activity.getClass().getSimpleName().equals("SignInHubActivity")){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }else{
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }                activity.setTitle(getResources().getString(R.string.app_name));

                setCurrentAct(activity);
                Utils.runGC();

                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                if (activity instanceof MainActivity || activity instanceof DriverArrivedActivity || activity instanceof ActiveTripActivity) {
                    //Reset PubNub instance
                    configPuSubInstance();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Utils.runGC();
            }

            @Override
            public void onActivityResumed(Activity activity) {

                setCurrentAct(activity);

                isAppInBackground = false;
                Utils.runGC();
                Utils.printLog("AppBackground", "FromResume");
                Utils.sendBroadCast(getApplicationContext(), CommonUtilities.BACKGROUND_APP_RECEIVER_INTENT_ACTION);
                LocalNotification.clearAllNotifications();

                if (currentAct instanceof MainActivity || currentAct instanceof DriverArrivedActivity || currentAct instanceof ActiveTripActivity) {
                    ViewGroup viewGroup = (ViewGroup) currentAct.findViewById(android.R.id.content);
                    new Handler().postDelayed(() -> {
                        Utils.printELog("AppBackground", "MYapp");
                        OpenNoLocationView.getInstance(currentAct, viewGroup).configView(false);
                    }, 1000);
                }
                configureAppBadgeFloat();

            }

            @Override
            public void onActivityPaused(Activity activity) {

                isAppInBackground = true;
                Utils.runGC();
                Utils.printLog("AppBackground", "FromPause");
                Utils.sendBroadCast(getApplicationContext(), CommonUtilities.BACKGROUND_APP_RECEIVER_INTENT_ACTION);
                configureAppBadgeFloat();

            }

            private void configureAppBadgeFloat() {
                if (GetLocationUpdates.retrieveInstance() == null) {
                    return;
                }

                new Handler().postDelayed(() -> {
                    if (GetLocationUpdates.retrieveInstance() != null) {
                        if (isMyAppInBackGround()) {
                            GetLocationUpdates.retrieveInstance().showAppBadgeFloat();
                        } else {
                            GetLocationUpdates.retrieveInstance().hideAppBadgeFloat();
                        }
                    }
                }, 1000);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Utils.printLog("AppBackground", "onStop");
                Utils.runGC();


            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                /*Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated by this method will be passed to both).*/
                removeAllRunningInstances();
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Utils.hideKeyboard(activity);
                Utils.runGC();

//                connectReceiver(false);

                if (activity instanceof DriverArrivedActivity && activity
                        == driverArrivedAct) {
                    driverArrivedAct = null;
                }
                if (activity instanceof MainActivity && activity == mainAct) {
                    mainAct = null;
                }
                if (activity instanceof ActiveTripActivity && activity == activeTripAct) {
                    activeTripAct = null;
                }

                if ((activity instanceof DriverArrivedActivity && activity == driverArrivedAct) || (activity instanceof MainActivity && activity == mainAct) || (activity instanceof ActiveTripActivity) && activity == activeTripAct) {
                    terminatePuSubInstance();
                }

            }


        });
    }

    private void connectReceiver(boolean isConnect) {
        if (isConnect && mNetWorkReceiver == null) {
            registerNetWorkReceiver();
        } else if (!isConnect && mNetWorkReceiver != null) {
            unregisterNetWorkReceiver();
        }
    }

    public Activity getCurrentAct() {
        return currentAct;
    }

    private void setCurrentAct(Activity currentAct) {
        this.currentAct = currentAct;

        if (currentAct instanceof LauncherActivity) {
            mainAct = null;
            driverArrivedAct = null;
            activeTripAct = null;
        }

        if (currentAct instanceof MainActivity) {
            activeTripAct = null;
            driverArrivedAct = null;
            mainAct = (MainActivity) currentAct;
        }

        if (currentAct instanceof DriverArrivedActivity) {
            mainAct = null;
            activeTripAct = null;
            driverArrivedAct = (DriverArrivedActivity) currentAct;
        }

        if (currentAct instanceof ActiveTripActivity) {
            mainAct = null;
            driverArrivedAct = null;
            activeTripAct = (ActiveTripActivity) currentAct;
        }

        connectReceiver(true);
    }

    private String extractLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        String path = Environment.getExternalStorageDirectory() + "/" + "MyApp/";
        String fullName = path + "Log";
        Utils.printLog("Api", "fullName" + fullName);
        // Extract to file.
        File file = new File(fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        } catch (IOException e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                }

            // You might want to write a failure message to the log here.
            return null;
        }

        return fullName;
    }

    private void configPuSubInstance() {
        ConfigPubNub.getInstance(true).buildPubSub();
    }

    private void terminatePuSubInstance() {

        Utils.printLog("terminatePuSubInstance", "::call 1");
        if (ConfigPubNub.retrieveInstance() != null) {
            Utils.printLog("terminatePuSubInstance", "::call 2");
            ConfigPubNub.getInstance().releasePubSubInstance();
        }
    }
}
