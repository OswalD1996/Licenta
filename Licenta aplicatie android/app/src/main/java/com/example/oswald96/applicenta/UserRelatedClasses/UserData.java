package com.example.oswald96.applicenta.UserRelatedClasses;

import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.oswald96.applicenta.Activities.RegisterActivity;
import com.example.oswald96.applicenta.GetRealAdressWithLatLng;
import com.example.oswald96.applicenta.StructureClasses.DataFromWAQI;
import com.example.oswald96.applicenta.StructureClasses.JSONArrayParserAQI;
import com.example.oswald96.applicenta.StructureClasses.StructuraDateAQI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class UserData {
    private String username;
    private Integer alarmInterval;
    private String defaultLat;
    private String defaultLong;


    public UserData(String username, int alarmInterval, String defaultLat, String defaultLong)
    {
        this.username = username;
        this.alarmInterval = alarmInterval;
        this.defaultLat = defaultLat;
        this.defaultLong = defaultLong;
    }
    public UserData(){};

    public static UserData getUserData(String username)
    {
        ArrayList<StructuraDateUser> dateUser = new ArrayList<>();
        UserData dateFinaleUser = new UserData();
        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String rezultat;
        JSONObject rezultatfinal = null;
        URL url = null;
        try {
            url = new URL("http://exactonly.ro:13000/user_data/"+username);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream in = null;
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            rezultat = readStream(in);

            try {
                rezultatfinal = new JSONObject(rezultat);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonParser parser = new JsonParser();
            JsonElement tradeElement = parser.parse(rezultatfinal.getJSONArray("message").toString());
            JsonArray finalsucces = tradeElement.getAsJsonArray();
            dateUser = JSONArrayParserUser.convert(finalsucces);

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        for(StructuraDateUser element : dateUser)
        {
            dateFinaleUser = new UserData(element.getUsername(), element.getAlarmTimeSec(), element.getDefaultLat(), element.getDefaultLong());
        }
        return dateFinaleUser;
    }
    public void setUserData(String alarminterval, String defaultlocation)
    {
        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
        HttpPost request = new HttpPost("http://exactonly.ro:13000/user_data/ChangeData");
        String latlng[] = defaultlocation.split(",");

        Map< String, Object > jsonValues = new HashMap< String, Object >();
        jsonValues.put("Username", username);
        jsonValues.put("AlarmSec", alarminterval);
        jsonValues.put("DefaultLat", latlng[0]);
        jsonValues.put("DefaultLong", latlng[1]);

        JSONObject json = new JSONObject(jsonValues);
        StringEntity entity = new StringEntity(json.toString(), "UTF8");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        request.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity respEntity = response.getEntity();
            if (respEntity != null) {
                // EntityUtils to get the response content
                String content =  EntityUtils.toString(respEntity);
                JSONObject rezultatfinal = new JSONObject(content);
                content = rezultatfinal.getString("message").toString();
                //Toast.makeText(RegisterActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) { e.printStackTrace(); } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Integer getAlarmInterval() {
        return alarmInterval;
    }
    public void setAlarmInterval(int alarmInterval) {
        this.alarmInterval = alarmInterval;
    }
    public String getDefaultLat() {
        return defaultLat;
    }
    public void setDefaultLat(String defaultLat) {
        this.defaultLat = defaultLat;
    }
    public String getDefaultLong() {
        return defaultLong;
    }
    public void setDefaultLong(String defaultLong) {
        this.defaultLong = defaultLong;
    }
}
