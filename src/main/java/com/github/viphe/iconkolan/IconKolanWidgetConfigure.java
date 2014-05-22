package com.github.viphe.iconkolan;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class IconKolanWidgetConfigure extends Activity {

    private int appWidgetId;


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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
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
