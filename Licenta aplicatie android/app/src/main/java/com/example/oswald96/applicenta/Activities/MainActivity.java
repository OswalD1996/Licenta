package com.example.oswald96.applicenta.Activities;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.oswald96.applicenta.AlarmReciever;
import com.example.oswald96.applicenta.R;
import com.example.oswald96.applicenta.UserRelatedClasses.UserData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    String username, previousActivty;
    UserData dateUser;
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = getIntent().getStringExtra("Username");
        View someView = findViewById(R.id.btnMap);
        View root = someView.getRootView();

        dateUser = UserData.getUserData(username);
        try{
            previousActivty = getIntent().getStringExtra("PreviousActivity");
            if(previousActivty.equals("Main"))
            {
                scheduleAlarm();
            }
        }catch (Exception e){}

        if(isServicesOK())
        {
            init();
        }
    }

    private void init()
    {
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view){
                                          Intent intent = new Intent(MainActivity.this, MapPoluareActivity.class);
                                          intent.putExtra("Username", username);
                                          startActivity(intent);
                                          finish();
                                      }
                                  }
        );
        Button btngrafipoluanti = (Button) findViewById(R.id.btngraficpoluanti);
        btngrafipoluanti.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View view){
                                              Intent intent = new Intent(MainActivity.this, GraficIndiciPoluareActivity.class);
                                              intent.putExtra("Username", username.toString());
                                              startActivity(intent);
                                              finish();
                                          }
                                      }
        );

        Button btnSet = (Button) findViewById(R.id.btnSettings);
        btnSet.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View view)
                                       {
                                           Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                           intent.putExtra("Username", username.toString());
                                           startActivity(intent);
                                           finish();
                                       }
                                   }
        );
        Button btnUmid = (Button) findViewById(R.id.btnMap2);
        btnUmid.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View view)
                                       {
                                           Intent intent = new Intent(MainActivity.this, UserHistoricMapActivity.class);
                                           intent.putExtra("Username", username.toString());
                                           startActivity(intent);
                                           finish();
                                       }
                                   }
        );
    }

    public boolean isServicesOK()
    {
        Log.d(TAG, "isServiceOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //se poate conecta
            Log.d(TAG, "totul este ok");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //nu se poate conecta

            Log.d(TAG, "este o eroare dar e reparabila");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "Telefonul nu se poate conecta", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scheduleAlarm()
    {
        AlarmReciever.setContext(this);

        AlarmManager mgrAlarm = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

        Intent intent = new Intent(this, AlarmReciever.class);
        // Loop counter `i` is used as a `requestCode`
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)
        Date dat = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(dat);
        cal_alarm.setTime(dat);
        if(dateUser.getAlarmInterval() == 0)
        {
            Toast.makeText(this, "You have no alarms set ", Toast.LENGTH_SHORT).show();
        }
        else {
            mgrAlarm.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), 1000 * dateUser.getAlarmInterval(), pendingIntent);
            Integer nr = 1000 * dateUser.getAlarmInterval();
            Toast.makeText(this, "Alarma e setat la intervalul: " + nr.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
