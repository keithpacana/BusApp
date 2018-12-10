package com.example.keith.bus;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Search extends AppCompatActivity {
    /*Num busses*/
    String bus_1;
    String bus_2;
    String bus_3;

    /*bus titles on search res*/
    TextView bus_title;
    TextView bus_title2;
    TextView bus_title3;

    /*textviews of all the full bus names*/
    TextView toward_1;
    TextView toward_2;
    TextView toward_3;

    Intent show_map;
    String poly_points;
    LatLng dest;
    String destin;
    String origin;

    ArrayList<String> poly_lines;
    ArrayList<String> bus_list;
    ArrayList<String> full_names;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /*finding all the bus_titles on search result page*/
        bus_title = (TextView) findViewById(R.id.bus_name);
        bus_title2 = (TextView) findViewById(R.id.bus_name2);
        bus_title3 = (TextView) findViewById(R.id.bus_name3);

        /*finding all the full names*/
        toward_1 = (TextView) findViewById(R.id.to_text);
        toward_2 = (TextView) findViewById(R.id.to_text2);
        toward_3 = (TextView) findViewById(R.id.to_text3);

        /*Get intents from previous page*/
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        dest =  bundle.getParcelable("dest");
        loc = bundle.getParcelable("loc");


        /*get all the lists*/
        bus_list = (ArrayList<String>) getIntent().getSerializableExtra("list");
        full_names = (ArrayList<String>) getIntent().getSerializableExtra("full_names");
        poly_lines = (ArrayList<String>) getIntent().getSerializableExtra("poly_list");



        destin = getIntent().getStringExtra("destin");
        origin = getIntent().getStringExtra("origin");

        /*Currently only 3 buses can be displayed on the page
        Iterate through the bus_list and display them respectively. Definitely can
        add more later.
         */
        if (bus_list.size() >= 1) {
            bus_title.setText(bus_list.get(0));
            toward_1.setText(full_names.get(0));

            /*hide other result display if there is only 1 bus */
            if (bus_list.size() == 1 ) {
                bus_title2.setVisibility(View.INVISIBLE);
                bus_title3.setVisibility(View.INVISIBLE);

                /*hide all the full_names of the display*/
                toward_2.setVisibility(View.INVISIBLE);
                toward_3.setVisibility(View.INVISIBLE);
            }
        }
        if (bus_list.size() >= 2) {
            bus_title2.setText(bus_list.get(1));
            toward_2.setText(full_names.get(1));

            if (bus_list.size() == 2) {
                bus_title3.setVisibility(View.INVISIBLE);
                toward_3.setVisibility(View.INVISIBLE);
            }
        }
        if (bus_list.size() >= 3) {
            bus_title3.setText(bus_list.get(2));
            toward_3.setText(bus_list.get(2));
        }

    /* Set on click for each bus name so when the user clicks them it'll
    draw out the poly line
     */
        bus_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_map = new Intent(Search.this,MapsActivity.class);
                Bundle bundle2 = getIntent().getParcelableExtra("bundle");
                /*Send all required data to next intent to show the poly line on the map*/
                bundle2.putParcelable("dest",dest);
                show_map.putExtra("bundle2",bundle2);
                show_map.putExtra("has",true);
                show_map.putExtra("poly",poly_lines.get(0));
                show_map.putExtra("destin",destin);
                show_map.putExtra("origin",origin);
                startActivity(show_map);
            }
        });
        bus_title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_map = new Intent(Search.this,MapsActivity.class);
                Bundle bundle2 = getIntent().getParcelableExtra("bundle");
                /*Send all required data to next intent to show the poly line on the map*/
                bundle2.putParcelable("dest",dest);
                show_map.putExtra("bundle2",bundle2);
                show_map.putExtra("has",true);
                show_map.putExtra("poly",poly_lines.get(1));
                show_map.putExtra("destin",destin);
                show_map.putExtra("origin",origin);
                startActivity(show_map);
            }
        });
        bus_title3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_map = new Intent(Search.this,MapsActivity.class);
                Bundle bundle2 = getIntent().getParcelableExtra("bundle");

                /*Send all required data to next intent to show the poly line on the map*/
                bundle2.putParcelable("dest",dest);
                show_map.putExtra("bundle2",bundle2);
                show_map.putExtra("has",true);
                show_map.putExtra("poly",poly_lines.get(2));
                show_map.putExtra("destin",destin);
                show_map.putExtra("origin",origin);
                startActivity(show_map);
            }
        });

    }
}
