package com.github.viphe.iconkolan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Map;

public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        String packageName = data.getEncodedSchemeSpecificPart();

        Map<Integer, String> widgetPackages = Config.getWidgetPackages(context);
        for (Map.Entry<Integer, String> entry : widgetPackages.entrySet()) {
            if (packageName.equals(entry.getValue())) {
                int appWidgetId = entry.getKey();
                IconKolanWidgetProvider.updateWidget(
                    context.getApplicationContext(), appWidgetId, packageName);
            }
        }
    }
}
