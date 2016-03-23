package com.unwiredlabs.unwireddemo;


import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//Import statements for Unwired LocationAPI Library
import com.unwiredlabs.locationapi.Location.LocationAdapter;
import com.unwiredlabs.locationapi.Location.UnwiredLocationListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //todo: Change the token here to your developer token
    private final String mToken = "your_api_token";


    private GoogleMap mMap;
    private Marker unwiredMarker;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the Map
        initializeMap();

        /*
         * You can request a location in 2 ways
         *
         * A. The Easy Way - getLastLocation(); this is a synchronous request & will try to return the quickest available location
         * B. The Hard Way - getLocation(); this is an async request. You will select desired accuracy level,
         * setup a listener & once the location is available, your handle it
         *
         */

        //A. The Easy Way
        FloatingActionButton getLastLocation = (FloatingActionButton) findViewById(R.id.get_unwired_last_location);
        getLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar.make(coordinatorLayout, "Fetching via getLastLocation...", Snackbar.LENGTH_LONG);
                snackbar.show();
                //Call the function
                getLastLocation();
            }
        });

        //A. The Hard Way
        FloatingActionButton getLocation = (FloatingActionButton) findViewById(R.id.get_unwired_location);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar.make(coordinatorLayout, "Fetching via getLocation...", Snackbar.LENGTH_LONG);
                snackbar.show();
                //Call the function
                getLocation();
            }
        });

    }


    /**
     * Unwired's synchronous location call-back example
     */

    public void getLastLocation() {
        /*
         * 1. Initialize the LocationAdapter class with Context & your developer Token
         */

        final LocationAdapter locationAdapter = new LocationAdapter(this,mToken);

        /*
         * 2. Request for the last location
         */

        Location quickLocation = locationAdapter.getLastLocation();

        /*
         * 3. Handle the returned location
         */

        handleLocation(quickLocation);

    }

    /**
     * Unwired's asynchronous location call-back example
     */

    public void getLocation() {

        /*
         * 1. Initialize the LocationAdapter class with Context & your developer Token
         */

        final LocationAdapter locationAdapter = new LocationAdapter(this,mToken);

        /*
         * 2. Set the priority or accuracy level for your location
         */

        locationAdapter.setPriority(LocationAdapter.PRIORITY_BALANCED_POWER_ACCURACY);

        /*
         * 3. Initialize your Location listener; you can do it once at a global level, or each time
         * To set it once at global-level, use this function function: locationAdapter.setLocationListener(YOUR_LISTENER);
         * To specify a new listner each time while calling getLocation function, do this:
         */
        UnwiredLocationListener unwiredLocationListener = new UnwiredLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Call the function that will handle the location once returned
                handleLocation(location);
            }
        };

        /*
         * 4. Finally just call getLocation method to get location
         */
        locationAdapter.getLocation(unwiredLocationListener);

    }

    /**
     * Plots returned location on a Map, if it's valid
     * @param location
     */
    private void handleLocation(Location location) {
        snackbar.dismiss();
        if(location!=null) {
            // Use updated Unwired Location
            if(unwiredMarker!=null){
                unwiredMarker.remove();
            }
            unwiredMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("Unwired Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.unwired)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),14));

        }else{
            // Unable to find location for this Priority
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Unable to find location...", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void initializeMap() {
        setContentView(R.layout.activity_maps);
        ImageView myImage = (ImageView) findViewById(R.id.logo);
        myImage.setAlpha(127); //value: [0-255]. Where 0 is fully transparent and 255 is fully opaque
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinator_layout);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

}
