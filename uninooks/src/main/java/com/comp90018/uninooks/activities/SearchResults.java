package com.comp90018.uninooks.activities;

import android.content.Intent;
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

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.location.library.Library;
import com.comp90018.uninooks.models.location.resource.Resource;
import com.comp90018.uninooks.models.location.resource.ResourceType;
import com.comp90018.uninooks.models.location.restaurant.Restaurant;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.resource.ResourceServiceImpl;
import com.comp90018.uninooks.service.review.ReviewServiceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchResults extends AppCompatActivity {
    LinearLayout resultCardArea;
    List<Location> results;

    CardView cardView;
    ImageButton returnButton;
    HashMap<Integer, List<Object>> locationToUI;
    HashMap<String, String> ratingsByLocation;
    HashMap<String, List<Resource>> resourcesByLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);
        Intent intent = getIntent();


//        String searchString = intent.getStringExtra("searchQuery");
//
//        Toast.makeText(getApplicationContext(), searchString, Toast.LENGTH_SHORT).show();

        resultCardArea = findViewById(R.id.resultCardArea);
        returnButton = findViewById(R.id.returnButton);
        results = new ArrayList<>();
        locationToUI = new HashMap<>();
        ratingsByLocation = new HashMap<>();
        resourcesByLocation = new HashMap<>();

