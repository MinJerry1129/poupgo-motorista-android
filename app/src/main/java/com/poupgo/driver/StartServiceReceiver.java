package com.poupgo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.general.files.MyBackGroundService;
import com.utilities.general.files.StartActProcess;

/**
 * Created by Admin on 27-01-2016.
 */
public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MyBackGroundService.class));
        } else {
            new StartActProcess(context).startService(MyBackGroundService.class);
        }

    }
}