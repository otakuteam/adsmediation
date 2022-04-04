package com.otaku.ads.testmediation;

import android.util.Log;

public class LogDebug {
    private static final String TAG = "LogDebug@";
    public static void d(String tag, String msg) {
        Log.d(TAG + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.d(TAG + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.d(TAG + tag, msg);
    }

    public static void v(String tag, String msg) {
        Log.d(TAG + tag, msg);
    }
}
