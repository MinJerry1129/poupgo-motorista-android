package com.general.files;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.poupgo.driver.BuildConfig;
import com.poupgo.driver.R;
import com.utils.CommonUtilities;
import com.general.functions.Utils;

/**
 * Created by Admin on 20/03/18.
 */

public class LocalNotification {
    static Context mContext;

    private static String CHANNEL_ID = BuildConfig.APPLICATION_ID;
    private static NotificationManager mNotificationManager = null;

    public static void dispatchLocalNotification(Context context, String message, boolean onlyInBackground) {
        mContext = context;

        if (MyApp.getInstance().getCurrentAct() == null && mContext == null) {
            return;
        }

        continueDispatchNotification(message, onlyInBackground);

    }

    private static void continueDispatchNotification(String message, boolean onlyInBackground) {
        Intent intent = null;
        if (Utils.getPreviousIntent(mContext) != null) {
            intent = Utils.getPreviousIntent(mContext);
        } else {
            intent = mContext
                    .getPackageManager()
                    .getLaunchIntentForPackage(mContext.getPackageName());

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_driver_logo)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
        }

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Receive Notifications in >26 version devices

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CommonUtilities.package_name);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    mContext.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }


        if (onlyInBackground == true && MyApp.getInstance().isMyAppInBackGround()) {
//            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(Utils.NOTIFICATION_ID, mBuilder.build());
        } else if (onlyInBackground == false) {
//            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(Utils.NOTIFICATION_ID, mBuilder.build());
        }
    }

    public static void clearAllNotifications() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
        }
    }
}
