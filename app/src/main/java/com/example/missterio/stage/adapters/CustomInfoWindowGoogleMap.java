package com.example.missterio.stage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.missterio.stage.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater mInflater;

    public CustomInfoWindowGoogleMap(LayoutInflater inflater){
        this.mInflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = mInflater
                .inflate(R.layout.map_custom_infowindow, null);

        TextView wiMatricule = view.findViewById(R.id.wiMatricule);
        TextView wiDate = view.findViewById(R.id.wiDate);
        TextView wiEmplacement = view.findViewById(R.id.wiEmplacement);
        TextView wiSpeed = view.findViewById(R.id.wiSpeed);
        TextView wiChauffeur = view.findViewById(R.id.wiChauffeur);

        wiMatricule.setText(marker.getTitle());
        wiDate.setText(marker.getSnippet());
       InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
        wiEmplacement.setText(infoWindowData.getWiEmplacement());
        wiSpeed.setText(infoWindowData.getWiSpeed()+"");
        wiChauffeur.setText(infoWindowData.getWiChauffeur());




        return view;

    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = mInflater
                .inflate(R.layout.map_custom_infowindow, null);

        TextView wiMatricule = view.findViewById(R.id.wiMatricule);
        TextView wiDate = view.findViewById(R.id.wiDate);
        TextView wiEmplacement = view.findViewById(R.id.wiEmplacement);
        TextView wiSpeed = view.findViewById(R.id.wiSpeed);
        TextView wiChauffeur = view.findViewById(R.id.wiChauffeur);

        wiMatricule.setText(marker.getTitle());
        wiDate.setText(marker.getSnippet());
        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
        wiEmplacement.setText(infoWindowData.getWiEmplacement());
        wiSpeed.setText(infoWindowData.getWiSpeed()+"");
        wiChauffeur.setText(infoWindowData.getWiChauffeur());

        return view;
    }
}
