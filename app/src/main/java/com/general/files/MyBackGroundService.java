package com.general.files;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.general.functions.GeneralFunctions;
import com.utilities.general.files.UpdateFrequentTask;
import com.utils.CommonUtilities;

/**
 * Created by Admin on 20-01-2016.
 */
public class MyBackGroundService extends Service implements UpdateFrequentTask.OnTaskRunCalled {

    GeneralFunctions generalFunc;
    BackgroundAppReceiver bgAppReceiver;

    public static String OPEN_APP_BTN = "com.Intent.Action.OPEN_APP";
    public static String GO_OFFLINE_BTN = "com.Intent.Action.GO_OFFLINE";
    public static String KEEP_ONLINE_BTN = "com.Intent.Action.KEEP_ONLINE";

    Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO do something useful
        super.onStartCommand(intent, flags, startId);

        generalFunc = new GeneralFunctions(getServiceContext());
        generalFunc.sendHeartBeat();

//        UpdateFrequentTask freqTask = new UpdateFrequentTask(2 * 60 * 1000);
//        freqTask.setTaskRunListener(this);
//        freqTask.startRepeatingTask();

        registerReceiver();
        configBackground();
        return Service.START_STICKY;

    }

    public void registerReceiver() {
        BackgroundAppReceiver bgAppReceiver = new BackgroundAppReceiver(getServiceContext());

        this.bgAppReceiver = bgAppReceiver;
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonUtilities.BACKGROUND_APP_RECEIVER_INTENT_ACTION);

        registerReceiver(bgAppReceiver, filter);
    }

    public void unRegisterReceiver() {
        if (bgAppReceiver != null) {
            try {
                unregisterReceiver(bgAppReceiver);
            } catch (Exception e) {

            }

            bgAppReceiver = null;
        }
    }

    public void configBackground() {
        if (handler == null) {
            handler = new Handler();
        } else {
            handler.removeCallbacks(myRunnable);
            handler.removeCallbacksAndMessages(null);

            handler = new Handler();
        }
        handler.postDelayed(myRunnable, 1000);

    }

    Runnable myRunnable = new Runnable() {
        public void run() {
            if (getApp().isMyAppInBackGround() == true && !generalFunc.getMemberId().equals("")
                    && generalFunc.retrieveValue(CommonUtilities.DRIVER_ONLINE_KEY).equals("true")) {

            } else {

            }
        }
    };

    public MyApp getApp() {
        return ((MyApp) getApplication());
    }


    public Context getServiceContext() {
        return MyBackGroundService.this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        releaseAllTask();

        super.onDestroy();

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1,
                restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1,
                restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        releaseAllTask();

        return super.onUnbind(intent);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        releaseAllTask();

        this.stopSelf();

        super.onTaskRemoved(rootIntent);
    }


    public void releaseAllTask() {
        unRegisterReceiver();
    }

    @Override
    public void onTaskRun() {
        generalFunc.sendHeartBeat();

        configBackground();
    }

}

