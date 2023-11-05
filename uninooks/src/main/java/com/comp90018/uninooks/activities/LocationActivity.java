package com.comp90018.uninooks.activities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

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
        String spaceID = intent.getStringExtra("SPACE_ID_EXTRA");
        System.out.println(spaceID);


//        List<Location> studySpacesNearby = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println(spaceID);
                    List<Review> reviews = new ReviewServiceImpl().getReviewsByEntity(Integer.valueOf(spaceID), ReviewType.STUDY_SPACE);
                    LinearLayout reviewsLayout = findViewById(R.id.reviews);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            for(Review review : reviews) {
                                System.out.println(review.getComment());
                                CardView card = (CardView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.review_layout, reviewsLayout, false);
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
}
