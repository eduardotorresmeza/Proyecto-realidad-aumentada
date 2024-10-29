package com.ossian.navar;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//Google Material Design
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

//Servicios de Locacion de Google
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;
import com.mapbox.mapboxsdk.geometry.LatLng;


public class IntroActivity extends AppCompatActivity {

    //Variables iniciales de la actividad
    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;

    //Botones de locacion del dispositivo
    Button btnLocationON;
    Button btnLocationOff;

    //Componentes de locacion
    Dialog myDialog;
    LatLng LocationReal;
    boolean FalseLocationSignal;
    boolean TrueLocationSignal;
    private static final int REQCODELOCATION = 1;
    private ProgressBar progressBar;
    String LongitudeTruePref;
    String LatitudeTruePref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  empezar la actividad en pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // verificar si la actividad ya se abrio una primera vez o no
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(mainActivity);
            finish();
        }
        setContentView(R.layout.activity_intro);

        // ocultar la barra de accion
        getSupportActionBar().hide();

        // popup -> ventana de dialogo
        myDialog = new Dialog(this);

        // iniciar -> Views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);
        btnLocationON = findViewById(R.id.btn_location);
        btnLocationOff = findViewById(R.id.btn_location_off);
        progressBar = findViewById(R.id.progressBar);

        // datos para llenar la lista del ViewPager
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Explore", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit", R.drawable.img1));
        mList.add(new ScreenItem("Location", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit", R.drawable.img2));
        mList.add(new ScreenItem("Let us begin", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit", R.drawable.img3));

        // configurar ViewPager
        screenPager = findViewById(R.id.screen_viewpager);
        screenPager.beginFakeDrag();
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // configurar tablayout con viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // Click oyente del boton Next //***
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size() - 1) {
                    // en caso de que se llegue a la ultima pantalla cargar el metodo:
                    loaddLastScreenEnd();
                }
            }
        });

        // metodos que se cargan cuando se encuentran en una determinada posision de tablayout
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {
                    loaddLastScreenEnd();
                }
                if (tab.getPosition() == mList.size() - 2) {
                    loaddLastScreenLocation();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Click oyente del boton LocationOff //***
        btnLocationOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btnCancel;
                Button btnfalselocation;

                myDialog.setContentView(R.layout.popuplocationoff);
                btnfalselocation = myDialog.findViewById(R.id.btnfalseloc);
                btnCancel = myDialog.findViewById(R.id.btncancelpopup);

                btnfalselocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TrueLocationSignal = false;//*Desactivar TrueLocation para SharedPreferens*//
                        FalseLocationSignal = true;
                        btnNext.setEnabled(true);
                        myDialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.cancel();
                    }
                });
                myDialog.show();
            }
        });

        // Click oyente del boton LocationON //***
        btnLocationON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // verificar si el dispositivo tiene los permisos de locacion
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQCODELOCATION
                    );
                } else {
                    // caso contrario: obtener la ubicacion actual
                    getCurrentLocation();
                }

            }
        });

        // Click oyente del boton GetStarted //***
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // abrir la actividad -> MapActivity
                Intent introActivity = new Intent(getApplicationContext(), MapActivity.class);

                // si la locacion tiene un valor null guardar estado de la variable de "FalseLocationSignal"
                if (LocationReal == null) {
                    SharedPreferences prefst = getApplicationContext().getSharedPreferences("PrefsLocationFalse", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefst.edit();
                    editor.putBoolean("LocationFalsePref", FalseLocationSignal);
                    editor.apply(); // sincronizacion de forma asincrona
                    //editor.commit(); // sincronizacion de forma sincrona devolviendo en valor booleano si o si

                    // TODO :  en caso de requerir variables estaticas para MapActivity:
                    //  introActivity.putExtra("falselocation",FalseLocationSignal);

                }
                // caso contrario guardar estado de la variable "TrueLocationSignal"
                else {
                    SharedPreferences prefsf = getApplicationContext().getSharedPreferences("PrefsLocationTrue", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefsf.edit();
                    editor.putBoolean("LocationTruePref", TrueLocationSignal);
                    editor.putString("LatitudePref",LatitudeTruePref);
                    editor.putString("LongitudePref",LongitudeTruePref);
                    editor.apply();

                    // TODO :  en caso de requerir una variable de locacion estatica para MapActivity:
                    //  introActivity.putExtra("truelocation",TrueLocationSignal);
                    //  introActivity.putExtra("reallocation", LocationReal);
                }

                startActivity(introActivity);
                savePrefsData();
                finish();

            }
        });

    }

    // metodo de respuesta para los servicios de locacion en caso de denegar la locacion
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Permision Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // metodo para obtener la locacion actual
    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(IntroActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(IntroActivity.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double LatOrg = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double LonOrg = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                    LatitudeTruePref = Double.toString(LatOrg);
                    LongitudeTruePref = Double.toString(LonOrg);

                    LocationReal = new LatLng(LatOrg, LonOrg);
                    tvSkip.setText(String.format("Lat: %s\nLon: %s", LatitudeTruePref, LongitudeTruePref));
                    //tvSkip.setText(String.format("Lat: %s\nLon: %s", LatOrg, LonOrg));
                }
                FalseLocationSignal = false;//*Desactivar FalseLocation para SharedPreferens*//
                TrueLocationSignal = true;
                progressBar.setVisibility(View.GONE);
                btnNext.setEnabled(true);
            }
        }, Looper.getMainLooper());

    }

    // metodo de restauracion: cargar preferencias de la actividad al intertar abrirse otra vez
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;
    }

    // metodo de guardado: preferencias guardadas una vez que termina el ciclo de la actividad
    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();
    }

    // mostrar el boton -> comenzar y ocultar los demas elementos
    // ultima pestaña del view pager
    private void loaddLastScreenEnd() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnLocationOff.setVisibility(View.INVISIBLE);
        btnLocationON.setVisibility(View.INVISIBLE);
        // TODO : añadir la animacion a los boton de getstarted
        btnGetStarted.setAnimation(btnAnim);
    }

    // mostrar el boton -> locacionon y locacionoff  y ocultar los demas elementos
    // penultima pestaña del view pager
    private void loaddLastScreenLocation() {
        btnGetStarted.setVisibility(View.INVISIBLE);
        tvSkip.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);
        btnLocationOff.setVisibility(View.VISIBLE);
        btnLocationON.setVisibility(View.VISIBLE);
        btnNext.setEnabled(false);
        // TODO : añadir animacion a los siguientes botones
        btnLocationOff.setAnimation(btnAnim);
        btnLocationON.setAnimation(btnAnim);

    }
}