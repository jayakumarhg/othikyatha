package com.beatabout.othikyatha;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class ProfileWidget extends AppWidgetProvider {
  public static int ALARM_INTERVAL = 5000; 
  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
          int[] appWidgetIds) {
    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pInt = PendingIntent.getService(context, 0, new Intent(context, UpdateService.class), 0);
    alarmMgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), ALARM_INTERVAL, pInt);
  }
  
  public static class UpdateService extends Service {
    @Override
    public void onStart(Intent intent, int startId) {
      ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
      DataManager dataManager = new DataManager(contextWrapper);
        // Build the widget update for today
        RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.widget_message);
        PendingIntent pInt = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ProfileListActivity.class), 0);
        updateViews.setOnClickPendingIntent(R.id.widget, pInt);
        updateViews.setTextViewText(R.id.profile_name, dataManager.getActiveProfile().getName());
        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, ProfileWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, updateViews);
    }

    @Override
    public IBinder onBind(Intent arg0) {
      // TODO Auto-generated method stub
      return null;
    }
  }
}
