package com.example.mainactivity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.mainactivity.R;
import com.example.mainactivity.models.location.Location;
import com.example.mainactivity.models.location.library.Library;
import com.example.mainactivity.models.location.resource.Resource;
import com.example.mainactivity.models.location.resource.ResourceType;
import com.example.mainactivity.models.location.restaurant.Restaurant;
import com.example.mainactivity.models.location.study_space.StudySpace;

import com.example.mainactivity.service.location.LocationService;
import com.example.mainactivity.service.location.LocationServiceImpl;
import com.example.mainactivity.service.resource.ResourceService;
import com.example.mainactivity.service.resource.ResourceServiceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchResults extends AppCompatActivity {
//    private final LocationServiceImpl searchAPI = new LocationServiceImpl();
//    private final ResourceServiceImpl resourceAPI = new ResourceServiceImpl();
    LinearLayout resultCardArea;
    List<Location> results = new ArrayList<>();

    CardView cardView;
    HashMap<Integer, List<Object>> locationToUI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        // retrieve what was searched for/filtered for
        Intent intent = getIntent();
        String searchString = intent.getStringExtra("searchQuery");

        Toast.makeText(getApplicationContext(), searchString, Toast.LENGTH_SHORT).show();
        resultCardArea = findViewById(R.id.resultCardArea);

        // use API to get the search result
        // study includes both libraries and study spaces
//
        new Thread() {
            @Override
            public void run() {
                try {
                    results = new LocationServiceImpl().findAllLocations("STUDY", searchString, true);

//                    if (results.size() != 0) {
//                        // display something that says "no available results" --> go back to previous page
//                    }

                    Location onePlace = results.get(0);
                    String locName = onePlace.getName();
                    Toast.makeText(getApplicationContext(), locName, Toast.LENGTH_SHORT).show();
                    addResultsToPage(results);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        // have to move this to another thread....
//        try {
//            results = searchAPI.findAllLocations("STUDY", searchString, true);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        for (Location location : results) {
//            if (location instanceof StudySpace) {
//                cardView = createStudyLocationCard((StudySpace) location);
//            } else if (location instanceof Library) {
//                cardView = createLibraryLocationCard((Library) location);
//            } else {
//                cardView = createRestaurantLocationCard((Restaurant) location);
//            }
//            resultCardArea.addView(cardView);
//        }
    }
    private void addResultsToPage(List<Location> results) {
        for (Location location : results) {
            if (location instanceof StudySpace) {
                cardView = createStudyLocationCard((StudySpace) location);
            } else if (location instanceof Library) {
                cardView = createLibraryLocationCard((Library) location);
            } else {
                cardView = createRestaurantLocationCard((Restaurant) location);
            }
            resultCardArea.addView(cardView);
        }
    }

    private CardView createStudyLocationCard(StudySpace location) {
        List<Object> interactables = new ArrayList<>();
        int buildingID = location.getBuildingId();
        int locationID = location.getId();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, resultCardArea, true);
        LinearLayout facilities = findViewById(R.id.facilitiesIcons);
        TextView cardTitle = findViewById(R.id.title);
        TextView distanceAway = findViewById(R.id.distanceText);
        TextView openingHours = findViewById(R.id.openingHours);
        ProgressBar capacity = findViewById(R.id.progressBar);
        ImageView closingIcon = findViewById(R.id.closingIcon);
        ImageView locImage = findViewById(R.id.locImage);
        ImageButton addToFavButton = findViewById(R.id.addToFavButton);
        TextView ratingText = findViewById(R.id.ratingText);

        interactables.add(capacity);
        interactables.add(closingIcon);
        locationToUI.put(locationID, interactables);

        // first set the title
        setTitle(location, cardTitle);

        // set the distance away!!!!!

        // set the clock colour
        updateClockColour(location);

        // set opening hours text
        setOpeningHoursText(location, openingHours);

        // set rating text!!!!!


        // set icons for facilities available



        try {
            List<Resource> studySpaceResources = new ResourceServiceImpl().getResourceFromBuilding(buildingID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // I would need the location of the Location Type - maybe can just use google maps API

        return locationCard;
    }


    private CardView createLibraryLocationCard(Library location) {
        List<Object> interactables = new ArrayList<>();
        int buildingID = location.getBuildingId();
        int locationID = location.getId();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, resultCardArea, true);
        LinearLayout facilities = findViewById(R.id.facilitiesIcons);
        TextView cardTitle = findViewById(R.id.title);
        TextView distanceAway = findViewById(R.id.distanceText);
        TextView openingHours = findViewById(R.id.openingHours);
        ProgressBar capacity = findViewById(R.id.progressBar);
        ImageView closingIcon = findViewById(R.id.closingIcon);
        ImageView locImage = findViewById(R.id.locImage);
        ImageButton addToFavButton = findViewById(R.id.addToFavButton);
        TextView ratingText = findViewById(R.id.ratingText);

        interactables.add(capacity);
        interactables.add(closingIcon);
        locationToUI.put(locationID, interactables);

        // first set the title
        setTitle(location, cardTitle);

        // set the distance away!!!!!!

        // set the clock colour
        updateClockColour(location);

        // set opening hours text
        setOpeningHoursText(location, openingHours);

        // set rating text!!!!!!


        // set icons for facilities available
//
        try {
            List<Resource> libraryResources = new ResourceServiceImpl().getResourceFromBuilding(buildingID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return locationCard;
    }


    /**
     * Each location put in hash map from locationID --> [capacity, closingIcon]
     * @param location
     * @return
     */
    private CardView createRestaurantLocationCard(Restaurant location) {
        List<Object> interactables = new ArrayList<>();
        int buildingID = location.getBuildingId();
        int locationID = location.getId();

        LinearLayout facilities = findViewById(R.id.facilitiesIcons);
        TextView cardTitle = findViewById(R.id.title);
        TextView distanceAway = findViewById(R.id.distanceText);
        TextView openingHours = findViewById(R.id.openingHours);
        ProgressBar capacity = findViewById(R.id.progressBar);
        ImageView closingIcon = findViewById(R.id.closingIcon);
        ImageView locImage = findViewById(R.id.locImage);
        ImageButton addToFavButton = findViewById(R.id.addToFavButton);
        TextView ratingText = findViewById(R.id.ratingText);

        CardView locationCard = (CardView) LayoutInflater.from(this).
                inflate(R.layout.large_location_card, resultCardArea, true);

        interactables.add(capacity);
        interactables.add(closingIcon);
        locationToUI.put(locationID, interactables);

        // first set the title
        setTitle(location, cardTitle);

        // set the progress bar capacity -- have the max capacity of each building?

        // set the distance away!!!!

        // set the clock colour
        updateClockColour(location);

        // set opening hours text
        setOpeningHoursText(location, openingHours);

        // set rating text!!!!!! 5: VERY BUSY; 1 NOT BUSY AT ALL (accefss from


        // set icons for facilities available
//
        try {
            List<Resource> restaurantResources = new ResourceServiceImpl().getResourceFromBuilding(buildingID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return locationCard;
    }

    private void setTitle(Location location, TextView cardTitle) {
        String title = location.getName();
        cardTitle.setText(title);
    }

    private void setOpeningHoursText(Location location, TextView openingHours) {
        Time openingTime = location.getOpenTime();
        Time closingTime = location.getCloseTime();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String openingTimeText = sdf.format(openingTime);
        String closingTimeText = sdf.format(closingTime);

        String text = "Opening Hours: " + openingTimeText + " - " + closingTimeText;
        openingHours.setText(text);
    }

    /**
     * Wait for Lara's API
     * @param location
     * @param ratings
     */
    private void setRatingsText(Location location, TextView ratings) {

    }

    private void addIconsToFacilities(List<Resource> resources, LinearLayout facilities) {
        for (Resource resource : resources) {
            ResourceType resourceType = resource.getType();

            switch(resourceType) {
                case ATM:
                    System.out.println("add ATM icon");
                    break;
                case PARKING:
                    System.out.println("add Parking icon");
                    break;
                case KITCHEN:
                    System.out.println("add MICROWAVE icon");
                    break;
                case VENDING_MACHINE:
                    System.out.println("add vending machine icon");

            }
        }
    }

    private long calcTimeToClose(Time closingTime) {
        Date date = new Date();
        Time currTime = new Time(date.getTime());

        Long timeDiff = TimeUnit.MILLISECONDS.toHours(closingTime.getTime() - currTime.getTime());
        return timeDiff;
    }

    private void updateClockColour(Location location) {
        Time closingTime = location.getCloseTime();
        Long timeDiff = calcTimeToClose(closingTime);

        int locationID = location.getId();
        ImageView closingIcon = getClosingIcon(locationID);

        if (timeDiff <= 1) {
            closingIcon.setColorFilter(ContextCompat.getColor(MainActivity.getAppContext(),
                    R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            // setColorFilter(getApplicationContext().getResources().getColor.red));
        } else {
            closingIcon.setColorFilter(ContextCompat.getColor(MainActivity.getAppContext(),
                    R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private ImageView getClosingIcon(int locationID) {
        List<Object> interactables = locationToUI.get(locationID);
        ImageView icon = (ImageView) interactables.get(1);
        return icon;
    }

    private ProgressBar getProgressBar(int locationID) {
        List<Object> interactables = locationToUI.get(locationID);
        ProgressBar capacity = (ProgressBar) interactables.get(0);
        return capacity;
    }




}