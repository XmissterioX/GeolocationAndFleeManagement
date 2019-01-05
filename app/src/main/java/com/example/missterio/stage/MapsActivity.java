package com.example.missterio.stage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.missterio.stage.Services.CustomJsonArrayRequest;
import com.example.missterio.stage.Services.Ineed;
import com.example.missterio.stage.Services.MyItem;
import com.example.missterio.stage.Services.MyItemMarkerRender;
import com.example.missterio.stage.Services.MySingleton;
import com.example.missterio.stage.Services.OnMapAndViewReadyListener;
import com.example.missterio.stage.adapters.CustomInfoWindowGoogleMap;
import com.example.missterio.stage.adapters.InfoWindowData;
import com.example.missterio.stage.models.Location;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.missterio.stage.LoginActivity.PREFS_USER;

public class MapsActivity extends AppCompatActivity implements
       OnMapReadyCallback
      /*  GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>*/
{
    private ClusterManager<MyItem> mClusterManager;
    public static String url_Data = "http://www.webtracemobile.com/webmobilesvc/Service/TrackingPageWCFService.svc/GetTrackingObjectWithUpdateRefreshCompressed";
    public static String url_compressedData = "http://192.168.0.103:59612/decompressData";
    private GoogleMap mMap;
    private MyItem clickedClusterItem;
    //queue
    public RequestQueue queue;
    //request Data
    public JSONObject requestData;
    private Circle circle;

    List<Location> locationsVolley;

    private int[] mStyleIds = {
           R.string.Satellite, R.string.Default
    };

    private int mSelectedStyleId = R.string.Default;
    private static final String TAG = MapsActivity.class.getSimpleName();

    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setTitle("Localisation");
        //Queue
        queue = MySingleton.getInstance(getApplicationContext()).
                getRequestQueue();

        locationsVolley = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences(PREFS_USER, MODE_PRIVATE);
        String userPrefs = preferences.getString("SessionId", null);
        //Toast.makeText(getApplicationContext(),userPrefs,Toast.LENGTH_SHORT).show();

        requestData = new JSONObject();

        //mMap.setOnCameraIdleListener(mClusterManager);
        getCompressedData();



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setBuildingsEnabled(true);

        mClusterManager = new ClusterManager<>(this, mMap);
        final MyItemMarkerRender renderer = new MyItemMarkerRender(this, mMap, mClusterManager,mMap.getCameraPosition().zoom, 18.0f);
        mClusterManager.setRenderer(renderer);
        mClusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new CustomInfoWindowGoogleMap(LayoutInflater.from(this)));

        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        //mMap.OnCameraChangeListener(mClusterManager);
        final CameraPosition[] mPreviousCameraPosition = {null};
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition position = mMap.getCameraPosition();
                if(mPreviousCameraPosition[0] == null || mPreviousCameraPosition[0].zoom != position.zoom) {
                    mPreviousCameraPosition[0] = mMap.getCameraPosition();
                    mClusterManager.cluster();
                }
            }
        });
                mMap.setOnMarkerClickListener(mClusterManager);


        //mMap.setInfoWindowAdapter(customInfoWindow);


        mMap.setOnInfoWindowClickListener(mClusterManager); //added
        //mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        //mClusterManager.setOnClusterItemInfoWindowClickListener(this); //added
        mClusterManager
                .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        clickedClusterItem = item;
                        return false;
                    }
                });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"rafraichissement",Toast.LENGTH_SHORT).show();
                mClusterManager.clearItems();
                getCompressedData();
                handler.postDelayed(this, 30000);
            }
        }, 30000);  //the time is in miliseconds


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
    }

    private void getCompressedData(){
// prepare the Request
        locationsVolley.clear();
       // mClusterManager.clearItems();

       // Toast.makeText(getApplicationContext(),"aaaaa",Toast.LENGTH_SHORT).show();
        JsonObjectRequest  request = new JsonObjectRequest (Request.Method.POST, url_Data,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject  response) {
                        // display response
                    //   Toast.makeText(getApplicationContext(), "response => " + response.toString(), Toast.LENGTH_LONG).show();
                        //Log.d("Response", response.toString());
                   /*     try {
                            Toast.makeText(getsApplicationContext(),response.getString("GetTrackingObjectWithUpdateRefreshCompressedResult"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        try {
                            String res = response.getString("GetTrackingObjectWithUpdateRefreshCompressedResult");
                            String resEscape = res.replace("\\", "\\\\").toString();
                            byte [] gZipBuffer ;
                            gZipBuffer = Base64.decode(resEscape,0);
                            String resDecompressed = Ineed.decompress(gZipBuffer);
                            Log.d("dod",resDecompressed);
                            //requestData.put("compressedData",response.getString("GetTrackingObjectWithUpdateRefreshCompressedResult"));
                            JSONArray jsonArray = new JSONArray(resDecompressed);
                            //Toast.makeText(getApplicationContext(),"nizar"+jsonArray.length(),Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                Location l = new Location(jsonObject.getString("AcquisitionTimeString"),jsonObject.getString("NearestPlace"),
                                        jsonObject.getString("FullName"),jsonObject.getDouble("Latitude"),jsonObject.getDouble("Longitude"),
                                        jsonObject.getDouble("Speed"),jsonObject.getString("DriverName"));
                                //Toast.makeText(getApplicationContext(),"nizar"+l.getFullName(),Toast.LENGTH_SHORT).show();
                                if(l != null){
                                    locationsVolley.add(l);
                                }
                            }

                            if(locationsVolley != null){
                                for (Location l: locationsVolley) {
                                   /* Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(l.getLatitude(), l.getLongitude()))
                                            .title(l.getFullName())
                                            .snippet(l.getAcquisitionTimeString())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carpin64)));
                                    marker.showInfoWindow();
                                    LatLngBounds bounds = new LatLngBounds.Builder()
                                            .include(marker.getPosition())
                                            .build();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));*/
                                   MyItem myItem = new MyItem(new LatLng(l.getLatitude(),l.getLongitude()),
                                           l.getFullName(),l.getAcquisitionTimeString(),l.getNearestPlace(),l.getSpeed(),l.getDriverName());
                                  /*  InfoWindowData info = new InfoWindowData();
                                    info.setWiEmplacement(myItem.getEmplacement());
                                    info.setWiChauffeur(myItem.getChauffeur());
                                    info.setWiSpeed(myItem.getSpeed());
                                   mClusterManager.getMarkerCollection().getMarkers().add()*/


                                    mClusterManager.addItem(myItem);

                                }

                                mClusterManager.cluster();

                            }


                        //   Toast.makeText(getApplicationContext(),requestData.getString("compressedData"),Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                      //  getDecompressedData();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(),"erreur ! " ,Toast.LENGTH_SHORT).show();
                        if(error.networkResponse.statusCode == 400){
                            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                            i.putExtra("FLAG", 400);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            handler.removeCallbacksAndMessages(null);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "erreur !", Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", "error ! " +error.networkResponse.statusCode);
                    }

                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences(PREFS_USER, MODE_PRIVATE);
                String userPrefs = preferences.getString("SessionId", null);
                Map<String, String> param = new HashMap<String, String>();
                param.put("Cookie","ASP.NET_SessionId="+userPrefs);
                return param;
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

       // request.setShouldCache(true);
        //queue = Volley.newRequestQueue(getApplicationContext());

        request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        queue.add(request);
    }

    private void getDecompressedData() {
        JSONObject body = new JSONObject();
// Your code
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.POST, url_compressedData, requestData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              /*  Toast.makeText(getApplicationContext(),response.length()+"",Toast.LENGTH_SHORT).show();
                Log.d("ooo",response.toString());*/
                locationsVolley = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                      /*  Location l = new Location(jsonObject.getString("AcquisitionTimeString"),jsonObject.getString("NearestPlace"),
                                jsonObject.getString("FullName"),jsonObject.getDouble("Latitude"),jsonObject.getDouble("Longitude"));
                        if(l != null){
                            locationsVolley.add(l);
                        }*/





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                  //  Toast.makeText(getApplicationContext(),locationsVolley.get(0).getFullName(),Toast.LENGTH_SHORT).show();
                }

                if(locationsVolley != null){
                    for (Location l: locationsVolley) {
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(l.getLatitude(), l.getLongitude()))
                                .title(l.getFullName())
                                .snippet(l.getAcquisitionTimeString())
                                .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_directions_car_black_24dp)));
                        marker.showInfoWindow();
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(marker.getPosition())
                                .build();
                       // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "erreur!", Toast.LENGTH_SHORT).show();

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        queue.add(req);



    }


   /* @Override
    public boolean onMarkerClick(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),50));
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }*/

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.marker_azure);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth()+40 , vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.styled_map,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_style_choose) {
            showStylesDialog();
        }
        return true;
    }

    private void showStylesDialog() {
        // mStyleIds stores each style's resource ID, and we extract the names here, rather
        // than using an XML array resource which AlertDialog.Builder.setItems() can also
        // accept. We do this since using an array resource would mean we would not have
        // constant values we can switch/case on, when choosing which style to apply.
        List<String> styleNames = new ArrayList<>();
        for (int style : mStyleIds) {
            styleNames.add(getString(style));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choisir_style));
        builder.setItems(styleNames.toArray(new CharSequence[styleNames.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedStyleId = mStyleIds[which];
                        //String msg = getString(R.string.style_set_to, getString(mSelectedStyleId));
                        Toast.makeText(getApplicationContext(),"Style : " + getString(mSelectedStyleId),Toast.LENGTH_SHORT).show();
                       // Log.d(TAG, msg);
                        setSelectedStyle();
                    }
                });
        builder.show();
    }

    private void setSelectedStyle() {
        switch (mSelectedStyleId) {
            case R.string.Default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.string.Satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                return;
        }
    }


}
