package com.example.missterio.stage;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import java.util.Base64;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private CardView LocaliserBtn,vehiculesBtn,alertesBtn,deconnexionBtn;
    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setTitle("");
        getSupportActionBar().hide();
        //Localiser
        LocaliserBtn = findViewById(R.id.LocaliserBtn);
        final Intent intent1 = new Intent(this, MapsActivity.class);
        /*LocaliserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getApplicationContext(),"Localiser pressed",Toast.LENGTH_SHORT).show();
            startActivity(intent1);
            }
        });*/
        //Vehicule list
        final Intent intent2 = new Intent(this,VehiculeActivity.class);
        vehiculesBtn = findViewById(R.id.vehiculesBtn);
        /*vehiculesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent2);
            }
        });*/
        //Alert
        final Intent intent3 = new Intent(this,AllalertsActivity.class);
        alertesBtn = findViewById(R.id.btnAlert);
        /*alertesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent3);
            }
        });*/

        deconnexionBtn = findViewById(R.id.btnDeconnexion);
        LocaliserBtn.setOnClickListener(this);
        vehiculesBtn.setOnClickListener(this);
        alertesBtn.setOnClickListener(this);
        deconnexionBtn.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.LocaliserBtn   : i=new Intent(this, MapsActivity.class);startActivity(i); break;
            case R.id.vehiculesBtn : i = new Intent(this,VehiculeActivity.class);startActivity(i);break;
            case R.id.btnAlert : i = new Intent(this,AllalertsActivity.class);startActivity(i);break;
            case R.id.btnDeconnexion : i = new Intent(this,LoginActivity.class);
            i.putExtra("FLAG", 0);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            break;
            default:break;
        }
    }
}
