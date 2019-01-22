package com.nzarudna.shoppinglist.utils;

import android.util.Log;

public class ErrorHandler {

    public static void logError(String tag, String message, Throwable e) {
        Log.e(tag, message, e);
        // TODO add Crashlytics
    }
    public static void logError(String tag, String message) {
        Log.e(tag, message);
        // TODO add Crashlytics
    }
}
