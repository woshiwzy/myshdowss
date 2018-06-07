package com.vm.shadowsocks.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.vm.shadowsocks.constant.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

public class SharePersistent {

    public static final String PREFS_NAME = Constant.TAG;




    public static void savePreference(Context context, String key, String value) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static void savePreference(Context context, String key, long value) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static String getPerference(Context context, String key) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(key, "");
    }

    public static int getInt(Context context, String key) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(key, 0);
    }

    public static long getlong(Context context, String key) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(key, 0);
    }

    public static float getFloat(Context context, String key){
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getFloat(key, 0);
    }

    public static void saveFloat(Context context, String key, float value){
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.commit();
    }


    public static boolean getBoolean(Context context, String key) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defaultVal) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(key, defaultVal);
    }
}
