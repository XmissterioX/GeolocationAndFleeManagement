package com.example.missterio.stage.models;

public class Trajectory {
    private double Latitude;
    private double Longitude;
    private int Direction;
    private int speed;
    private String StopRunStatusEnumString;
    private String PeriodString;
    private String BeginDateLocalString;

    public Trajectory(double latitude, double longitude, int direction, int speed, String stopRunStatusEnumString, String periodString, String beginDateLocalString) {
        Latitude = latitude;
        Longitude = longitude;
        Direction = direction;
        this.speed = speed;
        StopRunStatusEnumString = stopRunStatusEnumString;
        PeriodString = periodString;
        BeginDateLocalString = beginDateLocalString;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getStopRunStatusEnumString() {
        return StopRunStatusEnumString;
    }

    public void setStopRunStatusEnumString(String stopRunStatusEnumString) {
        StopRunStatusEnumString = stopRunStatusEnumString;
    }

    public String getPeriodString() {
        return PeriodString;
    }

    public void setPeriodString(String periodString) {
        PeriodString = periodString;
    }

    public String getBeginDateLocalString() {
        return BeginDateLocalString;
    }

    public void setBeginDateLocalString(String beginDateLocalString) {
        BeginDateLocalString = beginDateLocalString;
    }
}
