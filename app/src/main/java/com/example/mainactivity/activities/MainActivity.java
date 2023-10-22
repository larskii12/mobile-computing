package com.example.mainactivity.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mainactivity.R;
import com.example.mainactivity.service.location.LocationService;
import com.example.mainactivity.service.review.ReviewService;
import com.example.mainactivity.service.user.UserService;


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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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

    public static Context getAppContext() {
        return context;
    }
}