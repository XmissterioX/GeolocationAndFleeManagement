package com.example.missterio.stage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.missterio.stage.Services.CustomDateTimePicker;
import com.example.missterio.stage.Services.CustomJsonArrayRequest;
import com.example.missterio.stage.Services.Ineed;
import com.example.missterio.stage.Services.MySingleton;
import com.example.missterio.stage.adapters.AlertAdapter;
import com.example.missterio.stage.models.Alerte;
import com.example.missterio.stage.models.Vehicule;

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
import java.util.TimeZone;

import static com.example.missterio.stage.LoginActivity.PREFS_USER;
import static com.example.missterio.stage.MapsActivity.url_compressedData;

public class AlertActivity extends AppCompatActivity {

    static long MaximumDate=0,MinimunDate=0;
    EditText txtDebut,txtFin;
    Button btnDebut,btnFin,btnGenererAlerts;
    TextView txtMatriculeAlerts,txtDateWaring,txtEmpty;
    public Intent intent;
    DatePickerDialog datePickerDialog;
    CustomDateTimePicker dateTimePickerDebut,dateTimePickerFin;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private JSONObject data,requestData;
    String startDateTimeGMT,endDateTimeGMT;
    RequestQueue queue;
    static int y;
    Handler handler = new Handler();
    private List<Alerte> alerteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlertAdapter mAdapter;
    public SkeletonScreen skeletonScreen;
    public static String url_GetAlertsElementCompressed ="http://www.webtracemobile.com/webmobilesvc/Service/TrackingPageWCFService.svc/GetAlertsElementCompressed";
    public  int visible = 1;
    public SearchView alertSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        getSupportActionBar().setTitle("Alertes");
        queue = MySingleton.getInstance(getApplicationContext()).
                getRequestQueue();
        recyclerView = findViewById(R.id.alertRecylerView);
        mAdapter = new AlertAdapter(getApplicationContext(),alerteList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        alertSearchView = findViewById(R.id.alertSearchView);
        alertSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                handler.removeCallbacksAndMessages(null);
                mAdapter.getFilter().filter(newText);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mAdapter.getItemCount() ==0)
                        {   txtEmpty.setVisibility(View.VISIBLE);
                            //Toast.makeText(getApplicationContext(), "empty" + mAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
                        }

