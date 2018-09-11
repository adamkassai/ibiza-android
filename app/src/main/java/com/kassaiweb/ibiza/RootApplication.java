package com.kassaiweb.ibiza;

import android.app.Application;
import android.content.Context;

public class RootApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        RootApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return RootApplication.context;
    }
}
