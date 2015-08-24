package com.navruz.parser.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateUtil {

    public NetworkState getCurrentNetworkState(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        if (haveConnectedWifi & haveConnectedMobile) {
            return NetworkState.Both;
        } else if (haveConnectedWifi) {
            return NetworkState.WIFI;
        } else if (haveConnectedMobile) {
            return NetworkState.Mobile;
        } else {
            return NetworkState.None;
        }
    }

    public boolean isBoth(Context context) {
        return getCurrentNetworkState(context).equals(NetworkState.Both);
    }

    public boolean isMobile(Context context) {
        return getCurrentNetworkState(context).equals(NetworkState.Mobile);
    }

    public boolean isWIFI(Context context) {
        return getCurrentNetworkState(context).equals(NetworkState.WIFI);
    }

    public boolean isNone(Context context) {
        return getCurrentNetworkState(context).equals(NetworkState.None);
    }

    public enum NetworkState {
        WIFI, Mobile, Both, None
    }

    public boolean isNetActive(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }
}
