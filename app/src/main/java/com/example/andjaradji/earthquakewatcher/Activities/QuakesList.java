package com.example.andjaradji.earthquakewatcher.Activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.andjaradji.earthquakewatcher.Model.EarthQuake;
import com.example.andjaradji.earthquakewatcher.R;
import com.example.andjaradji.earthquakewatcher.Utils.Constants;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class QuakesList extends AppCompatActivity {
    private ArrayList <String> arrayList;
    private ArrayList <Double> latList;
    private ArrayList<Double> lonList;
    private ListView quakesListView;
    private RequestQueue queue;
    private ArrayAdapter arrayAdapter;
    private Intent  detailMapsIntent;

    private List<EarthQuake> earthquakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);

        earthquakeList = new ArrayList<>();
        quakesListView = (ListView)findViewById(R.id.quakesListViewID);
        queue = Volley.newRequestQueue(this);
        arrayList = new ArrayList<>();
        latList = new ArrayList<>();
        lonList = new ArrayList<>();

        getQuakesList(Constants.URL);


    }

    private void getQuakesList(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final EarthQuake earthQuake = new EarthQuake();
                try {
                    final JSONArray features = response.getJSONArray("features");
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

                        earthquakeList.add(earthQuake);

                        arrayList.add(earthQuake.getEqLocation());
                        latList.add(earthQuake.getEqLat());
                        lonList.add(earthQuake.getEqLon());

                    }

                    arrayAdapter =  new ArrayAdapter<>(QuakesList.this, android.R.layout.simple_list_item_1,
                            android.R.id.text1,arrayList);
                    quakesListView.setAdapter(arrayAdapter);
                    quakesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long ID) {
                            Toast.makeText(getApplicationContext(), "Place: " +
                                    arrayList.get(position), Toast.LENGTH_LONG).show();



                            Bundle eqBundle = new Bundle();
                            eqBundle.putString("place", arrayList.get(position));
                            eqBundle.putDouble("latitude", latList.get(position));
                            eqBundle.putDouble("longitude", lonList.get(position));



                            detailMapsIntent = new Intent(QuakesList.this,DetailMaps.class );
                            detailMapsIntent.putExtras(eqBundle);


                            startActivity(detailMapsIntent);

                        }
                    });
                    arrayAdapter.notifyDataSetChanged();

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
}
