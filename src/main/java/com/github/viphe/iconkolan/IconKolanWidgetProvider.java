package com.github.viphe.iconkolan;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class IconKolanWidgetProvider extends AppWidgetProvider {

    private static final long DOUBLE_CLICK_PERIOD = 500;

    private static volatile int clickCount;


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            String packageName = Config.getWidgetPackage(context, appWidgetId);

            if (packageName != null) {
                RemoteViews views = buildRemoveViews(context, appWidgetManager, appWidgetId, packageName);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    public static RemoteViews buildRemoveViews(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String packageName) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(
                packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            return views;
        }

        String appName = applicationInfo.loadLabel(packageManager).toString();
        views.setTextViewText(R.id.app_name, appName);
         views.setImageViewBitmap(R.id.app_icon, drawableToBitmap(applicationInfo.loadIcon(packageManager)));

        Intent intent = new Intent(context, IconKolanWidgetProvider.class);
        intent.setAction("click");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.app_launcher, pendingIntent);

        return views;
    }

    private void launchApp(Context context, int appWidgetId) {
        String packageName = Config.getWidgetPackage(context, appWidgetId);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            Toast toast = Toast.makeText(
                context, "Cannot launch " + packageName, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void launchConfig(Context context, int appWidgetId) {
        Intent intent = new Intent(context, IconKolanWidgetConfigActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if ("click".equals(intent.getAction())) {
            clickCount++;

            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    int appWidgetId = intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                    if (clickCount == 1) {
                        ApplicationInfo applicationInfo = (ApplicationInfo)
                            intent.getExtras().get("application-info");
                        launchApp(context, appWidgetId);
                    } else {
                        launchConfig(context, appWidgetId);
                    }
                    clickCount = 0;
                }
            };

            if (clickCount == 1) new Thread() {
                @Override
                public void run(){
                    try {
                        synchronized(this) { wait(DOUBLE_CLICK_PERIOD); }
                        handler.sendEmptyMessage(0);
                    } catch(InterruptedException ex) {}
                }
            }.start();
        }

        super.onReceive(context, intent);
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
