package com.example.oswald96.applicenta.StructureClasses;

import java.util.ArrayList;

public class ContextForStrategy {
    private GetDataFromAPIStrategy strategy;

    public ContextForStrategy(GetDataFromAPIStrategy strategy){
        this.strategy = strategy;
    }

    public ArrayList<StructuraDateAQI> executeStrategy(String url){
        return strategy.getDateFinale(url);
    }
}
