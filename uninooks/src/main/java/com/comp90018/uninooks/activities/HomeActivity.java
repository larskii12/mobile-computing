package com.comp90018.uninooks.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import android.widget.Toast;
import com.comp90018.uninooks.activities.LoginActivity;
import com.comp90018.uninooks.R;
import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.busy_rating.BusyRating;
import com.comp90018.uninooks.models.favorite.Favorite;
import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.service.busy_rating.BusyRatingService;
import com.comp90018.uninooks.service.busy_rating.BusyRatingServiceImpl;
import com.comp90018.uninooks.service.favorite.FavoriteServiceImpl;
import com.comp90018.uninooks.service.location.LocationService;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.study_space.StudySpaceServiceImpl;
import com.google.android.gms.maps.model.LatLng;


public class HomeActivity extends AppCompatActivity {
    private LocationService locationAPI;

        private BusyRatingService busyRatings;

        // Declaring sensorManager
        // and acceleration constants
        private static Context context;

        private SensorManager sensorManager;
        private float acceleration = 0f;
        private float currentAcceleration = 0f;
        private float lastAcceleration = 0f;

        HashMap<String, Double> busyRatingsByLocation;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            context = getApplicationContext();
            Intent intent = getIntent();
            String username = intent.getStringExtra("USERNAME_EXTRA");
            String userID = intent.getStringExtra("USERID_EXTRA");
            System.out.println(userID);

            List<Location> studySpacesNearby = new ArrayList<>();
            List<Location> studySpacesTop = new ArrayList<>();

            busyRatingsByLocation = new HashMap<>();

            // Getting the Sensor Manager instance
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            Objects.requireNonNull(sensorManager).registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            acceleration = 10f;
            currentAcceleration = SensorManager.GRAVITY_EARTH;
            lastAcceleration = SensorManager.GRAVITY_EARTH;

            //Filter buttons at the top - These need on click functions
//            ImageButton studyButton = (ImageButton) findViewById(R.id.studyButton);
//            ImageButton foodButton = (ImageButton) findViewById(R.id.foodButton);
//            ImageButton favouritesButton = (ImageButton) findViewById(R.id.favouritesButton);

            TextView greetingMessage = (TextView) findViewById(R.id.textView);
            greetingMessage.setText("Good morning " + username);


