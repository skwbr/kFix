package com.d4.kfixpremium;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver{

        private static SharedPreferences preferences;
    
        @Override
        public void onReceive(Context context, Intent intent) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
                Intent i=new Intent(context.getApplicationContext(), MainService.class);
                context.startService(i);
                Log.i("kFix", "Received Boot Broadcast");
        }

}