package com.example.missterio.stage.models;

public class Vehicule {
    //matricule
    String FullName;
    double Speed;
    String NearestPlace;
    String AcquisitionTimeString;
    Boolean Connected;
    String DriverName;
    String Key;

    public Vehicule(String fullName, double speed, String nearestPlace, String acquisitionTimeString, Boolean connected, String driverName, String key) {
        FullName = fullName;
        Speed = speed;
        NearestPlace = nearestPlace;
        AcquisitionTimeString = acquisitionTimeString;
        Connected = connected;
        DriverName = driverName;
        Key = key;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public double getSpeed() {
        return Speed;
    }

    public void setSpeed(double speed) {
        Speed = speed;
    }

    public String getNearestPlace() {
        return NearestPlace;
    }

    public void setNearestPlace(String nearestPlace) {
        NearestPlace = nearestPlace;
    }

    public String getAcquisitionTimeString() {
        return AcquisitionTimeString;
    }

    public void setAcquisitionTimeString(String acquisitionTimeString) {
        AcquisitionTimeString = acquisitionTimeString;
    }

    public Boolean getConnected() {
        return Connected;
    }

    public void setConnected(Boolean connected) {
        Connected = connected;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
