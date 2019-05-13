package com.example.oswald96.applicenta;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.example.oswald96.applicenta.StructureClasses.DataFromWAQI;

import java.io.IOException;
import java.net.URISyntaxException;

public class AlarmReciever extends WakefulBroadcastReceiver
{
    private DataFromWAQI dateCurente;
    private static Context context;


    public static void setContext(Context context) {
        AlarmReciever.context = context;
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "Alarmaaaaa!!!!", Toast.LENGTH_LONG).show();

        try {
            dateCurente = new DataFromWAQI("http://exactonly.ro:13000/aqi_last_data", context);
            dateCurente.getLatestDataFromMyAPI("http://exactonly.ro:13000/aqi_last_data");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}