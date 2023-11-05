package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.location.LocationService;
import com.comp90018.uninooks.service.review.ReviewService;
import com.comp90018.uninooks.service.study_space.StudySpaceServiceImpl;
import com.comp90018.uninooks.service.user.UserService;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GPSService {

    private ReviewService reviewService;

    private LocationService locationService;

    private UserService userService;

    private static Context context;

    GPSServiceImpl gpsService;

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
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        // Ask user to grant permission
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        gpsService = new GPSServiceImpl(this, this);

        Button button = (Button) findViewById(R.id.button);
        Button loginButton = findViewById(R.id.loginButton);
        Button accountButton = findViewById(R.id.accountButton);

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            gpsService.stopGPSUpdates();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        // Test button
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            // Find resources based on keyword search
//                            List<Resource> a = new ResourceServiceImpl().getResourceFromKeyWord("atm");
//                            for (Resource r : a){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", r.getName());
//                            }


                            // How to access the closest libraries
//                            ArrayList<Library> libraries = new LibraryServiceImpl().getClosestLibraries(new LatLng(-1, -1), 10);
//                            for (Library library : libraries){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Name   " + library.getName());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Building ID   " + library.getBuildingId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Distance From Current Location   " + library.getDistanceFromCurrentPosition() + " meters");
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Library ID   " + library.getId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library is Open Today   " + library.issOpenToday());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library is Opening Now   " + library.isOpeningNow());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Opening Time   " + library.getOpenTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Closing Time   " + library.getCloseTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library has quiet zone  " + library.isHasQuietZones());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "\n\n");
//                            }

                            // How to access the top rated libraries
//                            ArrayList<Library> libraries = new LibraryServiceImpl().getTopRatedLibraries(new LatLng(-1, -1), 10);
//                            for (Library library : libraries){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Name   " + library.getName());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Building ID   " + library.getBuildingId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Distance From Current Location   " + library.getDistanceFromCurrentPosition() + " meters");
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Library ID   " + library.getId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library is Open Today   " + library.issOpenToday());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library is Opening Now   " + library.isOpeningNow());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Opening Time   " + library.getOpenTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Closing Time   " + library.getCloseTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library has quiet zone  " + library.isHasQuietZones());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library Rating   " + library.getAverage_rating());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "\n\n");
//                            }

//                             How to get closest study spaces
//                            ArrayList<StudySpace> closestStudySpaces = new StudySpaceServiceImpl().getClosestStudySpaces(new LatLng(-1, -1), 50);
//                            for (StudySpace studySpace : closestStudySpaces){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Name   " + studySpace.getName());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space ID   " + studySpace.getId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Building ID   " + studySpace.getBuildingId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Distance From Current Location   " + studySpace.getDistanceFromCurrentPosition() + " meters");
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Library ID   " + studySpace.getLibraryId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space is Open Today   " + studySpace.issOpenToday());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space is Opening Now   " + studySpace.isOpeningNow());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space is Talk Allowed   " + studySpace.isTalkAllowed());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Minimum AQF Level   " + studySpace.getMinimumAccessAQFLevel());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library is Opening Now   " + studySpace.isOpeningNow());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Opening Time   " + studySpace.getOpenTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Closing Time   " + studySpace.getCloseTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "\n\n");
//                            }



                            // How to get top rated study spaces
//                           ArrayList<StudySpace> s = new StudySpaceServiceImpl().getTopRatedStudySpaces(new LatLng(1, 1), 50);
//                           for (StudySpace studySpace: s){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Name   " + studySpace.getName());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space ID   " + studySpace.getId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Building ID   " + studySpace.getBuildingId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Distance From Current Location   " + studySpace.getDistanceFromCurrentPosition() + " meters");
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Library ID   " + studySpace.getLibraryId());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space is Open Today   " + studySpace.issOpenToday());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space is Opening Now   " + studySpace.isOpeningNow());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space is Talk Allowed   " + studySpace.isTalkAllowed());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Minimum AQF Level   " + studySpace.getMinimumAccessAQFLevel());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library is Opening Now   " + studySpace.isOpeningNow());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Opening Time   " + studySpace.getOpenTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Closing Time   " + studySpace.getCloseTime());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Rating   " + studySpace.getAverage_rating());
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "\n\n");
//                           }

                            // How to get a entity average review score
//                            double average_rating = new ReviewServiceImpl().getAverageRating(1, ReviewType.LIBRARY);
//                            Log.d("AAAAAAAAAAAAAAAAAAAAA", String.valueOf(average_rating));

//
//
                            // How to access review with a given entity and entity Type, to get id, you can obtain from the previous location service
//                            List<Review> reviews = new ReviewServiceImpl().getReviewsByEntity(3, ReviewType.LIBRARY);
//                            for (Review review : reviews) {
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Study Space Score: " + review.getScore() + "   Comments: " + review.getComment() + "    Review Time:  " + review.getTime());
//                            }
//
//
                            // How to get entity busy rating for right now. To get id, you can obtain from the previous location service
                            // HIGHER --- LESS BUSY.   LOWER - MORE BUSY        4 - 5 very very busy,  3 - 4 busy,  2 - 3 fair,   1 - 2, not busy    0 - 1 no body
//                            Double busyRatings = new BusyRatingServiceImpl().getAverageScoreFromEntity(3, LIBRARY);
//                            Log.d("AAAAAAAAAAAAAAAAAAAAA", "Library busy: " + busyRatings);
//
//
//
                            // How to add a review
//                            try {
//                                Review newReview = new ReviewServiceImpl().addReview(263, 3, LIBRARY, 5, "I love this excellent study space");
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Review successes, Your new review id is " + newReview.getReviewId());
//                            }
//                            catch (Exception e){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Review failed, please contact the IT administrator");
//                            }
//
//
//
                            // How to add a busy rating
//                            try {
//                                Boolean busyRating = new BusyRatingServiceImpl().addBusyRating(3, STUDY_SPACE, 4);
//
//                                // Check success or not
//                                if (busyRating){
//                                    Log.d("AAAAAAAAAAAAAAAAAAAAA", "Busy rating Success");
//                                }
//                                else{
//                                    throw new Exception();
//                                }
//                            }
//
//                            catch (Exception e){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", "Busy rating failed, please contact the IT administrator.");
//                            }




                            // Detect the recent 20 seconds used apps
//                            ArrayList<BackgroundApp> recentApps = new BackgroundAppServiceImpl(MainActivity.getAppContext()).getBackgroundApps(1000 * 20);
//                            for (BackgroundApp recentApp : recentApps){
//                                Log.d("AAAAAAAAAAAAAAAAAAAAA", recentApp.getPackageName() + "   Time:  " + recentApp.getLastTimeUsed());
//                            }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            gpsService.stopGPSUpdates();
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);
                        }

                        // If exception when operating
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public void onStart(){
        super.onStart();
        gpsService.startGPSUpdates();
    }

    public void onRestart(){
        super.onRestart();
        gpsService.startGPSUpdates();
    }

    // When back button pressed
    public void onBackPressed() {
        super.onBackPressed();
        gpsService.stopGPSUpdates();
    }

    public void onPause() {
        super.onPause();
        gpsService.stopGPSUpdates();
    }
    public void onResume() {
        super.onResume();
        gpsService.startGPSUpdates();
    }

    public void onStop(){
        super.onStop();

    }

    public void onDestroy(){
        super.onDestroy();
        gpsService.stopGPSUpdates();
    }


    public static Context getAppContext () {
        return context;
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

    @Override
    public void onGPSUpdate(Location location) {
        gpsService.stopGPSUpdates();
    }
}