            new Thread() {
                @Override
                public void run() {
                    try {
                        locationAPI = new LocationServiceImpl();
                        ArrayList<StudySpace> closestStudySpaces = new StudySpaceServiceImpl().getClosestStudySpaces(new LatLng(-1, -1), 10);
                        List<Location> studySpacesNearby = locationAPI.findAllLocations("STUDY", "", true);
                        List<Favorite> favouriteSpaces = new FavoriteServiceImpl().getFavoritesByUser(Integer.parseInt(userID), ReviewType.valueOf("STUDY_SPACE"));
                        List<StudySpace> favorites = new ArrayList<StudySpace>();
                        for (Favorite favorite: favouriteSpaces) {
                            StudySpace space = new LocationServiceImpl().findStudySpaceById(favorite.getStudySpaceId());
                            favorites.add(space);
                        }
                        getAllBusyRatings(closestStudySpaces);
//                        List<Favorite> userFavorites = new FavoriteServiceImpl().getFavoritesByUser(Integer.parseInt(userID), ReviewType.valueOf("STUDY_SPACES"));
                        System.out.println(studySpacesNearby.get(2).getName());
                        LinearLayout nearbyLayout = findViewById(R.id.nearbyLayout);
                        LinearLayout topRatedLayout = findViewById(R.id.topRatedLayout);
                        LinearLayout favoritesLayout = findViewById(R.id.favoritesLayout);
//                    int i=0; i<5; i++)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (StudySpace space : closestStudySpaces){
                                    CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.small_card_layout, nearbyLayout, false);
                                    CardView newCard = createNewSmallCard(card,space);
//                                    System.out.println(space.getId());
                                    String spaceID = String.valueOf(space.getId());
                                    nearbyLayout.addView(newCard);
                                    newCard.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        new Thread() {
                                            public void run() {
                                            Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                                            intent.putExtra("SPACE_ID_EXTRA", spaceID);
                                            System.out.println(spaceID);
                                            intent.putExtra("USERID_EXTRA", String.valueOf(userID));
                                            System.out.println(userID);
                                            startActivity(intent);
                                            }
                                        }.start();
                                        }
                                    });
                                }
                                for (StudySpace space : closestStudySpaces){
//                        Location space = studySpacesNearby.get(i);
                                    CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.small_card_layout, topRatedLayout, false);
                                    CardView newCard = createNewSmallCard(card,space);
                                    String spaceID = String.valueOf(space.getId());
                                    topRatedLayout.addView(newCard);
                                    card.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new Thread() {
                                                public void run() {
                                                    Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                                                    intent.putExtra("SPACE_ID_EXTRA", spaceID);
                                                    System.out.println(spaceID);
                                                    intent.putExtra("USERID_EXTRA", String.valueOf(userID));
                                                    System.out.println(userID);
                                                    startActivity(intent);
                                                }
                                            }.start();
                                        }
                                    });
                                }
                                for (StudySpace space : favorites){
//                        Location space = studySpacesNearby.get(i);
                                    CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.small_card_layout, favoritesLayout, false);
                                    CardView newCard = createNewSmallCard(card,space);
                                    String spaceID = String.valueOf(space.getId());
                                    favoritesLayout.addView(newCard);
                                    card.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new Thread() {
                                                public void run() {
                                                    Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                                                    intent.putExtra("SPACE_ID_EXTRA", spaceID);
                                                    System.out.println(spaceID);
                                                    intent.putExtra("USERID_EXTRA", String.valueOf(userID));
                                                    System.out.println(userID);
                                                    startActivity(intent);
                                                }
                                            }.start();
                                        }
                                    });
                                }
                            }
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }.start();

        }

        private void getAllBusyRatings(ArrayList<StudySpace> spaces) {
            for (StudySpace space : spaces) {
                String spaceName = space.getName();
                try {
                    int locationID = space.getId();
                    ReviewType type = ReviewType.valueOf(space.getType());
                    Double busyScore = new BusyRatingServiceImpl().getAverageScoreFromEntity(locationID, type);
                    busyRatingsByLocation.put(spaceName, busyScore);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
        }

        private CardView createNewSmallCard(CardView card, StudySpace space){
            ImageView banner = (ImageView) card.findViewById(R.id.banner);
            banner.setBackgroundResource(R.drawable.old_engineering);
            ProgressBar progress = (ProgressBar) card.findViewById(R.id.progressBar);
            Double score = busyRatingsByLocation.get(space.getName());
            Integer busyScore = (int) (score *20);
            progress.setProgress(busyScore);
            if (busyScore >= 0 && busyScore <= 40) {
                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            } else if (busyScore > 40 && busyScore <= 75) {
                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow)));
            } else {
                progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
            }
            TextView locationName = (TextView) card.findViewById(R.id.location);
            locationName.setText(space.getName());
            TextView locationHours = (TextView) card.findViewById(R.id.hours);
            TextView distanceLabel = (TextView) card.findViewById(R.id.timeLabel);
            ImageView hoursIcon = (ImageView) card.findViewById(R.id.clockIcon);
            hoursIcon.setBackgroundResource(R.drawable.baseline_access_time_24);
            if (space.getCloseTime() == null) {
                locationHours.setText("Closed");
                hoursIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                double hoursToClose = getTimeToClose(space.getCloseTime());
                System.out.println("Hours to close: " + hoursToClose);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                locationHours.setText(sdf.format(space.getOpenTime()) + " - " + sdf.format(space.getCloseTime()));
                hoursIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.deepBlue), android.graphics.PorterDuff.Mode.SRC_IN);

                if (hoursToClose > 1){
                    hoursIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.deepBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    hoursIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
            distanceLabel.setText(space.getDistanceFromCurrentPosition() + " m");

            ImageView favouriteIcon = (ImageView) card.findViewById(R.id.favouriteIcon);
            favouriteIcon.setBackgroundResource(R.drawable.baseline_favorite_border_24);
            favouriteIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
//        favouriteIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.red));
            //check if the favourites list includes this user and favourite
            return card;
        }

        private double getTimeToClose(Time closeTime){
            Date today = new Date();
            Time currentTime = new Time(today.getTime());
            int closeHour = closeTime.getHours();
            if (closeHour == 0) {
                closeHour = 24;
            }
            long timeDifference = (closeHour -currentTime.getHours()) * 3600
                    + (closeTime.getMinutes() - currentTime.getMinutes()) * 60
                    + (closeTime.getSeconds() - currentTime.getSeconds());
            double hours = (double) timeDifference / 3600;
            return hours;
        }
        private final SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                lastAcceleration = currentAcceleration;
                currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = currentAcceleration - lastAcceleration;
                acceleration = acceleration * 0.9f + delta;
                if (acceleration > 12) {
                    Toast.makeText(getApplicationContext(), "Shake event detected", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        @Override
        protected void onResume() {
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            super.onResume();
        }
        @Override
        protected void onPause() {
            sensorManager.unregisterListener(sensorListener);
            super.onPause();
        }
        public static Context getAppContext() {
            return context;
        }


}

