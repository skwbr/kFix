package com.d4.kfixpremium;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainService extends Service implements SensorEventListener{

    public static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    public static int brightness;
    public static float sensibility;
    private float current;
    private SensorManager mSensorManager;
    private Sensor mLight;
    private static SharedPreferences preferences;
    private boolean turnOff=true;
    
    @Override
    public void onCreate() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BCAST_CONFIGCHANGED);
        this.registerReceiver(mBroadcastReceiver, filter);
        Log.i("kFix", "Service / Receiver Started");
        Log.i("kFix", "Writing Control Values");
        try{
        WriteFile("255", "/sys/devices/platform/msm_pmic_misc_led.0/max::brightness");
        WriteFile("40", "/sys/devices/platform/msm_pmic_misc_led.0/max::current_ma");
        WriteFile("1", "/sys/devices/platform/msm_pmic_misc_led.0/als::cut-off");
        brightness = preferences.getInt("brightness", 220);
        sensibility = (float) preferences.getInt("sensibility", 3200);
        Log.i("kFix", "Control Values Sucessfully Wroten");
        Log.i("kFix", "Readed Values: Sensibility - "+sensibility+" Brightness - "+brightness);
        }catch(Exception e){
            Log.i("kFix", "ERROR: Writing Control Values - ERROR MESSAGE: "+e.getMessage());
        }
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener((SensorEventListener) this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        setState();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    public static void setKeyboardState(int state){
     if(state==1){
         WriteFile("0", "/sys/devices/platform/msm_pmic_misc_led.0/brightness");
     }
     else{
         WriteFile(String.valueOf(brightness), "/sys/devices/platform/msm_pmic_misc_led.0/brightness");        
     }
    }
    
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent myIntent) {
            if (myIntent.getAction().equals(BCAST_CONFIGCHANGED)) {
                    setState();
            }
        }
    };
           
   public static void WriteFile(String text, String file) {
     try{
      FileWriter fstream = new FileWriter(file);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(text);
      out.close();
     }catch (Exception e){
	  //Nothing
     }
    }
       
   public static String ReadFile(String where){
        try {
            String out="";
            FileInputStream fstream = new FileInputStream(where);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                 out += strLine;
            }
            in.close();
            return out;
        } catch (IOException ex) {
            //Nothing
            return null;
        }
   }
   
   public void setState(){
       if(getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO && preferences.getBoolean("enabled", true)){
                    if(current<=(float) sensibility){
                        setKeyboardState(0);
                    }
                    else{
                        setKeyboardState(1);
                    }
                }
                else {
                    if(turnOff){
                        turnOff=false;
                        setKeyboardState(1);
                    }
            }
   }

   @Override
    public void onSensorChanged(SensorEvent se) {
        if(se.sensor.getType() == Sensor.TYPE_LIGHT) {
        current = se.values[0];
        setState();
        }
    }

   @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do nothing
    }
}