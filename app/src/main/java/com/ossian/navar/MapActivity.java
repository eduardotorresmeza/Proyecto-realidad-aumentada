package com.ossian.navar;

//Default

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

//Google Material Design
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

//Biblioteca de clases del proyecto
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.ossian.navar.adapter.LocationRecyclerViewAdapter;
import com.ossian.navar.model.IndividualLocation;
import com.ossian.navar.util.LinearLayoutManagerWithSmoothScroller;

//Mapbox SDK
import com.mapbox.mapboxsdk.Mapbox;

//<--Mapbox Styles-->
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

//<--Mapbox GeoJson-->
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

//<--Mapbox Directions-->
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

//<--Mapbox Turf-->
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfConversion;

//<--Mapbox Utils-->
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

//// Clases necesarias para manejar permisos de ubicación
import com.mapbox.android.core.permissions.PermissionsManager;

//<--Clases necesarias para agregar el motor de ubicación-->

//Retrofit
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Java
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements LocationRecyclerViewAdapter.ClickListener, MapboxMap.OnMapClickListener {

    // TODO: en caso de requerir la locacion de MapBox: implements PermissionsListener

    // variables iniciales principales
    private MapboxMap mapboxMap;
    private MapView mapView;

    // variable para la limitacion de area
    private static final LatLngBounds LOCKED_MAP_CAMERA_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng(-13.431703351241168, -71.9863723587222))
            .include(new LatLng(-13.629763192294888, -71.86033934087786)).build();

    // variables de punto inicial de referencia de locacion
    public LatLng PointOrigin;
    // TODO: locacion estatica solo para MapActivity:
    //  private static final LatLng MOCK_DEVICE_LOCATION_LAT_LNG = new LatLng(-13.53819011061772,-71.94362303511991);

    // elementos del mapa de MapBox
    private static final int MAPBOX_LOGO_OPACITY = 75;
    private static final int CAMERA_MOVEMENT_SPEED_IN_MILSECS = 1200;
    private static final float NAVIGATION_LINE_WIDTH = 9;
    private static final float BUILDING_EXTRUSION_OPACITY = .8f;
    private static final String PROPERTY_SELECTED = "selected";
    private static final String BUILDING_EXTRUSION_COLOR = "#c4dbed";
    private DirectionsRoute currentRoute;
    private FeatureCollection featureCollection;
    // RecyclerView
    private RecyclerView locationsRecyclerView;
    private ArrayList<IndividualLocation> listOfIndividualLocations;
    private CustomThemeManager customThemeManager;
    private LocationRecyclerViewAdapter styleRvAdapter;
    private int chosenTheme;
    private String TAG = "MapActivity";
    // variables necesarias para manejar permisos de ubicación
    private PermissionsManager permissionsManager;
    // variables necesarias para agregar el motor de ubicación
    private LocationComponent locationComponent;
    public boolean LocationFalseStatic;
    public boolean LocationTrueStatic;
    public static LatLng LatLngIntro;
    public LatLng LatLngBasic;
    private static final int REQCODELOCATION = 1;


    // metodo inicial que contiene la vista de mapa.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // token de acceso de Mapbox
        Mapbox.getInstance(this, getString(R.string.access_token));

        // ocultar la barra de estado del mapa para llenar toda la pantalla
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // inflar el diseño con el MapView
        setContentView(R.layout.activity_map);

        // TODO: Cargar datos de la instancia guardada de IntroActivity
        TextView Indicador;
        Indicador = findViewById(R.id.tvrvsize);
        String LongTruePref, LatiTruePref;
        Double longpref, latipref;

        SharedPreferences prefst = getApplicationContext().getSharedPreferences("PrefsLocationTrue", MODE_PRIVATE);
        LocationTrueStatic = prefst.getBoolean("LocationTruePref", false);// false -> valor predeterminado
        LongTruePref = prefst.getString("LongitudePref", "");
        LatiTruePref = prefst.getString("LatitudePref", "");
        //longpref = Double.parseDouble(LongTruePref);
        //latipref = Double.parseDouble(LatiTruePref);

        SharedPreferences prefsf = getApplicationContext().getSharedPreferences("PrefsLocationTrue", MODE_PRIVATE);
        LocationFalseStatic = prefsf.getBoolean("LocationFalsePref", false);// false -> valor predeterminado

        // TODO: cargar variables estaticas desde IntroActivity
        //  LocationFalseStatic = getIntent().getExtras().getBoolean("falselocation");
        //  LocationTrueStatic = getIntent().getExtras().getBoolean("truelocation");
        //  LatLngIntro = getIntent().getExtras().getParcelable("reallocation");
        // FIXME: utilizar una funcion/metodo en caso de ser necesario


        //** variables de prueba para verificar datos/instancias **//
        // eliminar o desactivar  cuando ya no sean necesarias //

        String A;
        if (LocationTrueStatic == true) {
            A = "true";
            Indicador.setText(A);
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longpref = location.getLongitude();
            latipref = location.getLatitude();
            PointOrigin = new LatLng(latipref, longpref);

        } else if (LocationFalseStatic == true) {
            PointOrigin = new LatLng(-13.5163462, -71.9796072);
            A = "false";
            Indicador.setText(A);
        } else {
            PointOrigin = new LatLng(-13.5163462, -71.9796072);
            A = "def";
            Indicador.setText(A);
        }

        /*
        String A;
        if (LocationFalseStatic == true) {
            A = "false";
            Indicador.setText(A);
            PointOrigin = new LatLng(-13.5163462, -71.9796072);
        } else {
            if (LocationTrueStatic == true) {
                // obtener locacion actual
                PointOrigin = new LatLng(-13.5163462, -71.9796072);
                //PointOrigin = new LatLng(LatLngIntro.getLatitude(), LatLngIntro.getLongitude());
                A = "true";
                Indicador.setText(A);
            } else {
                PointOrigin = new LatLng(-13.5163462, -71.9796072);
                A = "def";
                Indicador.setText(A);
            }
        }
        */

        // menu de navegacion
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnavigationview);
        bottomNavigationView.setSelectedItemId(R.id.homeitem);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.homeitem:
                        return true;
                    case R.id.exploreitem:
                        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.aboutitem:
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        // colección de características GeoJSON del archivo GeoJSON en la carpeta de assets
        try {
            getFeatureCollectionFromJson();
        } catch (Exception exception) {
            Log.e("MapActivity", "onCreate: " + exception);
            Toast.makeText(this, R.string.failure_to_load_file, Toast.LENGTH_LONG).show();
        }

        // lista de objetos IndividualLocation para uso futuro con recyclerview
        listOfIndividualLocations = new ArrayList<>();

        // tema que se seleccionó en la actividad anterior.
        chosenTheme = R.style.AppTheme_Blue;

        // configurar el mapa Mapbox
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                // clase personalizada que maneja la creación del icono de marcador y el estilo del mapa basado en el tema seleccionado

                customThemeManager = new CustomThemeManager(chosenTheme, MapActivity.this);
                mapboxMap.setStyle(new Style.Builder().fromUrl(customThemeManager.getMapStyle()), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // establecer el objeto mapboxMap devuelto igual al "globalmente declarado"
                        MapActivity.this.mapboxMap = mapboxMap;

                        //Location Style Component
                        //enableLocationComponent(style);

                        // ajusta la opacidad del logotipo de Mapbox en la esquina inferior izquierda del mapa
                        ImageView logo = mapView.findViewById(R.id.logoView);
                        logo.setAlpha(MAPBOX_LOGO_OPACITY);

                        // limites para la cámara de mapa para que el usuario no pueda desplazar el mapa fuera del área establecida
                        mapboxMap.setLatLngBoundsForCameraTarget(LOCKED_MAP_CAMERA_BOUNDS);

                        // configurar el SymbolLayer que mostrará los íconos para cada ubicación de los puntos de interes
                        initPoiLocationIconSymbolLayer();

                        // configurar el SymbolLayer que mostrará el ícono del punto de interes
                        initSelectedPoiSymbolLayer();

                        // configurar LineLayer que mostrará la línea de ruta de navegación de la ubicación a un punto de interes
                        initNavigationPolylineLineLayer();

                        // crea una lista de características de la colección de características
                        List<Feature> featureList = featureCollection.features();

                        // recupera y actualiza la fuente designada para mostrar los iconos de ubicación de un punto de interes
                        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("store-location-source-id");
                        if (source != null) {
                            source.setGeoJson(FeatureCollection.fromFeatures(featureList));
                        }
                        if (featureList != null) {
                            for (int x = 0; x < featureList.size(); x++) {
                                Feature singleLocation = featureList.get(x);

                                // propiedades de la cadena de la ubicación para colocar un marcador de mapa
                                String singleLocationName = singleLocation.getStringProperty("name");
                                String singleLocationHours = singleLocation.getStringProperty("hours");
                                String singleLocationAddress = singleLocation.getStringProperty("address");
                                String singleLocationPhoneNum = singleLocation.getStringProperty("phone");
                                String singleImageUrlCv = singleLocation.getStringProperty("imgcardv");
                                String singleImageOne = singleLocation.getStringProperty("imgone");
                                String singleImageTwo = singleLocation.getStringProperty("imgtwo");
                                String singleImageThree = singleLocation.getStringProperty("imgthree");
                                String singleTypePoi = singleLocation.getStringProperty("typepoi");
                                String singleDescriptionPoi = singleLocation.getStringProperty("description");
                                String singleKeyPoi = singleLocation.getStringProperty("keypoi");

                                // agrege una propiedad booleana para ajustar el icono de la ubicación de los puntos de interes
                                singleLocation.addBooleanProperty(PROPERTY_SELECTED, false);

                                // obtener las coordenadas LatLng de la ubicación individual
                                Point singleLocationPosition = (Point) singleLocation.geometry();

                                // objeto LatLng con el objeto Posición creado anteriormente
                                LatLng singleLocationLatLng = new LatLng(singleLocationPosition.latitude(), singleLocationPosition.longitude());

                                // agregar la ubicación a la Lista de ubicaciones para su uso posterior en la vista de reciclaje
                                listOfIndividualLocations.add(new IndividualLocation(
                                        singleLocationName,
                                        singleLocationAddress,
                                        singleLocationHours,
                                        singleLocationPhoneNum,
                                        singleImageUrlCv,
                                        singleImageOne,
                                        singleImageTwo,
                                        singleImageThree,
                                        singleTypePoi,
                                        singleDescriptionPoi,
                                        singleKeyPoi,
                                        singleLocationLatLng
                                ));

                                // llamar a getInformationFromDirectionsApi () para mostrar la distancia desde la ubicación del dispositivo
                                getInformationFromDirectionsApi(singleLocationPosition, false, x);
                            }

                            // agregar el marcador de ubicación del dispositivo falso al mapa
                            addMockDeviceLocationMarkerToMap();
                            setUpRecyclerViewOfLocationCards(chosenTheme);
                            mapboxMap.addOnMapClickListener(MapActivity.this);
                            //Toast.makeText(MapActivity.this, "Haga click en una tarjeta", Toast.LENGTH_SHORT).show();
                            if (customThemeManager.getNavigationLineColor() == R.color.navigationRouteLine_blue) {
                                showBuildingExtrusions();
                            }
                        }
                    }

                });

            }
        });
    }

    // extrusiones de edificaciones en caso se encuentren disponibles, depende del estilo del mapa
    private void showBuildingExtrusions() {
        // complemento de construcción Mapbox para mostrar y personalizar la opacidad/color de las extrusiones de construcción
        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap, mapboxMap.getStyle());
        buildingPlugin.setVisibility(true);
        buildingPlugin.setOpacity(BUILDING_EXTRUSION_OPACITY);
        buildingPlugin.setColor(Color.parseColor(BUILDING_EXTRUSION_COLOR));
    }

    // desactivacion del evento de click en mapa
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
        return false;
    }

    // evento que se da en caso se haga click en un icono dentro del mapa
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, "store-location-layer-id");
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty("name");
            List<Feature> featureList = featureCollection.features();
            for (int i = 0; i < featureList.size(); i++) {

                if (featureList.get(i).getStringProperty("name").equals(name)) {
                    Point selectedFeaturePoint = (Point) featureList.get(i).geometry();

                    if (featureSelectStatus(i)) {
                        setFeatureSelectState(featureList.get(i), false);
                    } else {
                        setSelected(i);
                    }
                    if (selectedFeaturePoint.latitude() != PointOrigin.getLatitude()) {
                        for (int x = 0; x < featureCollection.features().size(); x++) {

                            if (listOfIndividualLocations.get(x).getLocation().getLatitude() == selectedFeaturePoint.latitude()) {
                                // desplaza la vista del reciclador a la tarjeta del marcador seleccionado.
                                // Es "x-1" a continuación porque el marcador de ubicación del dispositivo simulado es parte de la
                                // lista de marcadores pero no tiene su propia tarjeta en la vista actual del reciclador.
                                locationsRecyclerView.smoothScrollToPosition(x);
                            }
                        }
                    }
                } else {
                    setFeatureSelectState(featureList.get(i), false);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemClick(int position) {
        // ubicación individual seleccionada a través de la posicion de la tarjeta en la vista de reciclado de tarjetas
        IndividualLocation selectedLocation = listOfIndividualLocations.get(position);
        // se evalua el "estado de selección" de cada función para diseñar adecuadamente el icono de la ubicación
        List<Feature> featureList = featureCollection.features();
        Point selectedLocationPoint = (Point) featureCollection.features().get(position).geometry();
        for (int i = 0; i < featureList.size(); i++) {
            if (featureList.get(i).getStringProperty("name").equals(selectedLocation.getName())) {
                if (featureSelectStatus(i)) {
                    setFeatureSelectState(featureList.get(i), false);
                } else {
                    setSelected(i);
                }
            } else {
                setFeatureSelectState(featureList.get(i), false);
            }
        }

        // se coloca el objetivo de la cámara del mapa en el marcador seleccionado
        if (selectedLocation != null) {
            repositionMapCamera(selectedLocationPoint);
        }

        // se comprueba si hay una conexión a Internet antes de hacer la llamada a Mapbox Directions API
        if (deviceHasInternetConnection()) {
            // llamada a la API de indicaciones de Mapbox
            if (selectedLocation != null) {
                getInformationFromDirectionsApi(selectedLocationPoint, true, null);
            }
        } else {
            Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    private void initPoiLocationIconSymbolLayer() {
        Style style = mapboxMap.getStyle();
        if (style != null) {
            // se agrega la imagen del icono al mapa
            style.addImage("store-location-icon-id", customThemeManager.getUnselectedMarkerIcon());

            // se crea y agrega GeoJsonSource al mapa
            GeoJsonSource storeLocationGeoJsonSource = new GeoJsonSource("store-location-source-id");
            style.addSource(storeLocationGeoJsonSource);

            // se crea y agrega el icono de ubicación del POI SymbolLayer al mapa
            SymbolLayer storeLocationSymbolLayer = new SymbolLayer("store-location-layer-id",
                    "store-location-source-id");
            storeLocationSymbolLayer.withProperties(
                    iconImage("store-location-icon-id"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            );
            style.addLayer(storeLocationSymbolLayer);

        } else {
            Log.d("StoreFinderActivity", "initPoiLocationIconSymbolLayer: Style isn't ready yet.");
            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    private void initSelectedPoiSymbolLayer() {
        Style style = mapboxMap.getStyle();
        if (style != null) {
            // agrega la imagen del icono al mapa
            style.addImage("selected-store-location-icon-id", customThemeManager.getSelectedMarkerIcon());

            // crea y agrega el icono de ubicación de la tienda SymbolLayer al mapa
            SymbolLayer selectedStoreLocationSymbolLayer = new SymbolLayer("selected-store-location-layer-id",
                    "store-location-source-id");
            selectedStoreLocationSymbolLayer.withProperties(
                    iconImage("selected-store-location-icon-id"),
                    iconAllowOverlap(true)
            );
            selectedStoreLocationSymbolLayer.withFilter(eq((get(PROPERTY_SELECTED)), literal(true)));
            style.addLayer(selectedStoreLocationSymbolLayer);
        } else {
            Log.d("StoreFinderActivity", "initSelectedPoiSymbolLayer: Style isn't ready yet.");
            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    private boolean featureSelectStatus(int index) {
        // condicional en caso de que no exista ningun elemento en el arreglo
        if (featureCollection == null) {
            return false;
        }
        return featureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    private void setSelected(int index) {
        // condicional en caso de que se seleccione un elemento y exista un elemento en el arreglo
        Feature feature = featureCollection.features().get(index);
        setFeatureSelectState(feature, true);
        refreshSource();
    }

    private void setFeatureSelectState(Feature feature, boolean selectedState) {
        // enviar estado "seleccionado" en caso se seleccione un elemento del arreglo
        feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
        refreshSource();
    }

    private void refreshSource() {
        // extraer elemento desde el GeoJson
        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("store-location-source-id");
        if (source != null && featureCollection != null) {
            source.setGeoJson(featureCollection);
        }
    }

    // criterio de generacion de ruta entre la ubicacion del dispositivo y un punto de interes
    private void getInformationFromDirectionsApi
    (Point destinationPoint, final boolean fromMarkerClick, @Nullable final Integer listIndex) {
        // configurar coordenadas de origen y destino para la llamada a la API de indicaciones de Mapbox
        Point mockCurrentLocation = Point.fromLngLat(PointOrigin.getLongitude(),
                PointOrigin.getLatitude());
        Point destinationMarker = Point.fromLngLat(destinationPoint.longitude(), destinationPoint.latitude());

        // inicializar las direcciones del objeto ApiClient para eventualmente dibujar una ruta de navegación en el mapa
        MapboxDirections directionsApiClient = MapboxDirections.builder()
                .origin(mockCurrentLocation)
                .destination(destinationMarker)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.access_token))
                .build();

        directionsApiClient.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // comtrpobar que la respuesta no es nula y que la respuesta tiene una ruta
                if (response.body() == null) {
                    Log.e("MapActivity", "No routes found, make sure you set the right user and access token.");
                } else if (response.body().routes().size() < 1) {
                    Log.e("MapActivity", "No routes found");
                } else {
                    if (fromMarkerClick) {
                        // recuperar y dibujar la ruta de navegación en el mapa
                        currentRoute = response.body().routes().get(0);
                        drawNavigationPolylineRoute(currentRoute);
                    } else {
                        // usar el método auxiliar MapboxTurf para convertir metros a millas y luego formatear el número de millaje
                        DecimalFormat df = new DecimalFormat("#.#");
                        String finalConvertedFormattedDistance = String.valueOf(df.format(TurfConversion.convertLength(
                                response.body().routes().get(0).distance(), TurfConstants.UNIT_METERS, TurfConstants.UNIT_KILOMETERS)));
                        // TODO: se a establecido como medida inicial de medida: KM para ser mostrada en el cardview

                        // establecer la distancia para cada objeto de ubicación en la lista de ubicaciones
                        if (listIndex != null) {
                            listOfIndividualLocations.get(listIndex).setDistance(finalConvertedFormattedDistance);
                            // actualizar la vista de reciclador que se muestra cuando se establece la distancia de la ubicación
                            styleRvAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            // se llama a la clase <DirectionResponse.java> para recuperar la ruta de navegacion
            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(MapActivity.this, R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
            }
        });
    }

    // determina la posicion de la camara en el mapa dependiendo del poi selecionado <cardview>
    private void repositionMapCamera(Point newTarget) {
        CameraPosition newCameraPosition = new CameraPosition.Builder()
                .target(new LatLng(newTarget.latitude(), newTarget.longitude()))
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), CAMERA_MOVEMENT_SPEED_IN_MILSECS);
    }

    // se agrega el marcador de ubicación de usuario al mapa
    private void addMockDeviceLocationMarkerToMap() {
        Style style = mapboxMap.getStyle();
        if (style != null) {

            style.addImage("mock-device-location-icon-id", customThemeManager.getMockLocationIcon());

            style.addSource(new GeoJsonSource("mock-device-location-source-id", Feature.fromGeometry(
                    Point.fromLngLat(PointOrigin.getLongitude(), PointOrigin.getLatitude()))));

            style.addLayer(new SymbolLayer("mock-device-location-layer-id",
                    "mock-device-location-source-id").withProperties(
                    iconImage("mock-device-location-icon-id"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            ));
        } else {
            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    // método para convertir el archivo GeoJSON en un objeto FeatureCollection utilizable <fromJson()>
    private void getFeatureCollectionFromJson() throws IOException {
        try {

            featureCollection = FeatureCollection.fromJson(loadGeoJsonFromAsset("list_of_locations.geojson"));

        } catch (Exception exception) {
            Log.e("MapActivity", "getFeatureCollectionFromJson: " + exception);
        }
    }

    // metodo para cargar el archivo GeoJSON desde la carpeta local
    private String loadGeoJsonFromAsset(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (Exception exception) {
            Log.e("MapActivity", "Exception Loading GeoJSON: " + exception.toString());
            exception.printStackTrace();
            return null;
        }
    }

    // vista de reciclaje de las tarjetas de ubicación y una clase personalizada para el desplazamiento automático de tarjetas
    private void setUpRecyclerViewOfLocationCards(int chosenTheme) {
        locationsRecyclerView = findViewById(R.id.map_layout_rv);
        locationsRecyclerView.setHasFixedSize(true);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        styleRvAdapter = new LocationRecyclerViewAdapter(listOfIndividualLocations,
                getApplicationContext(), this, chosenTheme);
        locationsRecyclerView.setAdapter(styleRvAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(locationsRecyclerView);
    }

    // metodo para la busqueda de elementos dentro del MapActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activitymap, menu);
        MenuItem searchItem = menu.findItem(R.id.actionsearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                styleRvAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    // metodo para actualice la fuente designada para mostrar los iconos de ubicación de los puntos de interes
    private void drawNavigationPolylineRoute(DirectionsRoute route) {
        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("navigation-route-source-id");
        if (source != null) {
            source.setGeoJson(FeatureCollection.fromFeature(Feature.fromGeometry(
                    LineString.fromPolyline(route.geometry(), PRECISION_6))));
        }
    }

    // metodo para dibujar la linea entre un punto A yun punto B
    private void initNavigationPolylineLineLayer() {
        // se agrega el "id" de los POIS de GeoJsonSource
        GeoJsonSource navigationLineLayerGeoJsonSource = new GeoJsonSource("navigation-route-source-id");
        mapboxMap.getStyle().addSource(navigationLineLayerGeoJsonSource);

        // se crea y agrega el LineLayer al mapa para mostrar la línea de ruta de navegación
        LineLayer navigationRouteLineLayer = new LineLayer("navigation-route-layer-id",
                navigationLineLayerGeoJsonSource.getId());
        navigationRouteLineLayer.withProperties(
                lineColor(customThemeManager.getNavigationLineColor()),
                lineWidth(NAVIGATION_LINE_WIDTH)
        );
        mapboxMap.getStyle().addLayerBelow(navigationRouteLineLayer, "store-location-layer-id");
    }

    // TODO: metodo para determinar la ubicacion actual del dispositivo usando el API de MapBox

    /*
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            if (locationComponent.getLastKnownLocation() == null) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);

                double latitudeOrigin = location.getLatitude();
                double longitudeOrigin = location.getLongitude();
                //LatLng latLngOrigin = new LatLng(latitudeOrigin,longitudeOrigin);
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng latLngOrigin = new LatLng(loc.getLatitude(), loc.getLongitude());
                //Point originPoint = Point.fromLngLat(loc.getLongitude(),loc.getLatitude());
                PointOrigin = latLngOrigin;
            }
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }
     */

    // TODO: metodo/variables que determinan la ultima ubicacion del disposito

    /*
    public void LastLocation() {

        Point point = Point.fromLngLat(latitudeOrigin, longitudeOrigin);
        Point originPoint = Point.fromLngLat(location.getLastKnownLocation().getLongitude(), location.getLastKnownLocation().getLatitude());

        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        double LatOrg = locationComponent.getLastKnownLocation().getLatitude();
        double LonOrg = locationComponent.getLastKnownLocation().getLongitude();
        LatLng latLngOrg = new LatLng(LonOrg, LatOrg);
        LatLng latLng = new LatLng(latLngOrg.getLatitude(), latLngOrg.getLongitude());
        //PointOrigin = latLng;
    }
    */

    // Configuracion del tema inicial
    class CustomThemeManager {
        private int selectedTheme;
        private Context context;
        private Bitmap unselectedMarkerIcon;
        private Bitmap selectedMarkerIcon;
        private Bitmap mockLocationIcon;
        private int navigationLineColor;
        private String mapStyle;

        CustomThemeManager(int selectedTheme, Context context) {
            this.selectedTheme = selectedTheme;
            this.context = context;
            initializeTheme();
        }

        // TODO: temas pre-modificados de MapBox <style>
        //  mapStyle = Style.MAPBOX_STREETS;
        //  mapStyle = getString(R.string.blue_map_style);

        private void initializeTheme() {
            switch (selectedTheme) {
                case R.style.AppTheme_Blue:
                    mapStyle = Style.LIGHT;
                    mapStyle = getString(R.string.blue_map_style);
                    navigationLineColor = getResources().getColor(R.color.navigationRouteLine_blue);
                    unselectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.poiunselected);
                    selectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.poiselected);
                    mockLocationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_user_location);
                    break;
                case R.style.AppTheme_Neutral:
                    mapStyle = Style.MAPBOX_STREETS;
                    navigationLineColor = getResources().getColor(R.color.navigationRouteLine_neutral);
                    unselectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_circle);
                    selectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_circle);
                    mockLocationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.neutral_orange_user_location);
                    break;
            }
        }

        public Bitmap getUnselectedMarkerIcon() {
            return unselectedMarkerIcon;
        }

        public Bitmap getMockLocationIcon() {
            return mockLocationIcon;
        }

        public Bitmap getSelectedMarkerIcon() {
            return selectedMarkerIcon;
        }

        int getNavigationLineColor() {
            return navigationLineColor;
        }

        public String getMapStyle() {
            return mapStyle;
        }
    }

    // ciclos de vida de mapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private boolean deviceHasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
