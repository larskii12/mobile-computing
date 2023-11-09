package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatDelegate;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, GPSService {
    private final int standardCameraZoom = 18;
    private final int maxWalkDistance = 1000 * 20;
    GPSServiceImpl gpsService;
    SupportMapFragment bananaFragment;
    com.comp90018.uninooks.models.location.Location location;
    boolean isFavorite;
    private Context context;
    private ActivityLocationBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private int userId;
    private String userEmail;
    private String userName;
    private int locationId;

    private String locationType;
    private boolean showingReviews;
    private double averageRating;
    private boolean favouriteChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        context = getApplicationContext();

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");
        locationType = intent.getStringExtra("LOCATION_TYPE");

        if (locationType.equals("LIBRARY")) {
            location = intent.getParcelableExtra("LOCATION");
        } else if (locationType.equals("STUDY_SPACE")) {
            location = intent.getParcelableExtra("LOCATION");
        } else {
            location = intent.getParcelableExtra("LOCATION");
        }
        locationId = location.getId();

        new Thread() {
            @Override
            public void run() {
                try {

                    List<Review> reviews = new ReviewServiceImpl().getReviewsByEntity(locationId, ReviewType.valueOf(location.getType()));

                    Building building = new BuildingServiceImpl().getBuilding(location.getBuildingId(), ReviewType.valueOf(location.getType()));
                    List<Favorite> favorites = new FavoriteServiceImpl().getFavoritesByUser(userId, ReviewType.valueOf(location.getType()));
                    isFavorite = false;
                    favouriteChanged = false;
                    for (Favorite favorite : favorites) {
                        if (location.getType().equals("LIBRARY") && favorite.getLibraryId() == location.getId()) {
                            isFavorite = true;
                        } else if (location.getType().equals("STUDY_SPACE") && favorite.getStudySpaceId() == location.getId()) {
                            isFavorite = true;
                        } else if (location.getType().equals("RESTAURANT") && favorite.getRestaurantId() == location.getId()) {
                            isFavorite = true;
                        }

                    }

                    LinearLayout reviewsLayout = findViewById(R.id.reviews);
                    TextView locationName = findViewById(R.id.textView5);
                    ProgressBar progress = findViewById(R.id.progressBar);
                    Double business = new BusyRatingServiceImpl().getAverageScoreFromEntity(locationId, ReviewType.valueOf(location.getType()));
                    Integer busyScore = location.isOpeningNow() ? (int) (business * 20) : 0;
                    TextView progressValue = findViewById(R.id.textView7);
                    TextView distance = findViewById(R.id.distance);
                    TextView openHours = findViewById(R.id.openHours);
                    TextView reviewTitle = findViewById(R.id.reviewTitle);
                    Button addReviewButton = findViewById(R.id.add_review);
                    TextView ratingText = findViewById(R.id.rating_Text);
                    RatingBar aveRatingBar = findViewById(R.id.averageRatingBar);
                    averageRating = new ReviewServiceImpl().getAverageRating(locationId, ReviewType.valueOf(location.getType()));
//                    ratingAsInt = (int) averageRating;

                    ImageButton backButton = findViewById(R.id.imageButton);
                    ImageButton favouriteButton = findViewById(R.id.favoriteButton);
                    ImageButton locationButton = findViewById(R.id.locate_my_location);

                    Button addReview = findViewById(R.id.add_review);
                    Button showReviews = findViewById(R.id.show_reviews);
                    showingReviews = false;

                    TextView listTitle = findViewById(R.id.listTitle);
                    LinearLayout amenitiesList = findViewById(R.id.amenities);

                    List<Resource> resources = new ResourceServiceImpl().getResourceFromBuilding(location.getBuildingId());
                    locationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!GPSServiceImpl.getGPSPermission()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                                builder.setTitle("Notice");
                                builder.setMessage("To utilize the navigation function, you must grant permission for Precision Location Access.\n\nPlease navigate to the application settings to activate this feature.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.show();
                            }

                            else if (location.getDistanceFromCurrentPosition() >= maxWalkDistance) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                                builder.setTitle("Notice");
                                builder.setMessage("Apologies, we are unable to provide walking directions for distances exceeding 20 kilometers. For such distances, we recommend considering public transportation options.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.show();
                            } else {
                                Intent intent = new Intent(LocationActivity.this, NavigationActivity.class);

                                // Pass the user to next page
                                intent.putExtra("USER_ID_EXTRA", userId);
                                intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                                intent.putExtra("USER_NAME_EXTRA", userName);
                                intent.putExtra("LATITUDE", location.getLocation().latitude);
                                intent.putExtra("LONGITUDE", location.getLocation().longitude);
                                intent.putExtra("LOCATION_NAME", location.getName());

                                startActivity(intent);
                            }
                        }
                    });
                    //goes back to the home page
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (favouriteChanged) {
                                Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
                                intent.putExtra("USER_ID_EXTRA", userId);
                                intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                                intent.putExtra("USER_NAME_EXTRA", userName);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            finish();
                        }
                    });
                    //add or removes a location from the users favourites
                    favouriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    try {
                                        if (!isFavorite) {
                                            new FavoriteServiceImpl().addFavorite(userId, locationId, ReviewType.valueOf(location.getType()));
                                            isFavorite = true;
                                            favouriteChanged = !favouriteChanged;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_32);
                                                    favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red), PorterDuff.Mode.SRC_IN);
                                                }
                                            });
                                        } else {
                                            new FavoriteServiceImpl().removeFavorite(userId, locationId, ReviewType.valueOf(location.getType()));
                                            isFavorite = false;
                                            favouriteChanged = !favouriteChanged;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_32);
                                                    favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.deepBlue), PorterDuff.Mode.SRC_IN);
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
                    //shows and hides all the reviews
                    showReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            runOnUiThread(new Runnable() {
                                @SuppressLint("StringFormatInvalid")
                                public void run() {
                                    if (!showingReviews) {
                                        reviewsLayout.setVisibility(View.VISIBLE);
                                        showReviews.setText(getString(R.string.hide_reviews, "Hide Reviews"));
                                        showingReviews = true;
                                    } else {
                                        reviewsLayout.setVisibility(View.GONE);
                                        showReviews.setText(getString(R.string.show_reviews, "Show Reviews"));
                                        showingReviews = false;
                                    }
                                }
                            });
                        }
                    });

                    //open the add review dialog
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
                            locationButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_circle_button));
                            locationName.setText(location.getName());
                            listTitle.setText("Facilities");
                            progress.setProgress(busyScore);
                            progressValue.setText(busyScore + "%");
                            reviewTitle.setText("Reviews (" + reviews.size() + ")");
                            ratingText.setText("" + averageRating);
                            aveRatingBar.setRating((float) averageRating);
                            showReviews.setPaintFlags(showReviews.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            if (isFavorite) {
                                favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_32);
                                favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red), PorterDuff.Mode.SRC_IN);
                            } else {
                                favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_32);
                                favouriteButton.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.deepBlue), PorterDuff.Mode.SRC_IN);
                            }
                            if (location.getCloseTime() == null) {
                                openHours.setText("Close today");
                            } else if (location.getCloseTime() != null && location.isOpeningNow()) {
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

                            if (location.getDistanceFromCurrentPosition() == -1 || !GPSServiceImpl.getGPSPermission()) {
                                distance.setText("N/A");
                            } else {
                                distance.setText(location.getDistanceFromCurrentPosition() + "m");
                            }
                            for (Review review : reviews) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.review_layout, reviewsLayout, false);
                                CardView newCard = createNewSmallCard(card, review);
                                reviewsLayout.addView(newCard);
                            }
                            for (Resource resource : resources) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = card.findViewById(R.id.expandedListItem);
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
                            if (location instanceof StudySpace && ((StudySpace) location).isTalkAllowed()) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Discussion allowed");
                                resourceIcon.setBackgroundResource(R.drawable.volume_outline);
                                amenitiesList.addView(card);
                            }
                            if (location instanceof StudySpace && ((StudySpace) location).getMinimumAccessAQFLevel() > 7) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Graduate student space");
                                resourceIcon.setBackgroundResource(R.drawable.gradspace_outline);
                                amenitiesList.addView(card);
                            }
                            if (location.getCloseTime() != null) {
                                boolean openLate = getTimeToClose(location.getCloseTime());
                                if (openLate) {
                                    CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                    TextView resourceDef = card.findViewById(R.id.expandedListItem);
                                    ImageView resourceIcon = card.findViewById(R.id.icon);
                                    resourceDef.setText("After hours access");
                                    resourceIcon.setBackgroundResource(R.drawable.lateaccess_outline);
                                    amenitiesList.addView(card);
                                }
                            }
                            //check if the building is accessible and add that too
                            if (building.isHasAccessibility()) {
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.amenity_layout, amenitiesList, false);
                                TextView resourceDef = card.findViewById(R.id.expandedListItem);
                                ImageView resourceIcon = card.findViewById(R.id.icon);
                                resourceDef.setText("Accessible Building");
                                resourceIcon.setBackgroundResource(R.drawable.accessible_outline);
                                amenitiesList.addView(card);
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }.start();

        gpsService = new GPSServiceImpl(this, this, GPSServiceImpl.getGPSHistory());
