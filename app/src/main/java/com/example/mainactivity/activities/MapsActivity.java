package com.example.mainactivity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.fragment.app.FragmentActivity;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.ActivityMapsBinding;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SearchView searchBar;
    private ImageButton filterButton;
    private BottomNavigationView bottomNav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        searchBar = findViewById(R.id.searchBar);
        filterButton = (ImageButton) findViewById(R.id.filterButton);
        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.searchNav);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // for the map (mMap), get the nearby locations and display it as a pointer
        /**
         *
         * on below line we are adding marker to that position::
         * mMap.addMarker(new MarkerOptions().position(latLng).title(location));
         *
         *
         */
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String searchQuery = searchBar.getQuery().toString();
                Intent intent = new Intent(MapsActivity.this, SearchResults.class);
                intent.putExtra("searchQuery", searchQuery);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // launch the filter page
                Intent intent = new Intent(MapsActivity.this, FilterAdjustmentActivity.class);
                startActivity(intent);
            }
        });

//        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @SuppressLint("NonConstantResourceId")
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.homeNav:
//                        // go to home navigation page (Linda's page)
//                        break;
//                    case R.id.focusNav:
//                        // go to focus page
//                        break;
//                    case R.id.accountNav:
//                        // go to account page
//                        break;
//                }
//                return false;
//            }
//        });

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

        mMap.setMaxZoomPreference(30);
        mMap.setMinZoomPreference(15);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-37.800097099486244, 144.96440741828778);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}