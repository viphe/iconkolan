package com.github.viphe.iconkolan;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

    public static final Pattern WIDGET_PREF_REGEX = Pattern.compile("^widget_(\\d+)_package$");

    public static final String PREFS = "IconKolanConfig";

    public static void saveWidgetPackage(Context context, int widgetId, String packageName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString("widget_" + widgetId + "_package", packageName).commit();
    }

    public static String getWidgetPackage(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString("widget_" + widgetId + "_package", null);
    }

    public static Map<Integer, String> getWidgetPackages(Context context) {
        Map<Integer, String> packageNamesByAppWidgetIds = new HashMap<Integer, String>();

        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        for (Map.Entry<String, ?> pref : prefs.getAll().entrySet()) {
            String key = pref.getKey();

            Matcher matcher = WIDGET_PREF_REGEX.matcher(key);
            if (matcher.matches()) {
                int appWidgetId = Integer.parseInt(matcher.group(1));
                String packageName = (String) pref.getValue();
                packageNamesByAppWidgetIds.put(appWidgetId, packageName);
            }
        }

        return packageNamesByAppWidgetIds;
    }
}
