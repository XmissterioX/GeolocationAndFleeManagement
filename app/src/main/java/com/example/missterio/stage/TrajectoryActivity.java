package com.example.missterio.stage;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.missterio.stage.Services.CustomDateTimePicker;
import com.example.missterio.stage.Services.Ineed;
import com.example.missterio.stage.Services.MySingleton;
import com.example.missterio.stage.models.Trajectory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.missterio.stage.LoginActivity.PREFS_USER;

public class TrajectoryActivity extends AppCompatActivity implements OnMapReadyCallback {

    static long MaximumDate=0,MinimunDate=0;
    private GoogleMap mMap;
    public Intent intent;
    JSONObject data;
    TextView txtMatriculeTrajectory,txtDateWaringTrajectory;
    EditText txtDebut,txtFin;
    String url_trajectory = "http://www.webtracemobile.com/webmobilesvc/Service/TrackingPageWCFService.svc/GetTrajectoryCompressed";
    RequestQueue queue;
    CustomDateTimePicker dateTimePickerDebut,dateTimePickerFin;
    String startDateTimeGMT,endDateTimeGMT;
    Button btnDebut,btnFin,btnTracer;
    ToggleButton toggleButton;
    LinearLayout linearLayout,snackBar;
    Polyline polylineFinal;
    PolylineOptions polylineOptions;
    private List<Trajectory> trajectoryList = new ArrayList<>();

    private int[] mStyleIds = {
            R.string.Satellite, R.string.Default
    };

