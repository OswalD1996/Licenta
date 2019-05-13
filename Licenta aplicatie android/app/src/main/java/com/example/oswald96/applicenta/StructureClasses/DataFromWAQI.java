package com.example.oswald96.applicenta.StructureClasses;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.oswald96.applicenta.Activities.MapPoluareActivity;
import com.example.oswald96.applicenta.GetRealAdressWithLatLng;
import com.example.oswald96.applicenta.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import cz.msebera.android.httpclient.Header;



public class DataFromWAQI {
    private int PM25 = 0;
    private int PM10 = 0;
    private float O3 = 0;
    private float NO2 = 0;
    private float SO2 = 0;
    private float CO = 0;
    private String lat;
    private String lng;
    private String time_date;
    private String nameLocation;
    private String UserSender;
    private String url;
    private static int i = 0;
    private Context context;

    public ArrayList<DataFromWAQI> datefinale= new ArrayList<DataFromWAQI>();
    private static ArrayList<StructuraDateAQI> dateCurente = new ArrayList<>();
    public ArrayList<StructuraDateAQI> dateperlocatie = new ArrayList<StructuraDateAQI>();
    public ArrayList<DataFromWAQI> datefinaleperUser = new ArrayList<DataFromWAQI>();

    public DataFromWAQI(String url, Context context) {
        this.url = url;
        this.context = context;
    }
   /* public DataFromWAQI(String url) {
        this.url = url;
    }*/
    public DataFromWAQI(int PM25, int PM10, float O3, float NO2, float SO2, float CO, String lat, String lng, String time_date, String nameLocation, String UserSender)
    {
        this.PM25         = PM25;
        this.PM10         = PM10;
        this.O3           = O3;
        this.NO2          = NO2;
        this.SO2          = SO2;
        this.CO           = CO;
        this.lat          = lat;
        this.lng          = lng;
        this.time_date    = time_date;
        this.nameLocation = nameLocation;
        this.UserSender   = UserSender;
    }
    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
    public void getDataFromOfficialAPI()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject success) {
                super.onSuccess(statusCode, headers, success);
                try {
                    //astea is in toate
                    setLat(success.getJSONObject("data").getJSONObject("city").getJSONArray("geo").getString(0));
                    setLng(success.getJSONObject("data").getJSONObject("city").getJSONArray("geo").getString(1));
                    setNameLocation(success.getJSONObject("data").getJSONObject("city").getString("name"));
                    setTime_date(success.getJSONObject("data").getJSONObject("time").getString("s"));
                    //astea nu-i sigur ca-s in toate
                    try {
                        if(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("pm10").getString("v") != null)
                        {
                            setPM10(Integer.parseInt(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("pm10").getString("v")));
                            if(PM10>100)
                            {
                                createNotification("Indicele PM10 are valoare"+Integer.toString(PM10)+" este la un nivel critic in zona "+getNameLocation());
                            }
                        }
                    }catch (org.json.JSONException e){}
                    try {
                        if(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("o3").getString("v") != null)
                        {
                            setO3(Float.parseFloat(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("o3").getString("v")));
                        }
                    }catch (org.json.JSONException e){}
                    try {
                        if(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("no2").getString("v") != null)
                        {
                            setNO2(Float.parseFloat(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("no2").getString("v")));
                        }
                    }catch (org.json.JSONException e){}
                    try {
                        if(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("so2").getString("v") != null)
                        {
                            setSO2(Float.parseFloat(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("so2").getString("v")));
                        }
                    }catch (org.json.JSONException e){}
                    try {
                        if(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("co").getString("v") != null)
                        {
                            setCO(Float.parseFloat(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("co").getString("v")));
                        }
                    }catch (org.json.JSONException e){}
                    try {
                        if(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("pm25").getString("v") != null)
                        {
                            setPM25(Integer.parseInt(success.getJSONObject("data").getJSONObject("iaqi").getJSONObject("pm25").getString("v")));
                        }
                    }catch (org.json.JSONException e){}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    public float calculateaqi()
    {
        float values [] = new float[6], sum = 0;
        int i=0, ctr=6;
        values[i++] = getO3();
        values[i++] = getCO();
        values[i++] = getNO2();
        values[i++] = getSO2();
        values[i++] = getPM25();
        values[i] = getPM10();
        for(i = 0; i<values.length;i++)
        {
            if(values[i] == 0)
            {
                ctr--;
            }
            else
            {
                sum = sum + values[i];
            }
        }
        float test = sum/ctr;
        return sum/ctr;
    }
    public Date formatedDate()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStart = getTime_date();
        if(dateStart.length() > 21) {
            dateStart = dateStart.substring(0, 10)+ " " + dateStart.substring(11, 19);
        }
        Date d1 = null;
        try {
            d1 = format.parse(dateStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1;
    }
    public String timePassed()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStart = getTime_date();
        if(dateStart.length() > 21) {
            dateStart = dateStart.substring(0, 10)+ " " + dateStart.substring(11, 19);
        }
        long ore = 0;
        long minute = 0;
        long zile = 0;
        //HH converts hour in 24 hours format (0-23), day calculation
        String dateStop = format.format(Calendar.getInstance().getTime());
        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            minute = diff / (60 * 1000) % 60;
            ore = diff / (60 * 60 * 1000) % 24 - 2 ;
            zile = diff / (24 * 60 * 60 * 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String finaldate;
        if(zile <= 0)
        {
            if(ore <= 0)
            {
                finaldate = Long.toString(minute) + " minute";
            }
            else {
                finaldate = Long.toString(ore) + " ore";
            }
        }
        else {
            finaldate = Long.toString(zile) + " zile";
        }
        return finaldate;
    }

//preluare date recente de la API-ul node
    public void getLatestDataFromMyAPI(String urlprimit) throws URISyntaxException, IOException {
        dateCurente.clear();
        ContextForStrategy contextForStrategy = new ContextForStrategy(new GetLastDataStrategy());
        dateCurente = contextForStrategy.executeStrategy(urlprimit);
        int i = 0;
        for(StructuraDateAQI element : dateCurente)
        {
            datefinale.add(new DataFromWAQI(element.getPM25(), element.getPM10(), element.getO3(), element.getNO2(), element.getSO2(), element.getCO(), element.getLatitude(), element.getLongitude(), element.getDataSiOra(),element.getLocationName(), element.getUserSender()));
            if(element.getPM10()>100)
            {
                String realadress = GetRealAdressWithLatLng.getAddress(Double.parseDouble(element.getLatitude()),Double.parseDouble(element.getLongitude()), context);
                createNotification("Indicele PM10 are valoare "+element.getPM10()+ " ajungand la un nivel critic\n" +
                        "Locatia exacta: "+realadress + "\n"+
                        "User-ul care a generat datele este "+element.getUserSender());
            }
            i++;
        }
    }

    public void getDataPerUserFromMyAPI(String urlprimit) throws URISyntaxException, IOException {
        dateperlocatie.clear();
        ContextForStrategy contextForStrategy = new ContextForStrategy(new GetLastDataStrategy());
        dateperlocatie = contextForStrategy.executeStrategy(urlprimit);
        int i = 0;
        for(StructuraDateAQI element : dateperlocatie)
        {
            datefinaleperUser.add(new DataFromWAQI(element.getPM25(), element.getPM10(), element.getO3(), element.getNO2(), element.getSO2(), element.getCO(), element.getLatitude(), element.getLongitude(), element.getDataSiOra(),element.getLocationName(), element.getUserSender()));
            if(element.getPM10()>100)
            {
                String realadress = GetRealAdressWithLatLng.getAddress(Double.parseDouble(element.getLatitude()),Double.parseDouble(element.getLongitude()), context);
                createNotification("Indicele PM10 are valoare "+element.getPM10()+ " ajungand la un nivel critic\n" +
                        "Locatia exacta: "+realadress + "\n"+
                        "User-ul care a generat datele este "+element.getUserSender());
            }
            i++;
        }
    }

    public void createNotification(String afisare) {
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent ii = new Intent(context.getApplicationContext(), MapPoluareActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(afisare);
        bigText.setBigContentTitle("Alerta poluare!");
        bigText.setSummaryText("");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Alerta poluare!");
        mBuilder.setContentText(afisare);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        mNotificationManager.notify(0, mBuilder.build());
    }

// getters si setters
    public String getUserSender() {
        return UserSender;
    }
    public void setUserSender(String userSender) {
        UserSender = userSender;
    }
    public int getPM25() {
        return PM25;
    }
    public void setPM25(int PM25) {
        this.PM25 = PM25;
    }
    public float getSO2() {
        return SO2;
    }
    public void setSO2(float SO2) {
        this.SO2 = SO2;
    }
    public float getCO() {
        return CO;
    }
    public void setCO(float CO) {
        this.CO = CO;
    }
    public float getNO2() {
        return NO2;
    }
    public void setNO2(float NO2) {
        this.NO2 = NO2;
    }
    public float getO3() {
        return O3;
    }
    public void setO3(float o3) {
        O3 = o3;
    }
    public int getPM10() {
        return PM10;
    }
    public void setPM10(int PM10) {
        this.PM10 = PM10;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLng() {
        return lng;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getTime_date() {
        return time_date;
    }
    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }
    public String getNameLocation() {
        return nameLocation;
    }
    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }


}
