package com.example.turbo.dcidr.android_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.example.turbo.dcidr.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Turbo on 9/6/2016.
 */
public class MapViewActivity extends BaseActivity {

    private MapView mMapView;
    private LatLng mLatLong;
    private GoogleMap mGoogleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // make sure user is populated before proceeding, refer to base activity onCreate
        setUserFetchListener(new UserFetchListener() {
            @Override
            public void onFetchDone() {
                initActivity();
            }
        });
        super.onCreate(savedInstanceState);
    }
    public void initActivity(){
        setContentView(R.layout.activity_map_view);
        final RelativeLayout mMapViewRelativeLayout = (RelativeLayout) findViewById(R.id.map_view_relative_layout);
        // receive intent and populate data
        Intent intent = getIntent();
        final String markerTitle;
        final String markerSnippet;
        if (intent != null) {
            double latitude = intent.getDoubleExtra("LATITUDE", 0);
            double longitude = intent.getDoubleExtra("LONGITUDE", 0);
            markerTitle = intent.getStringExtra("MARKER_TITLE");
            markerSnippet = intent.getStringExtra("MARKER_SNIPPET");
            mLatLong = new LatLng(latitude, longitude);
        } else {
            mLatLong = new LatLng(0, 0);
            markerTitle = null;
            markerSnippet = null;
        }

        mMapView = (MapView) findViewById(R.id.event_map_view);
        mMapView.onCreate(null);
        mMapView.onResume();
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    requestPermission(MapViewActivity.this, mMapViewRelativeLayout, Manifest.permission.ACCESS_FINE_LOCATION, getResources().getString(R.string.location_permission_rationale), BaseActivity.LOCATION_ENABLE_REQUEST_CODE);
//                    requestPermission(MapViewActivity.this, mMapViewRelativeLayout, Manifest.permission.ACCESS_COARSE_LOCATION, getResources().getString(R.string.location_permission_rationale), BaseActivity.LOCATION_ENABLE_REQUEST_CODE);
//                } else {
//                    googleMap.setMyLocationEnabled(true);
//                }
                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(mLatLong));
                if(markerTitle != null){
                    marker.setTitle(markerTitle);
                    marker.setSnippet(markerSnippet);
                }
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLong, 16));
            }
        });

        // set app toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mMapView = null;
            mGoogleMap.clear();
            finish();
            return true;
        }else if(id == R.id.my_location){
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLong, 16));
        }
        return super.onOptionsItemSelected(item);
    }
    
    
}