//
        bananaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.banana);

        bananaFragment.getMapAsync(this);
    }

    /**
     * When back button pressed
     */
    public void onBackPressed() {
        if (favouriteChanged) {
            Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
            intent.putExtra("USER_ID_EXTRA", userId);
            intent.putExtra("USER_EMAIL_EXTRA", userEmail);
            intent.putExtra("USER_NAME_EXTRA", userName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }

    //calculates the time to closing
    private boolean getTimeToClose(Time closeTime) {
        Time currentTime = Time.valueOf("18:00:00");
        int closeHour = closeTime.getHours();
        if (closeHour == 0) {
            closeHour = 24;
        }
        return closeHour - currentTime.getHours() > 0;
    }

    //creates a new comment card
    private CardView createNewSmallCard(CardView card, Review review) {
        TextView userComment = card.findViewById(R.id.textView);
        TextView datePosted = card.findViewById(R.id.date_posted);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(review.getDate());
        datePosted.setText(date);
        userComment.setText(review.getComment());
        RatingBar rating = card.findViewById(R.id.ratingBar);
        rating.setRating(review.getScore());
        ImageView userImage = card.findViewById(R.id.imageView2);

        return card;
    }

    @Override
    public void onGPSUpdate(Location location) {

    }

    //opens map with the location
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(18);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        if (location != null) {
            LatLng currentLocation = location.getLocation();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title(location.getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, standardCameraZoom));
        } else {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(standardCameraZoom));
        }
    }

    //Open the dialog to add a new review
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
                new Thread() {
                    public void run() {
                        try {
                            Review addedReview = new ReviewServiceImpl().addReview(userId, locationId, ReviewType.valueOf(location.getType()), rating, review);
                            reloadActivity();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();


                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Reloads the page activity
    private void reloadActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("USER_ID_EXTRA", userId);
        intent.putExtra("USER_EMAIL_EXTRA", userEmail);
        intent.putExtra("USER_NAME_EXTRA", userName);
        intent.putExtra("LOCATION_TYPE", location.getType());
        intent.putExtra("LOCATION", location);
        finish();
        startActivity(intent);
    }
}