package com.example.missterio.stage;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.missterio.stage.Services.MySingleton;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class LoginActivity extends AppCompatActivity {

        //intent
        Intent intent;
        //loading dialog
        Dialog loadingDialog;
        ProgressBar progressBar;

        Button login;
        public static String url_Authentication ="http://www.webtracemobile.com/webmobilesvc/Service/TrackingPageWCFService.svc/Authenticate";

        EditText username,password;
        RequestQueue queue;
        public  JSONObject data;
//        final

    public static final String PREFS_USER = "prefs_user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        //To mainActivity
        intent = new Intent(this,MainActivity.class);
        //loadingDialog
        loadingDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        loadingDialog.setContentView(R.layout.progresspopup);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //Queue
        queue = MySingleton.getInstance(getApplicationContext()).
                        getRequestQueue();


        //test
        String compressedText = "H4sIAAAAAAAEAOy9B2AcSZYlJi9tynt\\/SvVK1+B0oQiAYBMk2JBAEOzBiM3mkuwdaUcjKasqgcplVmVdZhZAzO2dvPfee++999577733ujudTif33\\/8\\/XGZkAWz2zkrayZ4hgKrIHz9+fB8\\/Ir73iz86nk7zMq+ztqiWL6umwM+PHu2Mol+8butiefHRo492PkKDX7Qu5PM3xSJ\\/Xk2zkr76fe8+zdp8a\\/f+vXsPP7334OHuzs7OJzv0753f9+7AWxbs7u72zsH23s7uQbrz6aOde492d\\/FKUZ9Uyxm\\/w+3Os7LJ6XNCr31zvcqPS+qYetj1PntW58vp3EImAG02bdMvlwD4rsy\\/mxcX8\\/ajR9v39h58ejD66Emdvc1f5jOMQeGf5Mu2zkpC8a37rFy303mnXU0jytFDXblPn+bNtC5WQs+w\\/6dFnU8toZ9WVd241+riMq\\/xWeej13nWTvKytR+fLi+KZf4mX6wwS+s6d4PpfWXpcLac\\/aN\\/9XmxLAiNZzUh9ay6eC6UULDP1nn5PL\\/MSweOP6qubJPPX77utPj89RedT76dLWcT0NS+9e3sB1k9Czv7Nv31JM8WbvzfrqqZ\\/ePsYkkEe9TWa\\/rjObFiu54RwHv74wcHuw8P7t2jTysaq3y8uzN+cH9v\\/9NPPx199EVR5tlFzgR+kWd13rQvy2zqCHFvdPDgYTrL0xVNWnVRTNPX59k7osqXyy+qS4f1y6xp8uVFZ0pezqu2ep3XNC8vs3ZO8OjNVy+\\/4P7oZyAor6j\\/Hpnx4fP8vA3A4sNXaNb5dJoTB8y+yJuGx8SdEbnrJv+c3nAN15CPsJ\\/XqzyfAZH07eL3vTunN6McE+MV+Q5v1Nn0LX325eSniW9fZAvgsLuzfy9981W6u\\/+Am6yXTkre5tc0HbsH93cP9j7defhLRj\\/bemb\\/wd79r61ndg8e7d+\\/tZ7Zu52eOT\\/\\/OVY0jMCPNM37aRr9K1Q1Dx+STrn\\/UJ77PZVDDE7PLVTO\\/dGuqJxps1r8SNN8s5pmb2eftMAPQdMc3D\\/42ppmb+fRfQzzdprmRx7N\\/2\\/1TBvxaG5SM7s7u7fybH6kZn5W1QxJ\\/\\/2fdTXzYGd39+s6NA8e7R88uvcjh+ZHiibq0NwfH+x+utvTL5\\/u7X6quueW7swXWdFUy\\/RVVq3P06cFwaKZ\\/ZG6+UbVzb39B\\/fvf\\/pDUDcHe\\/e+pro5eLSz82jv3q3Vzf83vZp7yDX8SN28t18j2iaSqYG+YaL+SM\\/8v0HPPHhw74eQpyE983Dv6+uZ3Uf39m6tZ37k1vz\\/WM\\/oXz9SNP9fVDS7Oz\\/7imaXpvzrK5qDR\\/f\\/\\/+7Q\\/EjN3ELNtHF3xoVJ9yPa5v7Bj7TN\\/1u0DSVrHv7sJ4Uf7O4fPPza2mZ399Hew1trm2\\/UrflZVDc\\/cmu+hr7Rv27p1phc8Y\\/yNf8vUjj7pHN+1hXOwQPq7OspnN29R\\/d2f7QK9SN183Xdm\\/0fuTf\\/b9E2Dx5Sevjez762ebh77+suRu0+2rn\\/o8WoH+mbQffm\\/qcPPt2w7P3p7t6D2ymc\\/RHpLix7v\\/7yiy9PTl\\/9SNl8s8rm4B4tSv\\/sK5uHhMvXTRFTIPXg0e7tU8Q\\/cm3+f6tq2r5rcwtNc7s8MTTNwY80zc+epjnYOdj\\/2V\\/0fnjv4f7O19Q0e4\\/27j\\/a545upWl+5Nb8\\/1jX6F+estkfH+zc87IzPWXz4NPde7d1az598ADKZtqsFj\\/SNN+spnl4\\/+DTh3s\\/+5pmf2f\\/wdfXNA\\/of7fWND\\/yaf5\\/q2fark\\/zIzXz\\/xE18+nuzg8hT\\/Nw\\/\\/7e1w2dKCt8\\/9He7UOnHzk0\\/z9WNPpXoGkeHGAVam9Q09zfu7\\/76ae30DT3Rgef7kPTrGj6qotimr4+z979SOV8wyrnwd7Og92fZZWzv7O3Ty7U11M5tOwNrfOjhaifQ4Xz\\/xaF0\\/Y9G+ibqJa5pYr59Ecq5mdXxXz66d4erQr97KsY6uTTr69i7j3a+\\/TWKuZHXs3PP6\\/mIQdIPT2z\\/+ltvZkHD38eqZrdnztV87PvzZArc++98jR7LoA6eLR779H9H+VpfqRo4t4McQkUygfomYO9n0d65ufEpXmwR9O0t\\/9D0DO7+7tfX8\\/QytPurfXMj1ya\\/x9rGv2r59J8Glc1t0sFw6V58CNV87Ouavb3H\\/5s54T3dz59sLv\\/Xjlhp2p2dx7tk6q5fU74Ry7N\\/28VTRtP0MRDp\\/u3UzKf\\/nyKm35ulMz93f1P9w5+CErm4f2v6c+Qkrn\\/kHC9tZL5kT\\/z\\/2M1o391lrgf7N+L6ZlP73tL37dQOfujvYcHP1ri\\/lnSNPc\\/3f0hrDc92Nl7+DUzNJQMvkfB048yND\\/SMzF3Bmpm36qTSPj06f0Du\\/x9W3Xz6Y\\/Uzc+Sujk4+PTez3709CN18yN18\\/9ndfMjdXNbdUMm4cEPQd3cu7fzIermPnd0K3Xzozjq\\/8cKR\\/\\/qOjgPonnhT\\/fu3SovTA7Np\\/d+5ND87GmYB\\/d\\/CBrm06+78kQaZn\\/\\/0c7tMzU\\/cmj+f6tf2mj8NKRedm+1wv0j9fKzqV4e7j948LOfCNbFpq+pXu4\\/eLTzIwfmRwpm0IHZjSaC94l9buXA3B\\/t3b\\/\\/Iw3zs6NhKP374IfhwOxST19Tw+w92tl7tP8jB+ZH+iXuwPxIvfy\\/WL3s3d\\/d3fnZVy97lOT\\/2uplb+fRvb1bq5cfOTD\\/P1Yw+legYR7cv\\/dw0xLTzsNPOYK6UdPsjz7d\\/RSa5uTbx98+O\\/7\\/r67Z\\/bnRNaQJHn76w9A1B59+fV2z+2jv01vrmh+5Mv+\\/1TRt35X5kaJ5X0Xzc+TU3N9\\/ePBDiJnu3bt\\/8PUVzcNHuxjm7RTNj5ya\\/x+rGv0r1DX37n\\/KCd5Qx3x6cO\\/T+7fQMPdH+z9SMD97CubTB+RhDCuYb0jBHOzee\\/A1077IyOy9z7r1jzyZ\\/9+qlzbiydy7f\\/+gn5SBdtn99L7xcG6pZ\\/Z+pGh+thTNwQNaVt772Vc0ezTzX1PR3Hu0s\\/9o\\/96tFc2PPJn\\/H6sa\\/SvUNQe7D2IJ4Pt7+7dLAN8bHdx\\/AB2zommrLopp+vo8e\\/cjVfMNqxqKaO\\/9EHyavYP9r5kJhqp58Gjv9pngH\\/k0\\/79VNG3EpxnUM\\/dvqWQ+\\/ZGS+VlWMg937u0c3PvZVzL39u99zcwMKZndBz8fMjN7D+7\\/SNHcrGj0r86SNumKfm6GNM3D94ue9kd7Oz8PlrZ\\/bpabaC729n4Yuubhw92vrWv2Pn10b\\/fWuuZHDs3\\/b\\/VM23dooGYO7u2pOrkfUzf37Ne3VTf3\\/v+vbn6OXBsyqDv7P\\/vqhqKnr69u7u082rm9uvn\\/qmvzI4VzC4Wjf3U0DuUbrQcTcXA+9RTS7TTOj\\/TNz5a+ebD36ac\\/+4vcB8QNXz81fP\\/ho\\/u3Tw3\\/yL35\\/622aWPuzYP9aBTlQqjbKpm9h\\/s\\/UjM\\/i2rm0599NfPpwc6nX1PN7D\\/aRSB1azXzs+\\/WfDN6ppex2T\\/4kaq5WdXoX5317nsPP7V65X5P53y6K6roRl2zM9p9yOvd367avExPqrJo\\/tG\\/Ov+R1vmGtc7Bw3u7P\\/uLUQ93dikl\\/fW0zv1HOw8f3Xt4a63zI+fm\\/7cap40sRpHCiS1GkaK5fzs1c\\/DwR2rmZ1nNUILt0wcHPww183D36+Zs7j\\/a2310\\/0c5mx8pmkHX5v6nu\\/0w6tODe5+yArpR19wf7X+6C11z8u3jb58d\\/0jH\\/CzomN2ffR2zS5m7r61jKC+8e\\/sA6keuzP9vNUwbc2V+pGD+361gDj69\\/+BnX8HsPfz0\\/tdWMPc\\/fbRz\\/9YK5kdOzP+PVYz+1c0F34vngvffNxd8b+fTH+WCf3Y0ze7e\\/sNP7\\/3sa5oH93cPvqameUCIPrqPYd5O0\\/zIlfn\\/rZ5po0tO9w4iWRlactq9VfoX6mX\\/R+rlZ0e97NGzs\\/9DUC80219fvdBq0\\/\\/\\/szG7+\\/s\\/UjE3qxj9q6NjHt6PZX7v7+\\/dVsfsHPw80DG7P1dK5gEFTD\\/7SgYL2l9bydzbJc1xayXzIx\\/m\\/20K5htTMG3Mh\\/mRfrmNfvm5US\\/3KF7d\\/WGol4OH+19fvew\\/ur9\\/a\\/Xy\\/1Uf5kcK5hYKRv\\/qRkn7UQ3z6f2De3vvlYzZe3jwI03zs6hpHv5sa5rdnXuExdfUNAeP9u8\\/4jHcTtP8yJH5\\/62eaaPJGFqyvK\\/qJJb7fW91c+9HyuZnT9ns7fwQlA3N99dXNgePdh\\/eWtn8yK35\\/7G60b86+ma3r2Q+3d29bdT0cP9H6uVnS73c393f+1lfwiZUdg++btT08NHOvUc7t4+afuTL\\/P9WubQxX2b3fmxh6dPdnfddv364\\/6PkzM+WmnlwsHfvh6Bm7t\\/f+fpqZu\\/++6iZH3kx\\/z9WNPpXoGke3Lv\\/KXssoab59ODep5yzuVHD3B\\/tf7oLDXPy7eNvnx3\\/SMd8wzrm4cN7u3u\\/5Pv\\/TwAAAP\\/\\/4BMb2gPYAAA=";
        byte [] gZipBuffer ;
        gZipBuffer = Base64.decode(compressedText,0);
      /*  byte[] buffer;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(gZipBuffer);

        try {
            GZIPInputStream gzipStream = new GZIPInputStream(inputStream);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            IOUtils.copyStream(gzipStream, outStream);
            buffer = outStream.toByteArray();
            Log.i("size",buffer.length+"");
            Toast.makeText(getApplicationContext(),new String(buffer, "UTF-8")+"",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),e.getMessage()+"",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }*/
       /* String res;
        try {
            res = decompress(gZipBuffer);
            Toast.makeText(getApplicationContext(),res,Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),res.length()+"ww",Toast.LENGTH_SHORT).show();
            Log.i("nizar",res.length()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //component
        login = findViewById(R.id.btnLogin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        //SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_USER, MODE_PRIVATE);
        String userPrefsUserName = preferences.getString("Username", null);
        String userPrefsPassword = preferences.getString("Password",null);
        //remember username
        if (userPrefsUserName != null) {
            username.setText(userPrefsUserName);
        }
        if(userPrefsPassword != null){
            password.setText(userPrefsPassword);
        }

        if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){

            data = new JSONObject();
            try {
                data.put("AppReselerNameUsedByClient", "Tnv");
                data.put("AppTypeWebOrWin", "WebMob");
                data.put("ClientCountry", "");
                data.put("ClientIPAddress", "");
                data.put("CultureInfoName", "fr-FR");
                data.put("Login", username.getText());
                data.put("Password", password.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            login();
        }

        //Se connecter
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Verify empty fields
                if (isEmptyField(username)) return;
                if (isEmptyField(password)) return;
                //data
                data = new JSONObject();
                try {
                    data.put("AppReselerNameUsedByClient", "Tnv");
                    data.put("AppTypeWebOrWin", "WebMob");
                    data.put("ClientCountry", "");
                    data.put("ClientIPAddress", "");
                    data.put("CultureInfoName", "fr-FR");
                    data.put("Login", username.getText());
                    data.put("Password", password.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //call function
                login();
              //
            }
        });
    }

    private boolean isEmptyField (EditText editText){
        boolean result = editText.getText().toString().length() <= 0;
        if (result)
            Toast.makeText(getApplicationContext(), "Compléter tous les champs!", Toast.LENGTH_SHORT).show();
        return result;
    }

    private void login(){
        loadingDialog.show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url_Authentication,data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                   //
                        // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                      // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        Log.d("response",response.toString());
                        try {
                            JSONObject AuthenticateResult = response.getJSONObject("AuthenticateResult");
                            if(AuthenticateResult.getBoolean("IsIncorrectLoginPass")== true){
                                Toast.makeText(getApplicationContext(),AuthenticateResult.getString("MessageToShowToUser"),Toast.LENGTH_SHORT).show();
                                password.getText().clear();
                            }
                            else
                            {
                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_USER, MODE_PRIVATE).edit();
                                editor.putString("CustomerName",AuthenticateResult.getString("CustomerName"));
                                editor.putString("Username",(username.getText().toString()));
                                if(!password.getText().toString().isEmpty())
                                    editor.putString("Password",password.getText().toString());
                                if(!AuthenticateResult.getString("CustomerName").isEmpty())
                                    editor.putString("SessionId",AuthenticateResult.getString("SessionId"));
                                editor.putString("AppVersion",AuthenticateResult.getString("AppVersion"));
                                editor.commit();
                                startActivity(intent);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //loadingDialog
                        loadingDialog.hide();
                       // onDestroy();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.hide();
                        Toast.makeText(getApplicationContext(),"Vérifier votre connexion internet et réessayer ",Toast.LENGTH_SHORT).show();
                        Log.d("wtf",error.getMessage()+"");
                    }
                }
        ){
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences(PREFS_USER, MODE_PRIVATE);
                String userPrefs = preferences.getString("SessionId", null);
                Map<String, String> param = new HashMap<String, String>();
                param.put("Cookie","ASP.NET_SessionId="+userPrefs);
                return param;
            }*/
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        request.setShouldCache(false);
      //queue = Volley.newRequestQueue(getApplicationContext());
       request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        queue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        int i = intent.getIntExtra("FLAG", 0);

        if(i == 0){
            SharedPreferences preferences = getSharedPreferences(PREFS_USER, MODE_PRIVATE);
            preferences.edit().clear().commit();
            password.getText().clear();

        }
        else if( i == 400){
            login();
        }
    }
}
