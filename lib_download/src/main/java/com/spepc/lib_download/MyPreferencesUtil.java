package com.spepc.lib_download;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.HashSet;
import java.util.Set;


public class MyPreferencesUtil {
    public static String MY_CONFIG_DATA_KEY = "my_config_data";
    public static String APK_VERSION_CODE = "apk_version_code"; //版本号
    private Context mContext;
    private SharedPreferences shp;

    private MyPreferencesUtil(Context context) {
        mContext = context;
        shp = mContext.getSharedPreferences(MY_CONFIG_DATA_KEY, Context.MODE_PRIVATE);
    }

    private static MyPreferencesUtil instance ;

    public static MyPreferencesUtil getInstance(Context context) {
        if(instance == null){
           instance = new MyPreferencesUtil(context);
        }
        return instance;
    }


    public void saveString(String key, String Value) {
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(key, Value);
        editor.apply();
    }

    public String loadString(String key, String defaultValue) {
        return shp.getString(key, defaultValue);
    }

    public void saveBoolean(String key, boolean Value) {

        SharedPreferences.Editor editor = shp.edit();
        editor.putBoolean(key, Value);
        editor.apply();
    }

    public boolean loadBoolean(String key, boolean defaultValue) {
        return shp.getBoolean(key, defaultValue);
    }

    public void saveLong(String key, long Value) {
        SharedPreferences.Editor editor = shp.edit();
        editor.putLong(key, Value);
        editor.apply();
    }

    public long loadLong(String key, long defaultValue) {
        return shp.getLong(key, defaultValue);
    }
}