    private int mSelectedStyleId = R.string.Default;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTrajectory);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("Trajectoire");
        btnDebut = findViewById(R.id.btnDebutTrajectory);
        btnFin = findViewById(R.id.btnFinTrajectory);
        btnFin.setEnabled(false);
        btnTracer = findViewById(R.id.btnGenererTrajectory);
        txtDebut = findViewById(R.id.txtDebutTrajectory);
        txtFin = findViewById(R.id.txtFinTrajectory);
        txtDateWaringTrajectory = findViewById(R.id.txtDateWarningTrajectory);
        toggleButton = findViewById(R.id.toggleButton);
        linearLayout = findViewById(R.id.linearLayoutTrajectory);
        snackBar = findViewById(R.id.snackBar);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleButton.isChecked()){
                    expand(linearLayout);
                }
                else
                    collapse(linearLayout);

            }
        });

        queue = MySingleton.getInstance(getApplicationContext()).
                getRequestQueue();

        intent = getIntent();

        txtMatriculeTrajectory = findViewById(R.id.txtMatriculeTrajectory);
        txtMatriculeTrajectory.setText(intent.getStringExtra("Matricule"));

        final Calendar c1 = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        c1.set(Calendar.HOUR_OF_DAY,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND,0);
        c1.set(Calendar.MILLISECOND,0);
        txtDebut.setText(sdf.format(c1.getTime()));
        startDateTimeGMT = "/Date(" + c1.getTimeInMillis() +")/";
        final Calendar c2 = Calendar.getInstance();
        /*c2.set(Calendar.HOUR_OF_DAY,23);
        c2.set(Calendar.MINUTE,59);
        c2.set(Calendar.SECOND,0);
        c2.set(Calendar.MILLISECOND,0);*/
        txtFin.setText(sdf.format(c2.getTime()));
        endDateTimeGMT = "/Date(" + c2.getTimeInMillis() +")/";
        polylineOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);



        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View vv = view;

                switch (vv.getId()) {
                    case R.id.btnDebutTrajectory:
                        CustomDateTimePicker dateTimePickerDebut = new CustomDateTimePicker(TrajectoryActivity.this,
                                new CustomDateTimePicker.ICustomDateTimeListener() {


                                    @Override
                                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                                      Date dateSelected, int year, String monthFullName,
                                                      String monthShortName, int monthNumber, int date,
                                                      String weekDayFullName, String weekDayShortName,
                                                      int hour24, int hour12, int min, int sec,
                                                      String AM_PM) {

                                        //                        ((TextInputEditText) findViewById(R.id.edtEventDateTime))
                                        txtDebut.setText("");

                                        Calendar upDateFrom = Calendar.getInstance();
                                        upDateFrom.set(year, monthNumber, date);
                                        txtDebut.setText(sdf.format(upDateFrom.getTime()));

                                        MinimunDate=dateSelected.getTime()+86400000;
                                        MaximumDate=dateSelected.getTime()+2628002880L;

                                        Log.i("millis",calendarSelected.getTimeInMillis()+"getTime"+dateSelected.getTime());
                                        // txtDebut.setText(calendarSelected.getTimeInMillis()+"");
                                        startDateTimeGMT = "/Date(" + calendarSelected.getTimeInMillis() +")/";
                                        btnFin.setEnabled(true);


                                        c1.set(year,monthNumber,date,hour24,min,sec);
                                        if(c1.getTimeInMillis() > c2.getTimeInMillis()){
                                            btnTracer.setVisibility(View.GONE);
                                            txtDateWaringTrajectory.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            btnTracer.setVisibility(View.VISIBLE);
                                            txtDateWaringTrajectory.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }

                                });
                        dateTimePickerDebut.set24HourFormat(true);
                        dateTimePickerDebut.setDate(c1);

                        dateTimePickerDebut.showDialog();
                        break;

                    case R.id.btnFinTrajectory:
                        dateTimePickerFin = new CustomDateTimePicker(TrajectoryActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                                txtFin.setText("");
                                Log.i("millis",calendarSelected.getTimeInMillis()+"");
                                txtFin.setText(sdf.format(calendarSelected.getTime()));
                                endDateTimeGMT = "/Date(" + calendarSelected.getTimeInMillis() +")/";
                                c2.set(year,monthNumber,date,hour24,min,sec);
                                if(c1.getTimeInMillis() > c2.getTimeInMillis()){
                                    btnTracer.setVisibility(View.GONE);
                                    txtDateWaringTrajectory.setVisibility(View.VISIBLE);
                                }
                                else {
                                    btnTracer.setVisibility(View.VISIBLE);
                                    txtDateWaringTrajectory.setVisibility(View.GONE);
                                }

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                        dateTimePickerFin.setDate(c2);
                        //txtFin.setText(sdf.format(c2.getTime()));
                        dateTimePickerFin.getDatePicker().setMinDate(MinimunDate);
                        dateTimePickerFin.getDatePicker().setMaxDate(MaximumDate);
                        dateTimePickerFin.showDialog();
                }

            }

        };

        btnDebut.setOnClickListener(showDatePicker);
        btnFin.setOnClickListener(showDatePicker);

        btnTracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(snackBar, "Chargement...", Snackbar.LENGTH_SHORT);

                snackbar.setActionTextColor(Color.RED);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.DKGRAY);
                TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();

                snackbar.show();
                data = new JSONObject();
                try {
                    data.put("keyTrackingObject", intent.getStringExtra("Key"));
                    data.put("startDateTimeGMT", startDateTimeGMT);
                    data.put("endDateTimeGMT", endDateTimeGMT);
                    GetTrajectoryCompressed();
                    toggleButton.setChecked(false);
                    collapse(linearLayout);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



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

        // Add a marker in Sydney and move the camera
      LatLng tun = new LatLng(36.4327999, 9.7201781);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tun,7));

    }

   private void GetTrajectoryCompressed() {
        // loadingDialog.show();
       //mMap.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url_trajectory, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mMap.clear();
                        // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        Log.d("response", response.toString());

                        try {
                            String res = response.getString("GetTrajectoryCompressedResult");
                            String resEscape = res.replace("\\", "\\\\").toString();
                            byte [] gZipBuffer ;
                            gZipBuffer = Base64.decode(resEscape,0);
                            try {
                                String resDecompressed = Ineed.decompress(gZipBuffer);
                                //Toast.makeText(getApplicationContext(),resDecompressed,Toast.LENGTH_SHORT).show();
                                JSONObject jsonObjectRes = new JSONObject(resDecompressed);
                                JSONArray AllExtendedGpsData = jsonObjectRes.getJSONArray("AllExtendedGpsData");

                                if(AllExtendedGpsData.length() <4 ){

                                    Toast.makeText(getApplicationContext(),"Aucun résultat trouvé",Toast.LENGTH_SHORT).show();
                                }
                                for (int i = 0; i < AllExtendedGpsData.length(); i++) {
                                    JSONObject jsonObject = AllExtendedGpsData.getJSONObject(i);
                                    Trajectory trajectory = new Trajectory(jsonObject.getDouble("Latitude"),jsonObject.getDouble("Longitude"),
                                            jsonObject.getInt("Direction"),jsonObject.getInt("Speed"),jsonObject.getString("StopRunStatusEnumString"),
                                            jsonObject.getString("PeriodString"),jsonObject.getString("BeginDateLocalString"));
                                    trajectoryList.add(trajectory);
                                   // Toast.makeText(getApplicationContext(),trajectory.getLatitude()+"aaa",Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(),trajectory.getLatitude()+"aha",Toast.LENGTH_SHORT).show();
                                }
                                if(trajectoryList != null){
                                   // Toast.makeText(getApplicationContext(),trajectoryList.size()+"aha",Toast.LENGTH_SHORT).show();
                                    for (Trajectory t: trajectoryList) {

                                        Log.d("test","Latitude :" + t.getLatitude());
                                        polylineOptions.add(new LatLng(t.getLatitude(),t.getLongitude()));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(t.getLatitude(),t.getLongitude()), 20));
                                        if(t.getSpeed() != 0) {
                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(t.getLatitude(), t.getLongitude()))
                                                    .rotation(t.getDirection())
                                                    .title("Vitesse : " + t.getSpeed()+" km/h")
                                                    .snippet("Date : " + t.getBeginDateLocalString())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.greenarrow16)));
                                        }
                                        if(t.getStopRunStatusEnumString().equals("Stop")){

                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(t.getLatitude(), t.getLongitude()))
                                                    .alpha(0.5f)
                                                    .title(t.getStopRunStatusEnumString())
                                                    .snippet("Période :"+t.getPeriodString()+" Date : " + t.getBeginDateLocalString() )

                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pause_icon)));
                                        }
                                    }
                                }
                                if(trajectoryList.get(0) != null){
                                    Trajectory start = trajectoryList.get(0);
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(start.getLatitude(), start.getLongitude()))
                                            .title(trajectoryList.get(0).getBeginDateLocalString())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_icon)));
                                }
                               if(trajectoryList.size() > 1 && trajectoryList.get(trajectoryList.size()-1) != null){
                                    Trajectory finish = trajectoryList.get(trajectoryList.size()-1);
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .title(trajectoryList.get(trajectoryList.size()-1).getBeginDateLocalString())
                                            .position(new LatLng(finish.getLatitude(), finish.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon)));
                                }
                                //Toast.makeText(getApplicationContext(),polylineOptions.)
                                Polyline polyline = mMap.addPolyline(polylineOptions);


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //getDecompressedData();
                        //loadingDialog
                        // loadingDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loadingDialog.hide();
                        if(error.networkResponse.statusCode == 400){
                            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                            i.putExtra("FLAG", 400);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                        }
                        else
                       Toast.makeText(getApplicationContext(), "erreur !", Toast.LENGTH_SHORT).show();
                        Log.d("wtf", error.getMessage() + "");

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences(PREFS_USER, MODE_PRIVATE);
                String userPrefs = preferences.getString("SessionId", null);
                Map<String, String> param = new HashMap<String, String>();
                param.put("Cookie", "ASP.NET_SessionId=" + userPrefs);
                return param;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

       /* polylineFinal = mMap.addPolyline (polylineOptions);
        polylineOptions.clickable(true);*/
        request.setShouldCache(false);
        //queue = Volley.newRequestQueue(getApplicationContext());
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 5, 5));
        queue.add(request);
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
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
                        String msg = getString(R.string.style_set_to, getString(mSelectedStyleId));
                       // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),"Style : " + getString(mSelectedStyleId),Toast.LENGTH_SHORT).show();
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
