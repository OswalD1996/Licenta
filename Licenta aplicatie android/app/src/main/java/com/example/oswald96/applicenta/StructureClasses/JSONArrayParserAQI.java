package com.example.oswald96.applicenta.StructureClasses;

import com.example.oswald96.applicenta.StructureClasses.StructuraDateAQI;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class JSONArrayParserAQI {
    public static ArrayList<StructuraDateAQI> convert(JsonArray jsonArray)
    {
        Gson gson = new Gson();
        ArrayList<StructuraDateAQI> retArray = new ArrayList<>();
        for(JsonElement el:jsonArray){
            retArray.add( gson.fromJson(el,StructuraDateAQI.class));
        }
        return retArray;
    }
}
