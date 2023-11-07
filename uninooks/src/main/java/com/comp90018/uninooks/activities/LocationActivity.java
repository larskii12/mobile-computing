package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.databinding.ActivityLocationBinding;
import com.comp90018.uninooks.models.favorite.Favorite;
import com.comp90018.uninooks.models.location.building.Building;
import com.comp90018.uninooks.models.location.resource.Resource;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.models.user.User;
import com.comp90018.uninooks.service.building.BuildingServiceImpl;
import com.comp90018.uninooks.service.busy_rating.BusyRatingServiceImpl;
import com.comp90018.uninooks.service.favorite.FavoriteServiceImpl;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.resource.ResourceServiceImpl;
import com.comp90018.uninooks.service.review.ReviewServiceImpl;
import com.comp90018.uninooks.service.user.UserServiceImpl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, GPSService{
    private static Context context;

    private String userName;
    private ActivityLocationBinding binding;
    GPSServiceImpl gpsService;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    SupportMapFragment bananaFragment;
    StudySpace space;
    boolean isFavorite;

    String userId;

    private final int standardCameraZoom = 18;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = getApplicationContext();
        Intent intent = getIntent();
        userId = intent.getStringExtra("USERID_EXTRA");
        String spaceID = intent.getStringExtra("SPACE_ID_EXTRA");
        System.out.println(spaceID);
        System.out.println(userId);

//        binding = ActivityLocationBinding.inflate(getLayoutInflater());
//
//        setContentView(binding.getRoot());
//
        gpsService = new GPSServiceImpl( this, this);
//
//        bananaFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.banana);
//
//        bananaFragment.getMapAsync( this);






//        List<Location> studySpacesNearby = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println(spaceID);
                    space = new LocationServiceImpl().findStudySpaceById(Integer.parseInt(spaceID));

                    // Get current Position
                    LatLng currentLocation = GPSServiceImpl.getCurrentLocation();

//                    space.setDistanceFromCurrentPosition(GPSServiceImpl.getCurrentLocation(), space.getLocation());
                    List<Review> reviews = new ReviewServiceImpl().getReviewsByEntity(Integer.valueOf(spaceID), ReviewType.valueOf(space.getType()));
                    System.out.println("Building id:" + space.getBuildingId());
                    Building building = new BuildingServiceImpl().getBuilding(space.getBuildingId(), ReviewType.valueOf(space.getType()));
                    List<Favorite> favorites = new FavoriteServiceImpl().getFavoritesByUser(Integer.parseInt(userId),ReviewType.valueOf(space.getType()));
                    isFavorite = false;
                    for (Favorite favorite: favorites) {
                        if (favorite.getStudySpaceId() == space.getId()) {
                            isFavorite = true;
                        }
                    }
                    LinearLayout reviewsLayout = findViewById(R.id.reviews);
                    TextView locationName = findViewById(R.id.textView5);
                    ProgressBar progress = findViewById(R.id.progressBar);
                    Double business = new BusyRatingServiceImpl().getAverageScoreFromEntity(Integer.valueOf(spaceID), ReviewType.valueOf(space.getType()));
                    Integer busyScore = space.isOpeningNow() ? (int) (business *20) : 0;
                    TextView progressValue = findViewById(R.id.textView7);
                    TextView distance = findViewById(R.id.distance);
                    TextView openHours = findViewById(R.id.openHours);

                    ImageButton backButton = findViewById(R.id.imageButton);
                    ImageButton favouriteButton = findViewById(R.id.favoriteButton);

                    Button addReview = findViewById(R.id.add_review);
                    User user = null;
                    System.out.println(userId);
                    user = new UserServiceImpl().getUser(Integer.parseInt(userId));

                    userName = user.getUserName();

                    TextView listTitle = findViewById(R.id.listTitle);
                    LinearLayout amenitiesList = findViewById(R.id.amenities);
                    System.out.println("Building id: " + space.getBuildingId());

                    List<Resource> resources = new ResourceServiceImpl().getResourceFromBuilding(space.getBuildingId());
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
                                    intent.putExtra("USERID_EXTRA", userId);
                                    intent.putExtra("USERNAME_EXTRA", userName);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }.start();
                        }
                    });
                    favouriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    try {
                                        Favorite newFavorite = new FavoriteServiceImpl().addFavorite(Integer.parseInt(userId), Integer.valueOf(spaceID),ReviewType.valueOf(space.getType()));
                                        isFavorite = true;
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }.start();
                        }
                    });
                   addReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
