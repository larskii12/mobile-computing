package com.example.mainactivity.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.LoginActivity;
import com.example.mainactivity.R;
import com.example.mainactivity.config.DatabaseHelper;
import com.example.mainactivity.service.location.LocationService;
import com.example.mainactivity.service.review.ReviewService;
import com.example.mainactivity.service.user.UserService;
import com.example.mainactivity.service.user.UserServiceImpl;

public class MainActivity extends AppCompatActivity {
    private ReviewService reviewService;
    private LocationService locationService;
    private UserService userService;

    private SearchView searchBar;

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

//        searchBar = findViewById(R.id.searchBar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        });

        // Test button
//        button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            DatabaseHelper db = new DatabaseHelper();
//                            if (db.databaseConnectionTest()) {
//                                System.out.println("Database online!");
//                                try {
////
////                                    // Login
////                                    if(new User().logIn("Jessica_Taylor@example.com", "xxx")){
////                                        System.out.println("Login Successfully.");
////                                    }
////
////
////
////
////                                    // Add an user
////                                    new User().addUser("test1", "test1@test1.com", "xxx", "IT", 8);
////                                    System.out.println("Add successfully.");
////
////
////
////
////                                    // Delete an user
////                                    new User().deleteUser(6);
////                                    System.out.println("delete successful.");
////
////
////
////
////                                    // Get an user information
////                                    User user = new User().getUser(15);
////                                    if (user == null) {
////                                        throw new Exception("Some error happened, please contact the IT administrator.");
////                                    }
////
////                                    else{
////                                        System.out.println("Query successful.");
////                                        System.out.println(user.getUserId());
////                                        System.out.println(user.getUserName());
////                                        System.out.println(user.getUserEmail());
////                                        System.out.println(user.getUserFaculty());
////                                        System.out.println(user.getUserAQFLevel());
////                                    }
////
////
////
////                                  // Update an user's user name information
////                                    new User().updateUserName(15, "Frank Martinez");
////                                    System.out.println("The user name has been updated successfully.");
////
////                                  // Update an user's user email information
////                                    new User().updateUserEmail(15,"useaaaaartest@tt.com");
////                                    System.out.println("The user email has been updated successfully.");
////
////                                  // Update an user's user password
////                                    new User().updateUserPassword(47, "ddd", "xxx");
////                                    System.out.println("The user password has been updated successfully.");
////
////                                  // Reset an user's user password
////                                    new User().resetUserPassword("Jessica_Taylor@example.com", "xxx");
////                                    System.out.println("Password reset successfully.");
////
////                                  // Update an user's user faculty information
////                                    new User().updateUserFaculty(15, "IT");
////                                    System.out.println("The user faculty has been updated successfully.");
////
//                                    // Update an user's user AQF level information
////                                    new User().updateUserAQFLevel(15, 8);
////                                    System.out.println("The user AQF level has been updated successfully.");
//
//
//                                }
//
//                                // If exception when operating
//                                catch (Exception e) {
//                                    System.out.println(e.getMessage());
//                                }
//                            }
//
//                            // If database is not reachable
//                            else {
//                                System.out.println("Database offline!");
//                            }
//                        }
//
//                        // If exception when operating
//                        catch (Exception e) {
//                            System.out.println(e);
//                        }
//                    }
//                }.start();
//            }
//        });

    }

    public static Context getAppContext() {
        return context;
    }
}