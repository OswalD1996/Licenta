package com.example.oswald96.applicenta.StructureClasses;

import android.os.Build;
import android.os.StrictMode;

import com.example.oswald96.applicenta.StructureClasses.GetDataFromAPIStrategy;
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

public class GetLastDataStrategy implements GetDataFromAPIStrategy {
    private static ArrayList<StructuraDateAQI> dateCurente = new ArrayList<>();
    @Override
    public ArrayList<StructuraDateAQI> getDateFinale(String urlprimit) {
        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String rezultat;
        JSONObject rezultatfinal = null;
        URL url = null;

        try {
            url = new URL(urlprimit);
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
            try {
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(rezultatfinal.getJSONArray("message").toString());
                JsonArray finalsucces = tradeElement.getAsJsonArray();
                dateCurente = JSONArrayParserAQI.convert(finalsucces);
            }catch (JSONException e){
            e.printStackTrace();
            }
        } finally {
            urlConnection.disconnect();
        }
        return dateCurente;
    }

    private String readStream(InputStream is) {
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
}
