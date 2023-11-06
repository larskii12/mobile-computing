package com.comp90018.uninooks.activities;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.databinding.ActivityLocationBinding;
import com.comp90018.uninooks.databinding.ActivityMapsBinding;
import com.comp90018.uninooks.models.busy_rating.BusyRating;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.comp90018.uninooks.models.location.building.Building;
import com.comp90018.uninooks.models.location.resource.Resource;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.models.user.User;
import com.comp90018.uninooks.service.building.BuildingServiceImpl;
import com.comp90018.uninooks.service.busy_rating.BusyRatingServiceImpl;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.resource.ResourceServiceImpl;
import com.comp90018.uninooks.service.review.ReviewServiceImpl;
import com.comp90018.uninooks.service.study_space.StudySpaceServiceImpl;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.location.LocationService;
import com.comp90018.uninooks.service.review.ReviewService;
import com.comp90018.uninooks.service.user.UserServiceImpl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, GPSService{
    private static Context context;

    private String userName;
    private ActivityLocationBinding binding;
    GPSServiceImpl gpsService;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    SupportMapFragment bananaFragment;
    StudySpace space;

    private final int standardCameraZoom = 18;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = getApplicationContext();
        Intent intent = getIntent();
        String userID = intent.getStringExtra("USERID_EXTRA");
        String spaceID = intent.getStringExtra("SPACE_ID_EXTRA");
        System.out.println(spaceID);
        System.out.println(userID);

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
                    List<Review> reviews = new ReviewServiceImpl().getReviewsByEntity(Integer.valueOf(spaceID), ReviewType.valueOf(space.getType()));
                    System.out.println("Building id:" + space.getBuildingId());
                    Building building = new BuildingServiceImpl().getBuilding(space.getBuildingId(), ReviewType.valueOf(space.getType()));


                    LinearLayout reviewsLayout = findViewById(R.id.reviews);
                    TextView locationName = findViewById(R.id.textView5);
                    ProgressBar progress = findViewById(R.id.progressBar);
                    Double business = new BusyRatingServiceImpl().getAverageScoreFromEntity(Integer.valueOf(spaceID), ReviewType.valueOf(space.getType()));
                    Integer busyScore = (int) (business *20);
                    TextView progressValue = findViewById(R.id.textView7);
                    TextView distance = findViewById(R.id.distance);
                    TextView openHours = findViewById(R.id.openHours);
                    Button addReviewButton = findViewById(R.id.add_review);

                    ImageButton backButton = findViewById(R.id.imageButton);

                    User user = null;
                        System.out.println(userID);
                        user = new UserServiceImpl().getUser(Integer.parseInt(userID));

                    userName = user.getUserName();

                    Button listTitle = findViewById(R.id.listTitle);
                    LinearLayout amenitiesList = findViewById(R.id.amenities);
                    System.out.println("Building id: " + space.getBuildingId());

                    List<Resource> resources = new ResourceServiceImpl().getResourceFromBuilding(space.getBuildingId());
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
                                    intent.putExtra("USERID_EXTRA", userID);
                                    intent.putExtra("USERNAME_EXTRA", userName);
                                    startActivity(intent);
                                }
                            }.start();
                        }
                    });
                    listTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
//                                    amenitiesList.setVisibility(View.VISIBLE);
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

                        @Override
                        public void run() {
                            locationName.setText(space.getName());
                            progress.setProgress(busyScore);
                            progressValue.setText(busyScore + "%");
                            if (space.getCloseTime() == null) {
                                openHours.setText("Closed");
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                openHours.setText("Open hours: " + sdf.format(space.getOpenTime()) + " - " + sdf.format(space.getCloseTime()));
                            }
                            if (busyScore >= 0 && busyScore <= 40) {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                            } else if (busyScore > 40 && busyScore <= 80) {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow)));
                            } else {
                                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                            }
                            distance.setText(space.getDistanceFromCurrentPosition() + " m");
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

    private void showAddReviewDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_review_dialog);

        RatingBar ratingBar = (RatingBar) findViewById(R.id.select_rating_bar);
        Button submitButton = dialog.findViewById(R.id.add_review_submit_Button);
        Button cancelButton = dialog.findViewById(R.id.add_review_cancel_Button);
        final EditText reviewEditText = dialog.findViewById(R.id.EditReview);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser) {
                    //Todo: add rating to database
                    // The rating has been changed by the user
                    // Process the rating value
                    // You can now send this rating to your server or use it in your app
                    Toast.makeText(getApplicationContext(), "Given Rating: " + rating, Toast.LENGTH_SHORT).show();
                }
            }
        });


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
                // TODO: Process the review string
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    }

