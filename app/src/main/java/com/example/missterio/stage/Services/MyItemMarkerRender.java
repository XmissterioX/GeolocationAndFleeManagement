package com.example.missterio.stage.Services;

import android.content.Context;
import android.util.Log;

import com.example.missterio.stage.R;
import com.example.missterio.stage.adapters.CustomInfoWindowGoogleMap;
import com.example.missterio.stage.adapters.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Random;

public class MyItemMarkerRender extends DefaultClusterRenderer<MyItem> implements GoogleMap.OnCameraMoveListener {


    private final GoogleMap mMap;
    private float currentZoomLevel, maxZoomLevel;
    private Context context;
    public MyItemMarkerRender(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager, float currentZoomLevel, float maxZoomLevel) {
        super(context, map, clusterManager);
        this.mMap = map;
        this.currentZoomLevel = currentZoomLevel;
        this.maxZoomLevel = maxZoomLevel;
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        Random rand = new Random();

        markerOptions.icon(markerDescriptor).snippet(item.getSnippet()).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_yellow_32)).rotation(rand.nextInt(360)).title(item.getTitle());

       // Log.d("hoh",getMarker(item))
        //getMarker(item).setTag(info);
    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        InfoWindowData info = new InfoWindowData();
        info.setWiSpeed("Vitesse : "+clusterItem.getSpeed());
        info.setWiChauffeur(clusterItem.getChauffeur());
        info.setWiEmplacement(clusterItem.getEmplacement());
        marker.setTag(info);
    }

  /* @Override
    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
        Bitmap venueCircle = VenueCircleFactory.createFromCluster(cluster);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(venueCircle));
    }*/



    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster){
        // determine if superclass would cluster first, based on cluster size
        //  boolean superWouldCluster = super.shouldRenderAsCluster(cluster);

        // if it would, then determine if you still want it to based on zoom level
        if(cluster.getSize() > 4 && currentZoomLevel < 9) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onCameraMove() {
        currentZoomLevel = mMap.getCameraPosition().zoom;
    }


}
