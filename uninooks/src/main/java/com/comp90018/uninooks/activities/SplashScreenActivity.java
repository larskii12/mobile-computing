//package com.comp90018.uninooks.activities;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.comp90018.uninooks.R;
//import com.comp90018.uninooks.service.emulator.EmulatorServiceImpl;
//import com.comp90018.uninooks.service.gps.GPSService;
//import com.comp90018.uninooks.service.gps.GPSServiceImpl;
//
//@SuppressLint("CustomSplashScreen")
//public class SplashScreenActivity extends AppCompatActivity implements GPSService {
//
//    private static final int REQUEST_LOCATION_PERMISSION = 1234;
//
//    SharedPreferences.Editor editor;
//
//    GPSServiceImpl gpsService;
//
//    @SuppressLint("HandlerLeak")
//    private final Handler handler = new Handler() {
//        @SuppressLint({"SetTextI18n", "HandlerLeak"})
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    String info = (String) msg.obj;
//                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_splash_screen);
//
//        SharedPreferences sharedPreferences = getSharedPreferences("firstLaunchCheckFile", MODE_PRIVATE);
//        boolean isFirstTimeLaunch = sharedPreferences.getBoolean("isFirstTimeLaunch", true);
//
//        editor = sharedPreferences.edit();
//
//        gpsService = new GPSServiceImpl(this, this);
//
//        // Check permission is given or not, if not given, pop up permission needed box
//        // Need to change to check whether the app is first time launch
//        if (isFirstTimeLaunch) {
//
//            // Show dialogue
//            showPermissionDialogueAndStartMainActivity();
//
//        }
//
//        // If all permission already granted, go to main activities directly
//        else {
//
//            // Set GPS status
//            GPSServiceImpl.setGPSPermissionStatus(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
//
//            if (GPSServiceImpl.getGPSPermission()){
//                gpsService.startGPSUpdates();
//            }
//
//            else{
//                Log.d("AAAAAAAAAAAAAAAAAAA", "gps NOT fetched and go to log in");
//                startMainActivity();
//            }
//        }
//
//        /**
//         * Emulator Testing Mode Detection
//         */
//        if (EmulatorServiceImpl.isEmulator()) {
//            showTextMessage("Emulator Testing Mode\nLocation: Melbourne Connect.");
//        }
//    }
//
//    public void onStart(){
//        super.onStart();
//    }
//
//    public void onRestart(){
//        super.onRestart();;
//    }
//
//    // When back button pressed
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//    public void onPause() {
//        super.onPause();
//    }
//    public void onResume() {
//        super.onResume();
//    }
//
//    public void onStop(){
//        super.onStop();
//    }
//
//    public void onDestroy(){
//        super.onDestroy();;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        // Update first launch is done and start Main Activity
//        Log.d("AAAAAAAAAAAAAAAAAAA", "First time launch");
//        editor.putBoolean("isFirstTimeLaunch", false);
//        editor.apply();
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                GPSServiceImpl.setGPSPermissionStatus(true);
//
//                gpsService.startGPSUpdates();
//            }
//
//            else {
//                GPSServiceImpl.setGPSPermissionStatus(false);
//
//                Log.d("AAAAAAAAAAAAAAAAAAA", "gps NOT fetched and go to log in");
//
//                startMainActivity();
//            }
//
//        }
//    }
//
//    /**
//     * Show the permission dialogue, only shows the first time app launch
//     */
//    private void showPermissionDialogueAndStartMainActivity(){
//
//        new AlertDialog.Builder(this)
//                .setTitle("Permissions Required")
//                .setMessage("To optimize your experience, Uninooks requires the following permissions: \n\n1. Access to location and accelerometer to enhance recommendations. \n\n2. Access to notification and usage data to facilitate study mode monitoring. \n\nPlease be assured that your data will be stored locally on your device and will not be shared with any third parties.")
//                .setPositiveButton("I understand", (dialog, which) -> {
//                    ActivityCompat.requestPermissions(
//                            SplashScreenActivity.this,
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                            REQUEST_LOCATION_PERMISSION
//                    );
//                })
//                .setCancelable(false)
//                .show();
//    }
//
//    private void showDialogue(String message){
//        new AlertDialog.Builder(this)
//                .setTitle("Permissions Required")
//                .setMessage(message)
//                .setPositiveButton("I understand", (dialog, which) -> {
//                })
//                .setCancelable(false)
//                .show();
//    }
//
//    /**
//     * Start the Main Activity
//     */
//    private void startMainActivity() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        overridePendingTransition(0, 0);
//        finish();
//    }
//
//    /**
//     * Show message text
//     *
//     * @param text as the showing message
//     */
//    private void showTextMessage(String text) {
//        Message msg = new Message();
//        msg.what = 0;
//        msg.obj = text;
//        handler.sendMessage(msg);
//    }
//
//    @Override
//    public void onGPSUpdate(Location location) {
//        gpsService.stopGPSUpdates();
//        Log.d("AAAAAAAAAAAAAAAAAAA", "gps fetched and go to log in");
//        startMainActivity();
//    }
//}