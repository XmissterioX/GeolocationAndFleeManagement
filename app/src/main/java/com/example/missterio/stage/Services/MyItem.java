package com.example.missterio.stage.Services;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private String Emplacement;
    private double Speed;
    private String Chauffeur;

    public MyItem(LatLng mPosition, String mTitle, String mSnippet, String emplacement, double speed, String chauffeur) {
        this.mPosition = mPosition;
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
        Emplacement = emplacement;
        Speed = speed;
        Chauffeur = chauffeur;
    }

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }



    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }



    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }



    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }


    public void setmSnippet(String mSnippet) {
        this.mSnippet = mSnippet;
    }

    public String getEmplacement() {
        return Emplacement;
    }

    public void setEmplacement(String emplacement) {
        Emplacement = emplacement;
    }

    public double getSpeed() {
        return Speed;
    }

    public void setSpeed(double speed) {
        Speed = speed;
    }

    public String getChauffeur() {
        return Chauffeur;
    }

    public void setChauffeur(String chauffeur) {
        Chauffeur = chauffeur;
    }

}