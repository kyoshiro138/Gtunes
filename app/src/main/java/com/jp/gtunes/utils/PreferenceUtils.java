package com.jp.gtunes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceUtils {
    public static final String PREFERENCE_NAME = "com.jp.gtunes";

    public static final String PREFERENCE_TYPE_STRING = "PREFERENCE_TYPE_STRING";
    public static final String PREFERENCE_TYPE_INTEGER = "PREFERENCE_TYPE_INTEGER";

    private static boolean mLogEnabled = true;

    public static boolean saveValue(Context context, String key, Object value, String valueType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (valueType) {
            case PREFERENCE_TYPE_STRING:
                editor.putString(key, String.valueOf(value));
                break;
            case PREFERENCE_TYPE_INTEGER:
                editor.putInt(key, (int) value);
                break;
            default:
                Log("TYPE NOT SUPPORTED");
                editor.apply();
                return false;
        }

        editor.apply();

        String logMessage = String.format("PREFERENCE SAVED [TYPE:%s] [KEY:%s] [VALUE:%s]", valueType, key, value.toString());
        Log(logMessage);
        return true;
    }

    public static Object getValue(Context context, String key, Object defValue, String valueType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        Object value;
        switch (valueType) {
            case PREFERENCE_TYPE_STRING:
                value = sharedPreferences.getString(key, String.valueOf(defValue));
                break;
            case PREFERENCE_TYPE_INTEGER:
                value = sharedPreferences.getInt(key, (int) defValue);
                break;
            default:
                Log("TYPE NOT SUPPORTED");
                return null;
        }

        String logMessage = String.format("PREFERENCE LOADED [TYPE:%s] [KEY:%s] [VALUE:%s]", valueType, key, value.toString());
        Log(logMessage);
        return value;
    }

    private static void Log(String message) {
        if (mLogEnabled) {
            Log.d("SHARE PREFERENCE", message);
        }
    }
}
