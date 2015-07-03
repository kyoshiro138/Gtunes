package com.jp.gtunes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ApplicationPreference {

    public static final String PREFERENCE_TYPE_STRING = "PREFERENCE_TYPE_STRING";
    public static final String PREFERENCE_TYPE_INTEGER = "PREFERENCE_TYPE_INTEGER";
    public static final String PREFERENCE_TYPE_BOOLEAN = "PREFERENCE_TYPE_BOOLEAN";

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public ApplicationPreference(Context context) {
        this(context, context.getPackageName());
    }

    public ApplicationPreference(Context context, String PrefName) {
        this(context, PrefName, Context.MODE_PRIVATE);
    }

    public ApplicationPreference(Context context, String PrefName, int mode) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(PrefName, mode);
    }

    public boolean saveValue(String key, Object value, String valueType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        switch (valueType) {
            case PREFERENCE_TYPE_STRING:
                editor.putString(key, String.valueOf(value));
                break;
            case PREFERENCE_TYPE_INTEGER:
                editor.putInt(key, (int) value);
                break;
            case PREFERENCE_TYPE_BOOLEAN:
                editor.putBoolean(key, (boolean) value);
                break;
            default:
                Log.d(mContext.getPackageName(), "PREFERENCE TYPE NOT SUPPORTED");
                editor.apply();
                return false;
        }

        editor.apply();

        String logMessage = String.format("PREFERENCE SAVED [TYPE:%s] [KEY:%s] [VALUE:%s]", valueType, key, value.toString());
        Log.d(mContext.getPackageName(), logMessage);
        return true;
    }

    public Object getValue(String key, Object defValue, String valueType) {
        Object value;
        switch (valueType) {
            case PREFERENCE_TYPE_STRING:
                value = mSharedPreferences.getString(key, String.valueOf(defValue));
                break;
            case PREFERENCE_TYPE_INTEGER:
                value = mSharedPreferences.getInt(key, (int) defValue);
                break;
            case PREFERENCE_TYPE_BOOLEAN:
                value = mSharedPreferences.getBoolean(key, (boolean) defValue);
                break;
            default:
                Log.d(mContext.getPackageName(), "PREFERENCE TYPE NOT SUPPORTED");
                return null;
        }

        String logMessage = String.format("PREFERENCE LOADED [TYPE:%s] [KEY:%s] [VALUE:%s]", valueType, key, value.toString());
        Log.d(mContext.getPackageName(), logMessage);
        return value;
    }
}
