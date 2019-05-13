package com.example.oswald96.applicenta.UserRelatedClasses;

public class StructuraDateUser {
    private int UserID;
    private String Username;
    private int AlarmTimeSec;
    private String Password;
    private String DefaultLat;
    private String DefaultLong;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getAlarmTimeSec() {
        return AlarmTimeSec;
    }

    public void setAlarmTimeSec(int alarmTimeSec) {
        AlarmTimeSec = alarmTimeSec;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDefaultLat() {
        return DefaultLat;
    }

    public void setDefaultLat(String defaultLat) {
        DefaultLat = defaultLat;
    }

    public String getDefaultLong() {
        return DefaultLong;
    }

    public void setDefaultLong(String defaultLong) {
        DefaultLong = defaultLong;
    }
}
