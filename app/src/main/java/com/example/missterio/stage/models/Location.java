package com.example.missterio.stage.models;



public class Location {

    private String AcquisitionTimeString;
    private String NearestPlace;
    //Matricule
    private String FullName;
    private double Latitude;
    private double Longitude;
    double Speed;
    String DriverName;

    public Location(String acquisitionTimeString, String nearestPlace, String fullName, double latitude, double longitude, double speed, String driverName) {
        AcquisitionTimeString = acquisitionTimeString;
        NearestPlace = nearestPlace;
        FullName = fullName;
        Latitude = latitude;
        Longitude = longitude;
        Speed = speed;
        DriverName = driverName;
    }

    public String getAcquisitionTimeString() {
        return AcquisitionTimeString;
    }

    public void setAcquisitionTimeString(String acquisitionTimeString) {
        AcquisitionTimeString = acquisitionTimeString;
    }

    public String getNearestPlace() {
        return NearestPlace;
    }

    public void setNearestPlace(String nearestPlace) {
        NearestPlace = nearestPlace;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
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

    public double getSpeed() {
        return Speed;
    }

    public void setSpeed(double speed) {
        Speed = speed;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }
}