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
import com.example.missterio.stage.Services.Ineed;
import com.example.missterio.stage.Services.MySingleton;
import com.example.missterio.stage.adapters.AlertAdapter;
import com.example.missterio.stage.adapters.AllAlertsAdapter;
import com.example.missterio.stage.models.Alerte;

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

public class AllalertsActivity extends AppCompatActivity {

    static long MaximumDate=0,MinimunDate=0;
    EditText txtDebut,txtFin;
    Button btnDebut,btnFin,btnGenererAlerts;
    TextView txtMatriculeAlerts,txtDateWaring,txtEmpty;
    //public Intent intent;
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
    private AllAlertsAdapter mAdapter;
    public SkeletonScreen skeletonScreen;
    public static String url_GetAlertsElementCompressed ="http://www.webtracemobile.com/webmobilesvc/Service/TrackingPageWCFService.svc/GetAlertsElementCompressed";
    public  int visible = 1;
    public SearchView alertSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allalerts);
        getSupportActionBar().setTitle("Alertes");
        queue = MySingleton.getInstance(getApplicationContext()).
                getRequestQueue();
        recyclerView = findViewById(R.id.alertRecylerViewALLAlerts);
        mAdapter = new AllAlertsAdapter(getApplicationContext(),alerteList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        alertSearchView = findViewById(R.id.alertSearchViewALLAlerts);
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

                        }
                        else if (mAdapter.getItemCount() >0)
                        { txtEmpty.setVisibility(View.GONE);

                        }
                    }
                }, 300);

                return true;
            }

        });

        final LinearLayout linearLayout ;
        linearLayout = findViewById(R.id.linearLayoutALLAlerts);
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

                y=dy;
            }
        });
        requestData = new JSONObject();
        startDateTimeGMT="";
        endDateTimeGMT="";
        txtDebut = findViewById(R.id.txtDebutALLAlerts);
        txtFin = findViewById(R.id.txtFinALLAlerts);
        txtDebut.setKeyListener(null);
        txtFin.setKeyListener(null);
        btnDebut = findViewById(R.id.btnDebutALLAlerts);
        btnFin = findViewById(R.id.btnFinALLAlerts);
        btnFin.setEnabled(false);
        txtDateWaring = findViewById(R.id.txtDateWarningALLAlerts);
        btnGenererAlerts = findViewById(R.id.btnGenererAlertsALLAlerts);
        txtMatriculeAlerts = findViewById(R.id.txtMatriculeALLAlerts);
        txtEmpty = findViewById(R.id.txtEmptyALLAlerts);
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
        //intent = getIntent();
        txtMatriculeAlerts.setText("Tous les véhicules");

        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View vv = view;

                switch (vv.getId()) {
                    case R.id.btnDebutALLAlerts:
                        CustomDateTimePicker dateTimePickerDebut = new CustomDateTimePicker(AllalertsActivity.this,
                                new CustomDateTimePicker.ICustomDateTimeListener() {


                                    @Override
                                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                                      Date dateSelected, int year, String monthFullName,
                                                      String monthShortName, int monthNumber, int date,
                                                      String weekDayFullName, String weekDayShortName,
                                                      int hour24, int hour12, int min, int sec,
                                                      String AM_PM) {

                                        txtDebut.setText("");
                                        Calendar upDateFrom = Calendar.getInstance();
                                        upDateFrom.set(year, monthNumber, date);
                                        txtDebut.setText(sdf.format(calendarSelected.getTime()));
                                        MinimunDate=dateSelected.getTime()+86400000;
                                        MaximumDate=dateSelected.getTime()+2628002880L;
                                        Log.i("millis",calendarSelected.getTimeInMillis()+"getTime"+dateSelected.getTime());
                                        startDateTimeGMT = "/Date(" + calendarSelected.getTimeInMillis() +")/";
                                        btnFin.setEnabled(true);
                                        c1.set(year,monthNumber,date,hour24,min,sec);
                                        if(c1.getTimeInMillis() > c2.getTimeInMillis()){
                                            btnGenererAlerts.setVisibility(View.GONE);
                                            txtDateWaring.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            btnGenererAlerts.setVisibility(View.VISIBLE);
                                            txtDateWaring.setVisibility(View.GONE);
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

                    case R.id.btnFinALLAlerts:
                        dateTimePickerFin = new CustomDateTimePicker(AllalertsActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                                txtFin.setText("");
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
                    data.put("keyTrackingObject", -1);
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
                        Log.d("response", response.toString());
                        //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
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


                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    //   Toast.makeText(getApplicationContext(), "aaaa", Toast.LENGTH_SHORT).show();
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Alerte a = new Alerte(jsonObject.getString("AcquisitionTimeLocalString"),jsonObject.getString("AlertTypeFrenchString"),
                                            jsonObject.getString("NearestPlaceString"),jsonObject.getString("Speed"),jsonObject.getString("TrackingObjectName"));
                                    alerteList.add(a);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"erreur!",Toast.LENGTH_SHORT).show();
                                }

                                mAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
