package com.github.viphe.iconkolan;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

public class IconKolanWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(
                        "com.enterpriseappzone", PackageManager.GET_UNINSTALLED_PACKAGES);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(R.id.app_name, applicationInfo.loadLabel(packageManager));
            views.setImageViewBitmap(R.id.app_icon, drawableToBitmap(applicationInfo.loadIcon(packageManager)));

            Intent intent = packageManager.getLaunchIntentForPackage("com.enterpriseappzone");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.app_launcher, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
