package com.github.viphe.iconkolan;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IconKolanWidgetConfigActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private int appWidgetId;
    private AppChoice selectedAppChoice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.config_layout);
        PackageManager packageManager = getPackageManager();
        if (packageManager != null) {
            List<AppChoice> appChoices = collectAppChoices(packageManager);

            ArrayAdapter<AppChoice> adapter = new ArrayAdapter<AppChoice>(
                this,
                android.R.layout.simple_spinner_item,
                appChoices.toArray(new AppChoice[appChoices.size()]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            AdapterView spinner = (AdapterView) findViewById(R.id.app_spinner);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    private List<AppChoice> collectAppChoices(PackageManager packageManager) {
        List<ApplicationInfo> appInfos =
            packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<AppChoice> appChoices = new ArrayList<AppChoice>(appInfos.size());
        for (ApplicationInfo appInfo : appInfos) {
            if (packageManager.getLaunchIntentForPackage(appInfo.packageName) != null) {
                appChoices.add(new AppChoice(appInfo, packageManager));
            }
        }
        Collections.sort(appChoices);
        appChoices.set(0, new AppChoice(null, packageManager));
        return appChoices;
    }

    private static class AppChoice implements Comparable<AppChoice> {
        private final ApplicationInfo appInfo;
        private final String label;

        private AppChoice(ApplicationInfo appInfo, PackageManager packageManager) {
            this.appInfo = appInfo;
            this.label = appInfo == null ? "" : appInfo.loadLabel(packageManager).toString();
        }

        public int compareTo(AppChoice another) {
            return label.compareToIgnoreCase(another.label);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || ((Object) this).getClass() != o.getClass()) return false;

            AppChoice appChoice = (AppChoice) o;

            if (appInfo != null ? !appInfo.equals(appChoice.appInfo) : appChoice.appInfo != null)
                return false;
            if (label != null ? !label.equals(appChoice.label) : appChoice.label != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return appInfo != null ? appInfo.hashCode() : 0;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getAdapter().getItem(position);

        if (item instanceof AppChoice) {
            selectedAppChoice = (AppChoice) item;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedAppChoice = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                String packageName = null;
                if (selectedAppChoice != null && selectedAppChoice.appInfo != null) {
                    packageName = selectedAppChoice.appInfo.packageName;
                    Config.saveWidgetPackage(this, appWidgetId, packageName);
                }

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);

                IconKolanWidgetProvider.updateWidget(
                    getApplicationContext(), appWidgetId, packageName);

                finish();
                return true;
        }

// Build/Update widget
         //   AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

// This is equivalent to your ChecksWidgetProvider.updateAppWidget()
//            appWidgetManager.updateAppWidget(appWidgetId,
//                    IconKolanWidgetProvider.buildRemoteViews(getApplicationContext(),
//                            appWidgetId));

// Updates the collection view, not necessary the first time
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.notes_list);

// Destroy activity
//        }
        return onOptionsItemSelected(item);
    }
}
