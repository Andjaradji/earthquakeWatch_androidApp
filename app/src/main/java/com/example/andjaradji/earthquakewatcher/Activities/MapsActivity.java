package com.example.andjaradji.earthquakewatcher.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.andjaradji.earthquakewatcher.Model.EarthQuake;
import com.example.andjaradji.earthquakewatcher.R;
import com.example.andjaradji.earthquakewatcher.UI.CustomInfoWindow;
import com.example.andjaradji.earthquakewatcher.Utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
,GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private RequestQueue queue;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private BitmapDescriptor [] iconColors;
    private Button showListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showListBtn = (Button) findViewById(R.id.showListBtnID);

        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this,QuakesList.class));
            }
        });

        iconColors = new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
//                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        };

        queue = Volley.newRequestQueue(this);

        getEQData ();

    }

    private void getEQData() {
        final EarthQuake earthQuake = new EarthQuake();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            for (int i=0; i<Constants.LIMIT; i++){
                                //get properties object
                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");

                                //get geometry object
                                JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");

                                //get coordinate array
                                JSONArray coordinates = geometry.getJSONArray("coordinates");

                                double lon = coordinates.getDouble(0);
                                double lat  = coordinates.getDouble(1);

                                earthQuake.setEqLocation(properties.getString("place"));
                                earthQuake.setEqType(properties.getString("type"));
                                earthQuake.setEqTime(properties.getLong("time"));
                                earthQuake.setEqMagnitude(properties.getDouble("mag"));
                                earthQuake.setEqDetailURL(properties.getString("detail"));
                                earthQuake.setEqLat(lat);
                                earthQuake.setEqLon(lon);

                                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();

                                String formattedDate = dateFormat.format(new Date(earthQuake.getEqTime()).getTime());

                                LatLng eqCoord = new LatLng(lat,lon);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.icon(iconColors[Constants.randomInt(iconColors.length,0)]);
                                markerOptions.title(earthQuake.getEqLocation());
                                markerOptions.position(eqCoord);
                                markerOptions.snippet("Magnitude: "
                                        +earthQuake.getEqMagnitude()
                                        + "\n" +"Date: " + formattedDate);

                                //Add Circle to markers that have magnitude > x
                                if (earthQuake.getEqMagnitude()>Constants.MAX_MAG){
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(new LatLng(earthQuake.getEqLat(), earthQuake.getEqLon()));
                                    circleOptions.radius(50000);
                                    circleOptions.strokeWidth(3.6f);
                                    circleOptions.fillColor(Color.RED);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    mMap.addCircle(circleOptions);
                                }


                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(earthQuake.getEqDetailURL());

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eqCoord,1));




                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
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

        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);


        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

            if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                getEQData();

            }
        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        getQuakeDetails (marker.getTag().toString());

//        Toast.makeText(getApplicationContext(),marker.getTag().toString(),Toast.LENGTH_LONG).show();

    }

    private void getQuakeDetails(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String detailsUrl = "";
                try {
                    JSONObject properties = response.getJSONObject("properties");
                    JSONObject products = properties.getJSONObject("products");
                    JSONArray geoserve = products.getJSONArray("geoserve");

                    for (int i=0; i<geoserve.length(); i++){
                        JSONObject geoserveObj = geoserve.getJSONObject(i);
                        JSONObject contents = geoserveObj.getJSONObject("contents");
                        JSONObject geoJsonObj = contents.getJSONObject("geoserve.json");

                        detailsUrl = geoJsonObj.getString("url");
                    }
                    Log.d("URL", detailsUrl);

                    getMoreDetail(detailsUrl);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getMoreDetail (String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                StringBuilder stringBuilder = new StringBuilder();
                String cityName = "";
                String cityDistance = "";
                String cityPopulation="";
                String webText="";

                try {

                    if (response.has("tectonicSummary") && response.getString("tectonicSummary")!= null){

                        JSONObject tectonic = response.getJSONObject("tectonicSummary");
                        if (tectonic.has("text") && tectonic.getString("text")!= null){
                            webText = tectonic.getString("text");
                        }
                    }


                    JSONArray cities = response.getJSONArray("cities");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject cityObj = cities.getJSONObject(i);
                        cityName = cityObj.getString("name");
                        cityDistance = cityObj.getString("distance");
                        cityPopulation = cityObj.getString("population");

                        stringBuilder.append("City: " + cityName
                        + "\n" + "Distance: " + cityDistance
                        + "\n" + "Population: " + cityPopulation);

                        stringBuilder.append("\n\n");

                    }
//                    Log.d("String: ", String.valueOf(stringBuilder));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.detail_popup, null);
                Button dismissButtonTop = (Button)view.findViewById(R.id.dismissBtnPopTopID);
                Button dismissButton = (Button)view.findViewById(R.id.dismissBtnPopID);
                TextView popListContent = (TextView)view.findViewById(R.id.popListContentID);
                WebView htmlWebView = (WebView)view.findViewById(R.id.htmlWebViewID);

                popListContent.setText(stringBuilder);
                htmlWebView.loadDataWithBaseURL(null,webText,"text/html","UTF-8",null);


                dialogBuilder.setView(view);
                dialog = dialogBuilder.create();
                dialog.show();

                dismissButtonTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }
}
