package com.example.oswald96.applicenta.StructureClasses;

import com.example.oswald96.applicenta.StructureClasses.StructuraDateAQI;

import java.util.ArrayList;

public interface GetDataFromAPIStrategy {
    public ArrayList<StructuraDateAQI> getDateFinale(String urlprimit);
}