                        else if (mAdapter.getItemCount() >0)
                        { txtEmpty.setVisibility(View.GONE);
                            //Toast.makeText(getApplicationContext(), "busy" + mAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 300);

                return true;
            }

        });

        final LinearLayout linearLayout ;
        linearLayout = findViewById(R.id.linearLayout);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.SCROLL_STATE_IDLE==newState){
                    // fragProductLl.setVisibility(View.VISIBLE);

                        if (y <= 0 & visible == 0) {
                            expand(linearLayout);
                        } else if(y > 0 & visible == 1) {
                            y = 0;
                            collapse(linearLayout);
                        }
                    }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
               y=dy;
            }
        });



        requestData = new JSONObject();
        startDateTimeGMT="";
        endDateTimeGMT="";

        txtDebut = findViewById(R.id.txtDebut);
        txtFin = findViewById(R.id.txtFin);
        txtDebut.setKeyListener(null);
        txtFin.setKeyListener(null);
        btnDebut = findViewById(R.id.btnDebut);
        btnFin = findViewById(R.id.btnFin);
        btnFin.setEnabled(false);
        txtDateWaring = findViewById(R.id.txtDateWarning);
        //txtDateWaring.setVisibility(View.GONE);
        btnGenererAlerts = findViewById(R.id.btnGenererAlerts);
        txtMatriculeAlerts = findViewById(R.id.txtMatriculeAlerts);
        txtEmpty = findViewById(R.id.txtEmpty);
        final Calendar c1 = Calendar.getInstance();
         final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        c1.set(Calendar.HOUR_OF_DAY,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND,0);
        c1.set(Calendar.MILLISECOND,0);
        txtDebut.setText(sdf.format(c1.getTime()));
        //Toast.makeText(getApplicationContext(),c1.getTime()+"",Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),sdf.format(c.getTime()),Toast.LENGTH_SHORT).show();
        startDateTimeGMT = "/Date(" + c1.getTimeInMillis() +")/";
        final Calendar c2 = Calendar.getInstance();
       /* c2.set(Calendar.HOUR_OF_DAY,23);
        c2.set(Calendar.MINUTE,59);
        c2.set(Calendar.SECOND,0);
        c2.set(Calendar.MILLISECOND,0);*/
        txtFin.setText(sdf.format(c2.getTime()));
        endDateTimeGMT = "/Date(" + c2.getTimeInMillis() +")/";
       /* btnDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePickerDialog = new DatePickerDialog(AlertActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                txtDebut.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year + "/" + c.getTimeInMillis());



                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });*/

        intent = getIntent();
        txtMatriculeAlerts.setText(intent.getStringExtra("Matricule"));


        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View vv = view;

                switch (vv.getId()) {
                    case R.id.btnDebut:
                        CustomDateTimePicker dateTimePickerDebut = new CustomDateTimePicker(AlertActivity.this,
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
                                        //calendarSelected.set(Calendar.SECOND,0);
                                        //calendarSelected.set(Calendar.MILLISECOND,0);
                        /*txtDebut.setText(year
                                + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)
                                + " " + hour24 + ":" + min
                                + ":" + sec);*/
                                        //txtDebut.setText(sdf.format(calendarSelected.getTime()));
                                        Log.i("millis",calendarSelected.getTimeInMillis()+"getTime"+dateSelected.getTime());
                                        txtDebut.setText(sdf.format(calendarSelected.getTime()));
                                        // txtDebut.setText(calendarSelected.getTimeInMillis()+"");
                                        startDateTimeGMT = "/Date(" + calendarSelected.getTimeInMillis() +")/";
                                        btnFin.setEnabled(true);


                                       /* dateTimePickerFin.getDatePicker().setMinDate(0);
                                        dateTimePickerFin.getDatePicker().setMaxDate(dateSelected.getTime()+2628002880L );
                                        dateTimePickerFin.getDatePicker().setMinDate(dateSelected.getTime()+86400000 );*/

                                        c1.set(year,monthNumber,date,hour24,min,sec);
                                        if(c1.getTimeInMillis() > c2.getTimeInMillis()){
                                            btnGenererAlerts.setVisibility(View.GONE);
                                            txtDateWaring.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            btnGenererAlerts.setVisibility(View.VISIBLE);
                                            txtDateWaring.setVisibility(View.GONE);
                                        }
                       /* if(!txtDebut.getText().toString().isEmpty() && !txtFin.getText().toString().isEmpty())
                            btnGenererAlerts.setVisibility(View.VISIBLE);
                        else
                            btnGenererAlerts.setVisibility(View.GONE);*/
                                    }

                                    @Override
                                    public void onCancel() {

                                    }

                                });
                        dateTimePickerDebut.set24HourFormat(true);
                        dateTimePickerDebut.setDate(c1);
                      //  txtDebut.setText(sdf.format(c1.getTime()));
                        dateTimePickerDebut.showDialog();
                        break;

                    case R.id.btnFin:
                        dateTimePickerFin = new CustomDateTimePicker(AlertActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {


                                //                        ((TextInputEditText) findViewById(R.id.edtEventDateTime))
                                txtFin.setText("");
                                //calendarSelected.set(Calendar.SECOND,0);
                                //calendarSelected.set(Calendar.MILLISECOND,0);
             /*   txtFin.setText(year
                        + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)
                        + " " + hour24 + ":" + min
                        + ":" + sec);*/


                                Log.i("millis",calendarSelected.getTimeInMillis()+"");
                                txtFin.setText(sdf.format(calendarSelected.getTime()));
                                endDateTimeGMT = "/Date(" + calendarSelected.getTimeInMillis() +")/";
                                c2.set(year,monthNumber,date,hour24,min,sec);
                                if(c1.getTimeInMillis() > c2.getTimeInMillis()){
                                    btnGenererAlerts.setVisibility(View.GONE);
                                    txtDateWaring.setVisibility(View.VISIBLE);
                                }
                                else {
                                    btnGenererAlerts.setVisibility(View.VISIBLE);
                                    txtDateWaring.setVisibility(View.GONE);
                                }

             /*   if(!txtDebut.getText().toString().isEmpty() && !txtFin.getText().toString().isEmpty())
                    btnGenererAlerts.setVisibility(View.VISIBLE);
                else
                    btnGenererAlerts.setVisibility(View.GONE);
                    */
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



        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */

        //dateTimePickerDebut.setDate(c.getTime());


       // Toast.makeText(getApplicationContext(),c.getTime().toString()+"",Toast.LENGTH_SHORT).show();
        /**
         * Pass Directly current data and time to show when it pop up
         */

       /* btnDebut.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dateTimePickerDebut.showDialog();
                    }
                });*/



       /* btnFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePickerFin.showDialog();
            }
        });*/


       /*****/








        btnGenererAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skeletonScreen = Skeleton.bind(recyclerView)
                        .adapter(mAdapter)
                        .shimmer(true)
                        .angle(20)
                        .frozen(false)
                        .duration(1200)
                        .count(10)
                        .load(R.layout.alerte_row_skeleton)
                        .show();
                data = new JSONObject();
                try {
                    data.put("commonAlert",-2);
                    data.put("keyTrackingObject", intent.getStringExtra("Key"));
                    data.put("startDateTimeGMT", startDateTimeGMT);
                    data.put("endDateTimeGMT", endDateTimeGMT);
                    GetAlertsCompressed();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void GetAlertsCompressed() {
       // loadingDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url_GetAlertsElementCompressed, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        skeletonScreen.hide();
                       // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        Log.d("response", response.toString());

                        try {
                            String res = response.getString("GetAlertsElementCompressedResult");
                            String resEscape = res.replace("\\", "\\\\").toString();
                            byte [] gZipBuffer ;
                            gZipBuffer = Base64.decode(resEscape,0);
                            String resDecompressed = Ineed.decompress(gZipBuffer);
                            skeletonScreen.hide();
                            alerteList.clear();
                            Log.d("dod",resDecompressed);
                            JSONArray jsonArray = new JSONArray(resDecompressed);
                            if(jsonArray.length() ==0)
                                txtEmpty.setVisibility(View.VISIBLE);
                            else
                                txtEmpty.setVisibility(View.GONE);
                           // requestData.put("compressedData", response.getString("GetAlertsElementCompressedResult"));
                               // Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    //   Toast.makeText(getApplicationContext(), "aaaa", Toast.LENGTH_SHORT).show();
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Alerte a = new Alerte(jsonObject.getString("AcquisitionTimeLocalString"),jsonObject.getString("AlertTypeFrenchString"),
                                            jsonObject.getString("NearestPlaceString"),jsonObject.getString("Speed"));
                                    //Toast.makeText(getApplicationContext(),v.getFullName()+"matricule",Toast.LENGTH_SHORT).show();

                                    //   if(v == null){
                                    alerteList.add(a);




                                    // Toast.makeText(getApplicationContext(),mAdapter.getItemCount()+"wwww",Toast.LENGTH_SHORT).show();




                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"erreur!",Toast.LENGTH_SHORT).show();
                                }

                                mAdapter.notifyDataSetChanged();
                                //Toast.makeText(getApplicationContext(),mAdapter.getItemCount()+"sizee",Toast.LENGTH_SHORT).show();
                                // Toast.makeText(getApplicationContext(),locationsVolley.get(0).getFullName(),Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                       // getDecompressedData();
                        //loadingDialog
                       // loadingDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loadingDialog.hide();
                       // Toast.makeText(getApplicationContext(), "verifier votre connexion internet et réessayer ", Toast.LENGTH_SHORT).show();
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
        request.setShouldCache(false);
        //queue = Volley.newRequestQueue(getApplicationContext());
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 5, 5));
        queue.add(request);
    }


    private void getDecompressedData() {

// Your code
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.POST, url_compressedData, requestData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                skeletonScreen.hide();
              //Toast.makeText(getApplicationContext(),response.length()+"",Toast.LENGTH_SHORT).show();
                Log.d("ooo",response.toString());

                //Toast.makeText(getApplicationContext(),response.length()+"length123",Toast.LENGTH_SHORT).show();
                alerteList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        //   Toast.makeText(getApplicationContext(), "aaaa", Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = response.getJSONObject(i);;
                        Alerte a = new Alerte(jsonObject.getString("AcquisitionTimeLocalString"),jsonObject.getString("AlertTypeFrenchString"),
                                jsonObject.getString("NearestPlaceString"),jsonObject.getString("Speed"));
                        //Toast.makeText(getApplicationContext(),v.getFullName()+"matricule",Toast.LENGTH_SHORT).show();

                        //   if(v == null){
                        alerteList.add(a);




                        // Toast.makeText(getApplicationContext(),mAdapter.getItemCount()+"wwww",Toast.LENGTH_SHORT).show();




                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"erreur!",Toast.LENGTH_SHORT).show();
                    }

                    mAdapter.notifyDataSetChanged();
                    //Toast.makeText(getApplicationContext(),mAdapter.getItemCount()+"sizee",Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getApplicationContext(),locationsVolley.get(0).getFullName(),Toast.LENGTH_SHORT).show();
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

    public  void expand(final View v) {
        visible = 1;
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

    public  void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        visible = 0;
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

}
