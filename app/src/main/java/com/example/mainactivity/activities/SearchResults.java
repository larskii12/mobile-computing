package com.example.mainactivity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import java.util.List;

public class SearchResults extends AppCompatActivity {
    LocationService searchAPI = new LocationServiceImpl();
    ResourceService resourceAPI = new ResourceServiceImpl();
    LinearLayout resultCardArea;
    List<Location> results;

    CardView cardView;

    LinearLayout facilities;
    TextView cardTitle;
    TextView distanceAway;
    TextView openingHours;
    ProgressBar capacity;
    ImageView closingIcon;
    ImageView locImage;
    ImageButton addToFavButton;
    TextView ratingText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // retrieve what was searched for/filtered for
        Intent intent = getIntent();
        resultCardArea = findViewById(R.id.resultCardArea);
        String searchString = intent.getStringExtra("searchQuery");

        // use API to get the search result
        // study includes both libraries and study spaces
        try {
            results = searchAPI.findAllLocations("STUDY", searchString, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        int buildingID = location.getBuildingId();
        int locationID = location.getId();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, null, false);
        facilities = findViewById(R.id.facilitiesIcons);
        cardTitle = findViewById(R.id.title);
        distanceAway = findViewById(R.id.distanceText);
        openingHours = findViewById(R.id.openingHours);
        capacity = findViewById(R.id.progressBar);
        closingIcon = findViewById(R.id.closingIcon);
        locImage = findViewById(R.id.locImage);
        addToFavButton = findViewById(R.id.addToFavButton);
        ratingText = findViewById(R.id.ratingText);

        // first set the title
        setTitle(location, cardTitle);

        // set the distance away

        // set the clock colour


        // set opening hours text

        // set rating text


        // set icons for facilities available



        try {
            List<Resource> studySpaceResources = resourceAPI.getResourceFromBuilding(buildingID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String title = location.getName();
        cardTitle.setText(title);

        // I would need the location of the Location Type - maybe can just use google maps API

        return locationCard;
    }


    private CardView createLibraryLocationCard(Library location) {
        int buildingID = location.getBuildingId();
        int locationID = location.getId();

        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, null, false);
        facilities = findViewById(R.id.facilitiesIcons);
        cardTitle = findViewById(R.id.title);
        distanceAway = findViewById(R.id.distanceText);
        openingHours = findViewById(R.id.openingHours);
        capacity = findViewById(R.id.progressBar);
        closingIcon = findViewById(R.id.closingIcon);
        locImage = findViewById(R.id.locImage);
        addToFavButton = findViewById(R.id.addToFavButton);
        ratingText = findViewById(R.id.ratingText);

        // first set the title
        setTitle(location, cardTitle);

        // set the distance away

        // set the clock colour

        // set opening hours text

        // set rating text


        // set icons for facilities available

        try {
            List<Resource> libraryResources = resourceAPI.getResourceFromBuilding(buildingID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return locationCard;
    }

    private CardView createRestaurantLocationCard(Restaurant location) {
        int buildingID = location.getBuildingId();
        int locationID = location.getId();

        facilities = findViewById(R.id.facilitiesIcons);
        cardTitle = findViewById(R.id.title);
        distanceAway = findViewById(R.id.distanceText);
        openingHours = findViewById(R.id.openingHours);
        capacity = findViewById(R.id.progressBar);
        closingIcon = findViewById(R.id.closingIcon);
        locImage = findViewById(R.id.locImage);
        addToFavButton = findViewById(R.id.addToFavButton);
        ratingText = findViewById(R.id.ratingText);


        CardView locationCard = (CardView) LayoutInflater.from(this).inflate(R.layout.large_location_card, null, false);

        // first set the title
        setTitle(location, cardTitle);

        // set the distance away

        // set the clock colour

        // set opening hours text

        // set rating text


        // set icons for facilities available

        try {
            List<Resource> restaurantResources = resourceAPI.getResourceFromBuilding(buildingID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return locationCard;
    }

    private void setTitle(Location location, TextView cardTitle) {
        String title = location.getName();
        cardTitle.setText(title);
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


}