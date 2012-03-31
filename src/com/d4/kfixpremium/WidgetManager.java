package com.d4.kfixpremium;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class WidgetManager extends AppWidgetProvider{
    
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    private static SharedPreferences preferences;
    public static boolean state;
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        state = preferences.getBoolean("enabled", true);
    for(int i=0;i < appWidgetIds.length;i++) {
        int appWigedid = appWidgetIds[i];
        Intent intent = new Intent(context, WidgetManager.class)
            .setAction(ACTION_WIDGET_RECEIVER);
        PendingIntent pendingintent = PendingIntent.getBroadcast(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
        views.setOnClickPendingIntent(R.id.tvWidget, pendingintent);
        views.setTextViewText(R.id.tvWidget, state ? "kFix: ON" : "kFix: OFF");
        appWidgetManager.updateAppWidget(appWigedid,views);
    }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {  
        final String action = intent.getAction();        
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {      
            final int appWidgetId = intent.getExtras().getInt(            
                    AppWidgetManager.EXTRA_APPWIDGET_ID,            
                    AppWidgetManager.INVALID_APPWIDGET_ID);            
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {            
                this.onDeleted(context, new int[] { appWidgetId });           
            }            
        } else if (intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            state = preferences.getBoolean("enabled", true);
            Intent serviceIntent = new Intent(context, MainService.class);
            if(preferences.getBoolean("enabled", true)){       
                preferences.edit().putBoolean("enabled", false).commit();
                context.startService(serviceIntent);            
            }       
            else if(!preferences.getBoolean("enabled", true)){        
                preferences.edit().putBoolean("enabled", true).commit();      
                context.stopService(serviceIntent);
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);            
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetManager.class.getName());            
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);          
            onUpdate(context, appWidgetManager, appWidgetIds);            
        }        
        super.onReceive(context, intent);
    }
}