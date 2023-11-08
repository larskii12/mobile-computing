package com.comp90018.uninooks.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.emulator.EmulatorServiceImpl;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1234;

    private static final int REQUEST_USAGE_ACCESS = 1001;

    SharedPreferences.Editor editor;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
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

        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("firstLaunchCheckFile", MODE_PRIVATE);
        boolean isFirstTimeLaunch = sharedPreferences.getBoolean("isFirstTimeLaunch", true);

        editor = sharedPreferences.edit();

        // Check permission is given or not, if not given, pop up permission needed box
        // Need to change to check whether the app is first time launch
        if (isFirstTimeLaunch) {

            // Show dialogue
            showPermissionDialogueAndStartMainActivity();

        }

        // If all permission already granted, go to main activities directly
        else {

            // Set GPS status
            GPSServiceImpl.setGPSPermissionStatus(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

            // Start main activity
            startMainActivity();
            finish();
        }

        /**
         * Emulator Testing Mode Detection
         */
        if (EmulatorServiceImpl.isEmulator()) {
            showTextMessage("Emulator Testing Mode");
        }
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

    public void onPause() {
        super.onPause();
    }
    public void onResume() {
        super.onResume();
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();;
    }

    /**
     * Call this method when user return from the usage permission page
     * @param requestCode as the request code
     * @param resultCode as the result code
     * @param data as the intent data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Regardless the usage permission given or not, request for location permission
        if (requestCode == REQUEST_USAGE_ACCESS) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GPSServiceImpl.setGPSPermissionStatus(true);

            }

            else {
                GPSServiceImpl.setGPSPermissionStatus(false);
                showTextMessage("Location permission not granted, some functions may not work properly.");

            }

            // Update first launch is done and start Main Activity
            editor.putBoolean("isFirstTimeLaunch", false);
            editor.apply();

            startMainActivity();
        }
    }

    /**
     * Show the permission dialogue, only shows the first time app launch
     */
    public void showPermissionDialogueAndStartMainActivity(){

        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("For your best experience, Uninook needs two permissions, usage data for study mode monitor and location for recommendation. Your data is store on your local device and not going to share with any third-party even Uninook.")
                .setPositiveButton("I understand", (dialog, which) -> {

                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivityForResult(intent, REQUEST_USAGE_ACCESS);
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Start the Main Activity
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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