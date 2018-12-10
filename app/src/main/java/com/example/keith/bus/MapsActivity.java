package com.example.keith.bus;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.location.Geocoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.support.v4.app.FragmentActivity;
import android.location.Address;
import java.lang.reflect.Array;
import java.util.ArrayList;
import  java.util.List;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import java.io.IOException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    boolean done;

    //Widgets
    private EditText origin;
    private EditText destination;
    private static final String TAG = "MapsActivity";
    private ImageView next_button;

    /*Variables for API calls*/
    private RequestQueue req_q;
    private String JSON_URL;
    private JsonObjectRequest request;
    private Intent bus_list;

    /*Other variables*/
    String poly_line;
    List<LatLng> decoded;
    LatLng new_dest;
    LatLng destt;
    Location loc;
    String input_origin;
    String input_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        req_q = Volley.newRequestQueue(getApplicationContext());

        /*Find all layout items*/
        origin = (EditText) findViewById(R.id.input_search);
        next_button = (ImageView) findViewById(R.id.next);
        destination = (EditText) findViewById(R.id.input_search2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*If the previous Intent has been called with an origin and destination
        display the origin and destination along with the polyline
         */
        if (getIntent().hasExtra("has")) {
            input_origin = getIntent().getStringExtra("origin");
            input_dest = getIntent().getStringExtra("destin");
            poly_line = getIntent().getStringExtra("poly");

            Bundle b = getIntent().getParcelableExtra("bundle2");
            destt = b.getParcelable("dest");
            decoded = PolyUtil.decode(poly_line);
            origin.setText(input_origin);
            destination.setText(input_dest);
        }


    }

    /*Requests permission to access location*/
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},1);
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
    private void init() {
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });
    }


    private void geoLocate(){
        Log.d(TAG, "geolocate: geolocating");

        /*Initializing data structures*/
        String searchString = origin.getText().toString();
        String destString = destination.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        List<Address> list2 = new ArrayList<>();

        try{
            /*if user inputs current location*/
            if (searchString.toLowerCase().equals("current location")) {
                list = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
            }
            else {
                list = geocoder.getFromLocationName(destString,1);
            }
            list2 = geocoder.getFromLocationName(destString,1);

        }
        catch (IOException e){
            Log.e(TAG,"geolocate : IOException" + e.getMessage());

        }

        if ((list.size() > 0 && list2.size() > 0 )) {
            /*Parse out information to get JSON_URL*/

            Address address = list.get(0);
            Address address2 = list2.get(0);
            LatLng new_origin = new LatLng(address.getLatitude(), address.getLongitude());
            new_dest = new LatLng(address2.getLatitude(),address2.getLongitude());
            String from = origin.getText().toString();

            if (searchString.toLowerCase().equals("current location")) {
                from = address.getAddressLine(0);
            }
            JSON_URL ="https://maps.googleapis.com/maps/api/directions/json?origin=" +  from +
                    "&destination=" + destination.getText().toString() +
                    "&mode=transit&transit_mode=bus&key=AIzaSyA3SsKSGdssA8" +
                    "I6eoFeWz67rbN4ZjIDIzI";
            jssonrequest();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            return;
        }
        client.getLastLocation().addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    loc = location;
                    mMap.setMyLocationEnabled(true);
                    LatLng curr_loc = new LatLng(location.getLatitude(), location.getLongitude());

                    /*If the previous intent has been called with an origin and destination enter
                    this if statement to draw the polyline and move the camera towards it else
                    just move the camera to the current location*/
                    if (getIntent().hasExtra("has")) {
                        /*Obtain all Lat and long for polyline*/
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        PolylineOptions line = new PolylineOptions();
                        for (LatLng latLng: decoded) {
                            line.add(latLng);
                            builder.include(latLng);
                        }
                        LatLngBounds bounds = builder.build();

                        /*Move map/camera to polyline*/
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                        mMap.addMarker(new MarkerOptions().position(destt).title("Destination"));
                        mMap.addPolyline(line.width(10).color(Color.RED));
                    }
                    else {
                        origin.setText("Current Location");
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr_loc));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curr_loc,20));
                    }
                    init();
                }
            }
        });


    }
    private void jssonrequest() {
        request = new JsonObjectRequest(Request.Method.GET, JSON_URL, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bus_list = new Intent(MapsActivity.this,Search.class);

                    /*Parse out JSON file*/
                    JSONArray resp = response.getJSONArray("routes");
                    JSONObject r2 = resp.getJSONObject(0);
                    JSONArray r3 = r2.getJSONArray("legs");
                    JSONObject r4 = r3.getJSONObject(0);
                    JSONArray r5 = r4.getJSONArray("steps");

                    /* Initialize ArrayList of all the bus short names and full names*/
                    ArrayList<String> all_buses = new ArrayList<String>();
                    ArrayList<String> full_names = new ArrayList<>();
                    ArrayList<String> poly_lines = new ArrayList<>();

                    /*iterate through JSON Array and obtain all
                    the transit methods and add their data to the
                    respective lists initialized above.
                     */
                    for (int i = 0; r5.length() > i ;i++) {
                        JSONObject r6 = r5.getJSONObject(i);
                        if ( r6.has("transit_details")) {
                            JSONObject p1 = r6.getJSONObject("polyline");
                            String p2 = p1.getString("points");

                            JSONObject r7 = r6.getJSONObject("transit_details");
                            JSONObject r8 = r7.getJSONObject("line");
                            String name = r8.getString("name");
                            String bus_name = r8.getString("short_name");

                            all_buses.add(bus_name);
                            full_names.add(name);
                            poly_lines.add(p2);

                        }
                    }
                    Bundle args = new Bundle();
                    args.putParcelable("dest",new_dest);
                    args.putParcelable("location",loc);

                    /*send all the lists to next activity*/
                    bus_list.putExtra("poly_list", poly_lines);
                    bus_list.putExtra("list",all_buses);
                    bus_list.putExtra("full_names",full_names);

                    /*send additional variables to search activity*/
                    bus_list.putExtra("bundle",args);
                    bus_list.putExtra("origin",origin.getText().toString());
                    bus_list.putExtra("destin",destination.getText().toString());

                    /*start new activity */
                    startActivity(bus_list);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG,"JSON failed");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"error");
            }
        }) ;

        /*add to request queue*/
        req_q.add(request);

    }
    }

