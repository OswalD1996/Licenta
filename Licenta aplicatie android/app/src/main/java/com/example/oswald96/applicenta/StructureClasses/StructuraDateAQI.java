package com.example.oswald96.applicenta.StructureClasses;

public class StructuraDateAQI {
    private int ID;
    private String LocationName;
    private String Latitude;
    private String Longitude;
    private int PM10;
    private int PM25;
    private float O3;
    private float NO2;
    private float SO2;
    private float CO;
    private String DataSiOra;
    private String UserSender;
    private String MaxDataSiOra;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public int getPM10() {
        return PM10;
    }

    public void setPM10(int PM10) {
        this.PM10 = PM10;
    }

    public int getPM25() {
        return PM25;
    }

    public void setPM25(int PM25) {
        this.PM25 = PM25;
    }

    public float getO3() {
        return O3;
    }

    public void setO3(float o3) {
        O3 = o3;
    }

    public float getNO2() {
        return NO2;
    }

    public void setNO2(float NO2) {
        this.NO2 = NO2;
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

    public String getDataSiOra() {
        return DataSiOra;
    }

    public void setDataSiOra(String dataSiOra) {
        DataSiOra = dataSiOra;
    }

    public String getUserSender() {
        return UserSender;
    }

    public void setUserSender(String userSender) {
        UserSender = userSender;
    }

    public String getMaxDataSiOra() {
        return MaxDataSiOra;
    }

    public void setMaxDataSiOra(String maxDataSiOra) {
        MaxDataSiOra = maxDataSiOra;
    }
}
