package com.general.functions;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;


public class InternetConnection {
    private Context context;

    public InternetConnection(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] localObject1;
        Object localObject2;
        if (Build.VERSION.SDK_INT >= 21) {
            localObject1 = localConnectivityManager.getAllNetworks();

            for (Network localNetwork : localObject1) {
                localObject2 = localConnectivityManager.getNetworkInfo(localNetwork);
                if (((((NetworkInfo) localObject2).getTypeName().equalsIgnoreCase("WIFI")) || (((NetworkInfo) localObject2).getTypeName().equalsIgnoreCase("MOBILE"))) && (((NetworkInfo) localObject2).isConnected()) && (((NetworkInfo) localObject2).isAvailable())) {
                    return true;
                }
            }
        } else if (localConnectivityManager != null) {
            NetworkInfo[] allNetworkInfo = localConnectivityManager.getAllNetworkInfo();
            if (allNetworkInfo != null) {
                for (NetworkInfo localObject4 : allNetworkInfo) {
                    if (((localObject4.getTypeName().equalsIgnoreCase("WIFI")) || (localObject4.getTypeName().equalsIgnoreCase("MOBILE"))) && (localObject4.isConnected()) && (localObject4.isAvailable())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean check_int() {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
        if (localNetworkInfo == null)
            return false;
        if (!localNetworkInfo.isConnected())
            return false;
        if (!localNetworkInfo.isAvailable())
            return false;
        return true;
    }
}
