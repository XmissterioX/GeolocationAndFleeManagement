package com.example.missterio.stage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.missterio.stage.Services.CustomJsonArrayRequest;
import com.example.missterio.stage.Services.Ineed;
import com.example.missterio.stage.Services.MySingleton;
import com.example.missterio.stage.Services.SwipeHelper;
import com.example.missterio.stage.adapters.VehicleAdapter;
import com.example.missterio.stage.models.Vehicule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.missterio.stage.LoginActivity.PREFS_USER;
import static com.example.missterio.stage.MapsActivity.url_compressedData;

public class VehiculeActivity extends AppCompatActivity {

    private List<Vehicule> vehiculeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private VehicleAdapter mAdapter;
    private SwipeHelper swipeHelper;
    //queue
    public RequestQueue queue;
    public SkeletonScreen skeletonScreen;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public JSONObject requestData;
    public SearchView vehiculeSearchView;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicule);
        getSupportActionBar().setTitle("Véhicules");
        queue = MySingleton.getInstance(getApplicationContext()).
                getRequestQueue();
        requestData = new JSONObject();

        recyclerView = findViewById(R.id.vehicleRecylerView);
        mAdapter = new VehicleAdapter(getApplicationContext(),vehiculeList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(mAdapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.vehicule_row_skeleton)
                .show();
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutVehicules);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCompressedData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        vehiculeSearchView = findViewById(R.id.vehiculeSearchView);
        vehiculeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                //swipeHelper.attachSwipe();
                return false;
            }
        });

        recyclerView.setEnabled(false);



         swipeHelper = new SwipeHelper(this, recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Evénements",
                        0,
                        Color.parseColor("#237fc5"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onAlertes
                               // Toast.makeText(getApplicationContext(),mAdapter.getVehiculesListFiltered().get(pos).getKey(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), AlertActivity.class);

                                intent.putExtra("Key", mAdapter.getVehiculesListFiltered().get(pos).getKey());
                                intent.putExtra("Matricule",mAdapter.getVehiculesListFiltered().get(pos).getFullName());
                                startActivity(intent);
                                handler.removeCallbacksAndMessages(null);
                            }
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Trajectoire",
                         0,
                        Color.parseColor("#959699"),

                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnTrajectoire
                                //Toast.makeText(getApplicationContext(),mAdapter.getVehiculesListFiltered().get(pos).getKey(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), TrajectoryActivity.class);

                                intent.putExtra("Key", mAdapter.getVehiculesListFiltered().get(pos).getKey());
                                intent.putExtra("Matricule",mAdapter.getVehiculesListFiltered().get(pos).getFullName());
                                startActivity(intent);
                                handler.removeCallbacksAndMessages(null);

                            }
                        }
                ));
              /*  underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Unshare",
                        0,
                        Color.parseColor("#C7C7CB"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnUnshare
                            }
                        }
                ));*/
            }
        };

        getCompressedData();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"rafraichissement",Toast.LENGTH_SHORT).show();
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
        // Toast.makeText(getApplicationContext(),"aaaaa",Toast.LENGTH_SHORT).show();
        JsonObjectRequest request = new JsonObjectRequest (Request.Method.POST, "http://www.webtracemobile.com/webmobilesvc/Service/TrackingPageWCFService.svc/" +
                "GetTrackingObjectWithUpdateRefreshCompressed",null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject  response) {
                        Log.d("last",response.toString());
                       // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        try {
                           // requestData.put("compressedData",response.getString("GetTrackingObjectWithUpdateRefreshCompressedResult"));
                            String res = response.getString("GetTrackingObjectWithUpdateRefreshCompressedResult");
                            String resEscape = res.replace("\\", "\\\\").toString();
                            byte [] gZipBuffer ;
                            gZipBuffer = Base64.decode(resEscape,0);
                            try {
                                String resDecompressed = Ineed.decompress(gZipBuffer);
                                Log.d("dod",resDecompressed);
                                skeletonScreen.hide();
                                vehiculeList.clear();
                                recyclerView.setEnabled(true);
                                JSONArray jsonArray = new JSONArray(resDecompressed);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Vehicule v = new Vehicule(jsonObject.getString("FullName"),jsonObject.getDouble("Speed"),jsonObject.getString("NearestPlace"),
                                            jsonObject.getString("AcquisitionTimeString"),jsonObject.getBoolean("Connected"),
                                            jsonObject.getString("DriverName"),jsonObject.getString("Key"));
                                    vehiculeList.add(v);

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //   Toast.makeText(getApplicationContext(),requestData.getString("compressedData"),Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                      //  getDecompressedData();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.d("last1", "error ! " +error.networkResponse.statusCode);
                        Log.d("last2", "error ! " +error.getMessage());
                        Log.d("last3", "error ! " +error.networkResponse.data.toString());
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
// Your code
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.POST, url_compressedData, requestData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                skeletonScreen.hide();
              /*  Toast.makeText(getApplicationContext(),response.length()+"",Toast.LENGTH_SHORT).show();
                Log.d("ooo",response.toString());*/
                //locationsVolley = new ArrayList<>();
                //Toast.makeText(getApplicationContext(),response.length()+"length123",Toast.LENGTH_SHORT).show();
                vehiculeList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                     //   Toast.makeText(getApplicationContext(), "aaaa", Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = response.getJSONObject(i);;
                        Vehicule v = new Vehicule(jsonObject.getString("FullName"),jsonObject.getDouble("Speed"),jsonObject.getString("NearestPlace"),
                                jsonObject.getString("AcquisitionTimeString"),jsonObject.getBoolean("Connected"),
                                jsonObject.getString("DriverName"),jsonObject.getString("Key"));
                                //Toast.makeText(getApplicationContext(),v.getFullName()+"matricule",Toast.LENGTH_SHORT).show();

                     //   if(v == null){
                            vehiculeList.add(v);




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
                Log.d("hwawi",error.getMessage()+"");

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        queue.add(req);


    }
}