//        try {
////            results = new LocationServiceImpl().findAllLocations("STUDY", searchString, true);
//            // get user and user id, and their favourited places
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        new Thread() {
            @Override
            public void run() {
                try {
                    // retrieve what was searched for/filtered for
                    if (intent.hasExtra("searchQuery")) {
                        String searchString = intent.getStringExtra("searchQuery");
                        System.out.println(searchString);
                        results = new LocationServiceImpl().findAllLocations("STUDY", searchString, true);


                    } else if (intent.hasExtra("filters")) {
                        String[] filters = intent.getStringArrayExtra("filters");
                    }


                    // have to get all the ratings here (any other API I call, have to be done here and not in the UI thread)
                    getAllRatings(results);
                    getAllResources(results);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (results.size() == 0) {
                                TextView noResults = findViewById(R.id.noResults);
                                noResults.setVisibility(View.VISIBLE);
                            } else {
                                addResultsToPage(results);
                            }

                        }
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        returnButton.setOnClickListener(returnListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Location location : results) {
            updateClockColour(location);
        }
    }

    /**
     * This activity finishes, returns back to the previous page (search page)
     */
    private View.OnClickListener returnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private void addResultsToPage(List<Location> results) {

        for (Location location : results) {
            System.out.println(location.getType());
            String type = location.getType();

            System.out.println(location.getName());
            System.out.println("type: " + type);


            if (type.equals("LIBRARY")) {
                cardView = createLibraryLocationCard((Library) location);
            } else if (type.equals("STUDY_SPACE")) {
                cardView = createStudyLocationCard((StudySpace) location);
            } else if (type.equals("RESTAURANT")) {
                cardView = createRestaurantLocationCard((Restaurant) location);
            }
            resultCardArea.addView(cardView);
        }
    }

    private CardView createStudyLocationCard(StudySpace location) {
        List<Object> interactables = new ArrayList<>();
        int buildingID = location.getBuildingId();
        int locationID = location.getId();
        String locationName = location.getName();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, null);
        LinearLayout facilities = locationCard.findViewById(R.id.facilitiesIcons);
        TextView cardTitle = locationCard.findViewById(R.id.title);
        TextView distanceAway = locationCard.findViewById(R.id.distanceText);
        TextView openingHours = locationCard.findViewById(R.id.openingHours);
        ProgressBar capacity = locationCard.findViewById(R.id.progressBar);
        ImageView closingIcon = locationCard.findViewById(R.id.closingIcon);
        ImageView locImage = locationCard.findViewById(R.id.locImage);
        ImageButton favButton = locationCard.findViewById(R.id.favButton);
        TextView ratingText = locationCard.findViewById(R.id.ratingText);

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

        // set rating text
        setRatingsText(location, ratingText);

        // set busy progress bar

        // set icons for facilities available
        List<Resource> resources = resourcesByLocation.get(locationName);
        addIconsToFacilities(resources, facilities);

        return locationCard;
    }


    private CardView createLibraryLocationCard(Library location) {
        List<Object> interactables = new ArrayList<>();
        int buildingID = location.getBuildingId();
        int locationID = location.getId();
        String locationName = location.getName();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, null);
        LinearLayout facilities = locationCard.findViewById(R.id.facilitiesIcons);
        TextView cardTitle = locationCard.findViewById(R.id.title);
        TextView distanceAway = locationCard.findViewById(R.id.distanceText);
        TextView openingHours = locationCard.findViewById(R.id.openingHours);
        ProgressBar capacity = locationCard.findViewById(R.id.progressBar);
        ImageView closingIcon = locationCard.findViewById(R.id.closingIcon);
        ImageView locImage = locationCard.findViewById(R.id.locImage);
        ImageButton favButton = locationCard.findViewById(R.id.favButton);
        TextView ratingText = locationCard.findViewById(R.id.ratingText);

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
        setRatingsText(location, ratingText);

        // set busy progress bar

        // set icons for facilities available
        List<Resource> resources = resourcesByLocation.get(locationName);
        addIconsToFacilities(resources, facilities);

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
        String locationName = location.getName();

        LinearLayout facilities = findViewById(R.id.facilitiesIcons);
        TextView cardTitle = findViewById(R.id.title);
        TextView distanceAway = findViewById(R.id.distanceText);
        TextView openingHours = findViewById(R.id.openingHours);
        ProgressBar capacity = findViewById(R.id.progressBar);
        ImageView closingIcon = findViewById(R.id.closingIcon);
        ImageView locImage = findViewById(R.id.locImage);
        ImageButton favButton = findViewById(R.id.favButton);
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

        // set rating text
        setRatingsText(location, ratingText);

        // set busy progress bar  5: VERY BUSY; 1 NOT BUSY AT ALL

        // set icons for facilities available
        List<Resource> resources = resourcesByLocation.get(locationName);
        addIconsToFacilities(resources, facilities);


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
     * Gets the corresponding rating for the library, and set that as the text in the location card
     * @param library
     * @param ratings
     */
    private void setRatingsText(Library library, TextView ratings) {
        String rating = ratingsByLocation.get(library.getName());
        ratings.setText(rating);
    }

    /**
     * Gets the corresponding rating for the study space, and set that as the text in the location card
     * @param studySpace
     * @param ratings
     */
    private void setRatingsText(StudySpace studySpace, TextView ratings) {
        String rating = ratingsByLocation.get(studySpace.getName());
        ratings.setText(rating);
    }


    /**
     * Gets the corresponding rating for the restaurant, and set that as the text in the location card
     * @param restaurant
     * @param ratings
     */
    private void setRatingsText(Restaurant restaurant, TextView ratings) {
        String rating = ratingsByLocation.get(restaurant.getName());
        ratings.setText(rating);
    }



    /**
     * Gets the average rating for each result location, and link it to the name of that location
     * Using getReviewByEntity (giving entityID and reviewType), it will return a list of reviews
     * With this list of reviews, for each review, get the score of the review (.getScore)
     * Then, calculate the average reviews for this, and set that as the text
     * @param results
     * @return
     */
    private void getAllRatings(List<Location> results) {
        for (Location location : results) {
            String locationName = location.getName();

            try {
                int locationID = location.getId();
                String type = location.getType();
                ReviewType typeEnum = ReviewType.valueOf(type);
                List<Review> reviewList = new ReviewServiceImpl().getReviewsByEntity(locationID, typeEnum);
                String averageRating = getAverageRating(reviewList);
                ratingsByLocation.put(locationName, averageRating);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getAverageRating(List<Review> reviewList) {
        double totalScore = 0;

        for (Review review : reviewList) {
            totalScore += review.getScore();
        }

        double averageScore = totalScore / reviewList.size();
        String averageScoreText = String.valueOf(averageScore);
        return averageScoreText;
    }

    /**
     * Gets all resources of the building the location is in
     * @param results
     */
    private void getAllResources(List<Location> results) {
        for (Location location : results) {
            int buildingID = location.getBuildingId();
            String locationName = location.getName();

            try {
                System.out.println("in search results, before getting avail resources");
                List<Resource> availResources = new ResourceServiceImpl().getResourceFromBuilding(buildingID);
                System.out.println("in search results, gotten avail resources");
                resourcesByLocation.put(locationName, availResources);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
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
            closingIcon.setColorFilter(ContextCompat.getColor(getApplication(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
//            closingIcon.setColorFilter(ContextCompat.getColor(MainActivity.getAppContext(),
//                    R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            // setColorFilter(getApplicationContext().getResources().getColor.red));
        } else {
            closingIcon.setColorFilter(ContextCompat.getColor(getApplication(), R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
//            closingIcon.setColorFilter(ContextCompat.getColor(MainActivity.getAppContext(),
//                    R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
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