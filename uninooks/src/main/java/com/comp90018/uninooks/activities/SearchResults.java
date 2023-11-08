package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.comp90018.uninooks.models.favorite.Favorite;
import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.location.building.Building;
import com.comp90018.uninooks.models.location.library.Library;
import com.comp90018.uninooks.models.location.resource.Resource;
import com.comp90018.uninooks.models.location.resource.ResourceType;
import com.comp90018.uninooks.models.location.restaurant.Restaurant;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.service.building.BuildingServiceImpl;
import com.comp90018.uninooks.service.busy_rating.BusyRatingServiceImpl;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.resource.ResourceServiceImpl;
import com.comp90018.uninooks.service.review.ReviewServiceImpl;
import com.comp90018.uninooks.service.sortingComparators.DistanceComparator;
import com.comp90018.uninooks.service.sortingComparators.NameComparator;
import com.comp90018.uninooks.service.sortingComparators.RatingComparator;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class SearchResults extends AppCompatActivity {
    private int userID;
    LinearLayout resultCardArea;
    List<Location> results;

    CardView cardView;
    ImageButton returnButton;
    HashMap<Integer, List<Object>> locationToUI;
    HashMap<Integer, Building> buildingsByLocation;
    HashMap<String, String> ratingsByLocation;
    HashMap<String, List<Resource>> resourcesByLocation;
    HashMap<String, Double> busyRatingByLocation;
    ArrayList<Favorite> userFavs;

    Comparator<Location> nameComparator;
    Comparator<Location> distanceComparator;
    Comparator<Location> ratingComparator;



    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);
        Intent intent = getIntent();
