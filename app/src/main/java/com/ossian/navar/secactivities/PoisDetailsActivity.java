package com.ossian.navar.secactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.ossian.navar.R;
import com.squareup.picasso.Picasso;

public class PoisDetailsActivity extends AppCompatActivity {


    Button buttonback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pois_details);
        getSupportActionBar().hide();

        //Nombre POI (GET)
        String namepoi = getIntent().getExtras().getString("namepoicv");

        //Direccion POI (GET)
        String direccpoi = getIntent().getExtras().getString("direccv");

        //Distancia POI (GET)
        String distanciapoi = getIntent().getExtras().getString("distance");

        //Descripcion POI (GET)
        String description = getIntent().getExtras().getString("description");

        //Imagenes POI (GET)
        String imagepoi = getIntent().getExtras().getString("urlcv");
        String imageone = getIntent().getExtras().getString("imgone");
        String imagetwo = getIntent().getExtras().getString("imgtwo");
        String imagethree = getIntent().getExtras().getString("imgthree");

        //***Inicializacion de Layout con archivos cargados***
        //Nombre
        TextView poiname = findViewById(R.id.poititle);
        poiname.setText(namepoi);

        //Direccion
        TextView dirrpoi = findViewById(R.id.descripionpoi);
        dirrpoi.setText(direccpoi);

        //Distancia
        TextView distpoi = findViewById(R.id.distancepoi);
        distpoi.setText(distanciapoi);

        //Descripcion
        TextView poidesc = findViewById(R.id.textpoi);
        poidesc.setText(description);

        //Imagen
        ImageView poiimage = findViewById(R.id.imagepoidet);
        Picasso.get().load(imagepoi).fit().centerInside().into(poiimage);

        ImageView poiimgone = findViewById(R.id.imagepoi1);
        Picasso.get().load(imageone).fit().centerInside().into(poiimgone);

        ImageView poiimgtwo = findViewById(R.id.imagepoi2);
        Picasso.get().load(imagetwo).fit().centerInside().into(poiimgtwo);

        ImageView poiimgthree = findViewById(R.id.imagepoi3);
        Picasso.get().load(imagethree).fit().centerInside().into(poiimgthree);

        //Evento Button
        buttonback = (Button) findViewById(R.id.pushback);
        buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
