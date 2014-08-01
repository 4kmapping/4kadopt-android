package com.msk.adopt4k.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHelper {


    private Context mContext;

    public ConnectionHelper(Context context) {
        mContext = context;
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mobile.isConnected() || wifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
