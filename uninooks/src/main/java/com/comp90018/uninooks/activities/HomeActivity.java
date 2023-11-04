package com.comp90018.uninooks.activities;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import android.widget.Toast;
import com.comp90018.uninooks.activities.LoginActivity;
import com.comp90018.uninooks.R;
import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.favorite.Favorite;
import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.service.busy_rating.BusyRatingService;
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


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            context = getApplicationContext();
            Intent intent = getIntent();
            String username = intent.getStringExtra("USERNAME_EXTRA");
            String userID = intent.getStringExtra("USERID_EXTRA");

            List<Location> studySpacesNearby = new ArrayList<>();
            List<Location> studySpacesTop = new ArrayList<>();

            // Getting the Sensor Manager instance
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            Objects.requireNonNull(sensorManager).registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            acceleration = 10f;
            currentAcceleration = SensorManager.GRAVITY_EARTH;
            lastAcceleration = SensorManager.GRAVITY_EARTH;

            //Filter buttons at the top - These need on click functions
            ImageButton studyButton = (ImageButton) findViewById(R.id.studyButton);
            ImageButton foodButton = (ImageButton) findViewById(R.id.foodButton);
            ImageButton favouritesButton = (ImageButton) findViewById(R.id.favouritesButton);

            TextView greetingMessage = (TextView) findViewById(R.id.textView);
            greetingMessage.setText("Good morning " + username);


            new Thread() {
                @Override
                public void run() {
                    try {
                        locationAPI = new LocationServiceImpl();
                        ArrayList<StudySpace> closestStudySpaces = new StudySpaceServiceImpl().getClosestStudySpaces(new LatLng(-1, -1), 10);
                        List<Location> studySpacesNearby = locationAPI.findAllLocations("STUDY", "", true);
//                        List<Favorite> userFavorites = new FavoriteServiceImpl().getFavoritesByUser(Integer.parseInt(userID), ReviewType.valueOf("STUDY_SPACES"));
                        System.out.println(studySpacesNearby.get(2).getName());
                        LinearLayout nearbyLayout = findViewById(R.id.nearbyLayout);
                        LinearLayout topRatedLayout = findViewById(R.id.topRatedLayout);
//                    int i=0; i<5; i++)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (StudySpace space : closestStudySpaces){
//                        Location space = studySpacesNearby.get(i);
                                    CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.small_card_layout, nearbyLayout, false);
                                    CardView newCard = createNewSmallCard(card,space);
                                    nearbyLayout.addView(newCard);
                                    card.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        new Thread() {
                                            public void run() {
                                            Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                                            intent.putExtra("SPACE_ID_EXTRA", space.getId());
//                                            intent.putExtra("SPACE_NAME_EXTRA", space.getName());
//                                            intent.putExtra("BUILDING_ID_EXTRA", space.getBuildingId());
//                                            intent.putExtra("LIBRARY_ID_EXTRA", space.getLibraryId());
//                                            intent.putExtra("DISTANCE_EXTRA", space.getDistanceFromCurrentPosition());
//                                            intent.putExtra("OPEN_EXTRA", space.getOpenTime());
//                                            intent.putExtra("CLOSE_EXTRA", space.getCloseTime());
                                            intent.putExtra("USERNAME_EXTRA", userID);
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
                                    topRatedLayout.addView(newCard);
                                    card.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new Thread() {
                                                public void run() {
//                                        Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
//                                        intent.putExtra("LOCATION_EXTRA", space.getBuildingId());
////                            intent.putExtra("USERNAME_EXTRA", logInUser.getUserName());
//                                        startActivity(intent);
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

        private CardView createNewSmallCard(CardView card, StudySpace space){
            ImageView banner = (ImageView) card.findViewById(R.id.banner);
            banner.setBackgroundResource(R.drawable.old_engineering);
            ProgressBar progress = (ProgressBar) card.findViewById(R.id.progressBar);
            TextView locationName = (TextView) card.findViewById(R.id.location);
            locationName.setText(space.getName());
            TextView locationHours = (TextView) card.findViewById(R.id.hours);
            TextView distanceLabel = (TextView) card.findViewById(R.id.timeLabel);

            long hoursToClose = getTimeToClose(space.getCloseTime());
            locationHours.setText(space.getOpenTime().getTime() + " - " + space.getCloseTime().getTime());
            distanceLabel.setText(space.getDistanceFromCurrentPosition() + " meters");
//            locationHours.setText(space.getOpenTime() +"am - " + space.getCloseTime() + "pm");
            ImageView hoursIcon = (ImageView) card.findViewById(R.id.clockIcon);
            hoursIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.deepBlue), android.graphics.PorterDuff.Mode.SRC_IN);
            hoursIcon.setBackgroundResource(R.drawable.baseline_access_time_24);
            if(hoursToClose <= 1){
//                hoursIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.red));
                hoursIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                hoursIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.deepBlue), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            ImageView favouriteIcon = (ImageView) card.findViewById(R.id.favouriteIcon);
//            if ()
            favouriteIcon.setBackgroundResource(R.drawable.baseline_favorite_border_24);
            favouriteIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
//        favouriteIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.red));
            //check if the favourites list includes this user and favourite
            return card;
        }

        private long getTimeToClose(Time closeTime){
            Date today = new Date();
            Time currentTime = new Time(today.getTime());
            long timeDifference = closeTime.getTime() - currentTime.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
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

