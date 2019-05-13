package com.example.oswald96.applicenta.UserRelatedClasses;

import com.example.oswald96.applicenta.StructureClasses.StructuraDateAQI;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class JSONArrayParserUser {
    public static ArrayList<StructuraDateUser> convert(JsonArray jsonArray)
    {
        Gson gson = new Gson();
        ArrayList<StructuraDateUser> retArray = new ArrayList<>();
        for(JsonElement el:jsonArray){
            retArray.add( gson.fromJson(el,StructuraDateUser.class));
        }
        return retArray;
    }
}
