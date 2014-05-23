package com.github.viphe.iconkolan;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    public static final String PREFS = "IconKolanConfig";

    public static void saveWidgetPackage(Context context, int widgetId, String packageName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString("widget_" + widgetId + "_package", packageName).commit();
    }

    public static String getWidgetPackage(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString("widget_" + widgetId + "_package", null);
    }
}
