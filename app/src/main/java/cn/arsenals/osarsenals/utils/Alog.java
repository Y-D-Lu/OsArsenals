package cn.arsenals.osarsenals.utils;

import android.util.Log;

public class Alog {
    private static String convertToAlogTag(String tag) {
        return "ARSENALS_$tag";
    }

    public static void verbose(String tag, String msg) {
        Log.v(convertToAlogTag(tag), msg);
    }

    public static void verbose(String tag, String msg, Throwable tr) {
        Log.v(convertToAlogTag(tag), msg, tr);
    }

    public static void debug(String tag, String msg) {
        Log.d(convertToAlogTag(tag), msg);
    }

    public static void debug(String tag, String msg, Throwable tr) {
        Log.d(convertToAlogTag(tag), msg, tr);
    }

    public static void info(String tag, String msg) {
        Log.i(convertToAlogTag(tag), msg);
    }

    public static void info(String tag, String msg, Throwable tr) {
        Log.i(convertToAlogTag(tag), msg, tr);
    }

    public static void warn(String tag, String msg) {
        Log.w(convertToAlogTag(tag), msg);
    }

    public static void warn(String tag, String msg, Throwable tr) {
        Log.w(convertToAlogTag(tag), msg, tr);
    }

    public static void error(String tag, String msg) {
        Log.e(convertToAlogTag(tag), msg);
    }

    public static void error(String tag, String msg, Throwable tr) {
        Log.e(convertToAlogTag(tag), msg, tr);
    }

    public static void wtf(String tag, String msg) {
        Log.wtf(convertToAlogTag(tag), msg);
    }

    public static void wtf(String tag, Throwable tr) {
        Log.wtf(convertToAlogTag(tag), tr);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        Log.wtf(convertToAlogTag(tag), msg, tr);
    }
}
