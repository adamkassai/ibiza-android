package com.kassaiweb.ibiza.Util;

import android.content.SharedPreferences;

import com.kassaiweb.ibiza.Constant;
import com.kassaiweb.ibiza.RootApplication;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * This is a Util class for accessing the {@link android.content.SharedPreferences}
 * key-value pairs.<br/>
 */
public class SPUtil {
    private static SharedPreferences sharedPreferences;

    private static void init() {
        if (sharedPreferences == null) {
            sharedPreferences = RootApplication.getAppContext()
                    .getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE);
        }
    }

    public synchronized static int getInt(String key, int defaultValue) {
        init();
        return sharedPreferences.getInt(key, defaultValue);
    }

    public synchronized static boolean getBoolean(String key, boolean defaultValue) {
        init();
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public synchronized static float getFloat(String key, float defaultValue) {
        init();
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public synchronized static long getLong(String key, long defaultValue) {
        init();
        return sharedPreferences.getLong(key, defaultValue);
    }

    public synchronized static String getString(String key, String defaultValue) {
        init();
        return sharedPreferences.getString(key, defaultValue);
    }

    public synchronized static void putInt(String key, int defaultValue) {
        init();
        sharedPreferences.edit().putInt(key, defaultValue).commit();
    }

    public synchronized static void putBoolean(String key, boolean defaultValue) {
        init();
        sharedPreferences.edit().putBoolean(key, defaultValue).commit();
    }

    public synchronized static void putFloat(String key, float defaultValue) {
        init();
        sharedPreferences.edit().putFloat(key, defaultValue).commit();
    }

    public synchronized static void putLong(String key, long defaultValue) {
        init();
        sharedPreferences.edit().putLong(key, defaultValue).commit();
    }

    public synchronized static void putString(String key, String defaultValue) {
        init();
        sharedPreferences.edit().putString(key, defaultValue).commit();
    }

    public synchronized static void putStrings(Map<String, String> keyValuePairs) {
        init();
        SharedPreferences.Editor edit = sharedPreferences.edit();
        for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
            edit.putString(entry.getKey(), entry.getValue());
        }
        edit.commit();
    }
}