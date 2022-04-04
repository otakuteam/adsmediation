package com.otaku.ads.mediation.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AdsPreferenceUtil {
    private static AdsPreferenceUtil instance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private AdsPreferenceUtil() {

    }

    public static AdsPreferenceUtil getInstance() {
        if (instance == null) {
            instance = new AdsPreferenceUtil();
        }
        return instance;
    }

    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences("ads_config", Context.MODE_PRIVATE);
        if (mSharedPreferences != null)
            mEditor = mSharedPreferences.edit();
    }

    public void putString(String key, String value) {
        if (mEditor != null) {
            mEditor.putString(key, value);
            mEditor.commit();
        }
    }

    public void putInt(String key, int value) {
        if (mEditor != null) {
            mEditor.putInt(key, value);
            mEditor.commit();
        }
    }

    public void putLong(String key, long value) {
        if (mEditor != null) {
            mEditor.putLong(key, value);
            mEditor.commit();
        }
    }

    public void putBoolean(String key, boolean value) {
        if (mEditor != null) {
            mEditor.putBoolean(key, value);
            mEditor.commit();
        }
    }

    public String getString(String key, String defVal) {
        if (mSharedPreferences != null)
            return mSharedPreferences.getString(key, defVal);
        return defVal;
    }

    public int getInt(String key, int defVal) {
        if (mSharedPreferences != null)
            return mSharedPreferences.getInt(key, defVal);
        return defVal;
    }

    public long getLong(String key, long defVal) {
        if (mSharedPreferences != null)
            return mSharedPreferences.getLong(key, defVal);
        return defVal;
    }

    public boolean getBoolean(String key, boolean defVal) {
        if (mSharedPreferences != null)
            return mSharedPreferences.getBoolean(key, defVal);
        return defVal;
    }

    public boolean getBoolean(String key) {
        if (mSharedPreferences != null)
            return mSharedPreferences.getBoolean(key, false);
        return false;
    }
}

