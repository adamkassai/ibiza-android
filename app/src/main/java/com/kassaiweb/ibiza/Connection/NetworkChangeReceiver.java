package com.kassaiweb.ibiza.Connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static List<NetworkChangeInterface> listeners = new ArrayList<>();

    public interface NetworkChangeInterface {
        void networkChanged(boolean isConnected);
    }

    public NetworkChangeReceiver() {
        // empty constructor
    }

    public static void addObserver(NetworkChangeInterface listener) {
        NetworkChangeReceiver.listeners.add(listener);
    }

    public static void removeObserver(NetworkChangeInterface listener) {
        NetworkChangeReceiver.listeners.remove(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            for(NetworkChangeInterface listener : listeners) {
                listener.networkChanged(true);
            }
        } else {
            for(NetworkChangeInterface listener : listeners) {
                listener.networkChanged(false);
            }
        }
    }
}