//                                    add code to add a new review and open pop up
                                }
                            }.start();
                        }
                    });

                    runOnUiThread(new Runnable() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            backButton.setBackgroundResource(R.drawable.arrow_back_fill);
                            locationName.setText(space.getName());
                            listTitle.setText("Facilities");
                            progress.setProgress(busyScore);
                            progressValue.setText(busyScore + "%");
                            if (isFavorite) {
                                favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_32);
                                favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.red), PorterDuff.Mode.SRC_IN);
                            } else {
                                favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_32);
                                favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.deepBlue), PorterDuff.Mode.SRC_IN);
                            }
                            if (space.getCloseTime() == null) {
                                openHours.setText("Closed");
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                openHours.setText("Open hours: " + sdf.format(space.getOpenTime()) + " - " + ("23:59".equals(sdf.format(space.getCloseTime())) ? "00:00" : sdf.format(space.getCloseTime())));
                            }
                            if (busyScore >= 0 && busyScore <= 40) {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                            } else if (busyScore > 40 && busyScore <= 75) {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow)));
                            } else {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                            }

                            if (space.getDistanceFromCurrentPosition() == -1 || !GPSServiceImpl.getGPSPermission()){
                                distance.setText("N/A");
                            }
                            else{
                                distance.setText(space.getDistanceFromCurrentPosition() + "m");
                            }
                            for(Review review : reviews) {
                                System.out.println(review.getComment());
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.review_layout, reviewsLayout, false);
                                CardView newCard = createNewSmallCard(card,review);
                                reviewsLayout.addView(newCard);
                            }
//                            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
//                            List<String> expandable = new ArrayList<String>();
//                            expandable.add("Amenities");
//                            List<String> amenities = new ArrayList<String>();
                            for (Resource resource: resources) {
//                                amenities.add(resource.getName());
                                System.out.println(resource.getName());
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = (TextView) card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText(resource.getName());
                                String resourceType = resource.getName();
                                if (resourceType.contains("Microwave")) {
                                    resourceIcon.setBackgroundResource(R.drawable.microwave_outline);
                                } else if (resourceType.contains("Car")) {
                                    resourceIcon.setBackgroundResource(R.drawable.parking_outline);
                                } else if (resourceType.contains("Kitchen")) {
                                    resourceIcon.setBackgroundResource(R.drawable.microwave_outline);
                                } else if (resourceType.contains("Vending")) {
                                    resourceIcon.setBackgroundResource(R.drawable.vending_machine_outline);
                                }
                                amenitiesList.addView(card);

                            }
                            if (space.isTalkAllowed()) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = (TextView) card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Discussion allowed");
                                resourceIcon.setBackgroundResource(R.drawable.volume_outline);
                                amenitiesList.addView(card);
                            }
                            if (space.getMinimumAccessAQFLevel() > 7) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = (TextView) card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Graduate student space");
                                resourceIcon.setBackgroundResource(R.drawable.gradspace_outline);
                                amenitiesList.addView(card);
                            }
                            if (space.getCloseTime()!= null) {
                                boolean openLate = getTimeToClose(space.getCloseTime());
                                if (openLate ) {
                                    CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                    TextView resourceDef = (TextView) card.findViewById(R.id.expandedListItem);
                                    ImageView resourceIcon = card.findViewById(R.id.icon);
                                    resourceDef.setText("After hours access");
                                    resourceIcon.setBackgroundResource(R.drawable.lateaccess_outline);
                                    amenitiesList.addView(card);
                                }
                            }
                            //check if the building is accessible and add that too
                            if (building.isHasAccessibility()) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = (TextView) card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Accessible Building");
                                resourceIcon.setBackgroundResource(R.drawable.accessible_outline);
                                amenitiesList.addView(card);
                            }
                        }
                    });
                } catch(
                        Exception e)

                {
                    throw new RuntimeException(e);
                }

            }

        }.start();
    }

    private boolean getTimeToClose(Time closeTime){
        Time currentTime = Time.valueOf("18:00:00");
        int closeHour = closeTime.getHours();
        if (closeHour == 0) {
            closeHour = 24;
        }
        if (closeHour - currentTime.getHours() > 0) {
            return true;
        }
        return false;
    }
    private CardView createNewSmallCard(CardView card, Review review){
        TextView userComment = (TextView) card.findViewById(R.id.textView);
        userComment.setText(review.getComment());
        RatingBar rating = (RatingBar) card.findViewById(R.id.ratingBar);
        rating.setRating(review.getScore());
        System.out.println("Review score:" + review.getScore());
        ImageView userImage = (ImageView) card.findViewById(R.id.imageView2);

        return card;
    }

    @Override
    public void onGPSUpdate(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int maxCameraZoom = 30;
        mMap.setMaxZoomPreference(maxCameraZoom);
        int minCameraZoom = 15;
        mMap.setMinZoomPreference(minCameraZoom);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(standardCameraZoom));

//        // Show the user location
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        }
//
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//
//        // Get the latest current position
////        fusedLocationClient.getLastLocation()
////                .addOnSuccessListener(this, location -> {
////                    if (location != null) {
////                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
////                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, standardCameraZoom));
////                    }
////                });
        if (space != null) {
            LatLng currentLocation = space.getLocation();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, standardCameraZoom));
        }
    }

    /**
     * Back gesture detested
     */
    public void onBackPressed() {
        Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
        intent.putExtra("USERID_EXTRA", userId);
        intent.putExtra("USERNAME_EXTRA", userName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
