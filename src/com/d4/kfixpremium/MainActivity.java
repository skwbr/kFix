package com.d4.kfixpremium;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
    
    private static SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    setContentView(R.layout.main);
    startService(new Intent(this, MainService.class));
    Button button1 = (Button) findViewById(R.id.button1);
    SeekBar sb1 = (SeekBar) findViewById(R.id.SeekBar01);
    SeekBar sb2 = (SeekBar) findViewById(R.id.seekBar1);
    CheckBox cb1 = (CheckBox) findViewById(R.id.checkBox1);
    button1.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          builder.setTitle("About")
                           .setMessage("kFix 3.0 by D4rKn3sSyS, Use with caution\nIm not responsible if you make any damage to your keyboard LED lights\nThank you for supporting this project buying donate version.")
                           .setCancelable(false)
                           .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                               }
                           });
                    AlertDialog alert = builder.create();
                    alert.show();
        }
      });
        sb1.setMax(255);
        sb1.setProgress(preferences.getInt("brightness", 220));
        sb1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onProgressChanged(SeekBar color, int value, boolean fromUser) {
                                MainService.brightness=value;
                                preferences.edit().putInt("brightness", value).commit();
                        }
                    });
        sb2.setMax(30000);
        sb2.setProgress(preferences.getInt("sensibility", 3200));
        sb2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onProgressChanged(SeekBar color, int value, boolean fromUser) {
                                MainService.sensibility=(float) value;
                                preferences.edit().putInt("sensibility", value).commit();
                        }
                    });
        cb1.setChecked(preferences.getBoolean("enabled", true) ? true : false);
        cb1.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                Intent updateWidgetIntent = new Intent(MainActivity.this, WidgetManager.class);
                updateWidgetIntent.setAction(WidgetManager.ACTION_WIDGET_RECEIVER);
                MainActivity.this.sendBroadcast(updateWidgetIntent);
        }
        });
    }
}
