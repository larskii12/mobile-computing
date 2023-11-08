package com.comp90018.uninooks.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudyZoneActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;

    private CountDownTimer pomodoroTimer;
    private boolean isInBackground = false;
    private List<String> unwantedApps;

    private int userId;
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");

        // Initialize UI components and set up timer controls
        // ...

        // Initialize unwantedApps list
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String unwantedAppsList = preferences.getString("unwanted_apps", "");
        unwantedApps = new ArrayList<>(Arrays.asList(unwantedAppsList.split(",")));
    }

    public void onStart(){
        super.onStart();
    }

    public void onRestart(){
        super.onRestart();;
    }

    // When back button pressed
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInBackground = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInBackground = false;
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    public void startPomodoroTimer(long duration) {
        pomodoroTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update timer UI
            }

            public void onFinish() {
                // Handle Pomodoro timer completion
            }
        }.start();
    }

    public void openSettings(View view) {
 //       Intent intent = new Intent(this, SettingsActivity.class);
 //       startActivity(intent);
    }

    // Add methods for checking and monitoring other apps
    // ...

    // Additional methods and logic
    // ...

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == Request_Code_Location){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                updateLocation();
//            }
//        }
//    }
}
