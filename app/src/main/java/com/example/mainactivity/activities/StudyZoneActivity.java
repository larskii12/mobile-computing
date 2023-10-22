package com.example.mainactivity.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.R;
import com.google.android.gms.common.api.internal.BackgroundDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudyZoneActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;

    private CountDownTimer pomodoroTimer;
    private boolean isInBackground = false;
    private List<String> unwantedApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        // Initialize UI components and set up timer controls
        // ...

        // Initialize unwantedApps list
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String unwantedAppsList = preferences.getString("unwanted_apps", "");
        unwantedApps = new ArrayList<>(Arrays.asList(unwantedAppsList.split(",")));
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
