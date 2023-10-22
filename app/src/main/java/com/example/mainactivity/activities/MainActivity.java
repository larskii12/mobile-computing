package com.example.mainactivity.activities;

import static com.example.mainactivity.models.review.ReviewType.LIBRARY;
import static com.example.mainactivity.models.review.ReviewType.STUDY_SPACE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.LoginActivity;
import com.example.mainactivity.R;
import com.example.mainactivity.config.DatabaseHelper;
import com.example.mainactivity.models.favorite.Favorite;
import com.example.mainactivity.models.location.Location;
import com.example.mainactivity.service.busy_rating.BusyRatingService;
import com.example.mainactivity.service.busy_rating.BusyRatingServiceImpl;
import com.example.mainactivity.service.favorite.FavoriteService;
import com.example.mainactivity.service.favorite.FavoriteServiceImpl;
import com.example.mainactivity.service.location.LocationService;
import com.example.mainactivity.service.location.LocationServiceImpl;
import com.example.mainactivity.service.review.ReviewService;
import com.example.mainactivity.service.user.UserService;
import com.example.mainactivity.service.user.UserServiceImpl;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ReviewService reviewService;

    private LocationService locationService;

    private UserService userService;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
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
                            DatabaseHelper db = new DatabaseHelper();
                            if (db.databaseConnectionTest()) {
                                System.out.println("Database online!");
                                try {
//
//                                    BusyRatingService busyRatingService = new BusyRatingServiceImpl();
//
//                                    System.out.println(busyRatingService.getAverageScoreFromEntity(4, STUDY_SPACE));

//                                    List<Location> allLocations = new LocationServiceImpl().findAllLocations("ALL", "", false);
//
//                                    for (Location l: allLocations) {
//                                        System.out.println(l.getName());
//                                    }

//                                    FavoriteService favoriteService = new FavoriteServiceImpl();
//
//                                    // Add an user
//                                    Favorite favorite = favoriteService.addFavorite(1, 2, LIBRARY);
//                                    System.out.println(favorite.getLibraryId());
//
//                                    System.out.println(favoriteService.isFavoriteByUser(1, 3, LIBRARY));
//                                    System.out.println(favoriteService.isFavoriteByUser(2, 3, LIBRARY));
//
//                                    List<Favorite> favorites = favoriteService.getFavoritesByUser(1, LIBRARY);
//                                    for (Favorite f : favorites) {
//                                        System.out.println(f.getId());
//                                    }

//                                    // Send OTP
//                                    OTPServiceImpl otpService = new OTPServiceImpl();
//                                    otpService.sendRegistrationOTP("afsyuanshouyi@outlook.com");
//
//                                    //
//                                    new UserServiceImpl().logIn("email@example.com", "abcd@123");
//
//
//
//
//                                    // Add an user
//                                    new User().addUser("test1", "test1@test1.com", "xxx", "IT", 8);
//                                    System.out.println("Add successfully.");
//
//
//
//
//                                    // Delete an user
//                                    new User().deleteUser(6);
//                                    System.out.println("delete successful.");
//
//
//
//
//                                    // Get an user information
//                                    User user = new User().getUser(15);
//                                    if (user == null) {
//                                        throw new Exception("Some error happened, please contact the IT administrator.");
//                                    }
//
//                                    else{
//                                        System.out.println("Query successful.");
//                                        System.out.println(user.getUserId());
//                                        System.out.println(user.getUserName());
//                                        System.out.println(user.getUserEmail());
//                                        System.out.println(user.getUserFaculty());
//                                        System.out.println(user.getUserAQFLevel());
//                                    }
//
//
//
//                                  // Update an user's user name information
//                                    new User().updateUserName(15, "Frank Martinez");
//                                    System.out.println("The user name has been updated successfully.");
//
//                                  // Update an user's user email information
//                                    new User().updateUserEmail(15,"useaaaaartest@tt.com");
//                                    System.out.println("The user email has been updated successfully.");
//
//                                  // Update an user's user password
//                                    new User().updateUserPassword(47, "ddd", "xxx");
//                                    System.out.println("The user password has been updated successfully.");
//
//                                  // Reset an user's user password
//                                    new User().resetUserPassword("Jessica_Taylor@example.com", "xxx");
//                                    System.out.println("Password reset successfully.");
//
//                                  // Update an user's user faculty information
//                                    new User().updateUserFaculty(15, "IT");
//                                    System.out.println("The user faculty has been updated successfully.");
//
//                                     Update an user's user AQF level information
//                                    new User().updateUserAQFLevel(15, 8);
//                                    System.out.println("The user AQF level has been updated successfully.");


                                }

                                // If exception when operating
                                catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }

                            // If database is not reachable
                            else {
                                System.out.println("Database offline!");
                            }
                        }

                        // If exception when operating
                        catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }.start();
            }
        });
    }

    public static Context getAppContext() {
        return context;
    }
}