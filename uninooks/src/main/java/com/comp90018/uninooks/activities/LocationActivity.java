package com.comp90018.uninooks.activities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.location.Location;

import java.util.ArrayList;
import java.util.List;

import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.service.review.ReviewServiceImpl;
import com.comp90018.uninooks.service.study_space.StudySpaceServiceImpl;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.location.LocationService;
import com.comp90018.uninooks.service.review.ReviewService;

public class LocationActivity extends AppCompatActivity{
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = getApplicationContext();
        Intent intent = getIntent();
        String userID = intent.getStringExtra("USERID_EXTRA");
        Integer spaceID = Integer.valueOf(intent.getStringExtra("SPACE_ID_EXTRA"));
        Integer buildingID = Integer.valueOf(intent.getStringExtra("SPACE_ID_EXTRA"));
        Integer distance = Integer.valueOf(intent.getStringExtra("SPACE_ID_EXTRA"));
        Integer libraryID = Integer.valueOf(intent.getStringExtra("SPACE_ID_EXTRA"));
        String noiseAllowed = intent.getStringExtra("SPACE_NOISE_EXTRA");
        String openTime = intent.getStringExtra("SPACE_ID_EXTRA");
        String closeTime = intent.getStringExtra("SPACE_ID_EXTRA");
//        StudySpace space = new StudySpaceServiceImpl().


//        List<Location> studySpacesNearby = new ArrayList<>();

        List<Review> reviews = null;
        try {
            reviews = new ReviewServiceImpl().getReviewsByEntity(3, ReviewType.LIBRARY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Review review : reviews) {

        }
    }
}
