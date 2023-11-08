package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.databinding.ActivityLocationBinding;
import com.comp90018.uninooks.models.favorite.Favorite;
import com.comp90018.uninooks.models.location.building.Building;
import com.comp90018.uninooks.models.location.library.Library;
import com.comp90018.uninooks.models.location.resource.Resource;
import com.comp90018.uninooks.models.location.restaurant.Restaurant;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.service.building.BuildingServiceImpl;
import com.comp90018.uninooks.service.busy_rating.BusyRatingServiceImpl;
import com.comp90018.uninooks.service.favorite.FavoriteServiceImpl;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.library.LibraryServiceImpl;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.resource.ResourceServiceImpl;
import com.comp90018.uninooks.service.review.ReviewServiceImpl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, GPSService{
    private Context context;

    private ActivityLocationBinding binding;
    GPSServiceImpl gpsService;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    SupportMapFragment bananaFragment;
    com.comp90018.uninooks.models.location.Location location;
    boolean isFavorite;

    private final int standardCameraZoom = 18;

//    private BottomNavigationView bottomNav;

    private int userId;
    private String userEmail;
    private String userName;
    private int locationId;

    private String locationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        context = getApplicationContext();

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");
//        locationId = intent.getIntExtra("LOCATION_ID", 0);
        locationType = intent.getStringExtra("LOCATION_TYPE");

        if (locationType.equals("LIBRARY")){
            location = (Library) intent.getParcelableExtra("LOCATION");
        } else if (locationType.equals("STUDY_SPACE")){
            location = (StudySpace) intent.getParcelableExtra("LOCATION");
        } else {
            location = (Restaurant) intent.getParcelableExtra("LOCATION");
        }


//        bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.setSelectedItemId(R.id.homeNav);

//        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @SuppressLint("NonConstantResourceId")
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//
//                if (id == R.id.homeNav){
//                    Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
//
//                    // Pass the user to next page
//                    intent.putExtra("USER_ID_EXTRA", userId);
//                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
//                    intent.putExtra("USER_NAME_EXTRA", userName);
//
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//                }
//
//                else if (id == R.id.searchNav) {
//                    Intent intent = new Intent(LocationActivity.this, MapsActivity.class);
//
//                    // Pass the user to next page
//                    intent.putExtra("USER_ID_EXTRA", userId);
//                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
//                    intent.putExtra("USER_NAME_EXTRA", userName);
//
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//
//                } else if (id == R.id.focusNav) {
//                    Intent intent = new Intent(LocationActivity.this, StudyZoneActivity.class);
//
//                    // Pass the user to next page
//                    intent.putExtra("USER_ID_EXTRA", userId);
//                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
//                    intent.putExtra("USER_NAME_EXTRA", userName);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    Intent intent = new Intent(LocationActivity.this, AccountActivity.class);
//
//                    // Pass the user to next page
//                    intent.putExtra("USER_ID_EXTRA", userId);
//                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
//                    intent.putExtra("USER_NAME_EXTRA", userName);
//                    startActivity(intent);
//                }
//
//                return false;
//            }
//        });

        new Thread() {
            @Override
            public void run() {
                try {

//                    if (locationType.equals("LIBRARY")){
//                        location = (Library) new LocationServiceImpl().findLibraryById(locationId);
//
//                    } else if (locationType.equals("STUDY_SPACE")){
//
//                        location = (StudySpace) new LocationServiceImpl().findStudySpaceById(locationId);
//                    } else {
//                        location = (Restaurant) new LocationServiceImpl().findRestaurantById(locationId);
//                    }

                    List<Review> reviews = new ReviewServiceImpl().getReviewsByEntity(locationId, ReviewType.valueOf(location.getType()));

                    //System.out.println("Building id:" + space.getBuildingId());
                    Building building = new BuildingServiceImpl().getBuilding(location.getBuildingId(), ReviewType.valueOf(location.getType()));
                    List<Favorite> favorites = new FavoriteServiceImpl().getFavoritesByUser(userId,ReviewType.valueOf(location.getType()));
                    isFavorite = false;
                    for (Favorite favorite: favorites) {
                        if (location.getType().equals("LIBRARY") && favorite.getLibraryId() == location.getId()) {
                            isFavorite = true;
                        }
                        else if (location.getType().equals("STUDY_SPACE") && favorite.getStudySpaceId() == location.getId()) {
                            isFavorite = true;
                        }
                        else if (location.getType().equals("RESTAURANT") && favorite.getRestaurantId() == location.getId()) {
                            isFavorite = true;
                        }

                    }

                    Log.d("AAAAAAAAAAAAAAAAAAA", String.valueOf(location.isOpeningNow()));
                    LinearLayout reviewsLayout = findViewById(R.id.reviews);
                    TextView locationName = findViewById(R.id.textView5);
                    ProgressBar progress = findViewById(R.id.progressBar);
                    Double business = new BusyRatingServiceImpl().getAverageScoreFromEntity(locationId, ReviewType.valueOf(location.getType()));
                    Integer busyScore = location.isOpeningNow() ? (int) (business *20) : 0;
                    TextView progressValue = findViewById(R.id.textView7);
                    TextView distance = findViewById(R.id.distance);
                    TextView openHours = findViewById(R.id.openHours);
                    Button addReviewButton = findViewById(R.id.add_review);

                    ImageButton backButton = findViewById(R.id.imageButton);
                    ImageButton favouriteButton = findViewById(R.id.favoriteButton);
                    ImageButton locationButton = findViewById(R.id.locate_my_location);

                    Button addReview = findViewById(R.id.add_review);
//                    User user = null;
//
//                    //System.out.println(userID);
//                    user = new UserServiceImpl().getUser(userId);
//
//                    userName = user.getUserName();

                    TextView listTitle = findViewById(R.id.listTitle);
                    LinearLayout amenitiesList = findViewById(R.id.amenities);
                    //System.out.println("Building id: " + space.getBuildingId());

                    List<Resource> resources = new ResourceServiceImpl().getResourceFromBuilding(location.getBuildingId());
                    locationButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            //navigate to the navigation part
                        }
                    });
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            finish();
//                            new Thread() {
//                                public void run() {
//                                    Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
//                                    intent.putExtra("USERID_EXTRA", userId);
//                                    intent.putExtra("USERNAME_EXTRA", userName);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }.start();
                        }
                    });
                    favouriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    try {
                                        if (!isFavorite) {
                                            new FavoriteServiceImpl().addFavorite(userId, locationId, ReviewType.valueOf(location.getType()));
                                            isFavorite = true;

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_32);
                                                    favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.red), PorterDuff.Mode.SRC_IN);
                                                }
                                            });
                                        }
                                        else{
                                            new FavoriteServiceImpl().removeFavorite(userId, locationId, ReviewType.valueOf(location.getType()));
                                            isFavorite = false;

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_32);
                                                    favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.deepBlue), PorterDuff.Mode.SRC_IN);
                                                }
                                            });
                                        }
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

                    addReviewButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                showAddReviewDialog();

                    }
                    });

                    runOnUiThread(new Runnable() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            backButton.setBackgroundResource(R.drawable.arrow_back_fill);
                            locationButton.setBackgroundResource(R.drawable.my_location_pin);
                            locationButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_circle_button) );
                            locationName.setText(location.getName());
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
                            if (location.getCloseTime() == null) {
                                openHours.setText("Close today");
                            } else if(location.getCloseTime() != null && location.isOpeningNow()){
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                openHours.setText("Open hours: " + sdf.format(location.getOpenTime()) + " - " + ("23:59".equals(sdf.format(location.getCloseTime())) ? "00:00" : sdf.format(location.getCloseTime())));
                            } else if (location.getCloseTime() != null && !location.isOpeningNow()) {
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                openHours.setText("Closed, open at " + sdf.format((location.getOpenTime())));
                            }
                            if (busyScore >= 0 && busyScore <= 40) {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                            } else if (busyScore > 40 && busyScore <= 75) {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow)));
                            } else {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                            }

                            if (location.getDistanceFromCurrentPosition() == -1 || !GPSServiceImpl.getGPSPermission()){
                                distance.setText("N/A");
                            }
                            else{
                                distance.setText(location.getDistanceFromCurrentPosition() + "m");
                            }
                            for(Review review : reviews) {
                                //System.out.println(review.getComment());
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
                                //System.out.println(resource.getName());
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
                            if (location instanceof StudySpace && ((StudySpace)location).isTalkAllowed()) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = (TextView) card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Discussion allowed");
                                resourceIcon.setBackgroundResource(R.drawable.volume_outline);
                                amenitiesList.addView(card);
                            }
                            if (location instanceof StudySpace && ((StudySpace)location).getMinimumAccessAQFLevel() > 7) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = (TextView) card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Graduate student space");
                                resourceIcon.setBackgroundResource(R.drawable.gradspace_outline);
                                amenitiesList.addView(card);
                            }
                            if (location.getCloseTime()!= null) {
                                boolean openLate = getTimeToClose(location.getCloseTime());
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

        gpsService = new GPSServiceImpl( this, this, GPSServiceImpl.getGPSHistory());
//
        bananaFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.banana);

        bananaFragment.getMapAsync( this);
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
        System.out.println("Review comment:" + review.getComment());
        ImageView userImage = (ImageView) card.findViewById(R.id.imageView2);

        return card;
    }

    @Override
    public void onGPSUpdate(Location location) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        int maxCameraZoom = 30;
        mMap.setMaxZoomPreference(maxCameraZoom);
        int minCameraZoom = 15;
        mMap.setMinZoomPreference(minCameraZoom);

        if (location != null) {
            LatLng currentLocation = location.getLocation();
            mMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title(location.getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, standardCameraZoom));
        } else {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(standardCameraZoom));
        }
    }

    private void showAddReviewDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_review_dialog);

        Button submitButton = dialog.findViewById(R.id.add_review_submit_Button);
        Button cancelButton = dialog.findViewById(R.id.add_review_cancel_Button);
        final EditText reviewEditText = dialog.findViewById(R.id.EditReview);
        final RatingBar ratingBar = dialog.findViewById(R.id.review_ratingBar);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close the dialog
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String review = reviewEditText.getText().toString();

                int rating = (int) ratingBar.getRating();

                if (rating == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a review", Toast.LENGTH_SHORT).show();
                    return;
                }
                //TODO: add review star rating (int rating) to database
                new Thread(){
                    public void run() {
                        try {
                            Review addedReview = new ReviewServiceImpl().addReview(userId,locationId,ReviewType.valueOf(location.getType()),rating,review);
                            System.out.println("Success");
//                            reloadActivity();
                        } catch (Exception e) {
                            System.out.println("Failed");
                            throw new RuntimeException(e);
                        }
                    }
                }.start();


                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void reloadActivity(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }
}