//        userID = Integer.parseInt(intent.getStringExtra("USERID_EXTRA"));

        resultCardArea = findViewById(R.id.resultCardArea);
        returnButton = findViewById(R.id.returnButton);

        results = new ArrayList<>();
        userFavs = new ArrayList<>();

        locationToUI = new HashMap<>();
        buildingsByLocation = new HashMap<>();
        ratingsByLocation = new HashMap<>();
        resourcesByLocation = new HashMap<>();
        busyRatingByLocation = new HashMap<>();

        nameComparator = new NameComparator();
        distanceComparator = new DistanceComparator();



        new Thread() {
            @Override
            public void run() {
                try {
                    // retrieve what was searched for/filtered for
                    if (intent.hasExtra("searchQuery")) {
                        String searchString = intent.getStringExtra("searchQuery");
                        results = new LocationServiceImpl().findAllLocations("STUDY", searchString, true);

                    } else if (intent.hasExtra("filters")) {
                        results = new LocationServiceImpl().findAllLocations("STUDY", null, true);
                        HashMap<String, String> filters = (HashMap<String, String>) getIntent().getSerializableExtra("filters");
                        getAllBuildingsOfLocs(results);
                        getAllRatings(results);
                        getAllResources(results);
                        ratingComparator = new RatingComparator(ratingsByLocation);
                        filterResults(filters);
                    }

                    // have to get all the ratings here (any other API I call, have to be done here and not in the UI thread)
                    getAllBuildingsOfLocs(results);
                    getAllRatings(results);
                    getAllResources(results);
                    getAllBusyRatings(results);
                    getAllUserFavs();

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
                    showTextMessage("Something went wrong, please try again.");
//                    throw new RuntimeException(e);
                }
            }
        }.start();

        returnButton.setOnClickListener(returnListener);
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
        ProgressBar loadingGIF = findViewById(R.id.loadingGIF);
        loadingGIF.setVisibility(View.VISIBLE);

        for (Location location : results) {
            String type = location.getType();

            if (type.equals("LIBRARY")) {
                cardView = createLibraryLocationCard((Library) location);
            } else if (type.equals("STUDY_SPACE")) {
                cardView = createStudyLocationCard((StudySpace) location);
            } else if (type.equals("RESTAURANT")) {
                cardView = createRestaurantLocationCard((Restaurant) location);
            }
            resultCardArea.addView(cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(SearchResults.this, LocationActivity.class);
//                    String userIDString = String.valueOf(userID);
                    String locationIDString = String.valueOf(location.getId());
//                    intent.putExtra("USERID_EXTRA", userIDString);
                    intent.putExtra("SPACE_ID_EXTRA", locationIDString);
                    startActivity(intent);
                }
            });
        }
        loadingGIF.setVisibility(View.GONE);
    }


    private CardView createStudyLocationCard(StudySpace location) {
        List<Object> interactables = new ArrayList<>();
        int buildingID = location.getBuildingId();
        int locationID = location.getId();
        String locationName = location.getName();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, null);
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

        // set the image!!!

        // set the distance away!!!!!
        Double dist = location.getDistanceFromCurrentPosition();
        int distInt = (int) Math.round(dist);
        distanceAway.setText(distInt + "m");

        // set the clock colour
        updateClockColour(location);

        // set opening hours text
        setOpeningHoursText(location, openingHours);

        // set rating text
        setRatingsText(location, ratingText);

        // set busy progress bar!!!!!
        updateProgressBar(location);

        // set whether something has been favourited or not!!!! <-- have to fist check whether it is in the favourite list!
        for (Favorite fav : userFavs) {
            if (fav.getStudySpaceId() == locationID) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.heart_fill);
                favButton.setBackground(drawable);
            }
        }

        // set icons for facilities available
        addIconsToFacilities(location, locationCard);
        return locationCard;
    }


    private CardView createLibraryLocationCard(Library location) {
        List<Object> interactables = new ArrayList<>();
        int buildingID = location.getBuildingId();
        int locationID = location.getId();
        String locationName = location.getName();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, null);
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
        Double dist = location.getDistanceFromCurrentPosition();
        int distInt = (int) Math.round(dist);
        distanceAway.setText(distInt + "m");

        // set the clock colour
        updateClockColour(location);

        // set opening hours text
        setOpeningHoursText(location, openingHours);

        // set rating text!!!!!!
        setRatingsText(location, ratingText);

        // set busy progress bar
        updateProgressBar(location);

        // set whether this location is in favourites or not
        for (Favorite fav : userFavs) {
            if (fav.getLibraryId() == locationID) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.heart_fill);
                favButton.setBackground(drawable);
            }
        }

        // set icons for facilities available
        addIconsToFacilities(location, locationCard);
        return locationCard;
    }


    /**
     * Each location put in hash map from locationID --> [capacity, closingIcon]
     *
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

        // set the distance away!!!!
        Double dist = location.getDistanceFromCurrentPosition();
        int distInt = (int) Math.round(dist);
        distanceAway.setText(distInt + "m");

        // set the clock colour
        updateClockColour(location);

        // set opening hours text
        setOpeningHoursText(location, openingHours);

        // set rating text
        setRatingsText(location, ratingText);

        // set busy progress bar  5: VERY BUSY; 1 NOT BUSY AT ALL
        updateProgressBar(location);

        // set whether this location is in user fav list
        for (Favorite fav : userFavs) {
            if (fav.getRestaurantId() == locationID) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.heart_fill);
                favButton.setBackground(drawable);
            }
        }

        // set icons for facilities available
        List<Resource> resources = resourcesByLocation.get(locationName);
        addResourcesToFacilities(resources, locationCard);

        return locationCard;
    }

    private void setTitle(Location location, TextView cardTitle) {
        String title = location.getName();
        cardTitle.setText(title);
    }

    private void setOpeningHoursText(Location location, TextView openingHours) {
        String text;
        Time openingTime = location.getOpenTime();
        Time closingTime = location.getCloseTime();

        if (closingTime == null) {
            text = "Closed";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String openingTimeText = sdf.format(openingTime);
            String closingTimeText = sdf.format(closingTime);

            double hoursToClose = calcTimeToClose(closingTime);

            LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
            int hour = localTimeAEDT.now().getHour();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Time currTime = new Time(calendar.getTimeInMillis());

            if (hoursToClose <= 0 || !withinRange(openingTime, closingTime, currTime)) {
                text = "Closed";
            } else {
                text = "Opening Hours: " + openingTimeText + " - " + closingTimeText;
            }
        }
        openingHours.setText(text);
    }

    /**
     * Checks whether the current time is within the range of opening time and closing time
     *
     * @param openingTime
     * @param closingTime
     * @param currTime
     * @return
     */
    private boolean withinRange(Time openingTime, Time closingTime, Time currTime) {
        int closingHours = closingTime.getHours();
        if (closingHours == 0) {
            closingHours = 24;
        }
        long openingTimeMillis = openingTime.getHours() * 3600 * 1000 + openingTime.getMinutes() * 60 * 1000;
        long closingTimeMillis = closingHours * 3600 * 1000 + closingTime.getMinutes() * 60 * 1000;

        long currTimeMillis = currTime.getHours() * 3600 * 1000 + currTime.getMinutes() * 60 * 1000;

        if (currTimeMillis >= openingTimeMillis && currTimeMillis <= closingTimeMillis) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the corresponding rating for the library, and set that as the text in the location card
     *
     * @param library
     * @param ratings
     */
    private void setRatingsText(Library library, TextView ratings) {
        String rating = ratingsByLocation.get(library.getName());
        ratings.setText(rating);
    }

    /**
     * Gets the corresponding rating for the study space, and set that as the text in the location card
     *
     * @param studySpace
     * @param ratings
     */
    private void setRatingsText(StudySpace studySpace, TextView ratings) {
        String rating = ratingsByLocation.get(studySpace.getName());
        ratings.setText(rating);
    }


    /**
     * Gets the corresponding rating for the restaurant, and set that as the text in the location card
     *
     * @param restaurant
     * @param ratings
     */
    private void setRatingsText(Restaurant restaurant, TextView ratings) {
        String rating = ratingsByLocation.get(restaurant.getName());
        ratings.setText(rating);
    }

    /**
     * Links the building to the location for all results received
     *
     * @param results
     */
    private void getAllBuildingsOfLocs(List<Location> results) {
        for (Location location : results) {
            try {
                int locationID = location.getId();
                int buildingID = location.getBuildingId();
                String type = location.getType();
                ReviewType typeEnum = ReviewType.valueOf(type);
                Building building = new BuildingServiceImpl().getBuilding(buildingID, typeEnum);
                buildingsByLocation.put(locationID, building);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Gets the average rating for each result location, and link it to the name of that location
     * Using getReviewByEntity (giving entityID and reviewType), it will return a list of reviews
     * With this list of reviews, for each review, get the score of the review (.getScore)
     * Then, calculate the average reviews for this, and set that as the text
     *
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
        DecimalFormat df = new DecimalFormat("#.0");

        for (Review review : reviewList) {
            totalScore += review.getScore();
        }

        double averageScore = totalScore / reviewList.size();
        String averageScoreText = df.format(averageScore);

        return averageScoreText;
    }

    /**
     * Gets all resources of the building the location is in
     *
     * @param results
     */
    private void getAllResources(List<Location> results) {
        for (Location location : results) {
            int buildingID = location.getBuildingId();
            String locationName = location.getName();

            try {
                List<Resource> availResources = new ResourceServiceImpl().getResourceFromBuilding(buildingID);
                for (Resource r : availResources) {
                    System.out.println(r.getName());
                }
                resourcesByLocation.put(locationName, availResources);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * Gets all the busy ratings of the locations
     *
     * @param results
     */
    private void getAllBusyRatings(List<Location> results) {
        for (Location location : results) {
            String locationName = location.getName();

            try {
                int locationID = location.getId();
                String type = location.getType();
                ReviewType typeEnum = ReviewType.valueOf(type);
                Double avgBusyRating = new BusyRatingServiceImpl().getAverageScoreFromEntity(locationID, typeEnum);
                busyRatingByLocation.put(locationName, avgBusyRating);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Ready to be implemented, just need to wait for the intent to get userID
     */
    private void getAllUserFavs() {
        List<Favorite> favLibraries = new ArrayList<>();
        List<Favorite> favStudySpaces = new ArrayList<>();

//        try {
//            favLibraries = new FavoriteServiceImpl().getFavoritesByUser(userID, ReviewType.LIBRARY);
//            favStudySpaces = new FavoriteServiceImpl().getFavoritesByUser(userID, ReviewType.STUDY_SPACE);
//            userFavs.addAll(favLibraries);
//            userFavs.addAll(favStudySpaces);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    private void addIconsToFacilities(StudySpace location, CardView locationCard) {
        Building building = buildingsByLocation.get(location.getId());
        ImageView gradIcon = locationCard.findViewById(R.id.gradSpace);
        ImageView noiseIcon = locationCard.findViewById(R.id.noise);
        ImageView afterHoursIcon = locationCard.findViewById(R.id.afterHours);
        ImageView accessibleIcon = locationCard.findViewById(R.id.accessible);

        Integer minimumAccess = location.getMinimumAccessAQFLevel();
        Boolean talkAllowed = location.isTalkAllowed();

        if (minimumAccess > 7) {
            gradIcon.setVisibility(View.VISIBLE);
        }

        if (talkAllowed) {
            noiseIcon.setVisibility(View.VISIBLE);
        } else {
            Drawable quietDrawable = ContextCompat.getDrawable(this, R.drawable.quiet_outline);
            noiseIcon.setBackground(quietDrawable);
            noiseIcon.setVisibility(View.VISIBLE);
        }

        afterHoursIcon.setVisibility(View.VISIBLE);

        if (building.isHasAccessibility()) {
            accessibleIcon.setVisibility(View.VISIBLE);
        }

        List<Resource> resources = resourcesByLocation.get(location.getName());
        addResourcesToFacilities(resources, locationCard);
    }


    private void addIconsToFacilities(Library location, CardView locationCard) {
        Building building = buildingsByLocation.get(location.getId());
        ImageView noiseIcon = locationCard.findViewById(R.id.noise);
        ImageView accessibleIcon = locationCard.findViewById(R.id.accessible);

        boolean quietZone = location.isHasQuietZones();
        if (!quietZone) {
            noiseIcon.setVisibility(View.VISIBLE);
        } else {
            Drawable quietDrawable = ContextCompat.getDrawable(this, R.drawable.quiet_outline);
            noiseIcon.setBackground(quietDrawable);
            noiseIcon.setVisibility(View.VISIBLE);
        }

        if (building.isHasAccessibility()) {
            accessibleIcon.setVisibility(View.VISIBLE);
        }

        List<Resource> resources = resourcesByLocation.get(location.getName());
        addResourcesToFacilities(resources, locationCard);
    }

    private void addResourcesToFacilities(List<Resource> resources, CardView locationCard) {
        ImageView atmIcon = locationCard.findViewById(R.id.atm);
        ImageView parkingIcon = locationCard.findViewById(R.id.parking);
        ImageView microwaveIcon = locationCard.findViewById(R.id.microwave);
        ImageView vendingIcon = locationCard.findViewById(R.id.vendingMachine);

        for (Resource resource : resources) {
            ResourceType resourceType = resource.getType();

            switch (resourceType) {
                case ATM:
                    atmIcon.setVisibility(View.VISIBLE);
                    break;
                case CAR_PARK:
                    parkingIcon.setVisibility(View.VISIBLE);
                    break;
                case KITCHEN:
                case MICROWAVE_OVEN:
                    microwaveIcon.setVisibility(View.VISIBLE);
                    break;
                case VENDING_MACHINE:
                    vendingIcon.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private double calcTimeToClose(Time closingTime) {
        LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
        int hour = LocalTime.now().getHour();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Time currTime = new Time(calendar.getTimeInMillis());

        int closeHour = closingTime.getHours();

        if (closeHour == 0) {
            closeHour = 24;
        }

        long timeDiffInSeconds = (closeHour - currTime.getHours()) * 3600
                + (closingTime.getMinutes() - currTime.getMinutes()) * 60
                + (closingTime.getSeconds() - currTime.getSeconds());

        double timeDiffInHours = (double) timeDiffInSeconds / 3600;

        return timeDiffInHours;
    }

    private void updateClockColour(Location location) {
        int locationID = location.getId();
        ImageView closingIcon = getClosingIcon(locationID);
        Time openingTime = location.getOpenTime();
        Time closingTime = location.getCloseTime();

        LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
        int hour = LocalTime.now().getHour();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Time currTime = new Time(calendar.getTimeInMillis());

        if (closingTime == null) {
            closingIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplication(), R.color.red), PorterDuff.Mode.SRC_IN);
        } else {
            Double timeDiff = calcTimeToClose(closingTime);

            if (timeDiff <= 1 || !withinRange(openingTime, closingTime, currTime)) {
                closingIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplication(), R.color.red), PorterDuff.Mode.SRC_IN);
            } else {
                closingIcon.getBackground().setColorFilter(ContextCompat.getColor(getApplication(), R.color.deepBlue), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private ImageView getClosingIcon(int locationID) {
        List<Object> interactables = locationToUI.get(locationID);
        ImageView icon = (ImageView) interactables.get(1);
        return icon;
    }

    private void updateProgressBar(Location location) {
        String locationName = location.getName();
        int locationID = location.getId();
        ProgressBar busyBar = getProgressBar(locationID);
        Time closingTime = location.getCloseTime();

        Double busyRating = busyRatingByLocation.get(locationName);
        if (busyRating == null || closingTime == null) {
            busyBar.setProgress(5);
        } else {
            int busyRatingPercent = (int) (busyRating * 20);

//            double hoursToClose = calcTimeToClose(closingTime);
//            if (hoursToClose <= 1) {

//                busyBar.setProgress(5);
//            }
            busyBar.setProgress(busyRatingPercent);
            if (busyRatingPercent >= 0 && busyRatingPercent <= 45) {
                busyBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));

            } else if (busyRatingPercent > 45 && busyRatingPercent <= 75) {
                busyBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow)));

            } else {
                busyBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
            }
        }
    }

    /**
     * Progress bar is for how busy a location is currently (from busy rating)
     *
     * @param locationID
     * @return
     */
    private ProgressBar getProgressBar(int locationID) {
        List<Object> interactables = locationToUI.get(locationID);
        ProgressBar capacity = (ProgressBar) interactables.get(0);
        return capacity;
    }


    /**
     * Filters the results to be displayed
     */
    private void filterResults(HashMap<String, String> filters) {
        // DISTANCE
        if (filters.containsKey("DISTANCE")) {
            // remove results that are beyond this distance
            int distance = Integer.parseInt(filters.get("DISTANCE"));
            distanceRemove(distance);
        }

        // RADIO
        if (filters.containsKey("RADIO")) {
            String sortBy = filters.get("RADIO");
            sortResults(sortBy);
        }

        // CHECKBOX
        for (String key : filters.keySet()) {
            if (key.startsWith("CHECKBOX")) {
                String facility = filters.get(key);
                // remove results that do not have this facility
                results = facilityRemove(facility);
            }
        }
    }

    private void distanceRemove(int distance) {
        for (Location location : results) {
            if (location.getDistanceFromCurrentPosition() > distance) {
                results.remove(location);
            }
        }
    }

    private void sortResults(String sortBy) {
        if (sortBy.contains("Name")) {
            if (sortBy.contains("asc")) {
                results.sort(nameComparator);
            } else {
                results.sort(Collections.reverseOrder(nameComparator));
            }
        } else if (sortBy.contains("Distance")) {
            if (sortBy.contains("asc")) {
                results.sort(distanceComparator);
            } else {
                results.sort(Collections.reverseOrder(distanceComparator));
            }
        } else if (sortBy.contains("Rating")) {
            if (sortBy.contains("asc")) {
                results.sort(ratingComparator);
            } else {
                results.sort(Collections.reverseOrder(ratingComparator));
            }
        }
    }

    private List<Location> facilityRemove(String facility) {
        // depending on the string on what facility is
        // facility names: gradSpace, quietStudy, afterHours, microwave, atm, accessible, vendingMachine, parking
        List<Location> filteredResults = new ArrayList<>();

        for (Location location : results) {
            boolean keepLoc = true;
            List<Resource> resources = resourcesByLocation.get(location.getName());
            if (facility.equals("gradSpace")) {
                if (location.getType().equals("STUDY_SPACE")) {
                    StudySpace loc = (StudySpace) location;
                    if (loc.getMinimumAccessAQFLevel() == 7) {
                        keepLoc = false;
                    }
                } else if (location.getType().equals("LIBRARY")) {
                    keepLoc = false;
                }
            } else if (facility.equals("quietStudy")) {
                if (location.getType().equals("STUDY_SPACE")) {
                    StudySpace loc = (StudySpace) location;
                    if (loc.isTalkAllowed()) {
                        keepLoc = false;
                    }
                } else if (location.getType().equals("LIBRARY")) {
                    Library loc = (Library) location;
                    if (!loc.isHasQuietZones()) {
                        keepLoc = false;
                    }
                }
            } else if (facility.equals("afterHours") && !location.getType().equals("STUDY_SPACE")) {
                keepLoc = false;
            } else if (facility.equals("microwave")) {
                keepLoc = haveFacility(resources, ResourceType.MICROWAVE_OVEN);
            } else if (facility.equals("atm")) {
                keepLoc = haveFacility(resources, ResourceType.ATM);
            } else if (facility.equals("accessible")) {
                Building building = buildingsByLocation.get(location.getId());
                if (!building.isHasAccessibility()) {
                    keepLoc = false;
                }
            } else if (facility.equals("vendingMachine")) {
                keepLoc = haveFacility(resources, ResourceType.VENDING_MACHINE);
            } else if (facility.equals("parking")) {
                keepLoc = haveFacility(resources, ResourceType.CAR_PARK);
            }

            if (keepLoc) {
                filteredResults.add(location);
            }
        }
        return filteredResults;
    }

    private boolean haveFacility(List<Resource> resources, ResourceType name) {
        for (Resource resource : resources) {
            ResourceType resourceType = resource.getType();
            if (resourceType == name) {
                return true;
            }
        }
        return false;
    }

    /**
     * Show message text
     *
     * @param text as the showing message
     */
    private void showTextMessage(String text) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        handler.sendMessage(msg);
    }
}